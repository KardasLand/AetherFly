package com.kardasland.aetherfly.commands;

import com.kardasland.aetherfly.AetherFly;
import com.kardasland.aetherfly.beans.FlyPlayer;
import com.kardasland.aetherfly.beans.FlyType;
import com.kardasland.aetherfly.utils.ConfigManager;
import com.kardasland.aetherfly.utils.KUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Date;
import java.util.List;
import java.util.Locale;

public class TempFlyCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (commandSender instanceof Player){
            Player player = (Player) commandSender;
            if (KUtils.Messages.permissionCheck(player, "aetherfly.tempfly")) {
                if (strings.length == 1) {
                    FlyPlayer flyPlayer = AetherFly.instance.getFlyCache().getPlayer(player);
                    if (flyPlayer == null) {
                        // send you dont have tempfly
                        KUtils.Messages.sendConfiguredMessage(player, "tempfly.dont_have_tempfly", true);
                        return true;
                    }
                    //Bukkit.broadcastMessage(flyPlayer.toString());
                    if (strings[0].equalsIgnoreCase("on")) {
                        // if player world is in blacklisted
                        if (ConfigManager.get("config.yml").getStringList("blacklisted-worlds").contains(player.getWorld().getName())) {
                            KUtils.Messages.sendConfiguredMessage(player, "tempfly.blocked_world", true);
                            return true;
                        }
                        // enable tempfly
                        if (flyPlayer.isFlying()) {
                            // send you are already flying
                            KUtils.Messages.sendConfiguredMessage(player, "tempfly.already_flying", true);
                            return true;
                        }
                        flyPlayer.enableFlight();
                        KUtils.Messages.sendConfiguredMessage(player, "tempfly.enabled", true);
                    } else if (strings[0].equalsIgnoreCase("off")) {
                        flyPlayer.disableFlight();
                        KUtils.Messages.sendConfiguredMessage(player, "tempfly.disabled", true);
                        // disable tempfly
                    }// make info
                    else if (strings[0].equalsIgnoreCase("info")) {
                        KUtils.Messages.sendConfiguredMessage(flyPlayer, "tempfly.info.title", false);
                        KUtils.Messages.sendConfiguredMessage(flyPlayer, "tempfly.info.types." + flyPlayer.getFlyType().toString().toLowerCase(Locale.ROOT), false);
                        /* KUtils.Messages.sendMessage(player, "&aTempfly info", true);
                        KUtils.Messages.sendMessage(player, "&aPlayer: " + player.getName(), true);
                        KUtils.Messages.sendMessage(player, "&aFly type: " + flyPlayer.getFlyType().name(), true);
                        if (flyPlayer.getFlyType() == FlyType.TIME_LIMITED){
                            Date date = new Date(flyPlayer.getTimeLimitExpire());
                            KUtils.Messages.sendMessage(player, "&aTime limit expire: " + date, true);
                            // give remaining time based on current time
                            long remainingTime = flyPlayer.getTimeLimitExpire() - System.currentTimeMillis();
                            KUtils.Messages.sendMessage(player, "&aRemaining time: " + AetherFly.instance.getLocaleWrapper().translateLocaleTime(remainingTime / 1000, false), true);
                        }else if (flyPlayer.getFlyType() == FlyType.USAGE_LIMITED){
                            KUtils.Messages.sendMessage(player, "&aFly usage time: " + AetherFly.instance.getLocaleWrapper().translateLocaleTime(flyPlayer.getFlyUsageTime(), false),true);
                            KUtils.Messages.sendMessage(player, "&aMax usage time: " + AetherFly.instance.getLocaleWrapper().translateLocaleTime(flyPlayer.getMaxUsageTime(), false), true);
                        }else if (flyPlayer.getFlyType() == FlyType.SUBSCRIPTION){
                            KUtils.Messages.sendMessage(player, "&aFly usage time: " + AetherFly.instance.getLocaleWrapper().translateLocaleTime(flyPlayer.getFlyUsageTime(), false),true);
                            KUtils.Messages.sendMessage(player, "&aMax usage time: " + AetherFly.instance.getLocaleWrapper().translateLocaleTime(flyPlayer.getMaxUsageTime(), false), true);
                            Date date = new Date(flyPlayer.getSubscriptionExpire());
                            KUtils.Messages.sendMessage(player, "&aTime limit expire: " + date, true);
                            long remainingTime = flyPlayer.getSubscriptionExpire() - System.currentTimeMillis();
                            KUtils.Messages.sendMessage(player, "&aRemaining time: " + AetherFly.instance.getLocaleWrapper().translateLocaleTime(remainingTime / 1000, false), true);
                        } */
                    } else {
                        KUtils.Messages.sendConfiguredMessage(player, "tempfly.usage", true);
                    }
                } else {
                    KUtils.Messages.sendConfiguredMessage(player, "tempfly.usage", true);
                }
            }
        }
        return true;
    }
}
