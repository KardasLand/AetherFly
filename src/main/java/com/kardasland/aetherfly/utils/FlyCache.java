package com.kardasland.aetherfly.utils;

import com.kardasland.aetherfly.AetherFly;
import com.kardasland.aetherfly.beans.FlyPlayer;
import com.kardasland.aetherfly.utils.ConfigManager;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class FlyCache {
    List<FlyPlayer> flyPlayers;

    public FlyCache() {
        this.flyPlayers = new ArrayList<>();
    }

    public void addPlayer(Player player) {
        FlyPlayer flyPlayer = new FlyPlayer(player);
        this.flyPlayers.add(flyPlayer);
    }

    public void addPlayer(FlyPlayer player) {
        this.flyPlayers.add(player);
    }


    public void saveAll() {
        // remove all players from config
        FileConfiguration config = ConfigManager.get("players.yml");
        for (String key : config.getKeys(false)) {
            config.set(key, null);
        }
        for (FlyPlayer flyPlayer : this.flyPlayers) {
            flyPlayer.save();
        }
    }

    public void removePlayer(Player player) {
        // check the array
        for (FlyPlayer flyPlayer : this.flyPlayers) {
            if (flyPlayer.getPlayerUUID().equals(player.getUniqueId().toString())) {
                this.flyPlayers.remove(flyPlayer);
                break;
            }
        }
    }
    public void removePlayer(String playerUUID) {
        // check the array
        for (FlyPlayer flyPlayer : this.flyPlayers) {
            if (flyPlayer.getPlayerUUID().equals(playerUUID)) {
                this.flyPlayers.remove(flyPlayer);
                break;
            }
        }
    }

    public FlyPlayer getPlayer(Player player) {
        for (FlyPlayer flyPlayer : this.flyPlayers) {
            if (flyPlayer.getPlayerUUID().equals(player.getUniqueId().toString())) {
                //Bukkit.broadcastMessage("Found player: " + flyPlayer.getPlayerUUID());
                return flyPlayer;
            }
        }
        return null;
    }

    public void checkPlayers() {
        // copy the array
        List<FlyPlayer> copy = new ArrayList<>(this.flyPlayers);
        for (FlyPlayer flyPlayer : copy) {
            //Bukkit.broadcastMessage("Checking player: " + flyPlayer.getPlayerUUID());
            flyPlayer.check();
        }
    }

    public void loadAll() {
        // load all players from config
        FileConfiguration config = ConfigManager.get("players.yml");
        AetherFly.instance.getLogger().info("Loading players...");
        for (String key : config.getKeys(false)) {
            //AetherFly.instance.getLogger().info("Loading player: " + Bukkit.getOfflinePlayer(UUID.fromString(key)).getName());
            FlyPlayer flyPlayer = new FlyPlayer(key);
            //AetherFly.instance.getLogger().info("Loaded player: " + flyPlayer);
            this.flyPlayers.add(flyPlayer);
        }
    }
}
