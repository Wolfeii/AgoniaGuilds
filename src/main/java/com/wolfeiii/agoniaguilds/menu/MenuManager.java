package com.wolfeiii.agoniaguilds.menu;

import com.google.common.base.Preconditions;
import com.wolfeiii.agoniaguilds.AgoniaGuilds;
import com.wolfeiii.agoniaguilds.guild.Guild;
import com.wolfeiii.agoniaguilds.menu.interfaces.view.MenuView;
import com.wolfeiii.agoniaguilds.menu.view.MenuViewWrapper;
import com.wolfeiii.agoniaguilds.menu.view.args.GuildViewArgs;
import com.wolfeiii.agoniaguilds.user.GuildUser;
import org.jetbrains.annotations.Nullable;

public class MenuManager {

    private final AgoniaGuilds plugin;

    public MenuManager(AgoniaGuilds plugin) {
        this.plugin = plugin;
    }

    public void openMembers(GuildUser inventoryViewer, @Nullable IGuildMenu previousMenu, Guild targetGuild) {
        Preconditions.checkNotNull(inventoryViewer, "inventoryViewer kan inte vara null.");
        Preconditions.checkNotNull(targetGuild, "targetGuild kan inte vara null.");
        Menus.MENU_GUILD_MEMBERS.createView(inventoryViewer, new GuildViewArgs(targetGuild), previousMenu);
    }

    public void openLevels(GuildUser inventoryViewer, @Nullable IGuildMenu previousMenu, Guild targetGuild) {
        Preconditions.checkNotNull(inventoryViewer, "inventoryViewer kan inte vara null.");
        Preconditions.checkNotNull(targetGuild, "targetGuild kan inte vara null.");
        Menus.MENU_LEVELS.createView(inventoryViewer, new GuildViewArgs(targetGuild), previousMenu);
    }

    @Deprecated
    public IGuildMenu getOldMenuFromView(MenuView<?, ?> menuView) {
        return MenuViewWrapper.fromView(menuView);
    }

}
