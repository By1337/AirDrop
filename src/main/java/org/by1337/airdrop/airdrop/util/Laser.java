package org.by1337.airdrop.airdrop.util;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

public abstract class Laser {
    protected final int distanceSquared;
    protected final int duration;
    protected boolean durationInTicks = false;
    protected Location start;
    protected Location end;
    protected Plugin plugin;
    protected BukkitRunnable main;
    protected BukkitTask startMove;
    protected BukkitTask endMove;
    protected Set<Player> show = ConcurrentHashMap.newKeySet();
    private final Set<Player> seen = new HashSet<>();
    private final List<Runnable> executeEnd = new ArrayList<>(1);

    protected Laser(Location start, Location end, int duration, int distance) {
        if (!Packets.enabled) {
            throw new IllegalStateException("API лазерного луча отключен. Во время инициализации произошла ошибка.");
        } else if (start.getWorld() != end.getWorld()) {
            throw new IllegalArgumentException("Локации не принадлежат одним и тем же мирам.");
        } else {
            this.start = start;
            this.end = end;
            this.duration = duration;
            this.distanceSquared = distance < 0 ? -1 : distance * distance;
        }
    }

    public Laser executeEnd(Runnable runnable) {
        this.executeEnd.add(runnable);
        return this;
    }

    public Laser durationInTicks() {
        this.durationInTicks = true;
        return this;
    }

    public void start(Plugin plugin) {
        if (this.main != null) {
            throw new IllegalStateException("Задача уже запущена");
        } else {
            this.plugin = plugin;
            this.main = new BukkitRunnable() {
                int time = 0;

                public void run() {
                    try {
                        if (this.time == Laser.this.duration) {
                            this.cancel();
                            return;
                        }

                        if (!Laser.this.durationInTicks || this.time % 20 == 0) {

                            for (Player p : Objects.requireNonNull(Laser.this.start.getWorld()).getPlayers()) {
                                if (Laser.this.isCloseEnough(p)) {
                                    if (Laser.this.show.add(p)) {
                                        Laser.this.sendStartPackets(p, !Laser.this.seen.add(p));
                                    }
                                } else if (Laser.this.show.remove(p)) {
                                    Laser.this.sendDestroyPackets(p);
                                }
                            }
                        }

                        ++this.time;
                    } catch (ReflectiveOperationException var3) {
                        var3.printStackTrace();
                    }

                }

                public synchronized void cancel() throws IllegalStateException {
                    super.cancel();
                    Laser.this.main = null;

                    try {

                        for (Player p : Laser.this.show) {
                            Laser.this.sendDestroyPackets(p);
                        }

                        Laser.this.show.clear();
                        Laser.this.executeEnd.forEach(Runnable::run);
                    } catch (ReflectiveOperationException var3) {
                        var3.printStackTrace();
                    }

                }
            };
            this.main.runTaskTimerAsynchronously(plugin, 0L, this.durationInTicks ? 1L : 20L);
            LasersManager.laserList.remove(this);
        }
    }

    public void stop() {
        if (this.main == null) {
            throw new IllegalStateException("Задача не запущена");
        } else {
            this.main.cancel();
            LasersManager.laserList.remove(this);
        }
    }

    public boolean isStarted() {
        return this.main != null;
    }

    public abstract LaserType getLaserType();

    public abstract void moveStart(Location var1) throws ReflectiveOperationException;

    public abstract void moveEnd(Location var1) throws ReflectiveOperationException;

    public Location getStart() {
        return this.start;
    }

    public Location getEnd() {
        return this.end;
    }

    public void moveStart(Location location, int ticks, Runnable callback) {
        this.startMove = this.moveInternal(location, ticks, this.startMove, this::getStart, this::moveStart, callback);
    }

    public void moveEnd(Location location, int ticks, Runnable callback) {
        this.endMove = this.moveInternal(location, ticks, this.endMove, this::getEnd, this::moveEnd, callback);
    }

    private BukkitTask moveInternal(final Location location, final int ticks, BukkitTask oldTask, final Supplier<Location> locationSupplier, final ReflectiveConsumer<Location> moveConsumer, final Runnable callback) {
        if (ticks <= 0) {
            throw new IllegalArgumentException("Ticks должен иметь положительное значение.");
        } else if (this.plugin == null) {
            throw new IllegalStateException("Лазер должен быть запущен хотя бы один раз");
        } else {
            if (oldTask != null && !oldTask.isCancelled()) {
                oldTask.cancel();
            }

            return (new BukkitRunnable() {
                final double xPerTick = (location.getX() - ((Location)locationSupplier.get()).getX()) / (double)ticks;
                final double yPerTick = (location.getY() - ((Location)locationSupplier.get()).getY()) / (double)ticks;
                final double zPerTick = (location.getZ() - ((Location)locationSupplier.get()).getZ()) / (double)ticks;
                int elapsed = 0;

                public void run() {
                    try {
                        moveConsumer.accept(((Location)locationSupplier.get()).add(this.xPerTick, this.yPerTick, this.zPerTick));
                    } catch (ReflectiveOperationException var2) {
                        var2.printStackTrace();
                        this.cancel();
                        return;
                    }

                    if (++this.elapsed == ticks) {
                        this.cancel();
                        if (callback != null) {
                            callback.run();
                        }
                    }

                }
            }).runTaskTimer(this.plugin, 0L, 1L);
        }
    }

