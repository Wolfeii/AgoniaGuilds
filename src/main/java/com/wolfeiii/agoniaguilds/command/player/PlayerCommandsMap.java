package com.wolfeiii.agoniaguilds.command.player;

import com.wolfeiii.agoniaguilds.AgoniaGuilds;
import com.wolfeiii.agoniaguilds.command.CommandsMap;

public class PlayerCommandsMap extends CommandsMap {

    public PlayerCommandsMap(AgoniaGuilds plugin) {
        super(plugin);
    }

    @Override
    public void loadDefaultCommands() {
        registerCommand(new CommandAccept(), false);
        registerCommand(new CommandDemote(), false);
        registerCommand(new CommandKick(), false);
        registerCommand(new CommandPromote(), false);
    }
}
