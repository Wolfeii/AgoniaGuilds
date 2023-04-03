package com.wolfeiii.agoniaguilds.menu.layout;

import com.wolfeiii.agoniaguilds.AgoniaGuilds;
import com.wolfeiii.agoniaguilds.menu.button.AbstractMenuTemplateButton;
import com.wolfeiii.agoniaguilds.menu.button.impl.DummyButton;
import com.wolfeiii.agoniaguilds.menu.interfaces.button.MenuTemplateButton;
import com.wolfeiii.agoniaguilds.menu.interfaces.layout.MenuLayout;
import com.wolfeiii.agoniaguilds.menu.interfaces.view.MenuView;
import com.wolfeiii.agoniaguilds.menu.view.AbstractMenuView;
import com.wolfeiii.agoniaguilds.utilities.objects.SequentialListBuilder;
import org.bukkit.Bukkit;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public abstract class AbstractMenuLayout<V extends MenuView<V, ?>> implements MenuLayout<V> {

    public static final char[] BUTTON_SYMBOLS = new char[]{
            '!', '@', '#', '$', '%', '^', '&', '*', '-', '_', '+', '=',
            '~', '1', '2', '3', '4', '5', '6', '7', '8', '9', '0', '>',
            '<', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K',
            'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W',
            'X', 'Y', 'Z'
    };

    private static final AgoniaGuilds plugin = AgoniaGuilds.getAgoniaGuilds();

    protected final String title;
    protected final InventoryType inventoryType;
    protected final MenuTemplateButton<V>[] buttons;

    protected AbstractMenuLayout(String title, InventoryType inventoryType, MenuTemplateButton<V>[] buttons) {
        this.title = title;
        this.inventoryType = inventoryType;
        this.buttons = buttons;
    }

    @Override
    public final MenuTemplateButton<V> getButton(int slot) {
        return slot < 0 || slot >= this.buttons.length ? DummyButton.EMPTY_BUTTON : this.buttons[slot];
    }

    @Override
    public Collection<MenuTemplateButton<V>> getButtons() {
        return new SequentialListBuilder<MenuTemplateButton<V>>().build(Arrays.asList(buttons));
    }

    @Override
    public int getRowsCount() {
        return this.buttons.length / 9;
    }

    @Override
    public final Inventory buildInventory(V menuView) {

        String title = menuView instanceof AbstractMenuView ? ((AbstractMenuView<?, ?>) menuView).replaceTitle(this.title) : this.title;

        Inventory inventory = createInventory(menuView, title);

        populateInventory(inventory, menuView);

        return inventory;
    }

    protected abstract void populateInventory(Inventory inventory, V menuView);

    private Inventory createInventory(InventoryHolder holder, String title) {
        Inventory inventory;

        if (this.inventoryType != InventoryType.CHEST) {
            inventory = Bukkit.createInventory(holder, this.inventoryType, title);
        } else {
            inventory = Bukkit.createInventory(holder, this.buttons.length, title);
        }

        return inventory;
    }

    @SuppressWarnings("unchecked")
    public static abstract class AbstractBuilder<V extends MenuView<V, ?>> implements MenuLayout.Builder<V> {

        protected String title = "";
        protected InventoryType inventoryType = InventoryType.CHEST;
        protected MenuTemplateButton<V>[] buttons;
        private int rowsSize = 6;

        protected AbstractBuilder() {
        }

        @Override
        public AbstractBuilder<V> setTitle(String title) {
            this.title = title;
            return this;
        }

        @Override
        public AbstractBuilder<V> setInventoryType(InventoryType inventoryType) {
            this.inventoryType = inventoryType;
            updateButtons();
            return this;
        }

        @Override
        public AbstractBuilder<V> setRowsCount(int rowsCount) {
            this.rowsSize = rowsCount;
            updateButtons();
            return this;
        }

        @Override
        public AbstractBuilder<V> setButton(int slot, MenuTemplateButton<V> button) {
            if (slot >= 0 && slot < this.buttons.length)
                this.buttons[slot] = button;

            return this;
        }

        @Override
        public AbstractBuilder<V> setButtons(MenuTemplateButton<V>[] buttons) {
            setRowsCount(buttons.length / 9);

            for (int slot = 0; slot < this.buttons.length && slot < buttons.length; ++slot)
                this.buttons[slot] = buttons[slot];

            return this;
        }

        @Override
        public AbstractBuilder<V> setButtons(List<Integer> slots, MenuTemplateButton<V> button) {
            for (int slot : slots) {
                if (slot >= 0 && slot < this.buttons.length)
                    this.buttons[slot] = button;
            }
            return this;
        }

        @Override
        public AbstractBuilder<V> mapButton(int slot, MenuTemplateButton.Builder<V> buttonBuilder) {
            if (slot >= 0 && slot < this.buttons.length) {
                if (this.buttons[slot] == null) {
                    this.buttons[slot] = buttonBuilder.build();
                } else {
                    this.buttons[slot] = ((AbstractMenuTemplateButton<V>) this.buttons[slot]).applyToBuilder(buttonBuilder).build();
                }
            }
            return this;
        }

        @Override
        public AbstractBuilder<V> mapButtons(List<Integer> slots, MenuTemplateButton.Builder<V> buttonBuilder) {
            for (int slot : slots) {
                if (slot >= 0 && slot < this.buttons.length) {
                    if (this.buttons[slot] == null) {
                        this.buttons[slot] = buttonBuilder.build();
                    } else {
                        this.buttons[slot] = ((AbstractMenuTemplateButton<V>) this.buttons[slot]).applyToBuilder(buttonBuilder).build();
                    }
                }
            }
            return this;
        }

        @Override
        public abstract MenuLayout<V> build();

        private void updateButtons() {
            switch (inventoryType) {
                case CHEST:
                    rowsSize = Math.max(Math.min(rowsSize, 6), 1);
                    this.buttons = new MenuTemplateButton[rowsSize * 9];
                    break;
                case DROPPER:
                case DISPENSER:
                    this.buttons = new MenuTemplateButton[inventoryType.getDefaultSize() - 1];
                    break;
                default:
                    this.buttons = new MenuTemplateButton[inventoryType.getDefaultSize()];
                    break;
            }

            Arrays.fill(this.buttons, DummyButton.EMPTY_BUTTON);
        }

    }

}