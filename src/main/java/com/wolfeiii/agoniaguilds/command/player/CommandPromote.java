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

import java.util.Collections;
import java.util.List;

public class CommandPromote implements IPermissibleCommand {
    @Override
    public List<String> getAliases() {
        return Collections.singletonList("promote");
    }

    @Override
    public String getPermission() {
        return "agonia.guild.promote";
    }

    @Override
    public String getUsage() {
        return "promote <" + Message.COMMAND_ARGUMENT_PLAYER_NAME + ">";
    }

    @Override
    public String getDescription() {
        return Message.COMMAND_DESCRIPTION_PROMOTE.getMessage();
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
        return GuildPrivileges.PROMOTE_MEMBERS;
    }

    @Override
    public Message getPermissionLackMessage() {
        return Message.NO_PROMOTE_PERMISSION;
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

        PlayerRole playerRole = targetPlayer.getPlayerRole();

        if (playerRole.isLastRole()) {
            Message.LAST_ROLE_PROMOTE.send(guildUser);
            return;
        }

        if (!playerRole.isLessThan(guildUser.getPlayerRole())) {
            Message.PROMOTE_PLAYERS_WITH_LOWER_ROLE.send(guildUser);
            return;
        }

        PlayerRole nextRole = playerRole.getNextRole();

        if (nextRole == null || nextRole.isLastRole()) {
            Message.LAST_ROLE_PROMOTE.send(guildUser);
            return;
        }

        if (nextRole.isHigherThan(guildUser.getPlayerRole())) {
            Message.PROMOTE_PLAYERS_WITH_LOWER_ROLE.send(guildUser);
            return;
        }

        targetPlayer.setPlayerRole(nextRole);

        Message.PROMOTED_MEMBER.send(guildUser, targetPlayer.getName(), targetPlayer.getPlayerRole());
        Message.GOT_PROMOTED.send(targetPlayer, targetPlayer.getPlayerRole());
    }

    @Override
    public List<String> tabComplete(AgoniaGuilds plugin, GuildUser guildUser, Guild guild, String[] args) {
        return args.length != 2 ? Collections.emptyList() : CommandTabCompletes.getGuildMembers(guild, args[1], guildMember -> {
           PlayerRole playerRole = guildMember.getPlayerRole();
           PlayerRole nextRole = playerRole.getNextRole();

           return nextRole != null && !nextRole.isLastRole() && playerRole.isLessThan(guildUser.getPlayerRole()) &&
                   !nextRole.isHigherThan(guildUser.getPlayerRole());
        });
    }
}
