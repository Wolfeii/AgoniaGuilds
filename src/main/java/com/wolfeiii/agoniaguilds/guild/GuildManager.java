package com.wolfeiii.agoniaguilds.guild;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class GuildManager {

    private final Map<UUID, Guild> guildsByUuid = new HashMap<>();

    public Guild getGuildByUUID(UUID uuid) {
        return guildsByUuid.get(uuid);
    }
}
