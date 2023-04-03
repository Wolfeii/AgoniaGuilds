package com.wolfeiii.agoniaguilds.configuration.bossbar.impl;

import com.wolfeiii.agoniaguilds.configuration.bossbar.BossBar;
import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.entity.Player;

public class BossBarImpl implements BossBar {

    private final org.bukkit.boss.BossBar bossBar;
    private final BossBarTask bossBarTask;

    public BossBarImpl(String message, BarColor color, double ticksToRun) {
        bossBar = Bukkit.createBossBar(message, color, BarStyle.SOLID);
        this.bossBarTask = BossBarTask.create(this, ticksToRun);
    }

    @Override
    public void addPlayer(Player player) {
        this.bossBar.addPlayer(player);
        this.bossBarTask.registerTask(player);
    }

    @Override
    public void removeAll() {
        this.bossBar.removeAll();
        this.bossBar.getPlayers().forEach(this.bossBarTask::unregisterTask);
    }

    @Override
    public void setProgress(double progress) {
        this.bossBar.setProgress(progress);
    }

    @Override
    public double getProgress() {
        return this.bossBar.getProgress();
    }
}
