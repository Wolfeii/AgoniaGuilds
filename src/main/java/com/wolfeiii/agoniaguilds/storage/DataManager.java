package com.wolfeiii.agoniaguilds.storage;

import com.wolfeiii.agoniaguilds.AgoniaGuilds;
import com.wolfeiii.agoniaguilds.storage.loader.DatabaseLoader;
import com.wolfeiii.agoniaguilds.storage.loader.sql.SQLDatabaseLoader;
import com.wolfeiii.agoniaguilds.user.GuildUser;
import com.wolfeiii.agoniaguilds.utilities.logging.Log;
import com.wolfeiii.agoniaguilds.utilities.objects.BukkitExecutor;
import org.bukkit.Bukkit;

import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

public class DataManager {

    private final AgoniaGuilds plugin;

    private static final UUID CONSOLE_UUID = new UUID(0, 0);
    private final List<DatabaseLoader> databaseLoaders = new LinkedList<>();

    public DataManager(AgoniaGuilds plugin) {
        this.plugin = plugin;
    }

    public void loadData() throws RuntimeException {
        loadDatabaseLoaders();

        runState(DatabaseLoader.State.INITIALIZE);

        runState(DatabaseLoader.State.POST_INITIALIZE);

        runState(DatabaseLoader.State.PRE_LOAD_DATA);

        loadPlayers();

        runState(DatabaseLoader.State.POST_LOAD_DATA);
    }

    public void addDatabaseLoader(DatabaseLoader databaseLoader) {
        this.databaseLoaders.add(databaseLoader);
    }

    public void closeConnection() {
        for (DatabaseLoader databaseLoader : databaseLoaders) {
            try {
                databaseLoader.setState(DatabaseLoader.State.SHUTDOWN);
            } catch (Throwable ignored) {
            }
        }
    }

    private void loadDatabaseLoaders() {
        addDatabaseLoader(new CopyOldDatabase());
        addDatabaseLoader(new SQLDatabaseLoader(plugin));
    }

    private void loadPlayers() {

    }

    private void runState(DatabaseLoader.State state) throws RuntimeException {
        for (DatabaseLoader databaseLoader : databaseLoaders) {
            databaseLoader.setState(state);
        }
    }

    private class CopyOldDatabase implements DatabaseLoader {

        @Override
        public void setState(State state) throws RuntimeException {
            if (state == State.INITIALIZE) {
                File oldDataFile = new File(plugin.getDataFolder(), "database.db");
                if (oldDataFile.exists()) {
                    File newDataFile = new File(plugin.getDataFolder(), "datastore/database.db");
                    newDataFile.getParentFile().mkdirs();
                    if (!oldDataFile.renameTo(newDataFile))
                        throw new RuntimeException("Misslyckades att flytta gammal databas fil.");
                }
            }

        }
    }

}
