package com.wolfeiii.agoniaguilds.menu.impl;

import com.wolfeiii.agoniaguilds.guild.Guild;
import com.wolfeiii.agoniaguilds.menu.*;
import com.wolfeiii.agoniaguilds.menu.button.impl.LevelButton;
import com.wolfeiii.agoniaguilds.menu.interfaces.Menu;
import com.wolfeiii.agoniaguilds.menu.interfaces.layout.MenuLayout;
import com.wolfeiii.agoniaguilds.menu.interfaces.view.MenuView;
import com.wolfeiii.agoniaguilds.menu.view.AbstractPagedMenuView;
import com.wolfeiii.agoniaguilds.menu.view.GuildMenuView;
import com.wolfeiii.agoniaguilds.menu.view.args.GuildViewArgs;
import com.wolfeiii.agoniaguilds.user.GuildUser;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class MenuLevels extends AbstractMenu<GuildMenuView, GuildViewArgs> {

    protected MenuLevels(MenuParseResult<GuildMenuView> parseResult) {
        super(MenuIdentifiers.MENU_GUILD_LEVELS, parseResult);
    }

    @Nullable
    public static MenuLevels createInstance() {
        MenuParseResult<GuildMenuView> menuParseResult = MenuParserImpl.getInstance().loadMenu("levels.yml",
                null);

        if (menuParseResult == null)
            return null;

        MenuPatternSlots menuPatternSlots = menuParseResult.getPatternSlots();
        YamlConfiguration configuration = menuParseResult.getConfig();
        MenuLayout.Builder<GuildMenuView> menuViewBuilder = menuParseResult.getLayoutBuilder();

        if (configuration.isConfigurationSection("items")) {
            for (String itemSectionName : configuration.getConfigurationSection("items").getKeys(false)) {
                ConfigurationSection itemSection = configuration.getConfigurationSection("items." + itemSectionName);

                assert itemSection != null;

                if (!itemSection.isInt("level"))
                    continue;

                int levelValue = itemSection.getInt("level", 1);

                LevelButton.Builder buttonBuilder = new LevelButton.Builder(levelValue);

                menuViewBuilder.mapButtons(menuPatternSlots.getSlots(itemSectionName), buttonBuilder);
            }
        }

        return new MenuLevels(menuParseResult);
    }

    @Override
    protected GuildMenuView createViewInternal(GuildUser guildUser, GuildViewArgs args,
                                               @Nullable MenuView<?, ?> previousMenu) {
        return new GuildMenuView(guildUser, previousMenu, this, args);
    }
}
