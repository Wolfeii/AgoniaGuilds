package com.wolfeiii.agoniaguilds.guild;

import com.google.common.base.Preconditions;
import com.wolfeiii.agoniaguilds.AgoniaGuilds;
import com.wolfeiii.agoniaguilds.guild.builder.GuildBuilder;
import com.wolfeiii.agoniaguilds.storage.bridge.DatabaseBridgeMode;
import com.wolfeiii.agoniaguilds.storage.bridge.impl.GuildDatabaseBridge;
import com.wolfeiii.agoniaguilds.user.GuildUser;
import com.wolfeiii.agoniaguilds.utilities.formatters.Formatters;
import com.wolfeiii.agoniaguilds.utilities.logging.Debug;
import com.wolfeiii.agoniaguilds.utilities.logging.Log;
import com.wolfeiii.agoniaguilds.utilities.objects.BukkitExecutor;
import com.wolfeiii.agoniaguilds.utilities.objects.SequentialListBuilder;
import com.wolfeiii.agoniaguilds.utilities.objects.Synchronized;
import com.wolfeiii.agoniaguilds.utilities.objects.enums.EnumerateSet;
import com.wolfeiii.agoniaguilds.utilities.sorting.SortingType;
import org.bukkit.Bukkit;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

public class GuildManager {

    private final AgoniaGuilds plugin;

    private static final Function<Guild, UUID> GUILD_OWNERS_MAPPER = guild -> guild.getOwner().getUniqueId();

    private final Map<SortingType, Synchronized<List<Guild>>> sortedGuilds = new ConcurrentHashMap<>();
    private final Map<UUID, Guild> guildsByUuid = new ConcurrentHashMap<>();

    private final EnumerateSet<SortingType> notifiedValues = new EnumerateSet<>(SortingType.values());
    private final List<SortingType> pendingSortingTypes = new LinkedList<>();

    private BigDecimal totalLevel = BigDecimal.ZERO;
    private long lastTimeLevelUpdate = 0;

    private boolean pluginDisable = false;

    public GuildManager(AgoniaGuilds plugin) {
        this.plugin = plugin;
    }

    public void initializeData() {

    }

    public void createGuild(GuildBuilder builder) {
        Preconditions.checkNotNull(builder, "builder kan inte vara null.");
        Preconditions.checkArgument(builder.owner != null, "Kan ej skapa guild med ogiltig ägare.");

        try {
            if (!Bukkit.isPrimaryThread()) {
                BukkitExecutor.sync(() -> createGuildInternalAsync(builder));
            } else {
                createGuildInternalAsync(builder);
            }
        } catch (Throwable error) {
            Log.entering("GuildManager", "createGuild", "ENTER", builder.owner.getName(),
                    builder.guildName);
            Log.error(error, "Ett oförväntat fel uppstod vid skapandet av en Guild:");

            builder.owner.setPlayerGuild(null);
        }
    }

    private void createGuildInternalAsync(GuildBuilder guildBuilder) {
        assert guildBuilder.owner != null;

        Log.debug(Debug.CREATE_GUILD, "GuildManager", "createGuildInternalAsync",
                guildBuilder.owner.getName(), guildBuilder.guildName);

        try {
            Guild guild = guildBuilder.setUniqueId(createGuildUUID()).build();

            addGuild(guild);

            GuildDatabaseBridge.insertGuild(guild);
        } catch (Throwable error) {
            Log.entering("GuildManager", "createGuildInternalAsync", guildBuilder.owner.getName(),
                    guildBuilder.guildName);
            Log.error(error, "Ett oförväntat fel uppstod vid skapandet av en Guild:");

            guildBuilder.owner.setPlayerGuild(null);
        }
    }

    public Guild getGuild(GuildUser guildUser) {
        Preconditions.checkNotNull(guildUser, "guildUser kan inte vara null.");
        return guildUser.getGuild();
    }

    public Guild getGuild(int index, SortingType sortingType) {
        Preconditions.checkNotNull(sortingType, "sortingType kan inte vara null.");
        Preconditions.checkState(sortedGuilds.containsKey(sortingType), "Sorterings typen '" + sortingType + "' finns inte i databasen, skriv till Wolfeiii.");
        return this.sortedGuilds.get(sortingType).readAndGet(sortedGuilds -> {
            return index < 0 || index >= sortedGuilds.size() ? null : sortedGuilds.get(index);
        });
    }

