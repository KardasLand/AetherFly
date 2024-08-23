package com.kardasland.aetherfly.commands;

import com.kardasland.aetherfly.AetherFly;
import com.kardasland.aetherfly.beans.FlyPlayer;
import com.kardasland.aetherfly.utils.ConfigManager;
import com.kardasland.aetherfly.utils.KUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class AetherFlyCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        // admin command, give players tempfly
        Player player = commandSender instanceof Player ? (Player) commandSender : null;
        if (KUtils.Messages.permissionCheck(player, "aetherfly.admin")){
            //aetherfly give <player> <flytype> <time>
            // /aetherfly give kardasland time_limited 3600
            // /aetherfly give kardasland subscription 3600 3600
            // /aetherfly give kardasland usage_limited 3600

            if (strings.length >= 4 && strings[0].equalsIgnoreCase("give")){
                giveCommand(player, strings);
            }else if (strings.length == 2){
                // /aetherfly info <player>
                if (strings[0].equalsIgnoreCase("info")){
                    infoCommand(player, strings);
                }
                // remove
                else if (strings[0].equalsIgnoreCase("remove")){
                    removeCommand(player, strings);
                }
            }else if (strings.length == 1){
                if (strings[0].equalsIgnoreCase("reload")){
                    if (!KUtils.Messages.permissionCheck(player, "aetherfly.reload")){
                        return true;
                    }
                    ConfigManager.reload("config.yml");
                    ConfigManager.reload("messages.yml");
                    KUtils.Messages.sendConfiguredMessage(player, "aetherfly.config_reloaded", true);
                }
            }else {
                KUtils.Messages.sendMessage(player, "&cUsage: /aetherfly give <player> <time_limited/usage_limited> <time>", true);
                KUtils.Messages.sendMessage(player, "&cSub Usage: /aetherfly give <player> subscription <time> <usage>", true);
                KUtils.Messages.sendMessage(player, "&cUsage: /aetherfly info <player>", true);
                KUtils.Messages.sendMessage(player, "&cUsage: /aetherfly remove <player>", true);
                KUtils.Messages.sendMessage(player, "&cUsage: /aetherfly reload", true);
            }
        }
        return true;
    }

    public void removeCommand(Player player, String[] strings) {
        // /aetherfly remove <player>
        if (!KUtils.Messages.permissionCheck(player, "aetherfly.remove")){
            return;
        }
        Player target = Bukkit.getPlayer(strings[1]);
        if (target == null){
            KUtils.Messages.sendConfiguredMessage(player, "aetherfly.player_not_found", true);
            return;
        }
        FlyPlayer flyPlayer = AetherFly.instance.getFlyCache().getPlayer(target);
        if (flyPlayer == null){
            KUtils.Messages.sendConfiguredMessage(player, "aetherfly.player_not_found", true);
            return;
        }
        flyPlayer.destruct();
        KUtils.Messages.sendConfiguredMessage(player, "aetherfly.fly_removed", true);
    }

    public void infoCommand(Player player, String[] strings) {
        // /aetherfly info <player>
        if (!KUtils.Messages.permissionCheck(player, "aetherfly.info")){
            return;
        }
        Player target = Bukkit.getPlayer(strings[1]);
        if (target == null){
            KUtils.Messages.sendConfiguredMessage(player, "aetherfly.player_not_found", true);
            return;
        }
        FlyPlayer flyPlayer = AetherFly.instance.getFlyCache().getPlayer(target);
        if (flyPlayer == null){
            KUtils.Messages.sendConfiguredMessage(player, "aetherfly.player_not_found", true);
            return;
        }
        KUtils.Messages.sendConfiguredMessage(player, flyPlayer, "tempfly.info.title", false);
        KUtils.Messages.sendConfiguredMessage(player, flyPlayer,"tempfly.info.types." + flyPlayer.getFlyType().toString().toLowerCase(), false);
    }

    public void giveCommand(Player player, String[] strings) {
        // /aetherfly give <player> <flytype> <time>
        // /aetherfly give kardasland time_limited 3600
        // /aetherfly give kardasland subscription 3600 3600
        // /aetherfly give kardasland usage_limited 3600
        if (!KUtils.Messages.permissionCheck(player, "aetherfly.give")){
            return;
        }
        Player target = Bukkit.getPlayer(strings[1]);
        if (target == null) {
            KUtils.Messages.sendConfiguredMessage(player, "aetherfly.player_not_found", true);
            return;
        }
        FlyPlayer flyPlayer = new FlyPlayer(target);
        if (AetherFly.instance.getFlyCache().getPlayer(target) != null) {
            KUtils.Messages.sendMessage(player, "&7Player already has a temp fly. Remove it first.", true);
            return;
        }
        switch (strings[2].toLowerCase()) {
            case "time_limited" -> flyPlayer.createTimeLimited(Long.parseLong(strings[3]));
            case "subscription" -> flyPlayer.createSubscription(Long.parseLong(strings[3]), Long.parseLong(strings[4]));
            case "usage_limited" -> flyPlayer.createUsageLimited(Long.parseLong(strings[3]));
            default -> {
                KUtils.Messages.sendMessage(player, "&cUsage: /aetherfly give <player> <time_limited/usage_limited> <time>", true);
                KUtils.Messages.sendMessage(player, "&cSub Usage: /aetherfly give <player> subscription <time> <usage>", true);
                return;
            }
        }
        AetherFly.instance.getFlyCache().addPlayer(flyPlayer);
        KUtils.Messages.sendConfiguredMessage(player, flyPlayer, "aetherfly.fly_given", true);
    }

}
