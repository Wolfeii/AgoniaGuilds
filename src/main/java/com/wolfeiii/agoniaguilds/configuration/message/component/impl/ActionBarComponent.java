package com.wolfeiii.agoniaguilds.configuration.message.component.impl;

import com.wolfeiii.agoniaguilds.AgoniaGuilds;
import com.wolfeiii.agoniaguilds.configuration.message.IMessageComponent;
import com.wolfeiii.agoniaguilds.configuration.message.Message;
import com.wolfeiii.agoniaguilds.configuration.message.component.EmptyMessageComponent;
import com.wolfeiii.agoniaguilds.utilities.text.Text;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

public class ActionBarComponent implements IMessageComponent {

    private static final AgoniaGuilds plugin = AgoniaGuilds.getAgoniaGuilds();

    private final String message;

    public static IMessageComponent of(@Nullable String message) {
        return Text.isBlank(message) ? EmptyMessageComponent.getInstance() : new ActionBarComponent(message);
    }

    private ActionBarComponent(String message) {
        this.message = message;
    }

    @Override
    public Type getType() {
        return Type.ACTION_BAR;
    }

    @Override
    public String getMessage() {
        return this.message;
    }

    @Override
    public void sendMessage(CommandSender sender, Object... args) {
        Message.replaceArgs(this.message, args).ifPresent(message ->
                ((Player) sender).spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(message)));
    }

}