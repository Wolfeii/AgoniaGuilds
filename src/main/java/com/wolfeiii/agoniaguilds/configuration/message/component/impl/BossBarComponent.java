package com.wolfeiii.agoniaguilds.configuration.message.component.impl;

import com.wolfeiii.agoniaguilds.AgoniaGuilds;
import com.wolfeiii.agoniaguilds.configuration.bossbar.BossBar;
import com.wolfeiii.agoniaguilds.configuration.message.IMessageComponent;
import com.wolfeiii.agoniaguilds.configuration.message.Message;
import com.wolfeiii.agoniaguilds.configuration.message.component.EmptyMessageComponent;
import org.apache.logging.log4j.util.Strings;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

public class BossBarComponent implements IMessageComponent {

    private static final AgoniaGuilds plugin = AgoniaGuilds.getAgoniaGuilds();

    private final String message;
    private final BossBar.Color color;
    private final int ticksToRun;

    public static IMessageComponent of(@Nullable String message, BossBar.Color color, int ticks) {
        return ticks <= 0 || Strings.isBlank(message) ? EmptyMessageComponent.getInstance() : new BossBarComponent(message, color, ticks);
    }

    private BossBarComponent(String message, BossBar.Color color, int ticks) {
        this.message = message;
        this.color = color;
        this.ticksToRun = ticks;
    }

    @Override
    public Type getType() {
        return Type.BOSS_BAR;
    }

    @Override
    public String getMessage() {
        return this.message;
    }

    @Override
    public void sendMessage(CommandSender sender, Object... args) {
        if (sender instanceof Player) {
            Message.replaceArgs(this.message, args).ifPresent(message -> {
                plugin.getServicesHandler().getBossBarsService().createBossBar((Player) sender, message, this.color, this.ticksToRun);
            });
        }
    }

}