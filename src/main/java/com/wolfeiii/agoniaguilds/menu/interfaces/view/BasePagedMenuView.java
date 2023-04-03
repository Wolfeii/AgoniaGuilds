package com.wolfeiii.agoniaguilds.menu.interfaces.view;

import com.google.common.base.Preconditions;
import com.wolfeiii.agoniaguilds.menu.interfaces.Menu;
import com.wolfeiii.agoniaguilds.user.GuildUser;

import javax.annotation.Nullable;
import java.util.List;

public abstract class BasePagedMenuView<V extends MenuView<V, A>, A extends ViewArgs, E> extends BaseMenuView<V, A> implements PagedMenuView<V, A, E> {

    protected int currentPage = 1;

    protected BasePagedMenuView(GuildUser guildUser, Menu<V, A> menu, @Nullable MenuView<?, ?> previousMenuView) {
        super(guildUser, menu, previousMenuView);
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
        return this.currentPage;
    }

    @Override
    public abstract List<E> getPagedObjects();

    @Override
    public abstract void updatePagedObjects();

}