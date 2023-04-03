package com.wolfeiii.agoniaguilds.menu.view;

import com.wolfeiii.agoniaguilds.AgoniaGuilds;
import com.wolfeiii.agoniaguilds.configuration.message.Message;
import com.wolfeiii.agoniaguilds.menu.AbstractMenu;
import com.wolfeiii.agoniaguilds.menu.Menus;
import com.wolfeiii.agoniaguilds.menu.impl.internal.MenuBlank;
import com.wolfeiii.agoniaguilds.menu.interfaces.Menu;
import com.wolfeiii.agoniaguilds.menu.interfaces.view.BaseMenuView;
import com.wolfeiii.agoniaguilds.menu.interfaces.view.MenuView;
import com.wolfeiii.agoniaguilds.menu.interfaces.view.ViewArgs;
import com.wolfeiii.agoniaguilds.menu.view.args.EmptyViewArgs;
import com.wolfeiii.agoniaguilds.user.GuildUser;
import com.wolfeiii.agoniaguilds.utilities.logging.Debug;
import com.wolfeiii.agoniaguilds.utilities.logging.Log;
import com.wolfeiii.agoniaguilds.utilities.objects.BukkitExecutor;
import com.wolfeiii.agoniaguilds.utilities.sounds.GameSound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import javax.annotation.Nullable;
import java.util.Arrays;

public abstract class AbstractMenuView<V extends MenuView<V, A>, A extends ViewArgs> extends BaseMenuView<V, A> {

    private static final AgoniaGuilds plugin = AgoniaGuilds.getAgoniaGuilds();

    private Inventory inventory;

    private boolean closeButton = false;
    private boolean nextMove = false;
    private boolean closed = false;
    private boolean refreshing = false;

    protected AbstractMenuView(GuildUser inventoryViewer, @Nullable MenuView<?, ?> previousMenuView, Menu<V, A> menu) {
        super(inventoryViewer, menu, previousMenuView);
    }

    @Override
    @SuppressWarnings({"rawtypes", "unchecked"})
    public void refreshView() {
        if (refreshing)
            return;

        refreshing = true;
        previousMove = false;

        ((AbstractMenu) menu).refreshView(this).whenComplete((view, error) -> {
            if (error != null) {
                ((Throwable) error).printStackTrace();
            } else {
                refreshing = false;
                previousMove = true;
            }
        });
    }

    @Override
    public void closeView() {
        inventoryViewer.runIfOnline(player -> {
            previousMove = false;
            player.closeInventory();
        });
    }

    @Override
    public Inventory getInventory() {
        return this.inventory;
    }

    public void setInventory(Inventory inventory) {
        if (closed || this.inventory != inventory) {
            this.inventory = inventory;
            this.openView();
        }
    }

    public boolean isRefreshing() {
        return refreshing;
    }

    public void setClickedCloseButton() {
        closeButton = true;
    }

    public String replaceTitle(String title) {
        return title;
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private void openView() {
        Player player = inventoryViewer.asPlayer();

        if (player == null)
            return;

        if (player.isSleeping()) {
            Message.OPEN_MENU_WHILE_SLEEPING.send(inventoryViewer);
            return;
        }

        AbstractMenu menu = (AbstractMenu) getMenu();

        Log.debug(Debug.MENU_OPEN, inventoryViewer.getName());

        if (inventory == null || menu.getLayout() == null) {
            if (!(menu instanceof MenuBlank)) {
                Menus.MENU_BLANK.createView(inventoryViewer, EmptyViewArgs.INSTANCE, previousMenuView);
            }
            return;
        }

        MenuView<?, ?> currentOpenedView = inventoryViewer.getOpenedView();
        if (currentOpenedView instanceof AbstractMenuView) {
            ((AbstractMenuView<?, ?>) currentOpenedView).nextMove = true;
        }

        if (Arrays.equals(player.getOpenInventory().getTopInventory().getContents(), inventory.getContents()))
            return;

        if (previousMenuView != null)
            previousMenuView.setPreviousMove(false);

        if (currentOpenedView != null && previousMenuView != currentOpenedView)
            currentOpenedView.setPreviousMove(false);

        player.openInventory(inventory);

        if (closed) {
            // If the view was closed before, we want to register it again.
            closed = false;
            menu.addView(this);
        }

        GameSound.playSound(player, menu.getOpeningSound());

        this.previousMenuView = previousMenuView != null ? previousMenuView : previousMove ? currentOpenedView : null;
    }

    public void onClose() {
        closed = true;

        if (!nextMove && !closeButton && plugin.getConfiguration().isOnlyBackButton()) {
            BukkitExecutor.sync(this::openView);
        } else if (this.previousMenuView != null && this.menu.isPreviousMoveAllowed()) {
            if (previousMove) {
                BukkitExecutor.sync(previousMenuView::refreshView);
            } else {
                previousMove = true;
            }
        }

        closeButton = false;
        nextMove = false;
    }

}
