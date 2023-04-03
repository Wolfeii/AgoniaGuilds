package com.wolfeiii.agoniaguilds.configuration.message.component.impl;

import com.wolfeiii.agoniaguilds.AgoniaGuilds;
import com.wolfeiii.agoniaguilds.configuration.message.IMessageComponent;
import com.wolfeiii.agoniaguilds.configuration.message.Message;
import com.wolfeiii.agoniaguilds.configuration.message.component.EmptyMessageComponent;
import com.wolfeiii.agoniaguilds.utilities.text.Text;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

public class TitleComponent implements IMessageComponent {

    private static final AgoniaGuilds plugin = AgoniaGuilds.getAgoniaGuilds();

    private final String titleMessage;
    private final String subtitleMessage;
    private final int fadeIn;
    private final int duration;
    private final int fadeOut;

    public static IMessageComponent of(@Nullable String titleMessage, @Nullable String subtitleMessage,
                                       int fadeIn, int duration, int fadeOut) {
        return duration <= 0 || (Text.isBlank(titleMessage) && Text.isBlank(subtitleMessage)) ?
                EmptyMessageComponent.getInstance() : new TitleComponent(titleMessage, subtitleMessage, fadeIn, duration, fadeOut);
    }

    private TitleComponent(String titleMessage, String subtitleMessage, int fadeIn, int duration, int fadeOut) {
        this.titleMessage = titleMessage;
        this.subtitleMessage = subtitleMessage;
        this.fadeIn = fadeIn;
        this.duration = duration;
        this.fadeOut = fadeOut;
    }

    @Override
    public Type getType() {
        return Type.TITLE;
    }

    @Override
    public String getMessage() {
        return this.titleMessage;
    }

    @Override
    public void sendMessage(CommandSender sender, Object... args) {
        String titleMessage = Message.replaceArgs(this.titleMessage, args).orElse(null);
        String subtitleMessage = Message.replaceArgs(this.subtitleMessage, args).orElse(null);

        if (titleMessage != null || subtitleMessage != null) {
            ((Player) sender).sendTitle(titleMessage, subtitleMessage,
                    this.fadeIn, this.duration, this.fadeOut);
        }
    }

}