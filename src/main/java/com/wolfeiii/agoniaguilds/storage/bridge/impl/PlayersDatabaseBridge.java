package com.wolfeiii.agoniaguilds.storage.bridge.impl;

import com.wolfeiii.agoniaguilds.storage.bridge.DatabaseBridge;
import com.wolfeiii.agoniaguilds.storage.bridge.DatabaseBridgeMode;
import com.wolfeiii.agoniaguilds.storage.filter.DatabaseFilter;
import com.wolfeiii.agoniaguilds.user.GuildUser;
import com.wolfeiii.agoniaguilds.utilities.objects.Pair;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;

@SuppressWarnings("unchecked")
public class PlayersDatabaseBridge {

    private PlayersDatabaseBridge() {
    }

    public static void saveTextureValue(GuildUser guildUser) {
        runOperationIfRunning(guildUser.getDatabaseBridge(), databaseBridge -> databaseBridge.updateObject("players",
                createFilter("uuid", guildUser),
                new Pair<>("last_used_skin", guildUser.getTextureValue())
        ));
    }

    public static void savePlayerName(GuildUser guildUser) {
        runOperationIfRunning(guildUser.getDatabaseBridge(), databaseBridge -> databaseBridge.updateObject("players",
                createFilter("uuid", guildUser),
                new Pair<>("last_used_name", guildUser.getName())
        ));
    }


    public static void insertPlayer(GuildUser guildUser) {
        runOperationIfRunning(guildUser.getDatabaseBridge(), databaseBridge -> {
            databaseBridge.insertObject("players",
                    new Pair<>("uuid", guildUser.getUniqueId().toString()),
                    new Pair<>("last_used_name", guildUser.getName()),
                    new Pair<>("last_used_skin", guildUser.getTextureValue()),
                    new Pair<>("last_time_updated", guildUser.getLastTimeStatus())
            );
        });
    }


    public static void deletePlayer(GuildUser guildUser) {
        runOperationIfRunning(guildUser.getDatabaseBridge(), databaseBridge -> {
            // For everything else: DatabaseFilter playerFilter = createFilter("player", guildUser);
            databaseBridge.deleteObject("players", createFilter("uuid", guildUser));
        });
    }

    private static DatabaseFilter createFilter(String id, GuildUser guildUser, Pair<String, Object>... others) {
        List<Pair<String, Object>> filters = new LinkedList<>();
        filters.add(new Pair<>(id, guildUser.getUniqueId().toString()));
        if (others != null)
            filters.addAll(Arrays.asList(others));
        return DatabaseFilter.fromFilters(filters);
    }

    private static void runOperationIfRunning(DatabaseBridge databaseBridge, Consumer<DatabaseBridge> databaseBridgeConsumer) {
        if (databaseBridge.getDatabaseBridgeMode() == DatabaseBridgeMode.SAVE_DATA)
            databaseBridgeConsumer.accept(databaseBridge);
    }
}
