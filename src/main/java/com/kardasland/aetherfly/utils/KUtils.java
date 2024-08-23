package com.kardasland.aetherfly.utils;

import com.kardasland.aetherfly.AetherFly;
import com.kardasland.aetherfly.beans.FlyPlayer;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.profile.PlayerProfile;

import java.net.MalformedURLException;
import java.net.URI;
import java.util.*;

public class KUtils {

    private static JavaPlugin instance;

    public KUtils(JavaPlugin plugin){
        instance = plugin;
    }

    public static class Messages {
        public static String color(String s){
            return ChatColor.translateAlternateColorCodes('&', s);
        }
        public static List<String> color(List<String> s){
            List<String> temp = new ArrayList<>();
            for (String line : s){
                temp.add(ChatColor.translateAlternateColorCodes('&', line));
            }
            return temp;
        }
        public static List<String> color(String... s){
            return color(List.of(s));
        }
        public static void sendMessage(Player player, String message, boolean hasPrefix){
            String prefix = ConfigManager.get("messages.yml").getString("prefix");
            if (player != null){
                player.sendMessage(color((hasPrefix ? prefix : "") + message));
            }else {
                AetherFly.instance.getLogger().info(color((hasPrefix ? prefix : "") + message));
            }
        }
        public static boolean permissionCheck(Player player, String permission){
            if (player == null) {
                return true;
            }
            if (player.hasPermission(permission)){
                return true;
            }else {
                sendMessage(player, ConfigManager.get("messages.yml").getString("no_permission").replace("%permission%", permission), true);
                return false;
            }
        }
        public static void sendConfiguredMessage(Player player, String path, boolean hasPrefix){
            sendMessage(player, ConfigManager.get("messages.yml").getString(path), hasPrefix);
        }
        public static void sendConfiguredMessage(Player player, String path, boolean hasPrefix, Map<String, String> placeholders){
            String message = Objects.requireNonNull(ConfigManager.get("messages.yml")).getString(path);
            for (Map.Entry<String, String> entry : placeholders.entrySet()){
                assert message != null;
                message = message.replace(entry.getKey(), entry.getValue());
            }
            sendMessage(player, message, hasPrefix);
        }
        public static void sendConfiguredMessage(FlyPlayer flyPlayer, String path, boolean hasPrefix){
            // check if path is list or string
            if (ConfigManager.get("messages.yml").isList(path)) {
                List<String> list = ConfigManager.get("messages.yml").getStringList(path);
                for (String s : list) {
                    sendMessage(Bukkit.getPlayer(UUID.fromString(flyPlayer.getPlayerUUID())), Placeholders.replacePlaceholder(flyPlayer, s), hasPrefix);
                }
            }else {
                sendMessage(Bukkit.getPlayer(UUID.fromString(flyPlayer.getPlayerUUID())), Placeholders.replacePlaceholder(flyPlayer, ConfigManager.get("messages.yml").getString(path)), hasPrefix);
            }
        }
        public static void sendConfiguredMessage(Player player, FlyPlayer target, String path, boolean hasPrefix){
            // check if path is list or string
            if (ConfigManager.get("messages.yml").isList(path)) {
                List<String> list = ConfigManager.get("messages.yml").getStringList(path);
                for (String s : list) {
                    sendMessage(player, Placeholders.replacePlaceholder(target, s), hasPrefix);
                }
            }else {
                sendMessage(player, Placeholders.replacePlaceholder(target, ConfigManager.get("messages.yml").getString(path)), hasPrefix);
            }
        }
    }
    public static class Placeholders{

            public static String replacePlaceholder(FlyPlayer data, String text){
                if (data == null) return Messages.color(text);
                text = text.replace("%player%", Objects.requireNonNull(Bukkit.getOfflinePlayer(UUID.fromString(data.getPlayerUUID())).getName()))
                        .replace("%fly_type%", data.getFlyType().toString()
                );
                //Bukkit.broadcastMessage(data.toString());
                switch (data.getFlyType()) {
                    case SUBSCRIPTION -> {
                        text = text
                                .replace("%usage_time_formatted%", AetherFly.instance.getLocaleWrapper().translateLocaleTime(data.getFlyUsageTime(), false))
                                        .replace("%max_usage_time_formatted%", AetherFly.instance.getLocaleWrapper().translateLocaleTime(data.getMaxUsageTime(), false))
                                        .replace("%expire_date%", new Date(data.getSubscriptionExpire()).toString())
                                        .replace("%remaining_time_formatted%", AetherFly.instance.getLocaleWrapper().translateLocaleTime((data.getSubscriptionExpire() - System.currentTimeMillis()) / 1000, false)
                                );
                        return Messages.color(text);
                    }
                    case TIME_LIMITED -> {
                        text = text
                                .replace("%expire_date%", new Date(data.getTimeLimitExpire()).toString())
                                .replace("%remaining_time_formatted%", AetherFly.instance.getLocaleWrapper().translateLocaleTime((data.getTimeLimitExpire() - System.currentTimeMillis()) / 1000, false));
                        return Messages.color(text);
                    }
                    case USAGE_LIMITED -> {
                        text = text
                                .replace("%usage_time_formatted%", AetherFly.instance.getLocaleWrapper().translateLocaleTime(data.getFlyUsageTime(), false))
                                .replace("%max_usage_time_formatted%", AetherFly.instance.getLocaleWrapper().translateLocaleTime(data.getMaxUsageTime(), false));
                        return Messages.color(text);
                    }
                    default -> {
                        return Messages.color(text);
                    }
                }
            }

