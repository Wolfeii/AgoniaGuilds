package com.wolfeiii.agoniaguilds.listener;

import com.wolfeiii.agoniaguilds.AgoniaGuilds;
import com.wolfeiii.agoniaguilds.guild.Guild;
import com.wolfeiii.agoniaguilds.user.GuildUser;
import com.wolfeiii.agoniaguilds.utilities.logging.Log;
import com.wolfeiii.agoniaguilds.utilities.objects.BukkitExecutor;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.WeatherType;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerGameModeChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class PlayersListener implements Listener {

    private final AgoniaGuilds plugin;

    public PlayersListener(AgoniaGuilds plugin) {
        this.plugin = plugin;
    }

    /* PLAYER NOTIFIERS */

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    private void onPlayerLogin(PlayerLoginEvent e) {
        GuildUser guildUser = plugin.getUserManager().getGuildUser(e.getPlayer().getUniqueId());
        List<GuildUser> duplicatedPlayers = plugin.getUserManager().matchAllPlayers(_guildUser ->
                _guildUser != guildUser && _guildUser.getName().equalsIgnoreCase(e.getPlayer().getName()));
        if (!duplicatedPlayers.isEmpty()) {
            Log.info("Changing UUID of " + guildUser.getName() + " to " + guildUser.getUniqueId());
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    private void onPlayerJoin(PlayerJoinEvent e) {
        GuildUser guildUser = plugin.getUserManager().getGuildUser(e.getPlayer().getUniqueId());

        // Updating the name of the player.
        if (!guildUser.getName().equals(e.getPlayer().getName())) {
            guildUser.updateName();
        }

        guildUser.updateLastTimeStatus();

        if (guildUser.isShownAsOnline())
            notifyPlayerJoin(guildUser);
    }

    public void notifyPlayerJoin(GuildUser guildUser) {
        Guild guild = guildUser.getGuild();
        if (guild != null) {
            guild.updateLastTime();
            guild.setCurrentlyActive();
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    private void onPlayerQuit(PlayerQuitEvent e) {
        GuildUser guildUser = plugin.getUserManager().getGuildUser(e.getPlayer());

        guildUser.updateLastTimeStatus();

        // Handling player quit
        if (guildUser.isShownAsOnline())
            notifyPlayerQuit(guildUser);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    private void onPlayerGameModeChange(PlayerGameModeChangeEvent e) {
        if (e.getNewGameMode() == GameMode.SPECTATOR) {
            notifyPlayerQuit(plugin.getUserManager().getGuildUser(e.getPlayer()));
        } else if (e.getPlayer().getGameMode() == GameMode.SPECTATOR) {
            notifyPlayerJoin(plugin.getUserManager().getGuildUser(e.getPlayer()));
        }
    }

    public void notifyPlayerQuit(GuildUser guildUser) {
        Guild guild = guildUser.getGuild();

        if (guild == null)
            return;

        boolean anyOnline = guild.getGuildMembers(true).stream().anyMatch(guildMember ->
                guildMember != guildUser && guildMember.isOnline());

        if (!anyOnline)
            guild.setLastTimeUpdate(System.currentTimeMillis() / 1000);
    }
}