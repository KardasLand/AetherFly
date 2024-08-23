package com.kardasland.aetherfly.runnable;

import com.kardasland.aetherfly.AetherFly;
import org.bukkit.scheduler.BukkitRunnable;

public class AutoSaveRunnable extends BukkitRunnable {
    int interval;
    public AutoSaveRunnable(int interval) {
        runTaskTimer(AetherFly.instance, interval * 20L, interval * 20L);
    }
    @Override
    public void run() {
        AetherFly.instance.getFlyCache().saveAll();
    }
}