    protected void moveFakeEntity(Location location, int entityId, Object fakeEntity) throws ReflectiveOperationException {
        if (fakeEntity != null) {
            Packets.moveFakeEntity(fakeEntity, location);
        }

        if (this.main != null) {
            Object packet;
            if (fakeEntity == null) {
                packet = Packets.createPacketMoveEntity(location, entityId);
            } else {
                packet = Packets.createPacketMoveEntity(fakeEntity);
            }

            for (Player p : this.show) {
                Packets.sendPackets(p, packet);
            }

        }
    }

    protected abstract void sendStartPackets(Player var1, boolean var2) throws ReflectiveOperationException;

    protected abstract void sendDestroyPackets(Player var1) throws ReflectiveOperationException;

    protected boolean isCloseEnough(Player player) {
        if (this.distanceSquared == -1) {
            return true;
        } else {
            Location location = player.getLocation();
            return this.getStart().distanceSquared(location) <= (double)this.distanceSquared || this.getEnd().distanceSquared(location) <= (double)this.distanceSquared;
        }
    }

    @FunctionalInterface
    public interface ReflectiveConsumer<T> {
        void accept(T var1) throws ReflectiveOperationException;
    }

    private static class Packets {
        private static final AtomicInteger lastIssuedEID = new AtomicInteger(2000000000);
        private static Logger logger;
        private static int version;
        private static int versionMinor;
        private static final String npack;
        private static String cpack;
        private static ProtocolMappings mappings;
        private static final int crystalID;
        private static Object crystalType;
        private static Object squidType;
        private static Object guardianType;
        private static Constructor<?> crystalConstructor;
        private static Constructor<?> squidConstructor;
        private static Constructor<?> guardianConstructor;
        private static Object watcherObject1;
        private static Object watcherObject2;
        private static Object watcherObject3;
        private static Object watcherObject4;
        private static Object watcherObject5;
        private static Constructor<?> watcherConstructor;
        private static Method watcherSet;
        private static Method watcherRegister;
        private static Method watcherDirty;
        private static Constructor<?> blockPositionConstructor;
        private static Constructor<?> packetSpawnLiving;
        private static Constructor<?> packetSpawnNormal;
        private static Constructor<?> packetRemove;
        private static Constructor<?> packetTeleport;
        private static Constructor<?> packetMetadata;
        private static Class<?> packetTeam;
        private static Method createTeamPacket;
        private static Constructor<?> createTeam;
        private static Constructor<?> createScoreboard;
        private static Method setTeamPush;
        private static Object pushNever;
        private static Method getTeamPlayers;
        private static Method getHandle;
        private static Field playerConnection;
        private static Method sendPacket;
        private static Method setLocation;
        private static Method setUUID;
        private static Method setID;
        private static Object fakeSquid;
        private static Object fakeSquidWatcher;
        private static Object nmsWorld;
        public static boolean enabled;

        private Packets() {
        }

        static int generateEID() {
            return lastIssuedEID.getAndIncrement();
        }

        public static void sendPackets(Player p, Object... packets) throws ReflectiveOperationException {
            Object connection = playerConnection.get(getHandle.invoke(p));
            int var4 = packets.length;

            for (Object packet : packets) {
                if (packet != null) {
                    sendPacket.invoke(connection, packet);
                }
            }

        }

        public static Object createFakeDataWatcher() throws ReflectiveOperationException {
            Object watcher = watcherConstructor.newInstance(fakeSquid);
            if (version > 13) {
                setField(watcher, "registrationLocked", false);
            }

            return watcher;
        }

        public static void setDirtyWatcher(Object watcher) throws ReflectiveOperationException {
            if (version >= 15) {
                watcherDirty.invoke(watcher, watcherObject1);
            }

        }

        public static Object createSquid(Location location, UUID uuid, int id) throws ReflectiveOperationException {
            Object entity = squidConstructor.newInstance(squidType, nmsWorld);
            setEntityIDs(entity, uuid, id);
            moveFakeEntity(entity, location);
            return entity;
        }

        public static Object createGuardian(Location location, UUID uuid, int id) throws ReflectiveOperationException {
            Object entity = guardianConstructor.newInstance(guardianType, nmsWorld);
            setEntityIDs(entity, uuid, id);
            moveFakeEntity(entity, location);
            return entity;
        }

        public static Object createCrystal(Location location, UUID uuid, int id) throws ReflectiveOperationException {
            Object entity = crystalConstructor.newInstance(nmsWorld, location.getX(), location.getY(), location.getZ());
            setEntityIDs(entity, uuid, id);
            return entity;
        }

