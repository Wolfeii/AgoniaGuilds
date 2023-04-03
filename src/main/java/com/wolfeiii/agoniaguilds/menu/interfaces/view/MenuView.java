package com.wolfeiii.agoniaguilds.menu.interfaces.view;

import com.wolfeiii.agoniaguilds.menu.interfaces.Menu;
import com.wolfeiii.agoniaguilds.user.GuildUser;
import org.bukkit.inventory.InventoryHolder;

import javax.annotation.Nullable;

public interface MenuView<V extends MenuView<V, A>, A extends ViewArgs> extends InventoryHolder {

    /**
     * Get the player currently viewing the menu.
     */
    GuildUser getInventoryViewer();

    /**
     * Get the menu of this view.
     */
    Menu<V, A> getMenu();

    /**
     * Get the previous menu that was opened for the player.
     */
    @Nullable
    MenuView<?, ?> getPreviousMenuView();

    /**
     * Set the previous menu to be opened after closing this view.
     *
     * @param previousMenuView The menu to open after that.
     * @param keepOlderViews   If previousMenuView is not null and set to true, older views will be kept.
     *                         In other words, it will only replace the last previous view.
     *                         If false, the previous views will be the ones of previousMenuView.
     */
    void setPreviousMenuView(@Nullable MenuView<?, ?> previousMenuView, boolean keepOlderViews);

    /**
     * Set whether closing the menu should open the previous menu.
     */
    void setPreviousMove(boolean previousMove);

    /**
     * Get whether closing the menu should open the previous menu.
     */
    boolean isPreviousMenu();

    /**
     * Refresh this view for the player.
     * This will re-build the inventory from scratch and update it for the player viewing it.
     */
    void refreshView();

    /**
     * Close the view for the player.
     * The view can later be reopened using {@link #refreshView()} or by creating a new one from {@link Menu}
     */
    void closeView();

}