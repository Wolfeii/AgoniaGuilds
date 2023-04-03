package com.wolfeiii.agoniaguilds.menu.view;

import com.wolfeiii.agoniaguilds.guild.Guild;
import com.wolfeiii.agoniaguilds.menu.interfaces.Menu;
import com.wolfeiii.agoniaguilds.menu.interfaces.view.MenuView;
import com.wolfeiii.agoniaguilds.menu.view.args.GuildViewArgs;
import com.wolfeiii.agoniaguilds.menu.view.args.PlayerViewArgs;
import com.wolfeiii.agoniaguilds.user.GuildUser;

import javax.annotation.Nullable;

public class GuildMenuView extends AbstractMenuView<GuildMenuView, GuildViewArgs> {

    private final Guild guild;

    public GuildMenuView(GuildUser inventoryViewer, @Nullable MenuView<?, ?> previousMenuView,
                          Menu<GuildMenuView, GuildViewArgs> menu, GuildViewArgs args) {
        super(inventoryViewer, previousMenuView, menu);
        this.guild = args.getGuild();
    }

    public Guild getGuild() {
        return guild;
    }

}