package com.wolfeiii.agoniaguilds.menu.button.impl;

import com.wolfeiii.agoniaguilds.menu.button.AbstractMenuTemplateButton;
import com.wolfeiii.agoniaguilds.menu.button.AbstractMenuViewButton;
import com.wolfeiii.agoniaguilds.menu.button.MenuTemplateButtonImpl;
import com.wolfeiii.agoniaguilds.menu.interfaces.button.MenuTemplateButton;
import com.wolfeiii.agoniaguilds.menu.interfaces.view.MenuView;
import org.bukkit.event.inventory.InventoryClickEvent;

public class DummyButton<V extends MenuView<V, ?>> extends AbstractMenuViewButton<V> {

    public static final MenuTemplateButton EMPTY_BUTTON = new DummyButton.Builder<>().build();

    private DummyButton(AbstractMenuTemplateButton<V> templateButton, V menuView) {
        super(templateButton, menuView);
    }

    @Override
    public void onButtonClick(InventoryClickEvent clickEvent) {
        // Dummy button
    }

    public static class Builder<V extends MenuView<V, ?>> extends AbstractMenuTemplateButton.AbstractBuilder<V> {

        @Override
        public MenuTemplateButton<V> build() {
            return new MenuTemplateButtonImpl<>(buttonItem, clickSound, commands, requiredPermission,
                    lackPermissionSound, DummyButton.class, DummyButton::new);
        }

    }

}