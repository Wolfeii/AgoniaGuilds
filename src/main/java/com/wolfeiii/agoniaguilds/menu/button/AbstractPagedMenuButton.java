package com.wolfeiii.agoniaguilds.menu.button;

import com.wolfeiii.agoniaguilds.menu.interfaces.button.MenuTemplateButton;
import com.wolfeiii.agoniaguilds.menu.interfaces.button.PagedMenuViewButton;
import com.wolfeiii.agoniaguilds.menu.interfaces.view.MenuView;
import org.bukkit.inventory.ItemStack;

public abstract class AbstractPagedMenuButton<V extends MenuView<V, ?>, E>
        extends AbstractMenuViewButton<V> implements PagedMenuViewButton<V, E> {

    protected E pagedObject = null;

    protected AbstractPagedMenuButton(MenuTemplateButton<V> templateButton, V menuView) {
        super(templateButton, menuView);
    }

    @Override
    public void updateObject(E pagedObject) {
        this.pagedObject = pagedObject;
    }

    @Override
    public E getPagedObject() {
        return pagedObject;
    }

    @Override
    public final ItemStack createViewItem() {
        return modifyViewItem(super.createViewItem());
    }

    public abstract ItemStack modifyViewItem(ItemStack buttonItem);

}