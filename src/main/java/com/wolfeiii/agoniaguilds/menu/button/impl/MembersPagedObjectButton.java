package com.wolfeiii.agoniaguilds.menu.button.impl;

import com.wolfeiii.agoniaguilds.guild.Guild;
import com.wolfeiii.agoniaguilds.menu.button.AbstractPagedMenuButton;
import com.wolfeiii.agoniaguilds.menu.button.PagedMenuTemplateButtonImpl;
import com.wolfeiii.agoniaguilds.menu.impl.MenuGuildMembers;
import com.wolfeiii.agoniaguilds.menu.interfaces.button.MenuTemplateButton;
import com.wolfeiii.agoniaguilds.menu.interfaces.button.PagedMenuTemplateButton;
import com.wolfeiii.agoniaguilds.menu.interfaces.button.PagedMenuViewButton;
import com.wolfeiii.agoniaguilds.menu.interfaces.view.MenuView;
import com.wolfeiii.agoniaguilds.menu.view.args.GuildViewArgs;
import com.wolfeiii.agoniaguilds.user.GuildUser;
import com.wolfeiii.agoniaguilds.utilities.formatters.Formatters;
import com.wolfeiii.agoniaguilds.utilities.itemstack.ItemBuilder;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Date;

public class MembersPagedObjectButton extends AbstractPagedMenuButton<MenuGuildMembers.View, GuildUser> {

    private MembersPagedObjectButton(MenuTemplateButton<MenuGuildMembers.View> templateButton, MenuGuildMembers.View menuView) {
        super(templateButton, menuView);
    }

    @Override
    public void onButtonClick(InventoryClickEvent clickEvent) {
    }

    @Override
    public ItemStack modifyViewItem(ItemStack buttonItem) {
        return new ItemBuilder(buttonItem)
                .replaceAll("{0}", pagedObject.getName())
                .replaceAll("{1}", pagedObject.getPlayerRole() + "")
                .replaceAll("{2}", pagedObject.isOnline() ? "&aOnline" : Formatters.DATE_FORMATTER.format(new Date(pagedObject.getLastTimeStatus())))
                .asSkullOf(pagedObject)
                .build(pagedObject);
    }

    public static class Builder extends PagedMenuTemplateButtonImpl.AbstractBuilder<MenuGuildMembers.View, GuildUser> {

        @Override
        public PagedMenuTemplateButton<MenuGuildMembers.View, GuildUser> build() {
            return new PagedMenuTemplateButtonImpl<>(buttonItem, clickSound, commands, requiredPermission,
                    lackPermissionSound, nullItem, getButtonIndex(), MembersPagedObjectButton.class,
                    MembersPagedObjectButton::new);
        }
    }
}
