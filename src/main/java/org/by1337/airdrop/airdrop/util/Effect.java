package org.by1337.airdrop.airdrop.util;

import org.bukkit.*;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.inventory.meta.FireworkMeta;

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
        try{
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
        }catch (Exception e){
            Message.Error("Ошибка при попытке заспавнить мобов!");
            Message.Error(e.getLocalizedMessage());
        }

    }

}
