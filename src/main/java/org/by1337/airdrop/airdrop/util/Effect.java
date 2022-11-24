package org.by1337.airdrop.airdrop.util;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Firework;
import org.bukkit.inventory.meta.FireworkMeta;

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

}
