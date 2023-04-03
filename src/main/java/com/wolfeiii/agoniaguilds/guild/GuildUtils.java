package com.wolfeiii.agoniaguilds.guild;

import com.wolfeiii.agoniaguilds.AgoniaGuilds;
import com.wolfeiii.agoniaguilds.configuration.message.Message;
import com.wolfeiii.agoniaguilds.user.GuildUser;

import java.util.List;
import java.util.UUID;

public class GuildUtils {

    private static final AgoniaGuilds plugin = AgoniaGuilds.getAgoniaGuilds();

    private GuildUtils() {

    }

    public static void sendMessage(Guild guild, Message message, List<UUID> ignoredMembers, Object... args) {
        for (GuildUser guildMember : guild.getGuildMembers(true)) {
            if (!ignoredMembers.contains(guildMember.getUniqueId()))
                message.send(guildMember, args);
        }
    }

    public static boolean checkKickRestriction(GuildUser guildUser, Guild guild, GuildUser targetPlayer) {
        if (!guild.isMember(guildUser)) {
            Message.PLAYER_NOT_IN_GUILD.send(guildUser);
            return false;
        }

        if (!targetPlayer.getPlayerRole().isLessThan(guildUser.getPlayerRole())) {
            Message.KICK_PLAYERS_WITH_LOWER_ROLE.send(guildUser);
            return false;
        }

        return true;
    }
}
