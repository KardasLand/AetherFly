package com.kardasland.aetherfly.wrappers.protection;

import com.github.sirblobman.combatlogx.api.ICombatLogX;
import com.github.sirblobman.combatlogx.api.event.PlayerTagEvent;
import com.github.sirblobman.combatlogx.api.manager.ICombatManager;
import com.kardasland.aetherfly.AetherFly;
import com.kardasland.aetherfly.beans.FlyPlayer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;

public class CombatLogXHandler implements Listener {

    public ICombatLogX getHandler() {
        PluginManager pluginManager = Bukkit.getPluginManager();
        Plugin plugin = pluginManager.getPlugin("CombatLogX");
        return (ICombatLogX) plugin;
    }

    public boolean isInCombat(Player player) {
        ICombatLogX plugin = getHandler();
        ICombatManager combatManager = plugin.getCombatManager();
        return combatManager.isInCombat(player);
    }

    @EventHandler
    public void onCombatLogX(PlayerTagEvent event) {
        //Bukkit.broadcastMessage("CombatLogX event triggered for " + event.getPlayer().getName());
        if (AetherFly.instance.getFlyCache().getPlayer(event.getPlayer()) != null) {
            FlyPlayer flyPlayer = AetherFly.instance.getFlyCache().getPlayer(event.getPlayer());
            flyPlayer.disableFlight();
        }

    }
}
