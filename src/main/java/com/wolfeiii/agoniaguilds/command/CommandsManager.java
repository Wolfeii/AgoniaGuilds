package com.wolfeiii.agoniaguilds.command;

import com.google.common.base.Preconditions;
import com.wolfeiii.agoniaguilds.AgoniaGuilds;
import com.wolfeiii.agoniaguilds.command.admin.AdminCommandsMap;
import com.wolfeiii.agoniaguilds.command.player.PlayerCommandsMap;
import com.wolfeiii.agoniaguilds.configuration.message.Message;
import com.wolfeiii.agoniaguilds.guild.Guild;
import com.wolfeiii.agoniaguilds.user.GuildUser;
import com.wolfeiii.agoniaguilds.utilities.Utilities;
import com.wolfeiii.agoniaguilds.utilities.formatters.Formatters;
import com.wolfeiii.agoniaguilds.utilities.logging.Debug;
import com.wolfeiii.agoniaguilds.utilities.logging.Log;
import com.wolfeiii.agoniaguilds.utilities.objects.Pair;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.defaults.BukkitCommand;
import org.bukkit.craftbukkit.v1_19_R3.CraftServer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.lang.reflect.Constructor;
import java.time.Duration;
import java.util.*;

public class CommandsManager {

    private final AgoniaGuilds plugin;

    private final Map<UUID, Map<String, Long>> commandsCooldown = new HashMap<>();

    private final CommandsMap playerCommandsMap;
    private final CommandsMap adminCommandsMap;

    private Set<Runnable> pendingCommands = new HashSet<>();

    private PluginCommand pluginCommand;
    private String label = null;

    public CommandsManager(AgoniaGuilds plugin) {
        this.plugin = plugin;
        this.playerCommandsMap = new PlayerCommandsMap(plugin);
        this.adminCommandsMap = new AdminCommandsMap(plugin);
    }

    public void initializeCommands() {
        String guildCommand = plugin.getConfiguration().getGuildCommand();
        label = guildCommand.split(",")[0];

        pluginCommand = new PluginCommand(label);

        String[] commandSections = guildCommand.split(",");

        if (commandSections.length > 1) {
            pluginCommand.setAliases(Arrays.asList(Arrays.copyOfRange(commandSections, 1, commandSections.length)));
        }

        Utilities.registerCommand(pluginCommand);

        playerCommandsMap.loadDefaultCommands();
        adminCommandsMap.loadDefaultCommands();

        if (this.pendingCommands != null) {
            Set<Runnable> pendingCommands = new HashSet<>(this.pendingCommands);
            this.pendingCommands = null;
            pendingCommands.forEach(Runnable::run);
        }
    }

    public void registerCommand(IGuildCommand guildCommand) {
        Preconditions.checkNotNull(guildCommand, "guildCommand parameter cannot be null.");
        registerCommand(guildCommand, true);
    }

    public void unregisterCommand(IGuildCommand guildCommand) {
        playerCommandsMap.unregisterCommand(guildCommand);
    }

    public void registerAdminCommand(IGuildCommand guildCommand) {
        if (pendingCommands != null) {
            pendingCommands.add(() -> registerAdminCommand(guildCommand));
            return;
        }

        Preconditions.checkNotNull(guildCommand, "guildCommand parameter cannot be null.");
        adminCommandsMap.registerCommand(guildCommand, true);
    }

    public void unregisterAdminCommand(IGuildCommand guildCommand) {
        Preconditions.checkNotNull(guildCommand, "guildCommand parameter cannot be null.");
        adminCommandsMap.unregisterCommand(guildCommand);
    }

    public List<IGuildCommand> getSubCommands() {
        return getSubCommands(false);
    }

    public List<IGuildCommand> getSubCommands(boolean includeDisabled) {
        return playerCommandsMap.getSubCommands(includeDisabled);
    }

    @Nullable
    public IGuildCommand getCommand(String commandLabel) {
        return playerCommandsMap.getCommand(commandLabel);
    }

    public List<IGuildCommand> getAdminSubCommands() {
        return adminCommandsMap.getSubCommands(true);
    }

    @Nullable
    public IGuildCommand getAdminCommand(String commandLabel) {
        return adminCommandsMap.getCommand(commandLabel);
    }

    public void dispatchSubCommand(CommandSender sender, String subCommand) {
        dispatchSubCommand(sender, subCommand, "");
    }

    public void dispatchSubCommand(CommandSender sender, String subCommand, String args) {
        String[] argsSplit = args.split(" ");
        String[] commandArguments;

        if (argsSplit.length == 1 && argsSplit[0].isEmpty()) {
            commandArguments = new String[1];
            commandArguments[0] = subCommand;
        } else {
            commandArguments = new String[argsSplit.length + 1];
            commandArguments[0] = subCommand;
            System.arraycopy(argsSplit, 0, commandArguments, 1, argsSplit.length);
        }

        pluginCommand.execute(sender, "", commandArguments);
    }

    public String getLabel() {
        return label;
    }

