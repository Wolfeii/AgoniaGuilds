package com.wolfeiii.agoniaguilds.command;

import com.wolfeiii.agoniaguilds.AgoniaGuilds;
import com.wolfeiii.agoniaguilds.command.arguments.CommandArguments;
import com.wolfeiii.agoniaguilds.command.arguments.GuildArgument;
import com.wolfeiii.agoniaguilds.command.arguments.GuildsListArgument;
import com.wolfeiii.agoniaguilds.guild.Guild;
import com.wolfeiii.agoniaguilds.user.GuildUser;
import org.bukkit.command.CommandSender;

import java.util.Collections;
import java.util.List;

public interface IAdminGuildCommand extends IGuildCommand {

    @Override
    default void execute(AgoniaGuilds plugin, CommandSender sender, String... args) {
        if (!supportMultipleGuilds()) {
            GuildArgument guildArgument = CommandArguments.getGuild(plugin, sender, args[2]);
            if (guildArgument.getGuild() != null)
                execute(plugin, sender, guildArgument.getGuildUser(), guildArgument.getGuild(), args);
        } else {
            GuildsListArgument argument = CommandArguments.getMultipleGuilds(plugin, sender, args[2]);
            if (!argument.getGuilds().isEmpty()) {
                execute(plugin, sender, argument.getGuildUser(), argument.getGuilds(), args);
            }
        }
    }


    boolean supportMultipleGuilds();

    default void execute(AgoniaGuilds plugin, CommandSender sender, GuildUser targetPlayer, Guild guild, String[] args) {
        // Not all commands should implement this method.
    }

    default void execute(AgoniaGuilds plugin, CommandSender sender, GuildUser targetPlayer, List<Guild> guilds, String[] args) {
        // Not all commands should implement this method.
    }

    default List<String> adminTabComplete(AgoniaGuilds plugin, CommandSender sender, Guild guild, String[] args) {
        return Collections.emptyList();
    }
}
