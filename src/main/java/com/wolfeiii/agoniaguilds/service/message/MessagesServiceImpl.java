package com.wolfeiii.agoniaguilds.service.message;

import com.google.common.base.Preconditions;
import com.wolfeiii.agoniaguilds.configuration.bossbar.BossBar;
import com.wolfeiii.agoniaguilds.configuration.message.IMessageComponent;
import com.wolfeiii.agoniaguilds.configuration.message.Message;
import com.wolfeiii.agoniaguilds.configuration.message.MessageService;
import com.wolfeiii.agoniaguilds.configuration.message.component.MultipleComponents;
import com.wolfeiii.agoniaguilds.configuration.message.component.impl.*;
import com.wolfeiii.agoniaguilds.utilities.formatters.Formatters;
import com.wolfeiii.agoniaguilds.utilities.sounds.GameSound;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Sound;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

public class MessagesServiceImpl implements MessageService {

    public MessagesServiceImpl() {

    }

    @Nullable
    @Override
    public IMessageComponent parseComponent(YamlConfiguration config, String path) {
        if (config.isConfigurationSection(path)) {
            return MultipleComponents.parseSection(config.getConfigurationSection(path));
        } else {
            return RawMessageComponent.of(Formatters.COLOR_FORMATTER.format(config.getString(path, "")));
        }
    }

    @Nullable
    @Override
    public IMessageComponent getComponent(String messageName) {
        Message message;

        try {
            message = Message.valueOf(messageName.toUpperCase(Locale.ENGLISH));
        } catch (IllegalArgumentException error) {
            // The given name was invalid.
            return null;
        }

        return message.isCustom() ? null : message.getComponent();
    }

    @Override
    public Builder newBuilder() {
        return new BuilderImpl();
    }

    private static class BuilderImpl implements Builder {

        private final List<IMessageComponent> messageComponents = new LinkedList<>();

        @Override
        public boolean addActionBar(@Nullable String message) {
            return addMessageComponent(ActionBarComponent.of(message));
        }

        @Override
        public boolean addBossBar(@Nullable String message, BossBar.Color color, int ticks) {
            return addMessageComponent(BossBarComponent.of(message, color, ticks));
        }

        @Override
        public boolean addComplexMessage(@Nullable TextComponent textComponent) {
            return addComplexMessage(new BaseComponent[]{textComponent});
        }

        @Override
        public boolean addComplexMessage(@Nullable BaseComponent[] baseComponents) {
            return addMessageComponent(ComplexMessageComponent.of(baseComponents));
        }

        @Override
        public boolean addRawMessage(@Nullable String message) {
            return addMessageComponent(RawMessageComponent.of(message));
        }

        @Override
        public boolean addSound(Sound sound, float volume, float pitch) {
            return addMessageComponent(SoundComponent.of(new GameSound(sound, volume, pitch)));
        }

        @Override
        public boolean addTitle(@Nullable String titleMessage, @Nullable String subtitleMessage, int fadeIn, int duration, int fadeOut) {
            return addMessageComponent(TitleComponent.of(titleMessage, subtitleMessage, fadeIn, duration, fadeOut));
        }

        @Override
        public boolean addMessageComponent(IMessageComponent messageComponent) {
            Preconditions.checkNotNull(messageComponent, "Cannot add null message components.");

            if (messageComponent.getType() != IMessageComponent.Type.EMPTY) {
                messageComponents.add(messageComponent);
                return true;
            }

            return false;
        }

        @Override
        public IMessageComponent build() {
            return MultipleComponents.of(messageComponents);
        }

    }


}
