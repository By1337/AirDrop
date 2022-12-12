package org.by1337.airdrop.airdrop.util;

import org.bukkit.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.util.Vector;
import org.by1337.airdrop.airdrop.AirDrop;

import java.util.concurrent.ThreadLocalRandom;

import static org.by1337.airdrop.airdrop.util.CfgManager.Config.*;
import static org.by1337.airdrop.airdrop.AirDrop.instance;

public class Effect {

    private static int taskID;
    private static double y;

    public static void StrikeLightning(Location loc) {
        loc.getWorld().strikeLightning(loc);
    }

    public static void FakeStrikeLightning(Location loc) {
        loc.getWorld().strikeLightningEffect(loc);
    }

    public static void Explosion(Location loc, double power) {
        loc.getWorld().createExplosion(loc.getX(), loc.getY() + 1, loc.getZ(), (float) power, true, true);
    }

    public static void createFireWork(Location loc) {
        y = 0;
        Location location = new Location(loc.getWorld(), loc.getX(), loc.getY(), loc.getZ());
        location.add(0.5D, 0, 0.5D);
        taskID = Bukkit.getScheduler().runTaskTimer(instance, () -> {
            y += 1;
            Firework firework = (Firework) location.getWorld().spawnEntity(location.add(0.0D, y, 0.0D), EntityType.FIREWORK);
            FireworkMeta fireworkMeta = firework.getFireworkMeta();
            fireworkMeta.addEffect(FireworkEffect.builder()
                    .withColor(Color.AQUA, Color.RED, Color.ORANGE, Color.BLACK, Color.GREEN, Color.BLACK, Color.LIME).flicker(true)
                    .with(FireworkEffect.Type.BALL_LARGE)
                    .build());
            firework.setFireworkMeta(fireworkMeta);
            fireworkMeta.setPower(20);
            firework.detonate();
            if (y >= 10)
                Bukkit.getScheduler().cancelTask(taskID);

        }, 5L, 5L).getTaskId();
    }

    public static void SpawnGuards(Location loc) {
        try {
            String name = Message.MessageBuilder(getDefendersName());
            EntityType gur = EntityType.valueOf(getDefendersType());
            World world = loc.getWorld();
            int x = loc.getBlockX();
            int z = loc.getBlockZ();
            assert world != null;
            world.strikeLightning(loc);
            Location south = new Location(world, (x + ThreadLocalRandom.current().nextInt(-3, 3)), (world.getHighestBlockYAt(x + ThreadLocalRandom.current().nextInt(-3, 3), z) + ThreadLocalRandom.current().nextInt(-3, 3)), z);
            Chunk southChunk = south.getChunk();
            world.loadChunk(southChunk);
            world.spawnEntity(south, gur).setCustomName(name);
        } catch (Exception e) {
            Message.Error("Ошибка при попытке заспавнить мобов!");
            Message.Error(e.getLocalizedMessage());
        }

    }

    public static void createRandomParticle(Location loc, double radius, String[] part, int count, int viewDistance) throws Exception {
        Particle particle = Particle.valueOf(part[0]);
        Particle particle2 = Particle.valueOf(part[1]);
        for (double i = 0; i <= count; i += 1) {
            double x = ThreadLocalRandom.current().nextDouble(-radius, radius),
                    y = ThreadLocalRandom.current().nextDouble(-1, radius),
                    z = ThreadLocalRandom.current().nextDouble(-radius, radius);
            for (Entity entity : loc.getWorld().getNearbyEntities(loc, viewDistance, viewDistance, viewDistance)) {
                if (entity instanceof Player) {
                    Player p = (Player) entity;
                    p.spawnParticle(particle2, new Location(loc.getWorld(), loc.getX() + x, loc.getY() + y, loc.getZ() + z), 0);
                    p.spawnParticle(particle, new Location(loc.getWorld(), loc.getX() - x, loc.getY() + y, loc.getZ() - z), 0);

                }
            }


        }
    }

    public static void createDoubleHelix(Location loc, double radius, Color color1, Color color2, int viewDistance, int height, Vector vector, double helixPitch) {
        for (double y = vector.getY(); y <= height; y += helixPitch) {
            double x = radius * Math.cos(y);
            double z = radius * Math.sin(y);
            radius = -radius;
            for (Entity entity : loc.getWorld().getNearbyEntities(loc, viewDistance, viewDistance, viewDistance)) {
                if (entity instanceof Player) {
                    Player p = (Player) entity;
                    if (radius < 0)
                        p.spawnParticle(Particle.REDSTONE, new Location(loc.getWorld(), loc.getX() + x + vector.getX(), loc.getY() + y, loc.getZ() + z + vector.getZ()), 1, new org.bukkit.Particle.DustOptions(color1, 5.0F));
                    else
                        p.spawnParticle(Particle.REDSTONE, new Location(loc.getWorld(), loc.getX() + x + vector.getX(), loc.getY() + y, loc.getZ() + z + vector.getZ()), 1, new org.bukkit.Particle.DustOptions(color2, 5.0F));
                }
            }

        }
    }

    public static void createHelix(Location loc, double radius, Color color, int viewDistance, int height, Vector vector, double helixPitch) {
        for (double y = vector.getY(); y <= height; y += helixPitch) {
            double x = radius * Math.cos(y);
            double z = radius * Math.sin(y);
            for (Entity entity : loc.getWorld().getNearbyEntities(loc, viewDistance, viewDistance, viewDistance)) {
                if (entity instanceof Player) {
                    Player p = (Player) entity;
                    p.spawnParticle(Particle.REDSTONE, new Location(loc.getWorld(), loc.getX() + x + vector.getX(), loc.getY() + y, loc.getZ() + z + vector.getZ()), 1, new org.bukkit.Particle.DustOptions(color, 5.0F));
                }
            }

        }
    }

    private void ParticleSender(Player p, Location location, org.bukkit.Particle.DustOptions d, int ticks) {
        for (int i = 0; i < ticks; ++i) {
            Bukkit.getScheduler().runTaskLater(AirDrop.instance, () -> {
                if (p.isOnline())
                    p.spawnParticle(Particle.FIREWORKS_SPARK, location, 1000, d);
            }, ((long) i * 20));
        }
    }


    public void handleParticle(Location l, int ticks, Color color) {
        assert l.getWorld() != null;
        for (Entity entity : l.getWorld().getNearbyEntities(l, 30, 30, 30)) {
            if (entity instanceof Player) {
                Player p = (Player) entity;
                if (p.getLocation().distance(l) > 150 || Math.abs(l.getY() - p.getLocation().getY()) > 30.0D)
                    return;
                ParticleSender(p, l, new org.bukkit.Particle.DustOptions(color, 10.0F), ticks);
            }
        }

    }

}
