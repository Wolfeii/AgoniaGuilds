package com.wolfeiii.agoniaguilds.menu.button.impl;

import com.wolfeiii.agoniaguilds.menu.button.AbstractMenuTemplateButton;
import com.wolfeiii.agoniaguilds.menu.button.AbstractMenuViewButton;
import com.wolfeiii.agoniaguilds.menu.button.MenuTemplateButtonImpl;
import com.wolfeiii.agoniaguilds.menu.interfaces.button.MenuTemplateButton;
import com.wolfeiii.agoniaguilds.menu.interfaces.view.PagedMenuView;
import org.bukkit.event.inventory.InventoryClickEvent;

public class PreviousPageButton<V extends PagedMenuView<V, ?, E>, E> extends AbstractMenuViewButton<V> {

    private PreviousPageButton(AbstractMenuTemplateButton<V> templateButton, V menuView) {
        super(templateButton, menuView);
    }

    @Override
    public void onButtonClick(InventoryClickEvent clickEvent) {
        int newPage = menuView.getCurrentPage() - 1;
        if (newPage >= 1)
            menuView.setCurrentPage(newPage);
    }

    public static class Builder<V extends PagedMenuView<V, ?, E>, E> extends AbstractMenuTemplateButton.AbstractBuilder<V> {

        @Override
        public MenuTemplateButton<V> build() {
            return new MenuTemplateButtonImpl<>(buttonItem, clickSound, commands, requiredPermission,
                    lackPermissionSound, PreviousPageButton.class, PreviousPageButton::new);
        }

    }

}