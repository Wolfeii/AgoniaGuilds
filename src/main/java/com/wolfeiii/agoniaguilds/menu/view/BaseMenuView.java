package com.wolfeiii.agoniaguilds.menu.view;

import com.wolfeiii.agoniaguilds.menu.interfaces.Menu;
import com.wolfeiii.agoniaguilds.menu.interfaces.view.MenuView;
import com.wolfeiii.agoniaguilds.menu.view.args.EmptyViewArgs;
import com.wolfeiii.agoniaguilds.user.GuildUser;
import org.jetbrains.annotations.Nullable;

public class BaseMenuView extends AbstractMenuView<BaseMenuView, EmptyViewArgs> {

    public BaseMenuView(GuildUser inventoryViewer, @Nullable MenuView<?, ?> previousMenuView,
                        Menu<BaseMenuView, EmptyViewArgs> menu) {
        super(inventoryViewer, previousMenuView, menu);
    }

}