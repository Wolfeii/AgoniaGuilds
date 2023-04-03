package com.wolfeiii.agoniaguilds;

import com.wolfeiii.agoniaguilds.command.CommandsManager;
import com.wolfeiii.agoniaguilds.command.CommandsMap;
import com.wolfeiii.agoniaguilds.configuration.CommentedConfiguration;
import com.wolfeiii.agoniaguilds.configuration.GuildsConfiguration;
import com.wolfeiii.agoniaguilds.configuration.bossbar.BossBarsService;
import com.wolfeiii.agoniaguilds.factory.FactoryManager;
import com.wolfeiii.agoniaguilds.guild.Guild;
import com.wolfeiii.agoniaguilds.guild.GuildManager;
import com.wolfeiii.agoniaguilds.guild.roles.RolesManager;
import com.wolfeiii.agoniaguilds.listener.BukkitListeners;
import com.wolfeiii.agoniaguilds.menu.MenuManager;
import com.wolfeiii.agoniaguilds.service.ServicesHandler;
import com.wolfeiii.agoniaguilds.service.message.MessagesServiceImpl;
import com.wolfeiii.agoniaguilds.storage.DataManager;
import com.wolfeiii.agoniaguilds.user.GuildUser;
import com.wolfeiii.agoniaguilds.user.UserManager;
import com.wolfeiii.agoniaguilds.utilities.logging.Log;
import com.wolfeiii.agoniaguilds.utilities.objects.BukkitExecutor;
import com.wolfeiii.agoniaguilds.utilities.sorting.SortingType;
import com.wolfeiii.agoniaguilds.utilities.sorting.SortingTypes;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public final class AgoniaGuilds extends JavaPlugin {

    private static AgoniaGuilds pluginInstance;

    private final CommandsManager commandsManager = new CommandsManager(this);
    private final ServicesHandler servicesHandler = new ServicesHandler(this);
    private final BukkitListeners bukkitListeners = new BukkitListeners(this);
    private final GuildManager guildManager = new GuildManager(this);
    private final RolesManager rolesManager = new RolesManager(this);
    private final MenuManager menuManager = new MenuManager(this);
    private final UserManager userManager = new UserManager(this);
    private final DataManager dataManager = new DataManager(this);
    private final FactoryManager factory = new FactoryManager();
    private GuildsConfiguration configuration;

    private boolean shouldEnable = true;

    @Override
    public void onLoad() {
        pluginInstance = this;

        bukkitListeners.registerListenerFailureFilter();

        SortingTypes.registerSortingTypes();

        initializeConfiguration();

        this.servicesHandler.registerBossBarsService(new BossBarsService());
        this.servicesHandler.registerMessagesService(new MessagesServiceImpl());
    }

    @Override
    public void onEnable() {
        try {
            if (!shouldEnable) {
                Bukkit.shutdown();
                return;
            }

            BukkitExecutor.init(this);

            commandsManager.initializeCommands();
            guildManager.initializeData();

            dataManager.loadData();
            SortingType.values().forEach(guildManager::sortGuilds);

            try {
                bukkitListeners.register();
            } catch (RuntimeException exception) {
                shouldEnable = false;
                Log.error(exception, "Ett fel uppstod vid registrering av listeners:");
                Bukkit.shutdown();
                return;
            }

            BukkitExecutor.sync(() -> {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    GuildUser guildUser = userManager.getGuildUser(player);
                    guildUser.updateLastTimeStatus();
                    Guild playerGuild = guildUser.getGuild();

                    if (playerGuild != null)
                        playerGuild.setCurrentlyActive();
                }
            }, 1L);


        } catch (Throwable error) {
            shouldEnable = false;
            Log.error(error, "Ett oförväntat fel uppstod när pluginet startades:");
            Bukkit.shutdown();
        }
    }

    @Override
    public void onDisable() {
        if (!shouldEnable)
            return;

        BukkitExecutor.syncDatabaseCalls();

        try {
            guildManager.disablePlugin();

            // Shutdown task is running from another thread, causing closing of inventories to cause errors.
            // This check should prevent it.
            if (Bukkit.isPrimaryThread()) {
                Bukkit.getOnlinePlayers().forEach(player -> {
                    GuildUser guildUser = userManager.getGuildUser(player);
                    player.closeInventory();
                });
            }
        } catch (Exception error) {
            Log.error(error, "Ett fel uppstod vid nedstängning av servern.");
        } finally {
            Log.info("Stänger ner Bukkit Executor...");
            BukkitExecutor.close();

            Log.info("Stänger databasen. Detta kanske 'hänger' servern. Stäng inte av den, annars förloras kanske data.");
            dataManager.closeConnection();
        }
    }

    public static AgoniaGuilds getAgoniaGuilds() {
        return pluginInstance;
    }

    public void initializeConfiguration() {
        File file = new File(getDataFolder(), "config.yml");

        if (!file.exists())
            saveResource("config.yml", false);

        CommentedConfiguration cfg = CommentedConfiguration.loadConfiguration(file);

        this.configuration = new GuildsConfiguration(this, cfg);
    }

    public MenuManager getMenuManager() {
        return menuManager;
    }

    public CommandsManager getCommandsManager() {
        return commandsManager;
    }

    public ServicesHandler getServicesHandler() {
        return servicesHandler;
    }

    public RolesManager getRolesManager() {
        return rolesManager;
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

    public FactoryManager getFactory() {
        return factory;
    }
}
