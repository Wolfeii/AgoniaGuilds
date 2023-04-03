package com.wolfeiii.agoniaguilds.menu.interfaces.layout;

import com.wolfeiii.agoniaguilds.menu.interfaces.button.PagedMenuTemplateButton;
import com.wolfeiii.agoniaguilds.menu.interfaces.view.MenuView;
import com.wolfeiii.agoniaguilds.menu.interfaces.view.PagedMenuView;
import com.wolfeiii.agoniaguilds.menu.layout.PagedMenuLayoutImpl;

import java.util.List;

public interface PagedMenuLayout<V extends MenuView<V, ?>> extends MenuLayout<V> {

    /**
     * Get the amount of paged objects in the layout.
     */
    int getObjectsPerPageCount();

    /**
     * Create a new {@link Builder} object for a new {@link PagedMenuLayout}.
     */
    static <V extends PagedMenuView<V, ?, E>, E> Builder<V, E> newBuilder() {
        return PagedMenuLayoutImpl.newBuilder();
    }

    interface Builder<V extends MenuView<V, ?>, E> extends MenuLayout.Builder<V> {

        /**
         * Set the previous-page button slots for this layout.
         *
         * @param slots The slots to set.
         */
        Builder<V, E> setPreviousPageSlots(List<Integer> slots);

        /**
         * Set the next-page button slots for this layout.
         *
         * @param slots The slots to set.
         */
        Builder<V, E> setNextPageSlots(List<Integer> slots);

        /**
         * Set the current-page display button slots for this layout.
         *
         * @param slots The slots to set.
         */
        Builder<V, E> setCurrentPageSlots(List<Integer> slots);

        /**
         * Set the page-object button slots for this layout.
         *
         * @param slots         The slots to set.
         * @param buttonBuilder The builder used for the paged-object.
         */
        Builder<V, E> setPagedObjectSlots(List<Integer> slots, PagedMenuTemplateButton.Builder<V, E> buttonBuilder);

        /**
         * Get the {@link PagedMenuLayout} from this builder.
         */
        @Override
        PagedMenuLayout<V> build();

    }

}