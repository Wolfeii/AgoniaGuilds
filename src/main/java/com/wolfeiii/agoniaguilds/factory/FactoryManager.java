package com.wolfeiii.agoniaguilds.factory;

import com.google.common.base.Preconditions;
import com.wolfeiii.agoniaguilds.user.GuildUser;
import com.wolfeiii.agoniaguilds.user.builder.GuildUserBuilder;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import java.util.UUID;

public class FactoryManager {

    public GuildUser createUser(UUID playerUUID) {
        Preconditions.checkNotNull(playerUUID, "playerUUID kan inte vara null.");

        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(playerUUID);
        GuildUserBuilder builder = createPlayerBuilder()
                .setUniqueId(playerUUID);

        if (offlinePlayer.getName() != null)
            builder.setName(offlinePlayer.getName());

        return builder.build();
    }

    public GuildUser createUser(GuildUserBuilder builder) {
        return new GuildUser(builder);
    }

    public GuildUserBuilder createPlayerBuilder() {
        return new GuildUserBuilder();
    }
}
