package com.wolfeiii.agoniaguilds.user.builder;

import com.google.common.base.Preconditions;
import com.wolfeiii.agoniaguilds.AgoniaGuilds;
import com.wolfeiii.agoniaguilds.guild.builder.GuildBuilder;
import com.wolfeiii.agoniaguilds.user.GuildUser;

import java.util.UUID;

public class GuildUserBuilder {

    private static final AgoniaGuilds plugin = AgoniaGuilds.getAgoniaGuilds();

    public UUID uuid;
    public String name;
    public String textureValue;
    public long lastTimeUpdated;

    public GuildUserBuilder() {

    }

    public GuildUserBuilder setUniqueId(UUID uuid) {
        Preconditions.checkNotNull(uuid, "uuid kan inte vara null.");
        Preconditions.checkState(plugin.getUserManager().getGuildUser(uuid, false) == null, "Det värdet som angavs är inte unikt.");
        this.uuid = uuid;
        return this;
    }

    public UUID getUniqueId() {
        return uuid;
    }


    public GuildUserBuilder setName(String name) {
        Preconditions.checkNotNull(name, "name kan inte vara null.");
        this.name = name;
        return this;
    }

    public String getName() {
        return this.name;
    }

    public GuildUser build() {
        if (this.uuid == null)
            throw new IllegalStateException("Cannot create a player with invalid uuid.");

        return plugin.getFactory().createUser(this);
    }
}
