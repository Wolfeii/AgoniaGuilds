package com.wolfeiii.agoniaguilds.menu.button.impl;

import com.wolfeiii.agoniaguilds.menu.button.AbstractMenuTemplateButton;
import com.wolfeiii.agoniaguilds.menu.button.AbstractMenuViewButton;
import com.wolfeiii.agoniaguilds.menu.button.MenuTemplateButtonImpl;
import com.wolfeiii.agoniaguilds.menu.interfaces.button.MenuTemplateButton;
import com.wolfeiii.agoniaguilds.menu.interfaces.layout.PagedMenuLayout;
import com.wolfeiii.agoniaguilds.menu.interfaces.view.PagedMenuView;
import org.bukkit.event.inventory.InventoryClickEvent;

public class NextPageButton<V extends PagedMenuView<V, ?, E>, E> extends AbstractMenuViewButton<V> {

    private NextPageButton(AbstractMenuTemplateButton<V> templateButton, V menuView) {
        super(templateButton, menuView);
    }

    @Override
    public void onButtonClick(InventoryClickEvent clickEvent) {
        PagedMenuLayout<V> pagedMenuPattern = (PagedMenuLayout<V>) menuView.getMenu().getLayout();

        if (pagedMenuPattern == null)
            return;

        int pageObjectSlotsAmount = pagedMenuPattern.getObjectsPerPageCount();
        int currentPage = menuView.getCurrentPage();
        int pagedObjectAmounts = menuView.getPagedObjects().size();

        if (pageObjectSlotsAmount * currentPage < pagedObjectAmounts)
            menuView.setCurrentPage(currentPage + 1);
    }

    public static class Builder<V extends PagedMenuView<V, ?, E>, E> extends AbstractMenuTemplateButton.AbstractBuilder<V> {

        @Override
        public MenuTemplateButton<V> build() {
            return new MenuTemplateButtonImpl<>(buttonItem, clickSound, commands, requiredPermission,
                    lackPermissionSound, NextPageButton.class, NextPageButton::new);
        }

    }

}