package com.wolfeiii.agoniaguilds.command.arguments;

import com.wolfeiii.agoniaguilds.AgoniaGuilds;
import com.wolfeiii.agoniaguilds.configuration.message.Message;
import com.wolfeiii.agoniaguilds.guild.Guild;
import com.wolfeiii.agoniaguilds.user.GuildUser;
import com.wolfeiii.agoniaguilds.utilities.formatters.Formatters;
import org.bukkit.command.CommandSender;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class CommandArguments {

    private CommandArguments() {

    }

    public static GuildArgument getGuild(AgoniaGuilds plugin, CommandSender sender, String argument) {
        GuildUser targetPlayer = plugin.getUserManager().getGuildUser(argument);
        Guild guild = targetPlayer == null ? plugin.getGuildManager().getGuild(argument) : targetPlayer.getGuild();

        if (guild == null) {
            if (argument.equalsIgnoreCase(sender.getName()))
                Message.INVALID_GUILD.send(sender);
            else if (targetPlayer == null)
                Message.INVALID_GUILD_OTHER_NAME.send(sender, Formatters.STRIP_COLOR_FORMATTER.format(argument));
            else
                Message.INVALID_GUILD_OTHER.send(sender, targetPlayer.getName());
        }

        return new GuildArgument(guild, targetPlayer);
    }

    public static GuildsListArgument getMultipleGuilds(AgoniaGuilds plugin, CommandSender sender, String argument) {
        List<Guild> guilds = new LinkedList<>();
        GuildUser targetPlayer;

        if (argument.equals("*")) {
            targetPlayer = null;
            guilds = plugin.getGuildManager().getGuilds();
        } else {
            GuildArgument arguments = getGuild(plugin, sender, argument);
            targetPlayer = arguments.getGuildUser();
            if (arguments.getGuild() != null)
                guilds.add(arguments.getGuild());
        }

        return new GuildsListArgument(Collections.unmodifiableList(guilds), targetPlayer);
    }

    public static GuildArgument getSenderGuild(AgoniaGuilds plugin, CommandSender sender) {
        GuildUser guildUser = plugin.getUserManager().getGuildUser(sender);
        Guild guild = guildUser.getGuild();

        if (guild == null)
            Message.INVALID_GUILD.send(guildUser);

        return new GuildArgument(guild, guildUser);
    }

    public static GuildUser getPlayer(AgoniaGuilds plugin, GuildUser guildUser, String argument) {
        return getPlayer(plugin, guildUser.asPlayer(), argument);
    }

    public static GuildUser getPlayer(AgoniaGuilds plugin, CommandSender sender, String argument) {
        GuildUser targetPlayer = plugin.getUserManager().getGuildUser(argument);

        if (targetPlayer == null)
            Message.INVALID_PLAYER.send(sender, argument);

        return targetPlayer;
    }

    public static List<GuildUser> getMultiplePlayers(AgoniaGuilds plugin, CommandSender sender, String argument) {
        List<GuildUser> players = new LinkedList<>();

        if (argument.equals("*")) {
            players = plugin.getUserManager().getAllPlayers();
        } else {
            GuildUser targetPlayer = getPlayer(plugin, sender, argument);
            if (targetPlayer != null)
                players.add(targetPlayer);
        }

        return Collections.unmodifiableList(players);
    }


}
