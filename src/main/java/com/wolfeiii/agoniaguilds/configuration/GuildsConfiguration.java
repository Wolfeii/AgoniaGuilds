package com.wolfeiii.agoniaguilds.configuration;

import com.wolfeiii.agoniaguilds.AgoniaGuilds;
import com.wolfeiii.agoniaguilds.configuration.section.DatabaseSection;
import com.wolfeiii.agoniaguilds.utilities.formatters.impl.DateFormatter;
import com.wolfeiii.agoniaguilds.utilities.formatters.impl.NumberFormatter;
import com.wolfeiii.agoniaguilds.utilities.objects.Pair;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

public class GuildsConfiguration {

    private final AgoniaGuilds plugin;

    public final DatabaseSection databaseSection;
    public final String guildCommand;
    public final int defaultTeamLimit;
    public final ConfigurationSection guildRolesSection;
    public final boolean guildTopIncludeLeader;
    public final Map<String, String> defaultPlaceholders;
    public final boolean disbandConfirm;
    public final boolean kickConfirm;
    public final boolean leaveConfirm;
    public final boolean guildNamesRequiredForCreation;
    public final int guildNamesMaxLength;
    public final int guildNamesMinLength;
    public final List<String> filteredGuildNames;
    public final boolean guildNamesColorSupport;
    public final boolean guildNamesGuildTop;
    public final boolean guildNamesPreventPlayerNames;
    public final Map<String, Pair<Integer, String>> commandsCooldown;
    public final String numberFormat;
    public final String dateFormat;
    public final boolean skipOneItemMenus;
    public final boolean onlyBackButton;
    public final List<String> disabledCommands;
    public final Map<String, List<String>> commandAliases;
    public final boolean tabCompleteHideVanished;
    public final long protectedMessageDelay;
    public final int bossBarLimit;

    public GuildsConfiguration(AgoniaGuilds plugin, YamlConfiguration configuration) throws RuntimeException {
        this.plugin = plugin;

        databaseSection = new DatabaseSection(plugin, configuration);
        guildCommand = configuration.getString("guild-command", "guild,is,guilds");
        defaultTeamLimit = configuration.getInt("default-values.team-limit", 4);
        guildRolesSection = configuration.getConfigurationSection("guild-roles");
        guildTopIncludeLeader = configuration.getBoolean("guild-top-include-leader", true);
        defaultPlaceholders = configuration.getStringList("default-placeholders").stream().collect(Collectors.toMap(
                line -> line.split(":")[0].replace("agoniaguilds_", "").toLowerCase(Locale.ENGLISH),
                line -> line.split(":")[1]
        ));
        disbandConfirm = configuration.getBoolean("disband-confirm");
        kickConfirm = configuration.getBoolean("kick-confirm");
        leaveConfirm = configuration.getBoolean("leave-confirm");
        guildNamesRequiredForCreation = configuration.getBoolean("guild-names.required-for-creation", true);
        guildNamesMaxLength = configuration.getInt("guild-names.max-length", 16);
        guildNamesMinLength = configuration.getInt("guild-names.min-length", 3);
        filteredGuildNames = configuration.getStringList("guild-names.filtered-names").stream()
                .map(str -> str.toLowerCase(Locale.ENGLISH))
                .collect(Collectors.toList());
        guildNamesColorSupport = configuration.getBoolean("guild-names.color-support", true);
        guildNamesGuildTop = configuration.getBoolean("guild-names.guild-top", true);
        guildNamesPreventPlayerNames = configuration.getBoolean("guild-names.prevent-player-names", true);
        commandsCooldown = new HashMap<>();
        for (String subCommand : configuration.getConfigurationSection("commands-cooldown").getKeys(false)) {
            int cooldown = configuration.getInt("commands-cooldown." + subCommand + ".cooldown");
            String permission = configuration.getString("commands-cooldown." + subCommand + ".bypass-permission");
            commandsCooldown.put(subCommand, new Pair<>(cooldown, permission));
        }
        numberFormat = configuration.getString("number-format", "en-US");
        NumberFormatter.setNumberFormatter(numberFormat);
        dateFormat = configuration.getString("date-format", "dd/MM/yyyy HH:mm:ss");
        DateFormatter.setDateFormatter(plugin, dateFormat);
        skipOneItemMenus = configuration.getBoolean("skip-one-item-menus", false);
        onlyBackButton = configuration.getBoolean("only-back-button", false);
        disabledCommands = configuration.getStringList("disabled-commands")
                .stream().map(str -> str.toLowerCase(Locale.ENGLISH)).collect(Collectors.toList());
        commandAliases = new HashMap<>();
        if (configuration.isConfigurationSection("command-aliases")) {
            for (String label : configuration.getConfigurationSection("command-aliases").getKeys(false)) {
                commandAliases.put(label.toLowerCase(Locale.ENGLISH), configuration.getStringList("command-aliases." + label));
            }
        }
        tabCompleteHideVanished = configuration.getBoolean("tab-complete-hide-vanished", true);
        protectedMessageDelay = configuration.getLong("protected-message-delay", 60L);
        bossBarLimit = configuration.getInt("bossbar-limit", 1);
    }

    public ConfigurationSection getGuildRolesSection() {
        return guildRolesSection;
    }

    public int getGuildNamesMaxLength() {
        return guildNamesMaxLength;
    }

    public int getGuildNamesMinLength() {
        return guildNamesMinLength;
    }

    public boolean isGuildNamesColorSupport() {
        return guildNamesColorSupport;
    }

    public DatabaseSection getDatabaseSection() {
        return databaseSection;
    }

    public String getGuildCommand() {
        return guildCommand;
    }

    public boolean isGuildTopIncludeLeader() {
        return guildTopIncludeLeader;
    }

    public Map<String, String> getDefaultPlaceholders() {
        return defaultPlaceholders;
    }

    public boolean isDisbandConfirm() {
        return disbandConfirm;
    }

    public boolean isKickConfirm() {
        return kickConfirm;
    }

    public boolean isLeaveConfirm() {
        return leaveConfirm;
    }

    public Map<String, Pair<Integer, String>> getCommandsCooldown() {
        return commandsCooldown;
    }

    public String getNumbersFormat() {
        return numberFormat;
    }

    public String getDateFormat() {
        return dateFormat;
    }

    public boolean isSkipOneItemMenus() {
        return skipOneItemMenus;
    }

    public boolean isOnlyBackButton() {
        return onlyBackButton;
    }

    public List<String> getDisabledCommands() {
        return disabledCommands;
    }

    public Map<String, List<String>> getCommandAliases() {
        return commandAliases;
    }

    public boolean isTabCompleteHideVanished() {
        return tabCompleteHideVanished;
    }

    public long getProtectedMessageDelay() {
        return protectedMessageDelay;
    }

    public int getBossBarLimit() {
        return bossBarLimit;
    }
}
