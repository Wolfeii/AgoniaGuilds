package com.wolfeiii.agoniaguilds.menu.interfaces.button;

import com.wolfeiii.agoniaguilds.menu.interfaces.view.MenuView;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public interface MenuViewButton<V extends MenuView<V, ?>> {

    /**
     * Get the template that was used to create this view button.
     */
    MenuTemplateButton<V> getTemplate();

    /**
     * Get the view that this button is used in.
     */
    V getView();

    /**
     * Create a new view item for this button.
     * This should use {@link MenuTemplateButton#getButtonItem()}
     */
    ItemStack createViewItem();

    /**
     * Method callback when clicking this button.
     * The event passed as an argument is already cancelled.
     *
     * @param clickEvent The click event.
     */
    void onButtonClick(InventoryClickEvent clickEvent);

}