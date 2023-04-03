package com.wolfeiii.agoniaguilds.command.player;

import com.wolfeiii.agoniaguilds.AgoniaGuilds;
import com.wolfeiii.agoniaguilds.command.CommandTabCompletes;
import com.wolfeiii.agoniaguilds.command.IGuildCommand;
import com.wolfeiii.agoniaguilds.configuration.message.Message;
import com.wolfeiii.agoniaguilds.guild.Guild;
import com.wolfeiii.agoniaguilds.guild.GuildUtils;
import com.wolfeiii.agoniaguilds.guild.roles.PlayerRole;
import com.wolfeiii.agoniaguilds.user.GuildUser;
import org.bukkit.command.CommandSender;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class CommandAccept implements IGuildCommand {
    @Override
    public List<String> getAliases() {
        return Arrays.asList("accept", "acceptera", "join", "anslut");
    }

    @Override
    public String getPermission() {
        return "agonia.guild.accept";
    }

    @Override
    public String getUsage() {
        return "accept [" +
                Message.COMMAND_ARGUMENT_PLAYER_NAME.getMessage() + "/" +
                Message.COMMAND_ARGUMENT_GUILD_NAME.getMessage() + "]";

    }

    @Override
    public String getDescription() {
        return Message.COMMAND_DESCRIPTION_ACCEPT.getMessage();
    }

    @Override
    public int getMinArgs() {
        return 1;
    }

    @Override
    public int getMaxArgs() {
        return 2;
    }

    @Override
    public boolean canBeExecutedByConsole() {
        return false;
    }

    @Override
    public void execute(AgoniaGuilds plugin, CommandSender sender, String... args) {
        GuildUser guildUser = plugin.getUserManager().getGuildUser(sender);

        GuildUser targetPlayer;
        Guild guild;

        if (args.length == 1) {
            List<Guild> pendingInvites = guildUser.getInvites();
            guild = pendingInvites.isEmpty() ? null : pendingInvites.get(0);
            targetPlayer = null;
        } else {
            targetPlayer = plugin.getUserManager().getGuildUser(args[1]);
            guild = targetPlayer == null ? plugin.getGuildManager().getGuild(args[1]) : targetPlayer.getGuild();
        }

        if (guild == null || !guild.isInvited(guildUser)) {
            Message.NO_GUILD_INVITE.send(guildUser);
            return;
        }

        if (guildUser.getGuild() != null) {
            Message.JOIN_WHILE_IN_GUILD.send(guildUser);
            return;
        }

        if (guild.getTeamLimit() >= 0 && guild.getGuildMembers(true).size() >= guild.getTeamLimit()) {
            Message.JOIN_FULL_GUILD.send(guildUser);
            guild.revokeInvite(guildUser);
            return;
        }

        GuildUtils.sendMessage(guild, Message.JOIN_ANNOUNCEMENT, Collections.emptyList(), guildUser.getName());

        guild.revokeInvite(guildUser);
        guild.addMember(guildUser, PlayerRole.defaultRole());

        if (targetPlayer == null) {
            Message.JOINED_GUILD_NAME.send(guildUser, guild.getName());
        } else{
            Message.JOINED_GUILD.send(guildUser, targetPlayer.getName());
        }
    }

    @Override
    public List<String> tabComplete(AgoniaGuilds plugin, CommandSender sender, String[] args) {
        GuildUser guildUser = plugin.getUserManager().getGuildUser(sender);
        return args.length == 2 ? CommandTabCompletes.getOnlinePlayersWithGuilds(plugin, args[1],
                true, (onlinePlayer, onlineGuild) ->
                        onlineGuild != null && onlineGuild.isInvited(guildUser)) : Collections.emptyList();
    }
}
