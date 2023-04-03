package com.wolfeiii.agoniaguilds.menu.impl.internal;

import com.wolfeiii.agoniaguilds.menu.AbstractMenu;
import com.wolfeiii.agoniaguilds.menu.MenuIdentifiers;
import com.wolfeiii.agoniaguilds.menu.MenuParseResult;
import com.wolfeiii.agoniaguilds.menu.TemplateItem;
import com.wolfeiii.agoniaguilds.menu.button.impl.DummyButton;
import com.wolfeiii.agoniaguilds.menu.interfaces.view.MenuView;
import com.wolfeiii.agoniaguilds.menu.layout.RegularMenuLayoutImpl;
import com.wolfeiii.agoniaguilds.menu.view.BaseMenuView;
import com.wolfeiii.agoniaguilds.menu.view.args.EmptyViewArgs;
import com.wolfeiii.agoniaguilds.user.GuildUser;
import com.wolfeiii.agoniaguilds.utilities.itemstack.ItemBuilder;
import com.wolfeiii.agoniaguilds.utilities.sounds.GameSound;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;

public class MenuBlank extends AbstractMenu<BaseMenuView, EmptyViewArgs> {

    private MenuBlank(MenuParseResult<BaseMenuView> parseResult) {
        super(MenuIdentifiers.MENU_BLANK, parseResult);
    }

    @Override
    protected BaseMenuView createViewInternal(GuildUser guildUser, EmptyViewArgs unused,
                                              @Nullable MenuView<?, ?> previousMenuView) {
        return new BaseMenuView(guildUser, previousMenuView, this);
    }

    public static MenuBlank createInstance() {
        RegularMenuLayoutImpl.Builder<BaseMenuView> patternBuilder = RegularMenuLayoutImpl.newBuilder();

        patternBuilder.setTitle("" + ChatColor.RED + ChatColor.BOLD + "ERROR");
        patternBuilder.setRowsCount(3);

        DummyButton.Builder<BaseMenuView> button = new DummyButton.Builder<>();
        button.setButtonItem(new TemplateItem(new ItemBuilder(Material.BEDROCK)
                .withName("&cOladdad Meny")
                .withLore(Arrays.asList(
                        "&7Det uppstod ett fel när vi skulle ladda in denna meny.",
                        "&7Skriv till en Ledare för att lösa problemet!.")
                )));
        button.setClickSound(new GameSound(Sound.BLOCK_ANVIL_PLACE, 0.2f, 0.2f));
        patternBuilder.setButton(13, button.build());

        return new MenuBlank(new MenuParseResult<>(patternBuilder));
    }


}
