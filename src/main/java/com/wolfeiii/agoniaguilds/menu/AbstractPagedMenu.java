package com.wolfeiii.agoniaguilds.menu;

import com.google.common.base.Preconditions;
import com.wolfeiii.agoniaguilds.menu.interfaces.PagedMenu;
import com.wolfeiii.agoniaguilds.menu.interfaces.button.MenuViewButton;
import com.wolfeiii.agoniaguilds.menu.interfaces.button.PagedMenuTemplateButton;
import com.wolfeiii.agoniaguilds.menu.interfaces.button.PagedMenuViewButton;
import com.wolfeiii.agoniaguilds.menu.interfaces.layout.MenuLayout;
import com.wolfeiii.agoniaguilds.menu.interfaces.layout.PagedMenuLayout;
import com.wolfeiii.agoniaguilds.menu.interfaces.view.ViewArgs;
import com.wolfeiii.agoniaguilds.menu.view.AbstractPagedMenuView;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.List;

public abstract class AbstractPagedMenu<V extends AbstractPagedMenuView<V, A, E>, A extends ViewArgs, E>
        extends AbstractMenu<V, A> implements PagedMenu<V, A, E> {

    private final boolean acceptNull;

    protected AbstractPagedMenu(String identifier, MenuParseResult<V> parseResult, boolean acceptNull) {
        super(identifier, parseResult);
        Preconditions.checkState(parseResult.getLayoutBuilder() instanceof PagedMenuLayout.Builder, "Paged menu " + identifier + " doesn't use the correct layout.");
        this.acceptNull = acceptNull;
    }

    @Override
    public boolean onPreButtonClick(MenuViewButton<V> menuButton, InventoryClickEvent clickEvent) {
        if (!(menuButton instanceof PagedMenuViewButton))
            return true;

        MenuLayout<V> menuLayout = getLayout();

        if (!(menuLayout instanceof PagedMenuLayout))
            return false;

        PagedMenuViewButton<V, E> pagedMenuButton = (PagedMenuViewButton<V, E>) menuButton;

        V menuView = menuButton.getView();

        menuView.updatePagedObjects();
        List<E> pagedObjects = menuView.getPagedObjects();

        int objectsPerPage = ((PagedMenuLayout<V>) menuLayout).getObjectsPerPageCount();

        int currentPage = menuView.getCurrentPage();

        int objectIndex = ((PagedMenuTemplateButton<V, E>) pagedMenuButton.getTemplate()).getButtonIndex() + (objectsPerPage * (currentPage - 1));

        if (objectIndex >= pagedObjects.size()) {
            if (this.acceptNull)
                pagedMenuButton.updateObject(null);
            return this.acceptNull;
        }

        pagedMenuButton.updateObject(pagedObjects.get(objectIndex));

        return true;
    }

}