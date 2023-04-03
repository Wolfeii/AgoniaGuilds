package com.wolfeiii.agoniaguilds.storage.bridge.impl;

import com.wolfeiii.agoniaguilds.guild.Guild;
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
public class GuildDatabaseBridge {

    public static void addMember(Guild guild, GuildUser guildUser, long addTime) {
        runOperationIfRunning(guild.getDatabaseBridge(), databaseBridge -> databaseBridge.insertObject("guilds_members",
                new Pair<>("guild", guild.getUniqueId().toString()),
                new Pair<>("player", guildUser.getUniqueId().toString()),
                new Pair<>("role", guildUser.getPlayerRole().getId()),
                new Pair<>("join_time", addTime)
        ));
    }

    public static void removeMember(Guild guild, GuildUser guildUser) {
        runOperationIfRunning(guild.getDatabaseBridge(), databaseBridge -> databaseBridge.deleteObject("guilds_members",
                createFilter("guild", guild, new Pair<>("player", guild.getUniqueId().toString()))
        ));
    }

    public static void saveMemberRole(Guild guild, GuildUser guildUser) {
        runOperationIfRunning(guild.getDatabaseBridge(), databaseBridge -> databaseBridge.updateObject("guilds_members",
                createFilter("guild", guild, new Pair<>("player", guildUser.getUniqueId().toString())),
                new Pair<>("role", guildUser.getPlayerRole().getId())
        ));
    }

    public static void saveName(Guild guild) {
        runOperationIfRunning(guild.getDatabaseBridge(), databaseBridge -> databaseBridge.updateObject("guilds",
                createFilter("uuid", guild),
                new Pair<>("name", guild.getName())
        ));
    }

    public static void saveDescription(Guild guild) {
        runOperationIfRunning(guild.getDatabaseBridge(), databaseBridge -> databaseBridge.updateObject("guilds",
                createFilter("uuid", guild),
                new Pair<>("description", guild.getDescription())
        ));
    }

    public static void saveLastTimeUpdate(Guild guild) {
        runOperationIfRunning(guild.getDatabaseBridge(), databaseBridge -> databaseBridge.updateObject("guilds",
                createFilter("uuid", guild),
                new Pair<>("last_time_updated", guild.getLastTimeUpdate())
        ));
    }

    public static void saveGuildLeader(Guild guild) {
        runOperationIfRunning(guild.getDatabaseBridge(), databaseBridge -> databaseBridge.updateObject("guilds",
                createFilter("uuid", guild),
                new Pair<>("owner", guild.getOwner().getUniqueId().toString())
        ));
    }

    public static void insertGuild(Guild guild) {
        runOperationIfRunning(guild.getDatabaseBridge(), databaseBridge -> {
            databaseBridge.insertObject("guilds",
                    new Pair<>("uuid", guild.getUniqueId().toString()),
                    new Pair<>("owner", guild.getOwner().getUniqueId().toString()),
                    new Pair<>("creation_time", guild.getCreationTime()),
                    new Pair<>("ignored", guild.isIgnored()),
                    new Pair<>("name", guild.getName()),
                    new Pair<>("description", guild.getDescription()),
                    new Pair<>("last_time_updated", System.currentTimeMillis() / 1000L)
            );
        });
    }

    private static DatabaseFilter createFilter(String id, Guild guild, Pair<String, Object>... others) {
        List<Pair<String, Object>> filters = new LinkedList<>();
        filters.add(new Pair<>(id, guild.getUniqueId().toString()));
        if (others != null)
            filters.addAll(Arrays.asList(others));
        return DatabaseFilter.fromFilters(filters);
    }

    private static void runOperationIfRunning(DatabaseBridge databaseBridge, Consumer<DatabaseBridge> databaseBridgeConsumer) {
        if (databaseBridge.getDatabaseBridgeMode() == DatabaseBridgeMode.SAVE_DATA)
            databaseBridgeConsumer.accept(databaseBridge);
    }


}
