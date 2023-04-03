package com.wolfeiii.agoniaguilds.command.arguments;

import com.wolfeiii.agoniaguilds.guild.Guild;
import com.wolfeiii.agoniaguilds.user.GuildUser;

import javax.annotation.Nullable;
import java.util.List;

public class GuildsListArgument extends Argument<List<Guild>, GuildUser> {

    public GuildsListArgument(@Nullable List<Guild> guild, GuildUser guildUser) {
        super(guild, guildUser);
    }

    public List<Guild> getGuilds() {
        return super.k;
    }

    @Nullable
    public GuildUser getGuildUser() {
        return super.v;
    }


}