        public static Object createPacketEntitySpawnLiving(Location location, int typeID, UUID uuid, int id) throws ReflectiveOperationException {
            Object packet = packetSpawnLiving.newInstance();
            setField(packet, "a", id);
            setField(packet, "b", uuid);
            setField(packet, "c", typeID);
            setField(packet, "d", location.getX());
            setField(packet, "e", location.getY());
            setField(packet, "f", location.getZ());
            setField(packet, "j", (byte)((int)(location.getYaw() * 256.0F / 360.0F)));
            setField(packet, "k", (byte)((int)(location.getPitch() * 256.0F / 360.0F)));
            if (version <= 14) {
                setField(packet, "m", fakeSquidWatcher);
            }

            return packet;
        }

        public static Object createPacketEntitySpawnNormal(Location location, int typeID, Object type, int id) throws ReflectiveOperationException {
            Object packet = packetSpawnNormal.newInstance();
            setField(packet, "a", id);
            setField(packet, "b", UUID.randomUUID());
            setField(packet, "c", location.getX());
            setField(packet, "d", location.getY());
            setField(packet, "e", location.getZ());
            setField(packet, "i", (int)(location.getYaw() * 256.0F / 360.0F));
            setField(packet, "j", (int)(location.getPitch() * 256.0F / 360.0F));
            setField(packet, "k", version < 13 ? typeID : type);
            return packet;
        }

        public static Object createPacketEntitySpawnLiving(Object entity) throws ReflectiveOperationException {
            return packetSpawnLiving.newInstance(entity);
        }

        public static Object createPacketEntitySpawnNormal(Object entity) throws ReflectiveOperationException {
            return packetSpawnNormal.newInstance(entity);
        }

        public static void initGuardianWatcher(Object watcher, int targetId) throws ReflectiveOperationException {
            tryWatcherSet(watcher, watcherObject1, (byte)32);
            tryWatcherSet(watcher, watcherObject2, Boolean.FALSE);
            tryWatcherSet(watcher, watcherObject3, targetId);
        }

        public static void setCrystalWatcher(Object watcher, Location target) throws ReflectiveOperationException {
            Object blockPosition = blockPositionConstructor.newInstance(target.getX(), target.getY(), target.getZ());
            tryWatcherSet(watcher, watcherObject4, Optional.of(blockPosition));
            tryWatcherSet(watcher, watcherObject5, Boolean.FALSE);
        }

        public static Object[] createPacketsRemoveEntities(int... entitiesId) throws ReflectiveOperationException {
            Object[] packets;
            if (version == 17 && versionMinor == 0) {
                packets = new Object[entitiesId.length];

                for(int i = 0; i < entitiesId.length; ++i) {
                    packets[i] = packetRemove.newInstance(entitiesId[i]);
                }
            } else {
                packets = new Object[]{packetRemove.newInstance((Object) entitiesId)};
            }

            return packets;
        }

        public static Object createPacketMoveEntity(Location location, int entityId) throws ReflectiveOperationException {
            Object packet = packetTeleport.newInstance();
            setField(packet, "a", entityId);
            setField(packet, "b", location.getX());
            setField(packet, "c", location.getY());
            setField(packet, "d", location.getZ());
            setField(packet, "e", (byte)((int)(location.getYaw() * 256.0F / 360.0F)));
            setField(packet, "f", (byte)((int)(location.getPitch() * 256.0F / 360.0F)));
            setField(packet, "g", true);
            return packet;
        }

        public static void setEntityIDs(Object entity, UUID uuid, int id) throws ReflectiveOperationException {
            setUUID.invoke(entity, uuid);
            setID.invoke(entity, id);
        }

        public static void moveFakeEntity(Object entity, Location location) throws ReflectiveOperationException {
            setLocation.invoke(entity, location.getX(), location.getY(), location.getZ(), location.getPitch(), location.getYaw());
        }

        public static Object createPacketMoveEntity(Object entity) throws ReflectiveOperationException {
            return packetTeleport.newInstance(entity);
        }

        public static Object createPacketTeamCreate(String teamName, UUID... entities) throws ReflectiveOperationException {
            Object packet;
            int var6;
            if (version < 17) {
                packet = packetTeam.newInstance();
                setField(packet, "a", teamName);
                setField(packet, "i", 0);
                setField(packet, "f", "never");
                Collection players = (Collection)getField(packetTeam, "h", packet);
                int var5 = entities.length;

                for(var6 = 0; var6 < var5; ++var6) {
                    UUID entity = entities[var6];
                    players.add(entity.toString());
                }
            } else {
                Object team = createTeam.newInstance(createScoreboard.newInstance(), teamName);
                setTeamPush.invoke(team, pushNever);
                Collection players = (Collection)getTeamPlayers.invoke(team);
                UUID[] var11 = entities;
                var6 = entities.length;

                for(int var12 = 0; var12 < var6; ++var12) {
                    UUID entity = var11[var12];
                    players.add(entity.toString());
                }

                packet = createTeamPacket.invoke((Object)null, team, true);
            }

            return packet;
        }

        private static Object createPacketMetadata(int entityId, Object watcher) throws ReflectiveOperationException {
            return packetMetadata.newInstance(entityId, watcher, false);
        }

