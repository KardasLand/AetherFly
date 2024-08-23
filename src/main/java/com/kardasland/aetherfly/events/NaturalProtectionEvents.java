package com.kardasland.aetherfly.events;

import com.kardasland.aetherfly.AetherFly;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
public class NaturalProtectionEvents implements Listener {
    @EventHandler
    public void worldchangeEvent(PlayerChangedWorldEvent event) {
        if (AetherFly.instance.getFlyCache().getPlayer(event.getPlayer()) != null
                && AetherFly.instance.getBlacklistedWorlds().contains(event.getPlayer().getWorld().getName())) {
            AetherFly.instance.getFlyCache().getPlayer(event.getPlayer()).disableFlight();
        }
    }

    @EventHandler
    public void damageEvent(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player) {
            Player player = (Player) event.getDamager();
            if (event.getEntity() instanceof Mob) {
                if (AetherFly.instance.getFlyCache().getPlayer(player) != null) {
                    AetherFly.instance.getFlyCache().getPlayer(player).disableFlight();
                }
            }
        }else if (event.getDamager() instanceof Mob) {
            if (event.getEntity() instanceof Player player) {
                if (AetherFly.instance.getFlyCache().getPlayer(player) != null) {
                    AetherFly.instance.getFlyCache().getPlayer(player).disableFlight();
                }
            }
        }
    }
}