    public int getGuildPosition(Guild guild, SortingType sortingType) {
        Preconditions.checkNotNull(sortingType, "sortingType kan inte vara null.");
        Preconditions.checkState(sortedGuilds.containsKey(sortingType), "Sorterings typen '" + sortingType + "' finns inte i databasen, skriv till Wolfeiii.");

        return this.sortedGuilds.get(sortingType).readAndGet(sortedGuilds -> {
            return sortedGuilds.indexOf(guild);
        });
    }

    public Guild getGuild(UUID uuid) {
        Preconditions.checkNotNull(uuid, "uuid kan inte vara null.");
        GuildUser guildUser = plugin.getUserManager().getGuildUser(uuid, false);
        return guildUser == null ? null : getGuild(guildUser);
    }

    public Guild getGuildByUUID(UUID uuid) {
        return guildsByUuid.get(uuid);
    }

    public Guild getGuild(String guildName) {
        Preconditions.checkNotNull(guildName, "guildName kan inte vara null.");
        String inputName = Formatters.STRIP_COLOR_FORMATTER.format(guildName);

        return guildsByUuid.values().stream()
                .filter(guild -> guild.getRawName().equalsIgnoreCase(inputName))
                .findFirst().orElse(null);
    }


    public int getSize() {
        return this.guildsByUuid.size();
    }

    public void sortGuilds(SortingType sortingType) {
        Preconditions.checkNotNull(sortingType, "sortingType kan inte vara null.");
        sortGuilds(sortingType, null);
    }

    public void sortGuilds(SortingType sortingType, Runnable onFinish) {
        Preconditions.checkNotNull(sortingType, "sortingType kan inte vara null.");
        Preconditions.checkState(sortedGuilds.containsKey(sortingType), "Sorterings typen '" + sortingType + "' finns inte i databasen, skriv till Wolfeiii.");

        Log.debug(Debug.SORT_GUILDS, "GuildManager", "sortGuilds", sortingType.getName());

        Synchronized<List<Guild>> sortedGuilds = this.sortedGuilds.get(sortingType);

        if (sortedGuilds.readAndGet(List::size) <= 1 || !notifiedValues.remove(sortingType)) {
            if (onFinish != null)
                onFinish.run();
            return;
        }

        if (Bukkit.isPrimaryThread()) {
            BukkitExecutor.async(() -> sortGuildsInternal(sortingType, onFinish));
        } else {
            sortGuildsInternal(sortingType, onFinish);
        }
    }

    public void addSortingType(SortingType sortingType, boolean sort) {
        Preconditions.checkArgument(!sortedGuilds.containsKey(sortingType), "Du kan inte registera en sorterings typ som redan existerar.");
        sortGuildsInternal(sortingType, null);
    }

    private void sortGuildsInternal(SortingType sortingType, Runnable onFinish) {
        List<Guild> newGuildsList = new ArrayList<>(guildsByUuid.values());
        newGuildsList.removeIf(Guild::isIgnored);

        newGuildsList.sort(sortingType);

        this.sortedGuilds.put(sortingType, Synchronized.of(newGuildsList));

        if (onFinish != null)
            onFinish.run();
    }

    public List<Guild> getGuilds() {
        return new SequentialListBuilder<Guild>().build(this.guildsByUuid.values());
    }

    public List<UUID> getAllGuilds(SortingType sortingType) {
        return new SequentialListBuilder<UUID>()
                .build(getSortedGuilds(sortingType), GUILD_OWNERS_MAPPER);
    }

    public List<Guild> getSortedGuilds(SortingType sortingType) {
        Preconditions.checkState(sortedGuilds.containsKey(sortingType), "Sorterings typen '" + sortingType + "' finns inte i databasen, skriv till Wolfeiii.");
        return this.sortedGuilds.get(sortingType).readAndGet(sortedGuilds ->
                new SequentialListBuilder<Guild>().build(sortedGuilds));
    }

    public void addGuild(Guild guild) {
        this.guildsByUuid.put(guild.getUniqueId(), guild);

        sortedGuilds.values().forEach(sortedGuilds -> {
            sortedGuilds.write(_sortedGuilds -> _sortedGuilds.add(guild));
        });
    }

    public void disablePlugin() {
        this.pluginDisable = true;
    }

    public UUID createGuildUUID() {
        UUID uuid;

        do {
            uuid = UUID.randomUUID();
        } while (getGuildByUUID(uuid) != null || plugin.getUserManager().getGuildUser(uuid, false) != null);

        return uuid;
    }
}