        private static void tryWatcherSet(Object watcher, Object watcherObject, Object watcherData) throws ReflectiveOperationException {
            try {
                watcherSet.invoke(watcher, watcherObject, watcherData);
            } catch (InvocationTargetException var4) {
                watcherRegister.invoke(watcher, watcherObject, watcherData);
                if (version >= 15) {
                    watcherDirty.invoke(watcher, watcherObject);
                }
            }

        }

        private static Method getMethod(Class<?> clazz, String name) throws NoSuchMethodException {
            Method[] var2 = clazz.getDeclaredMethods();
            int var3 = var2.length;

            for(int var4 = 0; var4 < var3; ++var4) {
                Method m = var2[var4];
                if (m.getName().equals(name)) {
                    return m;
                }
            }

            throw new NoSuchMethodException(name + " in " + clazz.getName());
        }

        private static void setField(Object instance, String name, Object value) throws ReflectiveOperationException {
            Field field = instance.getClass().getDeclaredField(name);
            field.setAccessible(true);
            field.set(instance, value);
        }

        private static Object getField(Class<?> clazz, String name, Object instance) throws ReflectiveOperationException {
            Field field = clazz.getDeclaredField(name);
            field.setAccessible(true);
            return field.get(instance);
        }

        private static Class<?> getNMSClass(String package17, String className) throws ClassNotFoundException {
            String var10000 = version < 17 ? npack : "net.minecraft." + package17;
            return Class.forName(var10000 + "." + className);
        }

