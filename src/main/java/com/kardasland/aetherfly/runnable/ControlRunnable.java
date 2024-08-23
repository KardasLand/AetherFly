package com.kardasland.aetherfly.runnable;

import com.kardasland.aetherfly.AetherFly;
import org.bukkit.scheduler.BukkitRunnable;

public class ControlRunnable extends BukkitRunnable {


    public ControlRunnable() {

    }
    @Override
    public void run() {
        AetherFly.instance.getFlyCache().checkPlayers();
    }
}