            public static List<String> replacePlaceholders(FlyPlayer flyPlayer, List<String> list){
                if (flyPlayer == null) return list;
                List<String> temp = new ArrayList<>();
                for (String s : list){
                    temp.add(Messages.color(replacePlaceholder(flyPlayer, s)));
                }
                return temp;
            }
        }
    @Getter
    public static class ItemStacks{
        public ItemStack createPlayerHead(String url) throws MalformedURLException {
            ItemStack itemStack = new ItemStack(Material.PLAYER_HEAD);
            if(itemStack.getItemMeta() instanceof SkullMeta skullMeta){
                PlayerProfile playerProfile = Bukkit.createPlayerProfile(UUID.randomUUID());
                playerProfile.getTextures().setSkin(URI.create(url).toURL());
                skullMeta.setOwnerProfile(playerProfile);
                itemStack.setItemMeta(skullMeta);
            }
            return itemStack;
        }
        public ItemStack createItemStack(String displayName, Material material, List<String> lore){
            ItemStack itemStack = new ItemStack(material);
            ItemMeta itemMeta = itemStack.getItemMeta();
            assert itemMeta != null;
            itemMeta.setDisplayName(Messages.color(displayName));
            itemMeta.setLore(Messages.color(lore));
            itemStack.setItemMeta(itemMeta);
            return itemStack;
        }
        public ItemStack createItemStack(String displayName, Material material, String... lore){
            ItemStack itemStack = new ItemStack(material);
            ItemMeta itemMeta = itemStack.getItemMeta();
            assert itemMeta != null;
            itemMeta.setDisplayName(Messages.color(displayName));
            List<String> lore2 = List.of(lore);
            itemMeta.setLore(Messages.color(lore2));
            itemStack.setItemMeta(itemMeta);
            return itemStack;
        }
        public ItemStack modifyItemStack(ItemStack itemStack, String displayName, List<String> lore){
            ItemStack returning = itemStack.clone();
            ItemMeta itemMeta = returning.getItemMeta();
            assert itemMeta != null;
            itemMeta.setDisplayName(Messages.color(displayName));
            itemMeta.setLore(Messages.color(lore));
            returning.setItemMeta(itemMeta);
            return returning;
        }
    }
    public static class Numbers {
        public List<Integer> primeFactors(int n)
        {
            List<Integer> numbers = new ArrayList<>();
            int c = 2;
            while (n > 1) {
                if (n % c == 0) {
                    numbers.add(c);
                    n /= c;
                }
                else
                    c++;
            }
            return numbers;
        }
    }

    public static class Econ {
        public static String format(long balance){
            if (balance < 1000) {
                if (balance == 0) return "0";
                return String.valueOf(balance);
            }else {
                int divide = 0;
                double divide2 = 0.0;
                String symbol = null;
                if (balance > 1000000000){
                    symbol = "B";
                    divide = 100000;
                    divide2 = 1000.0;
                } else if (balance > 1000000){
                    symbol = "M";
                    divide = 10000;
                    divide2 = 100.0;
                }else if (balance > 1000){
                    symbol = "K";
                    divide = 100;
                    divide2 = 10.0;
                }
                double equation = Math.round((float) balance / divide) / divide2;
                String format = String.valueOf(equation).replace(".0", "");
                return format + symbol;
            }
        }
    }

    public static class CooldownHandler {

        private static Map<String, CooldownHandler> cooldowns = new HashMap<String, CooldownHandler>();
        private long start;
        private final int timeInSeconds;
        private final UUID id;
        private final String cooldownName;

        public CooldownHandler(UUID id, String cooldownName, int timeInSeconds){
            this.id = id;
            this.cooldownName = cooldownName;
            this.timeInSeconds = timeInSeconds;
        }

        public static boolean isInCooldown(UUID id, String cooldownName){
            if(getTimeLeft(id, cooldownName)>=1){
                return true;
            } else {
                stop(id, cooldownName);
                return false;
            }
        }

        private static void stop(UUID id, String cooldownName){
            CooldownHandler.cooldowns.remove(id+cooldownName);
        }

        private static CooldownHandler getCooldown(UUID id, String cooldownName){
            return cooldowns.get(id.toString()+cooldownName);
        }

