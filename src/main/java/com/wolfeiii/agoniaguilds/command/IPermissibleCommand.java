package com.wolfeiii.agoniaguilds.command;

import com.wolfeiii.agoniaguilds.AgoniaGuilds;
import com.wolfeiii.agoniaguilds.command.arguments.CommandArguments;
import com.wolfeiii.agoniaguilds.command.arguments.GuildArgument;
import com.wolfeiii.agoniaguilds.configuration.message.Message;
import com.wolfeiii.agoniaguilds.guild.Guild;
import com.wolfeiii.agoniaguilds.guild.GuildUtils;
import com.wolfeiii.agoniaguilds.guild.privilege.GuildPrivilege;
import com.wolfeiii.agoniaguilds.user.GuildUser;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;

public interface IPermissibleCommand extends IGuildCommand {

    @Override
    default void execute(AgoniaGuilds plugin, CommandSender sender, String[] args) {
        Guild guild = null;
        GuildUser guildUser = null;

        if (!canBeExecutedByConsole() || sender instanceof Player) {
            GuildArgument arguments = CommandArguments.getSenderGuild(plugin, sender);

            guild = arguments.getGuild();

            if (guild == null)
                return;

            guildUser = arguments.getGuildUser();

            if (!guildUser.hasPermission(getPrivilege())) {
                getPermissionLackMessage().send(guildUser, guild.getRequiredPlayerRole(getPrivilege()));
                return;
            }
        }

        execute(plugin, guildUser, guild, args);
    }

    @Override
    default List<String> tabComplete(AgoniaGuilds plugin, CommandSender sender, String[] args) {
        Guild guild = null;
        GuildUser guildUser = null;

        if (!canBeExecutedByConsole() || sender instanceof Player) {
            guildUser = plugin.getUserManager().getGuildUser(sender);
            guild = guildUser.getGuild();
        }

        return guildUser == null || (guild != null && guildUser.hasPermission(getPrivilege())) ?
                tabComplete(plugin, guildUser, guild, args) : Collections.emptyList();
    }

    GuildPrivilege getPrivilege();

    Message getPermissionLackMessage();

    void execute(AgoniaGuilds plugin, GuildUser guildUser, Guild guild, String[] args);

    default List<String> tabComplete(AgoniaGuilds plugin, GuildUser guildUser, Guild guild, String[] args) {
        return Collections.emptyList();
    }

}