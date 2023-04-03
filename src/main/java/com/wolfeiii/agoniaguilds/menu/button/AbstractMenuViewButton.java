package com.wolfeiii.agoniaguilds.menu.button;

import com.wolfeiii.agoniaguilds.AgoniaGuilds;
import com.wolfeiii.agoniaguilds.menu.TemplateItem;
import com.wolfeiii.agoniaguilds.menu.interfaces.button.MenuTemplateButton;
import com.wolfeiii.agoniaguilds.menu.interfaces.button.MenuViewButton;
import com.wolfeiii.agoniaguilds.menu.interfaces.view.MenuView;
import com.wolfeiii.agoniaguilds.utilities.sounds.GameSound;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public abstract class AbstractMenuViewButton<V extends MenuView<V, ?>> implements MenuViewButton<V> {

    protected static final AgoniaGuilds plugin = AgoniaGuilds.getAgoniaGuilds();

    protected final V menuView;
    private final AbstractMenuTemplateButton<V> templateButton;

    protected AbstractMenuViewButton(MenuTemplateButton<V> templateButton, V menuView) {
        this.templateButton = (AbstractMenuTemplateButton<V>) templateButton;
        this.menuView = menuView;
    }

    @Override
    public MenuTemplateButton<V> getTemplate() {
        return this.templateButton;
    }

    @Override
    public V getView() {
        return this.menuView;
    }

    @Override
    public ItemStack createViewItem() {
        TemplateItem templateItem = this.templateButton.getButtonTemplateItem();
        return templateItem == null ? null : templateItem.getBuilder().build(menuView.getInventoryViewer());
    }

    @Override
    public abstract void onButtonClick(InventoryClickEvent clickEvent);

    public void onButtonClickLackPermission(InventoryClickEvent clickEvent) {
        GameSound.playSound(clickEvent.getWhoClicked(), this.templateButton.getLackPermissionSound());
    }

}