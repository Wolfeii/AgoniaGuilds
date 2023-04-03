package com.wolfeiii.agoniaguilds.command;

import com.wolfeiii.agoniaguilds.AgoniaGuilds;
import com.wolfeiii.agoniaguilds.command.arguments.CommandArguments;
import com.wolfeiii.agoniaguilds.configuration.message.Message;
import com.wolfeiii.agoniaguilds.guild.Guild;
import com.wolfeiii.agoniaguilds.user.GuildUser;
import org.bukkit.command.CommandSender;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public interface IAdminPlayerCommand extends IGuildCommand {

    @Override
    default void execute(AgoniaGuilds plugin, CommandSender sender, String... args) {
        if (!supportMultiplePlayers()) {
            GuildUser targetUser = CommandArguments.getPlayer(plugin, sender, args[2]);
            if (targetUser != null) {
                Guild userGuild = targetUser.getGuild();

                if (requireGuild() && userGuild == null) {
                    Message.INVALID_GUILD_OTHER.send(sender, targetUser.getName());
                    return;
                }

                execute(plugin, sender, targetUser, args);
            }
        } else {
            List<GuildUser> players = CommandArguments.getMultiplePlayers(plugin, sender, args[2]);
            if (!players.isEmpty())
                execute(plugin, sender, players, args);
        }
    }

    @Override
    default List<String> tabComplete(AgoniaGuilds plugin, CommandSender sender, String[] args) {
        List<String> tabVariables = new LinkedList<>();

        if (args.length == 3) {
            if (supportMultiplePlayers() && "*".contains(args[2]))
                tabVariables.add("*");

            tabVariables.addAll(CommandTabCompletes.getOnlinePlayers(plugin, args[2], false));
        } else if (args.length > 3) {
            if (supportMultiplePlayers()) {
                tabVariables = adminTabComplete(plugin, sender, null, args);
            } else {
                GuildUser targetPlayer = plugin.getUserManager().getGuildUser(args[2]);
                if (targetPlayer != null) {
                    tabVariables = adminTabComplete(plugin, sender, targetPlayer, args);
                }
            }
        }

        return Collections.unmodifiableList(tabVariables);
    }

    boolean supportMultiplePlayers();

    default boolean requireGuild() {
        return false;
    }


    default void execute(AgoniaGuilds plugin, CommandSender sender, GuildUser targetPlayer, String[] args) {
        // Not all commands should implement this method.
    }

    default void execute(AgoniaGuilds plugin, CommandSender sender, List<GuildUser> targetPlayers, String[] args) {
        // Not all commands should implement this method.
    }

    default List<String> adminTabComplete(AgoniaGuilds plugin, CommandSender sender, GuildUser targetPlayer, String[] args) {
        return Collections.emptyList();
    }


}
