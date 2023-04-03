package com.wolfeiii.agoniaguilds.menu;

import com.wolfeiii.agoniaguilds.AgoniaGuilds;
import com.wolfeiii.agoniaguilds.menu.impl.MenuGuildMembers;
import com.wolfeiii.agoniaguilds.menu.impl.internal.MenuBlank;
import com.wolfeiii.agoniaguilds.menu.interfaces.Menu;

public class Menus {

    private static final AgoniaGuilds plugin = AgoniaGuilds.getAgoniaGuilds();

    public static final MenuBlank MENU_BLANK = MenuBlank.createInstance();
    public static final MenuGuildMembers MENU_GUILD_MEMBERS = MenuGuildMembers.createInstance();
    public static final MenuGuildMembers MENU_LEVELS = MenuGuildMembers.createInstance();

}
