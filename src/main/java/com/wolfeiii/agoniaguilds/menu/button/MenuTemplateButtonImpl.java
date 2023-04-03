package com.wolfeiii.agoniaguilds.menu.button;

import com.wolfeiii.agoniaguilds.AgoniaGuilds;
import com.wolfeiii.agoniaguilds.menu.TemplateItem;
import com.wolfeiii.agoniaguilds.menu.interfaces.button.MenuTemplateButton;
import com.wolfeiii.agoniaguilds.menu.interfaces.button.MenuViewButton;
import com.wolfeiii.agoniaguilds.menu.interfaces.view.MenuView;
import com.wolfeiii.agoniaguilds.utilities.sounds.GameSound;
import org.checkerframework.checker.units.qual.A;

import java.util.List;

public class MenuTemplateButtonImpl<V extends MenuView<V, ?>> extends AbstractMenuTemplateButton<V> implements MenuTemplateButton<V> {

    protected static final AgoniaGuilds plugin = AgoniaGuilds.getAgoniaGuilds();

    private final ViewButtonCreator<V> viewButtonCreator;

    public MenuTemplateButtonImpl(TemplateItem buttonItem, GameSound clickSound, List<String> commands,
                                  String requiredPermission, GameSound lackPermissionSound,
                                  Class<?> viewButtonType, ViewButtonCreator<V> viewButtonCreator) {
        super(buttonItem, clickSound, commands, requiredPermission, lackPermissionSound, viewButtonType);
        this.viewButtonCreator = viewButtonCreator;
    }

    @Override
    public MenuViewButton<V> createViewButton(V menuView) {
        return ensureCorrectType(this.viewButtonCreator.create(this, menuView));
    }

    public interface ViewButtonCreator<V extends MenuView<V, ?>> {

        MenuViewButton<V> create(AbstractMenuTemplateButton<V> templateButton, V menuView);

    }

}