        static {
            String[] var10000 = Bukkit.getServer().getClass().getPackage().getName().replace(".", ",").split(",");
            npack = "net.minecraft.server." + var10000[3];
            cpack = Bukkit.getServer().getClass().getPackage().getName() + ".";
            crystalID = 51;
            enabled = false;

            try {
                logger = new Logger("GuardianBeam", (String)null) {
                    public void log(LogRecord logRecord) {
                        logRecord.setMessage("[GuardianBeam] " + logRecord.getMessage());
                        super.log(logRecord);
                    }
                };
                logger.setParent(Bukkit.getServer().getLogger());
                logger.setLevel(Level.ALL);
                String[] versions = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3].substring(1).split("_");
                version = Integer.parseInt(versions[1]);
                if (version >= 17) {
                    versions = Bukkit.getBukkitVersion().split("-R")[0].split("\\.");
                    versionMinor = versions.length <= 2 ? 0 : Integer.parseInt(versions[2]);
                } else {
                    versionMinor = Integer.parseInt(versions[2].substring(1));
                }

                logger.info("Found server version 1." + version + "." + versionMinor);
                mappings = ProtocolMappings.getMappings(version);
                if (mappings == null) {
                    mappings = ProtocolMappings.values()[ProtocolMappings.values().length - 1];
                    logger.warning("Loaded not matching version of the mappings for your server version (1." + version + "." + versionMinor + ")");
                }

                logger.info("Loaded mappings " + mappings.name());
                Class<?> entityTypesClass = getNMSClass("world.entity", "EntityTypes");
                Class<?> entityClass = getNMSClass("world.entity", "Entity");
                Class<?> crystalClass = getNMSClass("world.entity.boss.enderdragon", "EntityEnderCrystal");
                Class<?> squidClass = getNMSClass("world.entity.animal", "EntitySquid");
                Class<?> guardianClass = getNMSClass("world.entity.monster", "EntityGuardian");
                watcherObject1 = getField(entityClass, mappings.getWatcherFlags(), (Object)null);
                watcherObject2 = getField(guardianClass, mappings.getWatcherSpikes(), (Object)null);
                watcherObject3 = getField(guardianClass, mappings.getWatcherTargetEntity(), (Object)null);
                watcherObject4 = getField(crystalClass, mappings.getWatcherTargetLocation(), (Object)null);
                watcherObject5 = getField(crystalClass, mappings.getWatcherBasePlate(), (Object)null);
                if (version >= 13) {
                    crystalType = entityTypesClass.getDeclaredField(mappings.getCrystalTypeName()).get((Object)null);
                    if (version >= 17) {
                        squidType = entityTypesClass.getDeclaredField(mappings.getSquidTypeName()).get((Object)null);
                        guardianType = entityTypesClass.getDeclaredField(mappings.getGuardianTypeName()).get((Object)null);
                    }
                }

                Class<?> dataWatcherClass = getNMSClass("network.syncher", "DataWatcher");
                watcherConstructor = dataWatcherClass.getDeclaredConstructor(entityClass);
                if (version >= 18) {
                    watcherSet = dataWatcherClass.getDeclaredMethod("b", watcherObject1.getClass(), Object.class);
                    watcherRegister = dataWatcherClass.getDeclaredMethod("a", watcherObject1.getClass(), Object.class);
                } else {
                    watcherSet = getMethod(dataWatcherClass, "set");
                    watcherRegister = getMethod(dataWatcherClass, "register");
                }

                if (version >= 15) {
                    watcherDirty = getMethod(dataWatcherClass, "markDirty");
                }

                packetSpawnNormal = getNMSClass("network.protocol.game", "PacketPlayOutSpawnEntity").getDeclaredConstructor(version < 17 ? new Class[0] : new Class[]{getNMSClass("world.entity", "Entity")});
                packetSpawnLiving = version >= 19 ? packetSpawnNormal : getNMSClass("network.protocol.game", "PacketPlayOutSpawnEntityLiving").getDeclaredConstructor(version < 17 ? new Class[0] : new Class[]{getNMSClass("world.entity", "EntityLiving")});
                packetRemove = getNMSClass("network.protocol.game", "PacketPlayOutEntityDestroy").getDeclaredConstructor(version == 17 && versionMinor == 0 ? Integer.TYPE : int[].class);
                packetMetadata = getNMSClass("network.protocol.game", "PacketPlayOutEntityMetadata").getDeclaredConstructor(Integer.TYPE, dataWatcherClass, Boolean.TYPE);
                packetTeleport = getNMSClass("network.protocol.game", "PacketPlayOutEntityTeleport").getDeclaredConstructor(version < 17 ? new Class[0] : new Class[]{entityClass});
                packetTeam = getNMSClass("network.protocol.game", "PacketPlayOutScoreboardTeam");
                blockPositionConstructor = getNMSClass("core", "BlockPosition").getConstructor(Double.TYPE, Double.TYPE, Double.TYPE);
                nmsWorld = Class.forName(cpack + "CraftWorld").getDeclaredMethod("getHandle").invoke(Bukkit.getWorlds().get(0));
                squidConstructor = squidClass.getDeclaredConstructors()[0];
                if (version >= 17) {
                    guardianConstructor = guardianClass.getDeclaredConstructors()[0];
                    crystalConstructor = crystalClass.getDeclaredConstructor(nmsWorld.getClass().getSuperclass(), Double.TYPE, Double.TYPE, Double.TYPE);
                }

                Object[] entityConstructorParams = version < 14 ? new Object[]{nmsWorld} : new Object[]{entityTypesClass.getDeclaredField(mappings.getSquidTypeName()).get((Object)null), nmsWorld};
                fakeSquid = squidConstructor.newInstance(entityConstructorParams);
                fakeSquidWatcher = createFakeDataWatcher();
                tryWatcherSet(fakeSquidWatcher, watcherObject1, (byte)32);
                String var13 = cpack;
                getHandle = Class.forName(var13 + "entity.CraftPlayer").getDeclaredMethod("getHandle");
                playerConnection = getNMSClass("server.level", "EntityPlayer").getDeclaredField(version < 17 ? "playerConnection" : "b");
                sendPacket = getNMSClass("server.network", "PlayerConnection").getMethod(version < 18 ? "sendPacket" : "a", getNMSClass("network.protocol", "Packet"));
                if (version >= 17) {
                    setLocation = entityClass.getDeclaredMethod(version < 18 ? "setLocation" : "a", Double.TYPE, Double.TYPE, Double.TYPE, Float.TYPE, Float.TYPE);
                    setUUID = entityClass.getDeclaredMethod("a_", UUID.class);
                    setID = entityClass.getDeclaredMethod("e", Integer.TYPE);
                    createTeamPacket = packetTeam.getMethod("a", getNMSClass("world.scores", "ScoreboardTeam"), Boolean.TYPE);
                    Class<?> scoreboardClass = getNMSClass("world.scores", "Scoreboard");
                    Class<?> teamClass = getNMSClass("world.scores", "ScoreboardTeam");
                    Class<?> pushClass = getNMSClass("world.scores", "ScoreboardTeamBase$EnumTeamPush");
                    createTeam = teamClass.getDeclaredConstructor(scoreboardClass, String.class);
                    createScoreboard = scoreboardClass.getDeclaredConstructor();
                    setTeamPush = teamClass.getDeclaredMethod(mappings.getTeamSetCollision(), pushClass);
                    pushNever = pushClass.getDeclaredField("b").get((Object)null);
                    getTeamPlayers = teamClass.getDeclaredMethod(mappings.getTeamGetPlayers());
                }

                enabled = true;
            } catch (Exception var11) {
                var11.printStackTrace();
                String errorMsg = "Laser Beam reflection failed to initialize. The util is disabled. Please ensure your version (" + Bukkit.getServer().getClass().getPackage().getName() + ") is supported.";
                if (logger == null) {
                    System.err.println(errorMsg);
                } else {
                    logger.severe(errorMsg);
                }
            }

        }

