package com.wolfeiii.agoniaguilds.command.player;

import com.wolfeiii.agoniaguilds.AgoniaGuilds;
import com.wolfeiii.agoniaguilds.command.CommandTabCompletes;
import com.wolfeiii.agoniaguilds.command.IPermissibleCommand;
import com.wolfeiii.agoniaguilds.command.arguments.CommandArguments;
import com.wolfeiii.agoniaguilds.configuration.message.Message;
import com.wolfeiii.agoniaguilds.guild.Guild;
import com.wolfeiii.agoniaguilds.guild.privilege.GuildPrivilege;
import com.wolfeiii.agoniaguilds.guild.privilege.GuildPrivileges;
import com.wolfeiii.agoniaguilds.guild.roles.PlayerRole;
import com.wolfeiii.agoniaguilds.user.GuildUser;
import org.bukkit.command.CommandSender;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class CommandDemote implements IPermissibleCommand {
    @Override
    public List<String> getAliases() {
        return Collections.singletonList("demote");
    }

    @Override
    public String getPermission() {
        return "agonia.guild.demote";
    }

    @Override
    public String getUsage() {
        return "demote <" + Message.COMMAND_ARGUMENT_PLAYER_NAME.getMessage() + ">";
    }

    @Override
    public String getDescription() {
        return Message.COMMAND_DESCRIPTION_DEMOTE.getMessage();
    }

    @Override
    public int getMinArgs() {
        return 2;
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
    public GuildPrivilege getPrivilege() {
        return GuildPrivileges.DEMOTE_MEMBERS;
    }

    @Override
    public Message getPermissionLackMessage() {
        return Message.NO_DEMOTE_PERMISSION;
    }

    @Override
    public void execute(AgoniaGuilds plugin, GuildUser guildUser, Guild guild, String[] args) {
        GuildUser targetPlayer = CommandArguments.getPlayer(plugin, guildUser, args[1]);

        if (targetPlayer == null)
            return;

        if (!guild.isMember(targetPlayer)) {
            Message.PLAYER_NOT_IN_GUILD.send(guildUser);
            return;
        }

        if (!targetPlayer.getPlayerRole().isLessThan(guildUser.getPlayerRole())) {
            Message.DEMOTE_PLAYERS_WITH_LOWER_ROLE.send(guildUser);
            return;
        }

        PlayerRole previousRole = targetPlayer.getPlayerRole().getPreviousRole();

        if (previousRole == null) {
            Message.LAST_ROLE_DEMOTE.send(guildUser);
            return;
        }

        targetPlayer.setPlayerRole(previousRole);

        Message.DEMOTED_MEMBER.send(guildUser, targetPlayer.getName(), targetPlayer.getPlayerRole());
        Message.GOT_DEMOTED.send(targetPlayer, targetPlayer.getPlayerRole());
    }

    @Override
    public List<String> tabComplete(AgoniaGuilds plugin, GuildUser guildUser, Guild guild, String[] args) {
        return args.length != 2 ? Collections.emptyList() : CommandTabCompletes.getGuildMembers(guild, args[1], guildMember ->
                guildMember.getPlayerRole().isLessThan(guildUser.getPlayerRole()) &&
                        guildMember.getPlayerRole().getPreviousRole() != null);
    }
}
