package com.wolfeiii.agoniaguilds.menu.button.impl;

import com.wolfeiii.agoniaguilds.menu.TemplateItem;
import com.wolfeiii.agoniaguilds.menu.button.AbstractMenuTemplateButton;
import com.wolfeiii.agoniaguilds.menu.button.AbstractMenuViewButton;
import com.wolfeiii.agoniaguilds.menu.button.MenuTemplateButtonImpl;
import com.wolfeiii.agoniaguilds.menu.button.PagedMenuTemplateButtonImpl;
import com.wolfeiii.agoniaguilds.menu.impl.MenuGuildMembers;
import com.wolfeiii.agoniaguilds.menu.impl.MenuLevels;
import com.wolfeiii.agoniaguilds.menu.interfaces.Menu;
import com.wolfeiii.agoniaguilds.menu.interfaces.button.MenuTemplateButton;
import com.wolfeiii.agoniaguilds.menu.interfaces.button.PagedMenuTemplateButton;
import com.wolfeiii.agoniaguilds.menu.view.GuildMenuView;
import com.wolfeiii.agoniaguilds.user.GuildUser;
import com.wolfeiii.agoniaguilds.utilities.sounds.GameSound;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.List;

public class LevelButton extends AbstractMenuViewButton<GuildMenuView> {

    protected LevelButton(MenuTemplateButton<GuildMenuView> templateButton, GuildMenuView menuView) {
        super(templateButton, menuView);
    }

    @Override
    public Template getTemplate() {
        return (Template) super.getTemplate();
    }

    @Override
    public void onButtonClick(InventoryClickEvent clickEvent) {

    }

    public static class Builder extends AbstractMenuTemplateButton.AbstractBuilder<GuildMenuView> {

        private int levelIncrease;

        public Builder(int levelIncrease) {
            this.levelIncrease = levelIncrease;
        }

        public void setLevelIncrease(int levelIncrease) {
            this.levelIncrease = levelIncrease;
        }

        @Override
        public MenuTemplateButton<GuildMenuView> build() {
            return new Template(buttonItem, requiredPermission, lackPermissionSound,
                    clickSound, commands, levelIncrease);
        }

    }

    public static class Template extends MenuTemplateButtonImpl<GuildMenuView> {

        private final int levelIncrease;

        Template(TemplateItem buttonItem, String requiredPermission, GameSound lackPermissionSound,
                 GameSound sound, List<String> commands, int levelIncrease) {
            super(buttonItem, null, null, requiredPermission, lackPermissionSound,
                    LevelButton.class, LevelButton::new);
            this.levelIncrease = levelIncrease;
        }

        public int getLevelIncrease() {
            return levelIncrease;
        }
    }
}
