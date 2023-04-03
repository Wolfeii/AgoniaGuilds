package com.wolfeiii.agoniaguilds.menu;

import com.google.common.base.Preconditions;
import com.wolfeiii.agoniaguilds.AgoniaGuilds;
import com.wolfeiii.agoniaguilds.menu.button.AbstractMenuViewButton;
import com.wolfeiii.agoniaguilds.menu.interfaces.BaseMenu;
import com.wolfeiii.agoniaguilds.menu.interfaces.button.MenuViewButton;
import com.wolfeiii.agoniaguilds.menu.view.AbstractMenuView;
import com.wolfeiii.agoniaguilds.menu.interfaces.view.MenuView;
import com.wolfeiii.agoniaguilds.menu.interfaces.view.ViewArgs;
import com.wolfeiii.agoniaguilds.user.GuildUser;
import com.wolfeiii.agoniaguilds.utilities.objects.BukkitExecutor;
import org.bukkit.Bukkit;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;

import javax.annotation.Nullable;
import java.util.concurrent.CompletableFuture;

public abstract class AbstractMenu<V extends AbstractMenuView<V, A>, A extends ViewArgs> extends BaseMenu<V, A> {

    protected static final AgoniaGuilds plugin = AgoniaGuilds.getAgoniaGuilds();

    protected AbstractMenu(String identifier, MenuParseResult<V> parseResult) {
        super(identifier, parseResult.getLayoutBuilder().build(), parseResult.getOpeningSound(),
                parseResult.isPreviousMoveAllowed(), parseResult.isSkipOneItem());
    }

    @Override
    public final CompletableFuture<V> createView(GuildUser guildUser, A args) {
        return super.createView(guildUser, args);
    }

    @Override
    public final CompletableFuture<V> createView(GuildUser guildUser, A args, @Nullable MenuView<?, ?> previousMenu) {
        Preconditions.checkNotNull(guildUser, "guildUser parameter cannot be null.");
        Preconditions.checkState(guildUser.isOnline(), "Cannot create view for offline player: " + guildUser.getName());
        Preconditions.checkNotNull(args, "args parameter cannot be null.");
        V view = createViewInternal(guildUser, args, previousMenu);
        addView(view);
        return refreshView(view);
    }

    @Override
    public void onClick(InventoryClickEvent clickEvent, V menuView) {
        if (!menuView.isRefreshing())
            super.onClick(clickEvent, menuView);
    }

    @Override
    protected void onCloseInternal(InventoryCloseEvent closeEvent, V menuView) {
        menuView.onClose();
    }

    protected abstract V createViewInternal(GuildUser guildUser, A args, @Nullable MenuView<?, ?> previousMenu);

    public CompletableFuture<V> refreshView(V view) {
        CompletableFuture<V> res = new CompletableFuture<>();
        buildInventory(view).whenComplete((inventory, error) -> {
            if (error != null) {
                res.completeExceptionally(error);
            } else {
                BukkitExecutor.sync(() -> {
                    view.setInventory(inventory);
                    res.complete(view);
                });
            }
        });
        return res;
    }

    public CompletableFuture<Inventory> buildInventory(V menuView) {
        if (!Bukkit.isPrimaryThread()) {
            return CompletableFuture.completedFuture(this.menuLayout.buildInventory(menuView));
        }

        CompletableFuture<Inventory> inventoryFuture = new CompletableFuture<>();
        BukkitExecutor.async(() -> inventoryFuture.complete(this.menuLayout.buildInventory(menuView)));
        return inventoryFuture;
    }

    @Override
    protected void onButtonClickLackPermission(MenuViewButton<V> menuButton, InventoryClickEvent clickEvent) {
        if (menuButton instanceof AbstractMenuViewButton)
            ((AbstractMenuViewButton<V>) menuButton).onButtonClickLackPermission(clickEvent);
    }

    protected boolean onPreButtonClick(MenuViewButton<V> menuButton, InventoryClickEvent clickEvent) {
        return true;
    }

}