    public void registerCommand(IGuildCommand guildCommand, boolean sort) {
        if (pendingCommands != null) {
            pendingCommands.add(() -> registerCommand(guildCommand, sort));
            return;
        }

        playerCommandsMap.registerCommand(guildCommand, sort);
    }

    private class PluginCommand extends BukkitCommand {

        PluginCommand(String guildCommandLabel) {
            super(guildCommandLabel);
        }

        @Override
        public boolean execute(CommandSender sender, String label, String[] args) {
            if (args.length > 0) {
                Log.debug(Debug.EXECUTE_COMMAND, sender.getName(), args[0]);

                IGuildCommand command = playerCommandsMap.getCommand(args[0]);
                if (command != null) {
                    if (!(sender instanceof Player) && !command.canBeExecutedByConsole()) {
                        Message.CUSTOM.send(sender, "&cCan be executed only by players!", true);
                        return false;
                    }

                    if (!command.getPermission().isEmpty() && !sender.hasPermission(command.getPermission())) {
                        Log.debugResult(Debug.EXECUTE_COMMAND, "Return Missing Permission", command.getPermission());
                        Message.NO_COMMAND_PERMISSION.send(sender);
                        return false;
                    }

                    if (args.length < command.getMinArgs() || args.length > command.getMaxArgs()) {
                        Log.debugResult(Debug.EXECUTE_COMMAND, "Return Incorrect Usage", command.getUsage());
                        Message.COMMAND_USAGE.send(sender, getLabel() + " " + command.getUsage());
                        return false;
                    }

                    if (sender instanceof Player) {
                        UUID uuid = ((Player) sender).getUniqueId();
                        GuildUser guildUser = plugin.getUserManager().getGuildUser(uuid);
                        if (!guildUser.hasPermission("agonia.guilds.admin.bypass.cooldowns")) {
                            Pair<Integer, String> commandCooldown = getCooldown(command);
                            if (commandCooldown != null) {
                                String commandLabel = command.getAliases().get(0);

                                Map<String, Long> playerCooldowns = commandsCooldown.get(uuid);
                                long timeNow = System.currentTimeMillis();

                                if (playerCooldowns != null) {
                                    Long timeToExecute = playerCooldowns.get(commandLabel);
                                    if (timeToExecute != null) {
                                        if (timeNow < timeToExecute) {
                                            String formattedTime = Formatters.TIME_FORMATTER.format(Duration.ofMillis(timeToExecute - timeNow));
                                            Log.debugResult(Debug.EXECUTE_COMMAND, "Return Cooldown", formattedTime);
                                            Message.COMMAND_COOLDOWN_FORMAT.send(sender, formattedTime);
                                            return false;
                                        }
                                    }
                                }

                                commandsCooldown.computeIfAbsent(uuid, u -> new HashMap<>()).put(commandLabel,
                                        timeNow + commandCooldown.getKey());
                            }
                        }
                    }

                    command.execute(plugin, sender, args);
                    return false;
                }
            }

            if (sender instanceof Player) {
                GuildUser guildUser = plugin.getUserManager().getGuildUser(sender);

                if (guildUser != null) {
                    Guild guild = guildUser.getGuild();

                    if (args.length != 0) {
                        Bukkit.dispatchCommand(sender, label + " help");
                    } else if (guild == null) {
                        Bukkit.dispatchCommand(sender, label + " create");
                    } else {
                        Bukkit.dispatchCommand(sender, label + " panel");
                    }

                    return false;
                }
            }

            Message.NO_COMMAND_PERMISSION.send(sender);

            return false;
        }

        @Override
        public List<String> tabComplete(CommandSender sender, String label, String[] args) {
            if (args.length > 0) {
                IGuildCommand command = playerCommandsMap.getCommand(args[0]);
                if (command != null) {
                    return command.getPermission() != null && !sender.hasPermission(command.getPermission()) ?
                            Collections.emptyList() : command.tabComplete(plugin, sender, args);
                }
            }

            List<String> list = new LinkedList<>();

            for (IGuildCommand subCommand : getSubCommands()) {
                if (subCommand.getPermission() == null || sender.hasPermission(subCommand.getPermission())) {
                    List<String> aliases = new LinkedList<>(subCommand.getAliases());
                    aliases.addAll(plugin.getConfiguration().getCommandAliases().getOrDefault(aliases.get(0).toLowerCase(Locale.ENGLISH), Collections.emptyList()));
                    for (String _aliases : aliases) {
                        if (_aliases.contains(args[0].toLowerCase(Locale.ENGLISH))) {
                            list.add(_aliases);
                        }
                    }
                }
            }

            return list;
        }

    }

    @Nullable
    private Pair<Integer, String> getCooldown(IGuildCommand command) {
        for (String alias : command.getAliases()) {
            Pair<Integer, String> commandCooldown = plugin.getConfiguration().getCommandsCooldown().get(alias);
            if (commandCooldown != null)
                return commandCooldown;
        }

        return null;
    }


}
