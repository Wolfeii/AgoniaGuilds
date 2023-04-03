package com.wolfeiii.agoniaguilds.menu.view.args;

import com.wolfeiii.agoniaguilds.guild.Guild;
import com.wolfeiii.agoniaguilds.menu.interfaces.view.ViewArgs;
import com.wolfeiii.agoniaguilds.user.GuildUser;

public class GuildViewArgs implements ViewArgs {

    private final Guild guild;

    public GuildViewArgs(Guild guild) {
        this.guild = guild;
    }

    public Guild getGuild() {
        return guild;
    }
}