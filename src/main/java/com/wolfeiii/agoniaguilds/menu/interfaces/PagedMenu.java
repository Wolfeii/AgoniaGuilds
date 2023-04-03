package com.wolfeiii.agoniaguilds.menu.interfaces;

import com.wolfeiii.agoniaguilds.menu.interfaces.view.PagedMenuView;
import com.wolfeiii.agoniaguilds.menu.interfaces.view.ViewArgs;

/**
 * Similar to {@link Menu}, but used for describing page-based menus.
 * See {@link Menu}
 */
public interface PagedMenu<V extends PagedMenuView<V, A, E>, A extends ViewArgs, E> extends Menu<V, A> {

}