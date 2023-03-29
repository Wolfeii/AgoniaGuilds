package com.wolfeiii.agoniaguilds;

import com.wolfeiii.agoniaguilds.configuration.GuildsConfiguration;
import com.wolfeiii.agoniaguilds.guild.GuildManager;
import com.wolfeiii.agoniaguilds.user.UserManager;
import org.bukkit.plugin.java.JavaPlugin;

public final class AgoniaGuilds extends JavaPlugin {

    private static AgoniaGuilds pluginInstance;

    private final UserManager userManager = new UserManager();
    private final GuildManager guildManager = new GuildManager();
    private final GuildsConfiguration configuration = new GuildsConfiguration();

    @Override
    public void onLoad() {
        pluginInstance = this;
    }

    @Override
    public void onEnable() {
        // Plugin startup logic

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public static AgoniaGuilds getAgoniaGuilds() {
        return pluginInstance;
    }

    public UserManager getUserManager() {
        return userManager;
    }

    public GuildManager getGuildManager() {
        return guildManager;
    }

    public GuildsConfiguration getConfiguration() {
        return configuration;
    }
}
