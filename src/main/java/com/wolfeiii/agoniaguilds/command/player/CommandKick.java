package com.wolfeiii.agoniaguilds.command.player;

import com.wolfeiii.agoniaguilds.AgoniaGuilds;
import com.wolfeiii.agoniaguilds.command.IPermissibleCommand;
import com.wolfeiii.agoniaguilds.configuration.message.Message;
import com.wolfeiii.agoniaguilds.guild.Guild;
import com.wolfeiii.agoniaguilds.guild.privilege.GuildPrivilege;
import com.wolfeiii.agoniaguilds.guild.privilege.GuildPrivileges;
import com.wolfeiii.agoniaguilds.user.GuildUser;

import java.util.Arrays;
import java.util.List;

public class CommandKick implements IPermissibleCommand {
    @Override
    public List<String> getAliases() {
        return Arrays.asList("kick");
    }

    @Override
    public String getPermission() {
        return "agonia.guild.kick";
    }

    @Override
    public String getUsage() {
        return "kick <" + Message.COMMAND_ARGUMENT_PLAYER_NAME.getMessage() + ">";
    }

    @Override
    public String getDescription() {
        return Message.COMMAND_DESCRIPTION_KICK.getMessage();
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
        return GuildPrivileges.KICK_MEMBER;
    }

    @Override
    public Message getPermissionLackMessage() {
        return Message.NO_KICK_PERMISSION;
    }

    @Override
    public void execute(AgoniaGuilds plugin, GuildUser guildUser, Guild guild, String[] args) {

    }
}
