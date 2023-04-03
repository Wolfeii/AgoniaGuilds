package com.wolfeiii.agoniaguilds.command.arguments;

import com.wolfeiii.agoniaguilds.guild.Guild;
import com.wolfeiii.agoniaguilds.user.GuildUser;

import javax.annotation.Nullable;

public class GuildArgument extends Argument<Guild, GuildUser> {

    public static final GuildArgument EMPTY = new GuildArgument(null, null);

    public GuildArgument(@Nullable Guild guild, GuildUser guildUser) {
        super(guild, guildUser);
    }

    @Nullable
    public Guild getGuild() {
        return super.k;
    }

    public GuildUser getGuildUser() {
        return super.v;
    }


}
