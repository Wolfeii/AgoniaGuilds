package com.wolfeiii.agoniaguilds.configuration.message;

import com.wolfeiii.agoniaguilds.AgoniaGuilds;
import com.wolfeiii.agoniaguilds.configuration.CommentedConfiguration;
import com.wolfeiii.agoniaguilds.guild.Guild;
import com.wolfeiii.agoniaguilds.user.GuildUser;
import com.wolfeiii.agoniaguilds.utilities.formatters.Formatters;
import com.wolfeiii.agoniaguilds.utilities.logging.Log;
import com.wolfeiii.agoniaguilds.utilities.text.Text;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;

public enum Message {

    ADMIN_ADD_PLAYER,
    ADMIN_ADD_PLAYER_NAME,
    ADMIN_HELP_FOOTER,
    ADMIN_HELP_HEADER,
    ADMIN_HELP_LINE,
    ADMIN_HELP_NEXT_PAGE,
    ALREADY_IN_GUILD,
    ALREADY_IN_GUILD_OTHER,
    CHANGED_NAME,
    CHANGED_NAME_OTHER,
    CHANGED_NAME_OTHER_NAME,
    CHANGE_PERMISSION_FOR_HIGHER_ROLE,
    COMMAND_ARGUMENT_ALL_GUILDS("*"),
    COMMAND_ARGUMENT_ALL_PLAYERS("*"),
    COMMAND_ARGUMENT_AMOUNT("antal"),
    COMMAND_ARGUMENT_COMMAND("command..."),
    COMMAND_ARGUMENT_GUILD_NAME("guild-name"),
    COMMAND_ARGUMENT_GUILD_ROLE("guild-role"),
    COMMAND_ARGUMENT_LEADER("leader"),
    COMMAND_ARGUMENT_NEW_LEADER("new-leader"),
    COMMAND_ARGUMENT_PERMISSION("permission"),
    COMMAND_ARGUMENT_PLAYER_NAME("player-name"),
    COMMAND_COOLDOWN_FORMAT,
    COMMAND_DESCRIPTION_ACCEPT,
    COMMAND_DESCRIPTION_ADMIN,
    COMMAND_DESCRIPTION_ADMIN_ADD,
    COMMAND_DESCRIPTION_ADMIN_IGNORE,
    COMMAND_DESCRIPTION_ADMIN_JOIN,
    COMMAND_DESCRIPTION_ADMIN_KICK,
    COMMAND_DESCRIPTION_ADMIN_NAME,
    COMMAND_DESCRIPTION_ADMIN_PROMOTE,
    COMMAND_DESCRIPTION_ADMIN_RELOAD,
    COMMAND_DESCRIPTION_CREATE,
    COMMAND_DESCRIPTION_DEMOTE,
    COMMAND_DESCRIPTION_LEAVE,
    COMMAND_DESCRIPTION_KICK,
    COMMAND_DESCRIPTION_NAME,
    COMMAND_DESCRIPTION_PROMOTE,
    COMMAND_USAGE,
    CREATE_GUILD,
    CREATE_GUILD_FAILURE,
    DEBUG_MODE_DISABLED,
    DEBUG_MODE_ENABLED,
    DEBUG_MODE_FILTER_ADD,
    DEBUG_MODE_FILTER_CLEAR,
    DEBUG_MODE_FILTER_REMOVE,
    DEMOTED_MEMBER,
    DEMOTE_LEADER,
    DEMOTE_PLAYERS_WITH_LOWER_ROLE,
    DISBANDED_GUILD,
    DISBANDED_GUILD_OTHER,
    DISBANDED_GUILD_OTHER_NAME,
    DISBANDED_ANNOUNCEMENT,
    FORMAT_DAYS_NAME,
    FORMAT_DAY_NAME,
    FORMAT_HOURS_NAME,
    FORMAT_HOUR_NAME,
    FORMAT_MILLION,
    FORMAT_MINUTES_NAME,
    FORMAT_MINUTE_NAME,
    FORMAT_QUAD,
    FORMAT_SECONDS_NAME,
    FORMAT_SECOND_NAME,
    FORMAT_THOUSANDS,
    FORMAT_TRILLION,
    GOT_DEMOTED,
    GOT_INVITE,
    GOT_INVITE_TOOLTIP,
    GOT_KICKED,
    GOT_PROMOTED,
    GOT_REVOKED,
    HIT_GUILD_MEMBER,
    IGNORED_GUILD,
    IGNORED_GUILD_NAME,
    INVALID_AMOUNT,
    INVALID_GUILD,
    INVALID_GUILD_OTHER,
    INVALID_GUILD_OTHER_NAME,
    INVALID_GUILD_PERMISSION,
    INVALID_LEVEL,
    INVALID_PLAYER,
    INVALID_ROLE,
    INVITE_ANNOUNCEMENT,
    INVITE_TO_FULL_GUILD,
    GUILD_ALREADY_EXISTS,
    GUILD_CREATE_PROCESS_REQUEST,
    GUILD_CREATE_PROCESS_FAIL,
    GUILD_HELP_FOOTER,
    GUILD_HELP_HEADER,
    GUILD_HELP_LINE,
    GUILD_HELP_NEXT_PAGE,
    JOINED_GUILD,
    JOINED_GUILD_NAME,
    JOIN_ADMIN_ANNOUNCEMENT,
    JOIN_ANNOUNCEMENT,
    JOIN_FULL_GUILD,
    JOIN_WHILE_IN_GUILD,
    KICK_ANNOUNCEMENT,
    KICK_GUILD_LEADER,
    KICK_PLAYERS_WITH_LOWER_ROLE,
    LAST_CHANGE_PERMISSION,
    LAST_ROLE_DEMOTE,
    LAST_ROLE_PROMOTE,
    LEAVE_ANNOUNCEMENT,
    LEAVE_GUILD_AS_LEADER,
    LEFT_GUILD,
    OPEN_MENU_WHILE_SLEEPING,
    MAXIMUM_LEVEL,
    NAME_ANNOUNCEMENT,
    NAME_BLACKLISTED,
    NAME_SAME_AS_PLAYER,
    NAME_TOO_LONG,
    NAME_TOO_SHORT,
    NOT_ENOUGH_MONEY,
    NO_COMMAND_PERMISSION,
    NO_DEMOTE_PERMISSION,
    NO_DISBAND_PERMISSION,
    NO_INVITE_PERMISSION,
    NO_GUILD_INVITE,
    NO_KICK_PERMISSION,
    NO_PROMOTE_PERMISSION,
    PLACEHOLDER_NO,
    PLACEHOLDER_YES,
    PLAYER_ALREADY_IN_GUILD,
    PLAYER_ALREADY_IN_ROLE,
    PLAYER_NOT_ONLINE,
    PLAYER_NOT_IN_GUILD,
    PROMOTED_MEMBER,
    PROMOTE_PLAYERS_WITH_LOWER_ROLE,
    RELOAD_COMPLETED,
    RELOAD_PROCCESS_REQUEST,
    RANKUP_SUCCESS,
    RANKUP_SUCCESS_ALL,
    RANKUP_SUCCESS_NAME,
    REVOKE_INVITE_ANNOUNCEMENT,
    CUSTOM(true) {
        @Override
        public void send(CommandSender sender, Object... args) {
            String message = args.length == 0 ? null : args[0] == null ? null : args[0].toString();
            boolean translateColors = args.length >= 2 && args[1] instanceof Boolean && (boolean) args[1];
            if (!Text.isBlank(message))
                sender.sendMessage(translateColors ? Formatters.COLOR_FORMATTER.format(message) : message);
        }

    };

