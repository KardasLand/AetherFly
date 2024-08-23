package com.kardasland.aetherfly;

import com.kardasland.aetherfly.commands.AetherFlyCommand;
import com.kardasland.aetherfly.commands.TempFlyCommand;
import com.kardasland.aetherfly.events.NaturalProtectionEvents;
import com.kardasland.aetherfly.runnable.AutoSaveRunnable;
import com.kardasland.aetherfly.runnable.ControlRunnable;
import com.kardasland.aetherfly.utils.ConfigManager;
import com.kardasland.aetherfly.utils.FlyCache;
import com.kardasland.aetherfly.wrappers.LocaleWrapper;
import com.kardasland.aetherfly.wrappers.locale.LocaleEN;
import com.kardasland.aetherfly.wrappers.locale.LocaleTR;
import com.kardasland.aetherfly.wrappers.protection.CombatLogXHandler;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;

@Getter
public final class AetherFly extends JavaPlugin {

    public static AetherFly instance;
    FlyCache flyCache;
    LocaleWrapper localeWrapper;
    AutoSaveRunnable autoSaveRunnable;
    List<String> blacklistedWorlds;

    @Override
    public void onEnable() {
        instance = this;
        ConfigManager.load("config.yml");
        ConfigManager.load("players.yml");
        ConfigManager.load("messages.yml");
        initLocale();
        if (Bukkit.getPluginManager().isPluginEnabled("CombatLogX")){
            Bukkit.getPluginManager().registerEvents(new CombatLogXHandler(), this);
        }
        Bukkit.getPluginManager().registerEvents(new NaturalProtectionEvents(), this);
        this.flyCache = new FlyCache();
        this.flyCache.loadAll();
        getCommand("aetherfly").setExecutor(new AetherFlyCommand());
        getCommand("tempfly").setExecutor(new TempFlyCommand());
        autoSaveModule();
        blacklistModule();
        new ControlRunnable().runTaskTimer(this, 20, 20);
    }

    private void blacklistModule() {
        FileConfiguration cfg = ConfigManager.get("config.yml");
        assert cfg != null;
        if (cfg.getBoolean("blacklisted-worlds.enabled")){
            this.blacklistedWorlds = cfg.getStringList("blacklisted-worlds.worlds");
        }else {
            this.blacklistedWorlds = new ArrayList<>();
        }
    }

    private void autoSaveModule() {
        if (ConfigManager.get("config.yml").getBoolean("autosave.enabled")){
            int interval = ConfigManager.get("config.yml").getInt("autosave.interval");
            this.autoSaveRunnable = new AutoSaveRunnable(interval);
        }
    }

    private void initLocale() {
        FileConfiguration cfg = ConfigManager.get("config.yml");
        if (cfg.isSet("locale") && cfg.getString("locale").equalsIgnoreCase("tr")) {
            this.localeWrapper = new LocaleTR();
        } else {
            this.localeWrapper = new LocaleEN();
        }
    }

    @Override
    public void onDisable() {
        this.flyCache.saveAll();
    }
}