        public static int getTimeLeft(UUID id, String cooldownName){
            CooldownHandler cooldown = getCooldown(id, cooldownName);
            int f = -1;
            if(cooldown!=null){
                long now = System.currentTimeMillis();
                long cooldownTime = cooldown.start;
                int r = (int) (now - cooldownTime) / 1000;
                f = (r - cooldown.timeInSeconds) * (-1);
            }
            return f;
        }

        public void start(){
            this.start = System.currentTimeMillis();
            cooldowns.put(this.id.toString()+this.cooldownName, this);
        }

    }
    public static class Time {
        @Deprecated
        public static String calculateTime(long seconds, boolean compact) {
            return "sa";
            //return AetherFly.instance.getLocaleWrapper().translateLocaleTime(seconds, compact);
        }
        /**
         * Schedules a task to run at a certain hour every day.
         * @param task The task to run
         * @param hour [0-23] The hour of the day to run the task
         * @return Task id number (-1 if scheduling failed)
         */
        public static int scheduleRepeatAtTime(Runnable task, int hour)
        {
            //Calendar is a class that represents a certain time and date.
            Calendar cal = Calendar.getInstance(); //obtains a calendar instance that represents the current time and date

            //time is often represented in milliseconds since the epoch,
            //as a long, which represents how many milliseconds a time is after
            //January 1st, 1970, 00:00.

            //this gets the current time
            long now = cal.getTimeInMillis();
            //you could also say "long now = System.currentTimeMillis()"

            //since we have saved the current time, we need to figure out
            //how many milliseconds are between that and the next
            //time it is 7:00pm, or whatever was passed into hour
            //we do this by setting this calendar instance to the next 7:00pm (or whatever)
            //then we can compare the times

            //if it is already after 7:00pm,
            //we will schedule it for tomorrow,
            //since we can't schedule it for the past.
            //we are not time travelers.
            if(cal.get(Calendar.HOUR_OF_DAY) >= hour)
                cal.add(Calendar.DATE, 1); //do it tomorrow if now is after "hours"

            //we need to set this calendar instance to 7:00pm, or whatever.
            cal.set(Calendar.HOUR_OF_DAY, hour);
            cal.set(Calendar.MINUTE, 0);
            cal.set(Calendar.SECOND, 0);
            cal.set(Calendar.MILLISECOND, 0);

            //cal is now properly set to the next time it will be 7:00pm

            long offset = cal.getTimeInMillis() - now;
            long ticks = offset / 50L; //there are 50 milliseconds in a tick

            //we now know how many ticks are between now and the next time it is 7:00pm
            //we schedule an event to go off the next time it is 7:00pm,
            //and repeat every 24 hours.
            return Bukkit.getScheduler().scheduleSyncRepeatingTask(instance, task, ticks, 1728000L);
            //24 hrs/day * 60 mins/hr * 60 secs/min * 20 ticks/sec = 1728000 ticks
        }
    }
    public static class Probability {
        public static class Chance {
            private final int upperLimit;
            private final int lowerLimit;
            private final Object element;
            public Chance(Object element, int lowerLimit, int upperLimit) {
                this.element = element;
                this.upperLimit = upperLimit;
                this.lowerLimit = lowerLimit;
            }
            public int getUpperLimit() {
                return this.upperLimit;
            }
            public int getLowerLimit() {
                return this.lowerLimit;
            }
            public Object getElement() {
                return this.element;
            }
            public String toString() {
                return "[" + this.lowerLimit + "|" + this.upperLimit + "]: " + this.element.toString();
            }
        }
        private final List<Chance> chances;
        private int sum;
        private final Random random;
        public Probability() {
            this.random = new Random();
            this.chances = new ArrayList<>();
            this.sum = 0;
        }
        public Probability(long seed) {
            this.random = new Random(seed);
            this.chances = new ArrayList<>();
            this.sum = 0;
        }
        public void addChance(Object element, int chance) {
            if (!this.chances.contains(element)) {
                this.chances.add(new Chance(element, this.sum, this.sum + chance));
                this.sum = this.sum + chance;
            } else {
                // not sure yet, what to do, when the element already exists, since a list can't contain 2 equal entries. Right now a second, identical chance (element + chance must be equal) will be ignored
            }
        }
        public Object getRandomElement() {
            int index = this.random.nextInt(this.sum);
            // debug: System.out.println("Amount of chances: " + Integer.toString(this.chances.size()) + ", possible options: " + Integer.toString(this.sum) + ", chosen option: " + Integer.toString(index));
            for (Chance chance : this.chances) {
                // debug: System.out.println(chance.toString());
                if (chance.getLowerLimit() <= index && chance.getUpperLimit() > index) {
                    return chance.getElement();
                }
            }
            return null; // this should never happen, because the random can't be out of the limits
        }
        public int getOptions() { // might be needed sometimes
            return this.sum;
        }
        public int getChoices() { // might be needed sometimes
            return this.chances.size();
        }
    }
}
