package com.wolfeiii.agoniaguilds.menu.button.impl;

import com.wolfeiii.agoniaguilds.menu.button.AbstractMenuTemplateButton;
import com.wolfeiii.agoniaguilds.menu.button.AbstractMenuViewButton;
import com.wolfeiii.agoniaguilds.menu.button.MenuTemplateButtonImpl;
import com.wolfeiii.agoniaguilds.menu.interfaces.button.MenuTemplateButton;
import com.wolfeiii.agoniaguilds.menu.interfaces.view.PagedMenuView;
import org.bukkit.event.inventory.InventoryClickEvent;

public class BackButton<V extends PagedMenuView<V, ?, E>, E> extends AbstractMenuViewButton<V> {

    private BackButton(AbstractMenuTemplateButton<V> templateButton, V menuView) {
        super(templateButton, menuView);
    }

    @Override
    public void onButtonClick(InventoryClickEvent clickEvent) {
        // Dummy button
    }

    public static class Builder<V extends PagedMenuView<V, ?, E>, E> extends AbstractMenuTemplateButton.AbstractBuilder<V> {

        @Override
        public MenuTemplateButton<V> build() {
            return new MenuTemplateButtonImpl<>(buttonItem, clickSound, commands, requiredPermission,
                    lackPermissionSound, BackButton.class, BackButton::new);
        }

    }

}