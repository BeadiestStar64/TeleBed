package com.github.BeadiestStar64.Minecraft.Plugins.teleBed;

import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import org.bukkit.plugin.java.JavaPlugin;

public final class TeleBed extends JavaPlugin {

    private static BukkitAudiences audiences;
    private static TeleBed plugin;

    public static TeleBed getInstance() {
        return plugin;
    }

    public static BukkitAudiences getAudiences() {
        return audiences;
    }

    @Override
    public void onEnable() {
        plugin = this;

        new TeleFeather().registerRecipe(this);

        audiences = BukkitAudiences.create(this);

        getServer().getPluginManager().registerEvents(new TeleFeather(), this);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
