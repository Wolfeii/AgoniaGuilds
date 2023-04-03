package com.wolfeiii.agoniaguilds.configuration.bossbar;

import com.wolfeiii.agoniaguilds.configuration.bossbar.impl.BossBarImpl;
import org.bukkit.boss.BarColor;
import org.bukkit.entity.Player;

public class BossBarsService {

    public BossBar createBossBar(Player player, String message, BossBar.Color color, double ticksToRun) {
        BossBarImpl bossBar = new BossBarImpl(message, BarColor.valueOf(color.name()), ticksToRun);
        bossBar.addPlayer(player);
        return bossBar;
    }
}