        private static enum ProtocolMappings {
            V1_9(9, "Z", "bA", "bB", "b", "c", 94, 68),
            V1_10(10, V1_9),
            V1_11(11, V1_10),
            V1_12(12, V1_11),
            V1_13(13, "ac", "bF", "bG", "b", "c", 70, 28),
            V1_14(14, "W", "b", "bD", "c", "d", 73, 30),
            V1_15(15, "T", "b", "bA", "c", "d", 74, 31),
            V1_16(16, (String)null, "b", "d", "c", "d", -1, 31) {
                public int getSquidID() {
                    return Packets.versionMinor < 2 ? 74 : 81;
                }

                public String getWatcherFlags() {
                    return Packets.versionMinor < 2 ? "T" : "S";
                }
            },
            V1_17(17, "Z", "b", "e", "c", "d", 86, 35, "K", "aJ", "u", "setCollisionRule", "getPlayerNameSet"),
            V1_18(18, (String)null, "b", "e", "c", "d", 86, 35, "K", "aJ", "u", "a", "g") {
                public String getWatcherFlags() {
                    return Packets.versionMinor < 2 ? "aa" : "Z";
                }
            },
            V1_19(19, "Z", "b", "e", "c", "d", 89, 38, "N", "aM", "w", "a", "g");

            private final int major;
            private final String watcherFlags;
            private final String watcherSpikes;
            private final String watcherTargetEntity;
            private final String watcherTargetLocation;
            private final String watcherBasePlate;
            private final int squidID;
            private final int guardianID;
            private final String guardianTypeName;
            private final String squidTypeName;
            private final String crystalTypeName;
            private final String teamSetCollision;
            private final String teamGetPlayers;

            private ProtocolMappings(int major, ProtocolMappings parent) {
                this(major, parent.watcherFlags, parent.watcherSpikes, parent.watcherTargetEntity, parent.watcherTargetLocation, parent.watcherBasePlate, parent.squidID, parent.guardianID, parent.guardianTypeName, parent.squidTypeName, parent.crystalTypeName, parent.teamSetCollision, parent.teamGetPlayers);
            }

            private ProtocolMappings(int major, String watcherFlags, String watcherSpikes, String watcherTargetEntity, String watcherTargetLocation, String watcherBasePlate, int squidID, int guardianID) {
                this(major, watcherFlags, watcherSpikes, watcherTargetEntity, watcherTargetLocation, watcherBasePlate, squidID, guardianID, (String)null, "SQUID", "END_CRYSTAL", (String)null, (String)null);
            }

            private ProtocolMappings(int major, String watcherFlags, String watcherSpikes, String watcherTargetEntity, String watcherTargetLocation, String watcherBasePlate, int squidID, int guardianID, String guardianTypeName, String squidTypeName, String crystalTypeName, String teamSetCollision, String teamGetPlayers) {
                this.major = major;
                this.watcherFlags = watcherFlags;
                this.watcherSpikes = watcherSpikes;
                this.watcherTargetEntity = watcherTargetEntity;
                this.watcherTargetLocation = watcherTargetLocation;
                this.watcherBasePlate = watcherBasePlate;
                this.squidID = squidID;
                this.guardianID = guardianID;
                this.guardianTypeName = guardianTypeName;
                this.squidTypeName = squidTypeName;
                this.crystalTypeName = crystalTypeName;
                this.teamSetCollision = teamSetCollision;
                this.teamGetPlayers = teamGetPlayers;
            }

            public int getMajor() {
                return this.major;
            }

            public String getWatcherFlags() {
                return this.watcherFlags;
            }

            public String getWatcherSpikes() {
                return this.watcherSpikes;
            }

            public String getWatcherTargetEntity() {
                return this.watcherTargetEntity;
            }

            public String getWatcherTargetLocation() {
                return this.watcherTargetLocation;
            }

            public String getWatcherBasePlate() {
                return this.watcherBasePlate;
            }

            public int getSquidID() {
                return this.squidID;
            }

            public int getGuardianID() {
                return this.guardianID;
            }

            public String getGuardianTypeName() {
                return this.guardianTypeName;
            }

            public String getSquidTypeName() {
                return this.squidTypeName;
            }

            public String getCrystalTypeName() {
                return this.crystalTypeName;
            }

            public String getTeamSetCollision() {
                return this.teamSetCollision;
            }

            public String getTeamGetPlayers() {
                return this.teamGetPlayers;
            }

