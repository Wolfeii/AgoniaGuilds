package com.wolfeiii.agoniaguilds.configuration.message.component.impl;

import com.wolfeiii.agoniaguilds.configuration.message.IMessageComponent;
import com.wolfeiii.agoniaguilds.configuration.message.Message;
import com.wolfeiii.agoniaguilds.configuration.message.component.EmptyMessageComponent;
import com.wolfeiii.agoniaguilds.utilities.text.Text;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.Nullable;

public class RawMessageComponent implements IMessageComponent {

    private final String message;

    public static IMessageComponent of(@Nullable String message) {
        return Text.isBlank(message) ? EmptyMessageComponent.getInstance() : new RawMessageComponent(message);
    }

    private RawMessageComponent(String message) {
        this.message = message;
    }

    @Override
    public Type getType() {
        return Type.RAW_MESSAGE;
    }

    @Override
    public String getMessage() {
        return this.message;
    }

    @Override
    public void sendMessage(CommandSender sender, Object... args) {
        Message.replaceArgs(this.message, args).ifPresent(sender::sendMessage);
    }

}