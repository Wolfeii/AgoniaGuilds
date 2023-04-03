package com.wolfeiii.agoniaguilds.menu.interfaces.view;

import com.wolfeiii.agoniaguilds.menu.interfaces.Menu;
import com.wolfeiii.agoniaguilds.user.GuildUser;
import org.bukkit.inventory.Inventory;

import javax.annotation.Nullable;

public abstract class BaseMenuView<V extends MenuView<V, A>, A extends ViewArgs> implements MenuView<V, A> {

    protected final GuildUser inventoryViewer;
    protected final Menu<V, A> menu;

    @Nullable
    protected MenuView<?, ?> previousMenuView;
    protected boolean previousMove = true;

    protected BaseMenuView(GuildUser inventoryViewer, Menu<V, A> menu, @Nullable MenuView<?, ?> previousMenuView) {
        this.inventoryViewer = inventoryViewer;
        this.menu = menu;
        this.previousMenuView = previousMenuView;
    }

    @Override
    public GuildUser getInventoryViewer() {
        return this.inventoryViewer;
    }

    @Override
    public Menu<V, A> getMenu() {
        return this.menu;
    }

    @Nullable
    @Override
    public MenuView<?, ?> getPreviousMenuView() {
        return this.previousMenuView;
    }

    @Override
    public void setPreviousMenuView(@Nullable MenuView<?, ?> previousMenuView, boolean keepOlderViews) {
        MenuView<?, ?> oldPreviousMenuView = this.previousMenuView;
        this.previousMenuView = previousMenuView;
        if (keepOlderViews && oldPreviousMenuView != null && previousMenuView != null)
            previousMenuView.setPreviousMenuView(oldPreviousMenuView.getPreviousMenuView(), false);
    }

    @Override
    public void setPreviousMove(boolean previousMove) {
        this.previousMove = previousMove;
    }

    @Override
    public boolean isPreviousMenu() {
        return this.previousMove;
    }

    @Override
    public abstract void refreshView();

    @Override
    public abstract void closeView();

    @Override
    public abstract Inventory getInventory();

}