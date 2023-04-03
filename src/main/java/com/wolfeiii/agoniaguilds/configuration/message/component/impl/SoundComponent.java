package com.wolfeiii.agoniaguilds.configuration.message.component.impl;

import com.wolfeiii.agoniaguilds.configuration.message.IMessageComponent;
import com.wolfeiii.agoniaguilds.configuration.message.component.EmptyMessageComponent;
import com.wolfeiii.agoniaguilds.utilities.sounds.GameSound;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

public class SoundComponent implements IMessageComponent {

    private final GameSound gameSound;

    public static IMessageComponent of(@Nullable GameSound gameSound) {
        return GameSound.isEmpty(gameSound) ? EmptyMessageComponent.getInstance() : new SoundComponent(gameSound);
    }

    private SoundComponent(GameSound gameSound) {
        this.gameSound = gameSound;
    }


    @Override
    public Type getType() {
        return Type.SOUND;
    }

    @Override
    public String getMessage() {
        return "";
    }

    @Override
    public void sendMessage(CommandSender sender, Object... args) {
        if (sender instanceof Player)
            GameSound.playSound((Player) sender, gameSound);
    }

}