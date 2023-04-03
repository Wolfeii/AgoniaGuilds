package com.wolfeiii.agoniaguilds.menu.impl;

import com.wolfeiii.agoniaguilds.guild.Guild;
import com.wolfeiii.agoniaguilds.menu.*;
import com.wolfeiii.agoniaguilds.menu.button.impl.ControlPanelButton;
import com.wolfeiii.agoniaguilds.menu.interfaces.layout.MenuLayout;
import com.wolfeiii.agoniaguilds.menu.interfaces.view.MenuView;
import com.wolfeiii.agoniaguilds.menu.view.GuildMenuView;
import com.wolfeiii.agoniaguilds.menu.view.args.GuildViewArgs;
import com.wolfeiii.agoniaguilds.user.GuildUser;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.Nullable;

public class MenuControlPanel extends AbstractMenu<GuildMenuView, GuildViewArgs> {

    protected MenuControlPanel(MenuParseResult<GuildMenuView> parseResult) {
        super(MenuIdentifiers.MENU_CONTROL_PANEL, parseResult);
    }

    @Nullable
    public static MenuControlPanel createInstance() {
        MenuParseResult<GuildMenuView> menuParseResult = MenuParserImpl.getInstance().loadMenu("control-panel.yml",
                null);

        if (menuParseResult == null)
            return null;

        MenuPatternSlots menuPatternSlots = menuParseResult.getPatternSlots();
        YamlConfiguration configuration = menuParseResult.getConfig();
        MenuLayout.Builder<GuildMenuView> patternBuilder = menuParseResult.getLayoutBuilder();

        patternBuilder.mapButtons(MenuParserImpl.getInstance().parseButtonSlots(configuration, "members",
                        menuPatternSlots), new ControlPanelButton.Builder().setAction(ControlPanelButton.ControlPanelAction.OPEN_MEMBERS));
        patternBuilder.mapButtons(MenuParserImpl.getInstance().parseButtonSlots(configuration, "level",
                        menuPatternSlots), new ControlPanelButton.Builder().setAction(ControlPanelButton.ControlPanelAction.OPEN_LEVELS));
        patternBuilder.mapButtons(MenuParserImpl.getInstance().parseButtonSlots(configuration, "settings",
                        menuPatternSlots), new ControlPanelButton.Builder().setAction(ControlPanelButton.ControlPanelAction.OPEN_SETTINGS));

        return new MenuControlPanel(menuParseResult);
    }

    @Override
    protected GuildMenuView createViewInternal(GuildUser guildUser, GuildViewArgs args,
                                               @Nullable MenuView<?, ?> previousMenu) {
        return new GuildMenuView(guildUser, previousMenu, this, args);
    }
}