    private static final AgoniaGuilds plugin = AgoniaGuilds.getAgoniaGuilds();

    private final String defaultMessage;
    private final boolean isCustom;
    private IMessageComponent message;

    Message() {
        this(null);
    }

    Message(boolean isCustom) {
        this(null, isCustom);
    }

    Message(String defaultMessage) {
        this(defaultMessage, false);
    }

    Message(String defaultMessage, boolean isCustom) {
        this.defaultMessage = defaultMessage;
        this.isCustom = isCustom;
    }

    public static void reload() {
        Log.info("Loading messages started...");
        long startTime = System.currentTimeMillis();

        convertOldFile();

        File langFolder = new File(plugin.getDataFolder(), "lang");

        if (!langFolder.exists()) {
            plugin.saveResource("lang/de-DE.yml", false);
            plugin.saveResource("lang/en-US.yml", false);
            plugin.saveResource("lang/es-ES.yml", false);
            plugin.saveResource("lang/fr-FR.yml", false);
            plugin.saveResource("lang/it-IT.yml", false);
            plugin.saveResource("lang/iw-IL.yml", false);
            plugin.saveResource("lang/pl-PL.yml", false);
            plugin.saveResource("lang/vi-VN.yml", false);
            plugin.saveResource("lang/zh-CN.yml", false);
        }

        int messagesAmount = 0;
        boolean countMessages = true;

        File langFile = new File(plugin.getDataFolder(), "lang/locale.yml");

        CommentedConfiguration cfg = CommentedConfiguration.loadConfiguration(langFile);
        InputStream inputStream = plugin.getResource("lang/" + langFile.getName());

        try {
            cfg.syncWithConfig(langFile, inputStream == null ? plugin.getResource("lang/locale.yml") : inputStream, "lang/locale.yml");
        } catch (Exception error) {
            Log.error(error, "An unexpected error occurred while saving lang file ", langFile.getName(), ":");
        }

        for (Message locale : values()) {
            if (!locale.isCustom()) {
                locale.setMessage(plugin.getServicesHandler().getMessageService().parseComponent(cfg, locale.name()));
                if (countMessages)
                    messagesAmount++;
            }
        }

        countMessages = false;

        Log.info(" - Found " + messagesAmount + " messages in the language files.");
        Log.info("Loading messages done (Took " + (System.currentTimeMillis() - startTime) + "ms)");
    }

