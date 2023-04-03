package com.wolfeiii.agoniaguilds.menu;

import com.wolfeiii.agoniaguilds.AgoniaGuilds;
import com.wolfeiii.agoniaguilds.menu.interfaces.view.MenuView;

import javax.annotation.Nullable;

@Deprecated
public interface IGuildMenu extends MenuView {
    /**
     * Clone and open this menu to the {@link #getInventoryViewer()}
     *
     * @param previousMenu The previous menu to set.
     */
    void cloneAndOpen(@Nullable IGuildMenu previousMenu);

    /**
     * Get the previous menu of this menu.
     */
    @Nullable
    IGuildMenu getPreviousMenu();

    /**
     * Helper method to cast the new {@link MenuView} object to the old {@link IGuildMenu} object.
     */
    @Deprecated
    static IGuildMenu convertFromView(MenuView<?, ?> menuView) {
        return AgoniaGuilds.getAgoniaGuilds().getMenuManager().getOldMenuFromView(menuView);
    }
}
