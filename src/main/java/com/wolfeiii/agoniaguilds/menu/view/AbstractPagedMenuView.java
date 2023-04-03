package com.wolfeiii.agoniaguilds.menu.view;

import com.google.common.base.Preconditions;
import com.wolfeiii.agoniaguilds.menu.interfaces.Menu;
import com.wolfeiii.agoniaguilds.menu.interfaces.view.MenuView;
import com.wolfeiii.agoniaguilds.menu.interfaces.view.PagedMenuView;
import com.wolfeiii.agoniaguilds.menu.interfaces.view.ViewArgs;
import com.wolfeiii.agoniaguilds.user.GuildUser;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;

public abstract class AbstractPagedMenuView<V extends MenuView<V, A>, A extends ViewArgs, E>
        extends AbstractMenuView<V, A> implements PagedMenuView<V, A, E> {

    private List<E> objects;
    private int currentPage = 1;

    public AbstractPagedMenuView(GuildUser inventoryViewer, @Nullable MenuView<?, ?> previousMenuView, Menu<V, A> menu) {
        super(inventoryViewer, previousMenuView, menu);
    }

    @Override
    public void setCurrentPage(int currentPage) {
        Preconditions.checkArgument(currentPage >= 1, "invalid page " + currentPage);

        if (this.currentPage == currentPage)
            return;

        this.currentPage = currentPage;

        setPreviousMove(false);
        refreshView();
    }

    @Override
    public int getCurrentPage() {
        return currentPage;
    }

    @Override
    public List<E> getPagedObjects() {
        if (this.objects == null)
            updatePagedObjects();

        return Collections.unmodifiableList(this.objects);
    }

    @Override
    public void updatePagedObjects() {
        this.objects = requestObjects();
    }

    @Override
    public void refreshView() {
        updatePagedObjects();
        super.refreshView();
    }

    protected abstract List<E> requestObjects();

}