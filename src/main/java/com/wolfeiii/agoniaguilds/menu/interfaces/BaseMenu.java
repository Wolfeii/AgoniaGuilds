package com.wolfeiii.agoniaguilds.menu.interfaces;

import com.google.common.base.Preconditions;
import com.wolfeiii.agoniaguilds.AgoniaGuilds;
import com.wolfeiii.agoniaguilds.menu.MenuCommands;
import com.wolfeiii.agoniaguilds.menu.interfaces.button.MenuViewButton;
import com.wolfeiii.agoniaguilds.menu.interfaces.layout.MenuLayout;
import com.wolfeiii.agoniaguilds.menu.interfaces.view.MenuView;
import com.wolfeiii.agoniaguilds.menu.interfaces.view.ViewArgs;
import com.wolfeiii.agoniaguilds.user.GuildUser;
import com.wolfeiii.agoniaguilds.utilities.sounds.GameSound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;

import javax.annotation.Nullable;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.function.Predicate;

public abstract class BaseMenu<V extends MenuView<V, A>, A extends ViewArgs> implements Menu<V, A> {

    protected final List<V> openedMenuViews = new LinkedList<>();

    protected final String identifier;
    protected final MenuLayout<V> menuLayout;
    @Nullable
    protected final GameSound openingSound;
    protected final boolean isPreviousMoveAllowed;
    protected final boolean isSkipOneItem;

    protected BaseMenu(String identifier, MenuLayout<V> menuLayout, @Nullable GameSound openingSound,
                       boolean isPreviousMoveAllowed, boolean isSkipOneItem) {
        Preconditions.checkNotNull(identifier, "identifier parameter cannot be null.");
        Preconditions.checkNotNull(menuLayout, "menuLayout parameter cannot be null.");
        this.identifier = identifier;
        this.menuLayout = menuLayout;
        this.openingSound = openingSound;
        this.isPreviousMoveAllowed = isPreviousMoveAllowed;
        this.isSkipOneItem = isSkipOneItem;
    }

    @Override
    public String getIdentifier() {
        return this.identifier;
    }

    @Override
    public MenuLayout<V> getLayout() {
        return this.menuLayout;
    }

    @Override
    @Nullable
    public GameSound getOpeningSound() {
        return this.openingSound;
    }

    @Override
    public boolean isPreviousMoveAllowed() {
        return this.isPreviousMoveAllowed;
    }

    @Override
    public boolean isSkipOneItem() {
        return this.isSkipOneItem || AgoniaGuilds.getAgoniaGuilds().getConfiguration().isSkipOneItemMenus();
    }

    @Override
    public CompletableFuture<V> createView(GuildUser guildUser, A args) {
        Preconditions.checkNotNull(guildUser, "superiorPlayer parameter cannot be null.");
        Preconditions.checkNotNull(args, "args parameter cannot be null.");
        Preconditions.checkState(guildUser.isOnline(), "Cannot create view for offline player: " + guildUser.getName());
        return this.createView(guildUser, args, guildUser.getOpenedView());
    }

    @Override
    public abstract CompletableFuture<V> createView(GuildUser guildUser, A args, @Nullable MenuView<?, ?> previousMenu);

    @Override
    public void refreshViews() {
        openedMenuViews.forEach(V::refreshView);
    }

    @Override
    public void refreshViews(Predicate<V> viewFilter) {
        filterViews(viewFilter, V::refreshView);
    }

    @Override
    public void closeViews() {
        openedMenuViews.forEach(V::closeView);
    }

    @Override
    public void closeViews(Predicate<V> viewFilter) {
        filterViews(viewFilter, V::closeView);
    }

    public void addView(V view) {
        openedMenuViews.add(view);
    }

    public void removeView(V view) {
        openedMenuViews.remove(view);
    }

    protected final void filterViews(Predicate<V> viewFilter, Consumer<V> onMatch) {
        openedMenuViews.forEach(view -> {
            if (viewFilter.test(view))
                onMatch.accept(view);
        });
    }

    @Override
    public void onClick(InventoryClickEvent clickEvent, V menuView) {
        if (clickEvent.getCurrentItem() == null)
            return;

        Preconditions.checkNotNull(this.menuLayout, "menu wasn't initialized properly.");

        MenuViewButton<V> menuButton = this.menuLayout.getButton(clickEvent.getRawSlot()).createViewButton(menuView);

        String requiredPermission = menuButton.getTemplate().getRequiredPermission();
        if (requiredPermission != null && !menuView.getInventoryViewer().hasPermission(requiredPermission)) {
            onButtonClickLackPermission(menuButton, clickEvent);
            return;
        }

        Player player = (Player) clickEvent.getWhoClicked();

        GameSound clickSound = menuButton.getTemplate().getClickSound();
        if (clickSound != null)
            player.playSound(player.getLocation(), clickSound.getSound(), clickSound.getVolume(), clickSound.getPitch());

        menuButton.getTemplate().getClickCommands().forEach(command -> MenuCommands.getInstance().runCommand(menuView, command, clickEvent));

        if (onPreButtonClick(menuButton, clickEvent))
            menuButton.onButtonClick(clickEvent);
    }

    @Override
    public final void onClose(InventoryCloseEvent closeEvent, V menuView) {
        removeView(menuView);
        this.onCloseInternal(closeEvent, menuView);
    }

    protected abstract boolean onPreButtonClick(MenuViewButton<V> menuButton, InventoryClickEvent clickEvent);

    protected abstract void onButtonClickLackPermission(MenuViewButton<V> menuButton, InventoryClickEvent clickEvent);

    protected abstract void onCloseInternal(InventoryCloseEvent closeEvent, V menuView);

}