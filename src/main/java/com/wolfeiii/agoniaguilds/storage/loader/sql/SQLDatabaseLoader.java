package com.wolfeiii.agoniaguilds.storage.loader.sql;

import com.wolfeiii.agoniaguilds.AgoniaGuilds;
import com.wolfeiii.agoniaguilds.storage.loader.MachineStateDatabaseLoader;
import com.wolfeiii.agoniaguilds.storage.sql.SQLHelper;
import com.wolfeiii.agoniaguilds.storage.sql.session.QueryResult;
import com.wolfeiii.agoniaguilds.utilities.objects.Pair;

public class SQLDatabaseLoader extends MachineStateDatabaseLoader {

    private final AgoniaGuilds plugin;

    public SQLDatabaseLoader(AgoniaGuilds plugin) {
        this.plugin = plugin;
    }

    @Override
    protected void handleInitialize() throws RuntimeException {
        if (!SQLHelper.createConnection(plugin)) {
            throw new RuntimeException("Det gick inte att ansluta till databasen." +
                    "\nSe till att all information är korrekt.");
        }

        createGuildsTable();

    }

    @Override
    protected void handlePostInitialize() throws RuntimeException {
        SQLHelper.createIndex("guilds_members_index", "guilds_members",
                "guild", "player");
    }

    @Override
    protected void handlePreLoadData() throws RuntimeException {
        SQLHelper.setJournalMode("MEMORY", QueryResult.EMPTY_QUERY_RESULT);
    }

    @Override
    protected void handlePostLoadData() throws RuntimeException {
        SQLHelper.setJournalMode("DELETE", QueryResult.EMPTY_QUERY_RESULT);
    }

    @Override
    protected void handleShutdown() throws RuntimeException {
        SQLHelper.close();
    }

    @SuppressWarnings("unchecked")
    private void createGuildsTable() {
        SQLHelper.createTable("guilds",
                new Pair<>("uuid", "UUID PRIMARY KEY"),
                new Pair<>("owner", "UUID"),
                new Pair<>("creation_time", "BIGINT"),
                new Pair<>("ignored", "BOOLEAN"),
                new Pair<>("name", "TEXT"),
                new Pair<>("description", "TEXT"),
                new Pair<>("last_time_updated", "BIGINT")
        );

        SQLHelper.createTable("guilds_chests",
                new Pair<>("guild", "UUID"),
                new Pair<>("`index`", "INTEGER"),
                new Pair<>("contents", "LONGTEXT")
        );

        SQLHelper.createTable("guilds_members",
                new Pair<>("guild", "UUID"),
                new Pair<>("player", "UUID"),
                new Pair<>("join_time", "BIGINT")
        );

        SQLHelper.createTable("guilds_settings",
                new Pair<>("guild", "UUID PRIMARY KEY"),
                new Pair<>("members_limit", "INTEGER"),
                new Pair<>("crop_growth_multiplier", "DECIMAL"),
                new Pair<>("spawner_rates_multiplier", "DECIMAL"),
                new Pair<>("mob_drops_multiplier", "DECIMAL")
        );

        SQLHelper.modifyColumnType("guilds_settings", "crop_growth_multiplier", "DECIMAL");
        SQLHelper.modifyColumnType("guilds_settings", "spawner_rates_multiplier", "DECIMAL");
        SQLHelper.modifyColumnType("guilds_settings", "mob_drops_multiplier", "DECIMAL");
    }
}