    public boolean isCustom() {
        return isCustom;
    }

    public boolean isEmpty() {
        IMessageComponent messageContainer = getComponent();
        return messageContainer == null || messageContainer.getType() == IMessageComponent.Type.EMPTY ||
                messageContainer.getMessage().isEmpty();
    }

    @Nullable
    public IMessageComponent getComponent() {
        return message;
    }

    @Nullable
    public String getMessage(Object... objects) {
        return isEmpty() ? defaultMessage : replaceArgs(message.getMessage(), objects).orElse(null);
    }

    public final void send(GuildUser guildUser, Object... args) {
        guildUser.runIfOnline(player -> send(player, args));
    }

    public void send(CommandSender sender, Object... objects) {
        IMessageComponent messageComponent = getComponent();
        if (messageComponent != null) {
            messageComponent.sendMessage(sender, objects);
        }
    }

    private void setMessage(IMessageComponent messageComponent) {
        this.message = messageComponent;
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    private static void convertOldFile() {
        File file = new File(plugin.getDataFolder(), "lang.yml");
        if (file.exists()) {
            File dest = new File(plugin.getDataFolder(), "lang/en-US.yml");
            dest.getParentFile().mkdirs();
            file.renameTo(dest);
        }
    }

    public static Optional<String> replaceArgs(String msg, Object... objects) {
        if (Text.isBlank(msg))
            return Optional.empty();

        for (int i = 0; i < objects.length; i++) {
            String objectString = objects[i] instanceof BigDecimal ?
                    Formatters.NUMBER_FORMATTER.format((BigDecimal) objects[i]) : objects[i].toString();
            msg = msg.replace("{" + i + "}", objectString);
        }

        return msg.isEmpty() ? Optional.empty() : Optional.of(msg);
    }
}
