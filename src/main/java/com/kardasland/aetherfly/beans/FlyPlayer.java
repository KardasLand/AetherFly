package com.kardasland.aetherfly.beans;

import com.kardasland.aetherfly.AetherFly;
import com.kardasland.aetherfly.utils.ConfigManager;
import com.kardasland.aetherfly.utils.KUtils;
import lombok.Data;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.UUID;

@Data
public class FlyPlayer {
    String playerUUID;
    boolean isFlying;

    FlyType flyType = FlyType.UNKNOWN;


    // variables if subscription
    long subscriptionExpire;
    long expireTime;
    long flyUsageTime;
    long maxUsageTime;

    // variables if time limited
    long timeLimitExpire;
    long currentTime;


    private void init(String playerUUID){
        this.playerUUID = playerUUID;
        FileConfiguration config = ConfigManager.get("players.yml");
        if (config.contains(playerUUID)){
            // check enum
            this.flyType = FlyType.valueOf(config.getString(playerUUID + ".flyType"));
            if (flyType == FlyType.TIME_LIMITED){
                this.timeLimitExpire = config.getLong(playerUUID + ".timeLimitExpire");
            } else if (flyType == FlyType.USAGE_LIMITED){
                this.flyUsageTime = config.getLong(playerUUID + ".flyUsageTime");
                this.maxUsageTime = config.getLong(playerUUID + ".maxUsageTime");
            }else if (flyType == FlyType.SUBSCRIPTION){
                this.flyUsageTime = config.getLong(playerUUID + ".flyUsageTime");
                this.maxUsageTime = config.getLong(playerUUID + ".maxUsageTime");
                this.subscriptionExpire = config.getLong(playerUUID + ".subscriptionExpireTime");
            }
        }
    }

    public void destruct(){
        disableFlight();
        this.flyType = FlyType.DISABLED;
        Player player = Bukkit.getPlayer(UUID.fromString(playerUUID));
        if (player != null){
            player.setAllowFlight(false);
            player.setFlying(false);
            player.setFallDistance(0);
            //KUtils.Messages.sendMessage(player, "&cYour temporary flight has been disabled.", true);
            KUtils.Messages.sendConfiguredMessage(player, "tempfly.disabled", true);
        }
        AetherFly.instance.getFlyCache().removePlayer(playerUUID);
    }

    public void enableFlight(){
        this.isFlying = true;
        this.currentTime = System.currentTimeMillis();
        Player player = Bukkit.getPlayer(UUID.fromString(playerUUID));
        assert player != null;
        player.setAllowFlight(true);
    }

    public void disableFlight(){
        this.isFlying = false;
        Player player = Bukkit.getPlayer(UUID.fromString(playerUUID));
        assert player != null;
        player.setAllowFlight(false);
        player.setFlying(false);
        player.setFallDistance(0);
    }

    public void createSubscription(long expireTime, long maxUsageTime){
        this.flyType = FlyType.SUBSCRIPTION;
        this.subscriptionExpire = System.currentTimeMillis() + (expireTime * 1000);
        this.maxUsageTime = maxUsageTime;
    }

    public void createTimeLimited(long expireTime){
        this.flyType = FlyType.TIME_LIMITED;
        this.timeLimitExpire = System.currentTimeMillis() + (expireTime * 1000);
    }

    public void createUsageLimited(long maxUsageTime){
        this.flyType = FlyType.USAGE_LIMITED;
        this.maxUsageTime = maxUsageTime;
    }

    public FlyPlayer(Player player){
        init(player.getUniqueId().toString());
    }
    public FlyPlayer(String playerUUID){
        this.playerUUID = playerUUID;
        init(playerUUID);
    }

    public void check(){
        adjustValues();
        checkTime();
        checkUsage();
        // check disabled
        if (flyType == FlyType.DISABLED){
            destruct();
            /*
            Player player = Bukkit.getPlayer(UUID.fromString(playerUUID));
            if (player != null){
                player.setAllowFlight(false);
                player.setFlying(false);
                player.setFallDistance(0);
                KUtils.Messages.sendMessage(player, "§cYour temporary flight has been disabled.", true);
            }
            AetherFly.instance.getFlyCache().removePlayer(playerUUID);*/
        }
    }

    private void adjustValues() {
        if (flyType == FlyType.SUBSCRIPTION || flyType == FlyType.USAGE_LIMITED){
            // add time
            Player player = Bukkit.getPlayer(UUID.fromString(playerUUID));
            if (player != null && player.isFlying()){
                this.flyUsageTime++;
            }
        }
    }

    public void checkTime(){
        if (flyType == FlyType.TIME_LIMITED){
            if (System.currentTimeMillis() > timeLimitExpire){
                flyType = FlyType.DISABLED;
            }
        }
    }

    public void checkUsage(){
        if (flyType == FlyType.USAGE_LIMITED || flyType == FlyType.SUBSCRIPTION){
            if (flyUsageTime >= maxUsageTime){
                flyType = FlyType.DISABLED;
            }
        }
        if (flyType == FlyType.SUBSCRIPTION){
            if (System.currentTimeMillis() > subscriptionExpire){
                flyType = FlyType.DISABLED;
            }
        }
    }

    public void save(){
        FileConfiguration config = ConfigManager.get("players.yml");
        assert config != null;
        config.set(playerUUID + ".flyType", flyType.name());
        if (flyType == FlyType.TIME_LIMITED){
            config.set(playerUUID + ".timeLimitExpire", timeLimitExpire);
        } else if (flyType == FlyType.USAGE_LIMITED){
            config.set(playerUUID + ".flyUsageTime", flyUsageTime);
            config.set(playerUUID + ".maxUsageTime", maxUsageTime);
        }else if (flyType == FlyType.SUBSCRIPTION){
            config.set(playerUUID + ".flyUsageTime", flyUsageTime);
            config.set(playerUUID + ".maxUsageTime", maxUsageTime);
            config.set(playerUUID + ".subscriptionExpireTime", subscriptionExpire);
        }
        ConfigManager.save("players.yml");
    }

    /**
     *
     * @param time time to add in milliseconds
     */
    public void addExpireTime(long time){
        if (flyType == FlyType.TIME_LIMITED){
            this.timeLimitExpire += time;
        }else if (flyType == FlyType.SUBSCRIPTION){
            this.subscriptionExpire += time;
        }
    }

    /**
     *
     * @param time time to add in milliseconds
     */
    public void addUsageTime(long time){
        if (flyType == FlyType.USAGE_LIMITED || flyType == FlyType.SUBSCRIPTION){
            this.flyUsageTime += time;
        }
    }

    /**
     *
     * @param time time to add in milliseconds
     */
    public void addMaxUsageTime(long time){
        if (flyType == FlyType.USAGE_LIMITED || flyType == FlyType.SUBSCRIPTION){
            this.maxUsageTime += time;
        }
    }

}

