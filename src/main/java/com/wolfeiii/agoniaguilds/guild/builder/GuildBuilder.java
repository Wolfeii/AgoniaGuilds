package com.wolfeiii.agoniaguilds.guild.builder;

import com.google.common.base.Preconditions;
import com.wolfeiii.agoniaguilds.AgoniaGuilds;
import com.wolfeiii.agoniaguilds.user.GuildUser;
import com.wolfeiii.agoniaguilds.utilities.value.Value;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

public class GuildBuilder {

    private static final AgoniaGuilds plugin = AgoniaGuilds.getAgoniaGuilds();

    @Nullable
    public GuildUser owner;
    public UUID uuid = null;
    public String guildName = "";
    public long creationTime = System.currentTimeMillis() / 1000;
    public String description = "";
    public long lastTimeUpdated = System.currentTimeMillis() / 1000;
    public final List<GuildUser> members = new LinkedList<>();
    public Value<Integer> teamLimit = Value.syncedFixed(-1);

    public GuildBuilder setOwner(@Nullable GuildUser owner) {
        this.owner = owner;
        return this;
    }

    @Nullable
    public GuildUser getOwner() {
        return owner;
    }

    public GuildBuilder setUniqueId(UUID uuid) {
        Preconditions.checkNotNull(uuid, "uuid kan inte vara null.");
        Preconditions.checkState(plugin.getGuildManager().getGuildByUUID(uuid) == null, "Det värdet som angavs är inte unikt.");
        this.uuid = uuid;
        return this;
    }

    public UUID getUniqueId() {
        return uuid;
    }

    public GuildBuilder setName(String guildName) {
        Preconditions.checkNotNull(guildName, "guildName kan inte vara null.");
        this.guildName = guildName;
        return this;
    }

    public String getName() {
        return guildName;
    }

    public GuildBuilder setCreationTime(long creationTime) {
        this.creationTime = creationTime;
        return this;
    }

    public long getCreationTime() {
        return creationTime;
    }

    public GuildBuilder setDescription(String description) {
        Preconditions.checkNotNull(description,"description kan inte vara null");
        this.description = description;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public GuildBuilder setLastTimeUpdated(long lastTimeUpdated) {
        this.lastTimeUpdated = lastTimeUpdated;
        return this;
    }

    public long getLastTimeUpdated() {
        return lastTimeUpdated;
    }

    public GuildBuilder addGuildMember(GuildUser guildUser) {
        Preconditions.checkNotNull(guildUser, "guildUser kan inte vara null.");
        this.members.add(guildUser);
        return this;
    }

    public List<GuildUser> getGuildMembers() {
        return Collections.unmodifiableList(members);
    }
}
