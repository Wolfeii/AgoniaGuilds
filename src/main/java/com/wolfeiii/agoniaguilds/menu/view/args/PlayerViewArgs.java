package com.wolfeiii.agoniaguilds.menu.view.args;

import com.wolfeiii.agoniaguilds.menu.interfaces.view.ViewArgs;
import com.wolfeiii.agoniaguilds.user.GuildUser;

public class PlayerViewArgs implements ViewArgs {

    private final GuildUser guildUser;

    public PlayerViewArgs(GuildUser guildUser) {
        this.guildUser = guildUser;
    }

    public GuildUser getGuildUser() {
        return guildUser;
    }
}
