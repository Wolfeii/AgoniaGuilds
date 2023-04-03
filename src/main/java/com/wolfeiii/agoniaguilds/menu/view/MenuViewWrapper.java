package com.wolfeiii.agoniaguilds.menu.view;

import com.wolfeiii.agoniaguilds.menu.IGuildMenu;
import com.wolfeiii.agoniaguilds.menu.interfaces.Menu;
import com.wolfeiii.agoniaguilds.menu.interfaces.view.MenuView;
import com.wolfeiii.agoniaguilds.user.GuildUser;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class MenuViewWrapper implements IGuildMenu {

    private final MenuView<?, ?> menuView;

    @SuppressWarnings("deprecation")
    public static IGuildMenu fromView(@Nullable MenuView<?, ?> menuView) {
        return menuView == null || menuView instanceof IGuildMenu ? (IGuildMenu) menuView : new MenuViewWrapper(menuView);
    }

    private MenuViewWrapper(MenuView<?, ?> menuView) {
        this.menuView = menuView;
    }

    @Override
    public void cloneAndOpen(@Nullable IGuildMenu previousMenu) {
        this.menuView.setPreviousMenuView(previousMenu, false);
        this.menuView.refreshView();
    }

    @Nullable
    @Override
    public IGuildMenu getPreviousMenu() {
        return new MenuViewWrapper(menuView.getPreviousMenuView());
    }

    @Override
    public GuildUser getInventoryViewer() {
        return menuView.getInventoryViewer();
    }

    @Override
    public Menu<?, ?> getMenu() {
        return menuView.getMenu();
    }

    @Nullable
    @Override
    public MenuView<?, ?> getPreviousMenuView() {
        return menuView.getPreviousMenuView();
    }

    @Override
    public void setPreviousMove(boolean previousMove) {
        menuView.setPreviousMove(previousMove);
    }

    @Override
    public boolean isPreviousMenu() {
        return menuView.isPreviousMenu();
    }

    @Override
    public void refreshView() {
        this.menuView.refreshView();
    }

    @Override
    public void closeView() {
        this.menuView.closeView();
    }

    @Override
    @SuppressWarnings("rawtypes")
    public void setPreviousMenuView(MenuView previousMenuView, boolean keepOlderViews) {
        menuView.setPreviousMenuView(previousMenuView, keepOlderViews);
    }

    @NotNull
    @Override
    public Inventory getInventory() {
        return menuView.getInventory();
    }
}
