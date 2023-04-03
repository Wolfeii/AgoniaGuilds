package com.wolfeiii.agoniaguilds.menu.impl;

import com.wolfeiii.agoniaguilds.guild.Guild;
import com.wolfeiii.agoniaguilds.menu.AbstractPagedMenu;
import com.wolfeiii.agoniaguilds.menu.MenuIdentifiers;
import com.wolfeiii.agoniaguilds.menu.MenuParseResult;
import com.wolfeiii.agoniaguilds.menu.MenuParserImpl;
import com.wolfeiii.agoniaguilds.menu.button.impl.MembersPagedObjectButton;
import com.wolfeiii.agoniaguilds.menu.interfaces.Menu;
import com.wolfeiii.agoniaguilds.menu.interfaces.view.MenuView;
import com.wolfeiii.agoniaguilds.menu.view.AbstractPagedMenuView;
import com.wolfeiii.agoniaguilds.menu.view.args.GuildViewArgs;
import com.wolfeiii.agoniaguilds.user.GuildUser;

import javax.annotation.Nullable;
import java.util.List;

public class MenuGuildMembers extends AbstractPagedMenu<MenuGuildMembers.View, GuildViewArgs, GuildUser> {

    protected MenuGuildMembers(MenuParseResult<View> parseResult) {
        super(MenuIdentifiers.MENU_GUILD_MEMBERS, parseResult, false);
    }

    @Override
    protected View createViewInternal(GuildUser guildUser, GuildViewArgs args, @org.jetbrains.annotations.Nullable MenuView<?, ?> previousMenu) {
        return new View(guildUser, previousMenu, this, args);
    }

    @Nullable
    public static MenuGuildMembers createInstance() {
        MenuParseResult<View> menuParseResult = MenuParserImpl.getInstance().loadMenu("members.yml",
                null, new MembersPagedObjectButton.Builder());
        return menuParseResult == null ? null : new MenuGuildMembers(menuParseResult);
    }

    public static class View extends AbstractPagedMenuView<MenuGuildMembers.View, GuildViewArgs, GuildUser> {

        private final Guild guild;

        View(GuildUser inventoryViewer, @Nullable MenuView<?, ?> previousMenuView,
             Menu<View, GuildViewArgs> menu, GuildViewArgs viewArgs) {
            super(inventoryViewer, previousMenuView, menu);
            this.guild = viewArgs.getGuild();
        }

        @Override
        protected List<GuildUser> requestObjects() {
            return guild.getGuildMembers(true);
        }
    }
}
