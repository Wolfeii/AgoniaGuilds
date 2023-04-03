package com.wolfeiii.agoniaguilds.configuration.bossbar.impl;

import com.wolfeiii.agoniaguilds.AgoniaGuilds;
import com.wolfeiii.agoniaguilds.configuration.bossbar.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class BossBarTask extends BukkitRunnable {

    private static final AgoniaGuilds plugin = AgoniaGuilds.getAgoniaGuilds();

    private static final BossBarTask EMPTY_TASK = new BossBarTask(EmptyBossBar.getInstance(), 0);

    private static final Map<UUID, Queue<BossBarTask>> PLAYERS_RUNNING_TASKS = new HashMap<>();

    private final BossBar bossBar;
    private final double progressToRemovePerTick;
    private boolean reachedEndTask = false;

    public static BossBarTask create(BossBar bossBar, double ticksToRun) {
        return ticksToRun <= 0 ? EMPTY_TASK : new BossBarTask(bossBar, ticksToRun);
    }

    private BossBarTask(BossBar bossBar, double ticksToRun) {
        this.bossBar = bossBar;
        this.progressToRemovePerTick = this.bossBar.getProgress() / ticksToRun;
        if (progressToRemovePerTick > 0) {
            runTaskTimer(plugin, 1L, 1L);
        }
    }

    @Override
    public void run() {
        if (reachedEndTask) {
            cancel();
        } else {
            this.bossBar.setProgress(Math.max(0D, this.bossBar.getProgress() - progressToRemovePerTick));
            reachedEndTask = this.bossBar.getProgress() == 0D;
        }
    }

    @Override
    public synchronized void cancel() throws IllegalStateException {
        this.bossBar.removeAll();
        super.cancel();
    }

    public void registerTask(Player player) {
        Queue<BossBarTask> bossBarTasks = PLAYERS_RUNNING_TASKS.computeIfAbsent(player.getUniqueId(), s -> new LinkedList<>());

        if (bossBarTasks.size() >= plugin.getConfiguration().getBossBarLimit()) {
            BossBarTask lastRunningTask = bossBarTasks.poll();
            if (lastRunningTask != null)
                lastRunningTask.cancel();
        }

        bossBarTasks.add(this);
    }

    public void unregisterTask(Player player) {
        Queue<BossBarTask> bossBarTasks = PLAYERS_RUNNING_TASKS.get(player.getUniqueId());

        if (bossBarTasks == null)
            return;

        bossBarTasks.remove(this);
    }

}
