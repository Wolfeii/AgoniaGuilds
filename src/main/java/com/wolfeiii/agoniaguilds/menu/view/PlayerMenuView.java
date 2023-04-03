package com.wolfeiii.agoniaguilds.menu.view;

import com.wolfeiii.agoniaguilds.menu.interfaces.Menu;
import com.wolfeiii.agoniaguilds.menu.interfaces.view.MenuView;
import com.wolfeiii.agoniaguilds.menu.view.args.PlayerViewArgs;
import com.wolfeiii.agoniaguilds.user.GuildUser;

import javax.annotation.Nullable;

public class PlayerMenuView extends AbstractMenuView<PlayerMenuView, PlayerViewArgs> {

    private final GuildUser guildUser;

    public PlayerMenuView(GuildUser inventoryViewer, @Nullable MenuView<?, ?> previousMenuView,
                          Menu<PlayerMenuView, PlayerViewArgs> menu, PlayerViewArgs args) {
        super(inventoryViewer, previousMenuView, menu);
        this.guildUser = args.getGuildUser();
    }

    public GuildUser getGuildUser() {
        return guildUser;
    }

}