            public static ProtocolMappings getMappings(int major) {
                ProtocolMappings[] var1 = values();
                int var2 = var1.length;

                for(int var3 = 0; var3 < var2; ++var3) {
                    ProtocolMappings map = var1[var3];
                    if (major == map.getMajor()) {
                        return map;
                    }
                }

                return null;
            }
        }
    }

    public static enum LaserType {
        GUARDIAN,
        ENDER_CRYSTAL;

        private LaserType() {
        }

        public Laser create(Location start, Location end, int duration, int distance) throws ReflectiveOperationException {
            switch (this) {
                case ENDER_CRYSTAL:
                    return new CrystalLaser(start, end, duration, distance);
                case GUARDIAN:
                    return new GuardianLaser(start, end, duration, distance);
                default:
                    throw new IllegalStateException();
            }
        }
    }

    public static class CrystalLaser extends Laser {
        private Object createCrystalPacket;
        private Object metadataPacketCrystal;
        private Object[] destroyPackets;
        private Object fakeCrystalDataWatcher = Packets.createFakeDataWatcher();
        private final Object crystal;
        private final int crystalID = Packets.generateEID();

        public CrystalLaser(Location start, Location end, int duration, int distance) throws ReflectiveOperationException {
            super(start, end, duration, distance);
            Packets.setCrystalWatcher(this.fakeCrystalDataWatcher, end);
            if (Packets.version < 17) {
                this.crystal = null;
            } else {
                this.crystal = Packets.createCrystal(start, UUID.randomUUID(), this.crystalID);
            }

            this.metadataPacketCrystal = Packets.createPacketMetadata(this.crystalID, this.fakeCrystalDataWatcher);
            this.destroyPackets = Packets.createPacketsRemoveEntities(this.crystalID);
        }

        private Object getCrystalSpawnPacket() throws ReflectiveOperationException {
            if (this.createCrystalPacket == null) {
                if (Packets.version < 17) {
                    this.createCrystalPacket = Packets.createPacketEntitySpawnNormal(this.start, Packets.crystalID, Packets.crystalType, this.crystalID);
                } else {
                    this.createCrystalPacket = Packets.createPacketEntitySpawnNormal(this.crystal);
                }
            }

            return this.createCrystalPacket;
        }

        public LaserType getLaserType() {
            return LaserType.ENDER_CRYSTAL;
        }

        protected void sendStartPackets(Player p, boolean hasSeen) throws ReflectiveOperationException {
            Packets.sendPackets(p, this.getCrystalSpawnPacket());
            Packets.sendPackets(p, this.metadataPacketCrystal);
        }

        protected void sendDestroyPackets(Player p) throws ReflectiveOperationException {
            Packets.sendPackets(p, this.destroyPackets);
        }

        public void moveStart(Location location) throws ReflectiveOperationException {
            this.start = location;
            this.createCrystalPacket = null;
            this.moveFakeEntity(this.start, this.crystalID, this.crystal);
        }

        public void moveEnd(Location location) throws ReflectiveOperationException {
            this.end = location;
            if (this.main != null) {
                Packets.setCrystalWatcher(this.fakeCrystalDataWatcher, location);
                this.metadataPacketCrystal = Packets.createPacketMetadata(this.crystalID, this.fakeCrystalDataWatcher);
                Iterator var2 = this.show.iterator();

                while(var2.hasNext()) {
                    Player p = (Player)var2.next();
                    Packets.sendPackets(p, this.metadataPacketCrystal);
                }
            }

        }
    }

    public static class GuardianLaser extends Laser {
        private static AtomicInteger teamID = new AtomicInteger(ThreadLocalRandom.current().nextInt(0, Integer.MAX_VALUE));
        private Object createGuardianPacket;
        private Object createSquidPacket;
        private Object teamCreatePacket;
        private Object[] destroyPackets;
        private Object metadataPacketGuardian;
        private Object metadataPacketSquid;
        private Object fakeGuardianDataWatcher;
        private final UUID squidUUID = UUID.randomUUID();
        private final UUID guardianUUID = UUID.randomUUID();
        private final int squidID = Packets.generateEID();
        private final int guardianID = Packets.generateEID();
        private Object squid;
        private Object guardian;
        private int targetID;
        private UUID targetUUID;
        protected LivingEntity endEntity;
        private Location correctStart;
        private Location correctEnd;

        public GuardianLaser(Location start, Location end, int duration, int distance) throws ReflectiveOperationException {
            super(start, end, duration, distance);
            this.initSquid();
            this.targetID = this.squidID;
            this.targetUUID = this.squidUUID;
            this.initLaser();
        }

        public GuardianLaser(Location start, LivingEntity endEntity, int duration, int distance) throws ReflectiveOperationException {
            super(start, endEntity.getLocation(), duration, distance);
            this.targetID = endEntity.getEntityId();
            this.targetUUID = endEntity.getUniqueId();
            this.initLaser();
        }

        private void initLaser() throws ReflectiveOperationException {
            this.fakeGuardianDataWatcher = Packets.createFakeDataWatcher();
            Packets.initGuardianWatcher(this.fakeGuardianDataWatcher, this.targetID);
            if (Packets.version >= 17) {
                this.guardian = Packets.createGuardian(this.getCorrectStart(), this.guardianUUID, this.guardianID);
            }

            this.metadataPacketGuardian = Packets.createPacketMetadata(this.guardianID, this.fakeGuardianDataWatcher);
            this.teamCreatePacket = Packets.createPacketTeamCreate("noclip" + teamID.getAndIncrement(), this.squidUUID, this.guardianUUID);
            this.destroyPackets = Packets.createPacketsRemoveEntities(this.squidID, this.guardianID);
        }

        private void initSquid() throws ReflectiveOperationException {
            if (Packets.version >= 17) {
                this.squid = Packets.createSquid(this.getCorrectEnd(), this.squidUUID, this.squidID);
            }

            this.metadataPacketSquid = Packets.createPacketMetadata(this.squidID, Packets.fakeSquidWatcher);
            Packets.setDirtyWatcher(Packets.fakeSquidWatcher);
        }

        private Object getGuardianSpawnPacket() throws ReflectiveOperationException {
            if (this.createGuardianPacket == null) {
                if (Packets.version < 17) {
                    this.createGuardianPacket = Packets.createPacketEntitySpawnLiving(this.getCorrectStart(), Packets.mappings.getGuardianID(), this.guardianUUID, this.guardianID);
                } else {
                    this.createGuardianPacket = Packets.createPacketEntitySpawnLiving(this.guardian);
                }
            }

            return this.createGuardianPacket;
        }

        private Object getSquidSpawnPacket() throws ReflectiveOperationException {
            if (this.createSquidPacket == null) {
                if (Packets.version < 17) {
                    this.createSquidPacket = Packets.createPacketEntitySpawnLiving(this.getCorrectEnd(), Packets.mappings.getSquidID(), this.squidUUID, this.squidID);
                } else {
                    this.createSquidPacket = Packets.createPacketEntitySpawnLiving(this.squid);
                }
            }

            return this.createSquidPacket;
        }

        public LaserType getLaserType() {
            return LaserType.GUARDIAN;
        }

        public void attachEndEntity(LivingEntity entity) throws ReflectiveOperationException {
            if (entity.getWorld() != this.start.getWorld()) {
                throw new IllegalArgumentException("Прикрепленная сущность не находится в том же мире, что и лазер.");
            } else {
                this.endEntity = entity;
                this.setTargetEntity(entity.getUniqueId(), entity.getEntityId());
            }
        }

        public Entity getEndEntity() {
            return this.endEntity;
        }

        private void setTargetEntity(UUID uuid, int id) throws ReflectiveOperationException {
            this.targetUUID = uuid;
            this.targetID = id;
            this.fakeGuardianDataWatcher = Packets.createFakeDataWatcher();
            Packets.initGuardianWatcher(this.fakeGuardianDataWatcher, this.targetID);
            this.metadataPacketGuardian = Packets.createPacketMetadata(this.guardianID, this.fakeGuardianDataWatcher);
            Iterator var3 = this.show.iterator();

            while(var3.hasNext()) {
                Player p = (Player)var3.next();
                Packets.sendPackets(p, this.metadataPacketGuardian);
            }

        }

        public Location getEnd() {
            return this.endEntity == null ? this.end : this.endEntity.getLocation();
        }

        protected Location getCorrectStart() {
            if (this.correctStart == null) {
                this.correctStart = this.start.clone();
                this.correctStart.subtract(0.0, 0.5, 0.0);
            }

            return this.correctStart;
        }

        protected Location getCorrectEnd() {
            if (this.correctEnd == null) {
                this.correctEnd = this.end.clone();
                this.correctEnd.subtract(0.0, 0.5, 0.0);
                Vector corrective = this.correctEnd.toVector().subtract(this.getCorrectStart().toVector()).normalize();
                this.correctEnd.subtract(corrective);
            }

            return this.correctEnd;
        }

        protected boolean isCloseEnough(Player player) {
            return player == this.endEntity || super.isCloseEnough(player);
        }

        protected void sendStartPackets(Player p, boolean hasSeen) throws ReflectiveOperationException {
            if (this.squid == null) {
                Packets.sendPackets(p, this.getGuardianSpawnPacket(), this.metadataPacketGuardian);
            } else {
                Packets.sendPackets(p, this.getGuardianSpawnPacket(), this.getSquidSpawnPacket(), this.metadataPacketGuardian, this.metadataPacketSquid);
            }

            if (!hasSeen) {
                Packets.sendPackets(p, this.teamCreatePacket);
            }

        }

        protected void sendDestroyPackets(Player p) throws ReflectiveOperationException {
            Packets.sendPackets(p, this.destroyPackets);
        }

        public void moveStart(Location location) throws ReflectiveOperationException {
            this.start = location;
            this.correctStart = null;
            this.createGuardianPacket = null;
            this.moveFakeEntity(this.getCorrectStart(), this.guardianID, this.guardian);
            if (this.squid != null) {
                this.correctEnd = null;
                this.createSquidPacket = null;
                this.moveFakeEntity(this.getCorrectEnd(), this.squidID, this.squid);
            }

        }

        public void moveEnd(Location location) throws ReflectiveOperationException {
            this.end = location;
            this.createSquidPacket = null;
            this.correctEnd = null;
            if (this.squid == null) {
                this.initSquid();
                Iterator var2 = this.show.iterator();

                while(var2.hasNext()) {
                    Player p = (Player)var2.next();
                    Packets.sendPackets(p, this.getSquidSpawnPacket(), this.metadataPacketSquid);
                }
            } else {
                this.moveFakeEntity(this.getCorrectEnd(), this.squidID, this.squid);
            }

            if (this.targetUUID != this.squidUUID) {
                this.endEntity = null;
                this.setTargetEntity(this.squidUUID, this.squidID);
            }

        }

        public void callColorChange() throws ReflectiveOperationException {
            Iterator var1 = this.show.iterator();

            while(var1.hasNext()) {
                Player p = (Player)var1.next();
                Packets.sendPackets(p, this.metadataPacketGuardian);
            }

        }
    }
}