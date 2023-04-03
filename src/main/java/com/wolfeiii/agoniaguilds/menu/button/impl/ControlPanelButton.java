package com.wolfeiii.agoniaguilds.menu.button.impl;

import com.wolfeiii.agoniaguilds.guild.Guild;
import com.wolfeiii.agoniaguilds.menu.TemplateItem;
import com.wolfeiii.agoniaguilds.menu.button.AbstractMenuTemplateButton;
import com.wolfeiii.agoniaguilds.menu.button.AbstractMenuViewButton;
import com.wolfeiii.agoniaguilds.menu.button.MenuTemplateButtonImpl;
import com.wolfeiii.agoniaguilds.menu.interfaces.button.MenuTemplateButton;
import com.wolfeiii.agoniaguilds.menu.view.GuildMenuView;
import com.wolfeiii.agoniaguilds.menu.view.MenuViewWrapper;
import com.wolfeiii.agoniaguilds.user.GuildUser;
import com.wolfeiii.agoniaguilds.utilities.objects.BukkitExecutor;
import com.wolfeiii.agoniaguilds.utilities.sounds.GameSound;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.List;

public class ControlPanelButton extends AbstractMenuViewButton<GuildMenuView> {

    protected ControlPanelButton(MenuTemplateButton<GuildMenuView> templateButton, GuildMenuView menuView) {
        super(templateButton, menuView);
    }

    @Override
    public Template getTemplate() {
        return (Template) super.getTemplate();
    }

    @Override
    public void onButtonClick(InventoryClickEvent clickEvent) {
        GuildUser guildUser = plugin.getUserManager().getGuildUser(clickEvent.getWhoClicked());
        Guild targetGuild = menuView.getGuild();

        switch (getTemplate().getControlPanelAction()) {
            case OPEN_MEMBERS ->
                plugin.getMenuManager().openMembers(guildUser, MenuViewWrapper.fromView(menuView), targetGuild);
            case OPEN_LEVELS ->
                plugin.getMenuManager().openLevels(guildUser, MenuViewWrapper.fromView(menuView), targetGuild);
        }

        BukkitExecutor.sync(menuView::closeView, 1L);
    }

    public enum ControlPanelAction {

        OPEN_MEMBERS,
        OPEN_SETTINGS,
        OPEN_LEVELS,
    }

    public static class Builder extends AbstractMenuTemplateButton.AbstractBuilder<GuildMenuView> {

        private ControlPanelAction controlPanelAction;

        public Builder setAction(ControlPanelAction controlPanelAction) {
            this.controlPanelAction = controlPanelAction;
            return this;
        }

        @Override
        public MenuTemplateButton<GuildMenuView> build() {
            return new Template(buttonItem, clickSound, commands, requiredPermission, lackPermissionSound, controlPanelAction);
        }

    }

    public static class Template extends MenuTemplateButtonImpl<GuildMenuView> {

        private final ControlPanelAction controlPanelAction;

        Template(TemplateItem buttonItem, GameSound clickSound, List<String> commands,
                 String requiredPermission, GameSound lackPermissionSound, ControlPanelAction controlPanelAction) {
            super(buttonItem, clickSound, commands, requiredPermission, lackPermissionSound,
                    ControlPanelButton.class, ControlPanelButton::new);
            this.controlPanelAction = controlPanelAction;
        }

        public ControlPanelAction getControlPanelAction() {
            return controlPanelAction;
        }
    }


}
