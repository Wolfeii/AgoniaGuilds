package com.wolfeiii.agoniaguilds.user;

import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.wolfeiii.agoniaguilds.AgoniaGuilds;
import com.wolfeiii.agoniaguilds.guild.Guild;
import com.wolfeiii.agoniaguilds.utilities.logging.Debug;
import com.wolfeiii.agoniaguilds.utilities.logging.Log;
import com.wolfeiii.agoniaguilds.utilities.objects.SequentialListBuilder;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class UserManager {

    private static final AgoniaGuilds plugin = AgoniaGuilds.getAgoniaGuilds();

    private final Map<UUID, GuildUser> users = new HashMap<>();
    private final Map<String, GuildUser> usersByNames = new HashMap<>();

    public GuildUser getGuildUser(String name) {
        Preconditions.checkNotNull(name, "name kan inte vara null.");

        GuildUser guildUser = this.usersByNames.get(name.toLowerCase(Locale.ENGLISH));

        if (guildUser == null) {
            guildUser = users.values().stream()
                    .filter(player -> player.getName().equalsIgnoreCase(name))
                    .findFirst().orElse(null);

            if (guildUser != null) {
                this.usersByNames.put(name.toLowerCase(Locale.ENGLISH), guildUser);
            }
        }

        return guildUser;
    }

    public GuildUser getGuildUser(Player player) {
        Preconditions.checkNotNull(player, "player kan inte vara null.");
        return player.hasMetadata("NPC") ? null : getGuildUser(player.getUniqueId());
    }

    public GuildUser getGuildUser(UUID uuid) {
        return Objects.requireNonNull(getGuildUser(uuid, true));
    }

    @Nullable
    public GuildUser getGuildUser(UUID uuid, boolean createIfNotExists) {
        return this.getGuildUser(uuid, createIfNotExists, true);
    }

    @Nullable
    public GuildUser getGuildUser(UUID uuid, boolean createIfNotExists, boolean saveToDatabase) {
        Preconditions.checkNotNull(uuid, "uuid kan inte vara null.");
        GuildUser superiorPlayer = this.users.get(uuid);

        if (createIfNotExists && superiorPlayer == null) {
            superiorPlayer = plugin.getFactory().createPlayer(uuid);
            addPlayerInternal(superiorPlayer);
            if (saveToDatabase) {
                // TODO: Save to Database, PlayersDatabaseBridge.insertPlayer(superiorPlayer);
            }
        }

        return superiorPlayer;
    }

    public GuildUser getGuildUser(CommandSender commandSender) {
        return getGuildUser((Player) commandSender);
    }

    public List<GuildUser> matchAllPlayers(Predicate<? super GuildUser> predicate) {
        return new SequentialListBuilder<GuildUser>()
                .filter(predicate)
                .build(getAllPlayers());
    }

    public void replacePlayers(GuildUser originPlayer, @Nullable GuildUser newPlayer) {
        Log.debug(Debug.REPLACE_PLAYER, originPlayer, newPlayer);

        // We first want to replace the player for his own guild
        Guild playerGuild = originPlayer.getGuild();
        if (playerGuild != null) {
            playerGuild.replacePlayers(originPlayer, newPlayer);
            if (playerGuild.getOwner() != newPlayer)
                Log.debugResult(Debug.REPLACE_PLAYER, "Ägaren byttes inte ut", "Nuvarande ägare: " + playerGuild.getOwner().getUniqueId());
        }

        for (Guild guild : plugin.getGuildManager().getIslands()) {
            if (guild != playerGuild)
                guild.replacePlayers(originPlayer, newPlayer);
        }

        if (newPlayer == null) {
            // PlayersDatabaseBridge.deletePlayer(originPlayer);
        } else {
            newPlayer.merge(originPlayer);
            plugin.getEventsBus().callPlayerReplaceEvent(originPlayer, newPlayer);
        }
    }

    // Updating last time status
    public void savePlayers() {
        for (Player player : Bukkit.getOnlinePlayers())
            PlayersDatabaseBridge.saveLastTimeStatus(getSuperiorPlayer(player));

        List<GuildUser> modifiedPlayers = new SequentialListBuilder<GuildUser>()
                .filter(PlayersDatabaseBridge::isModified)
                .build(getAllPlayers());

        if (!modifiedPlayers.isEmpty())
            modifiedPlayers.forEach(PlayersDatabaseBridge::executeFutureSaves);
    }

    public List<GuildUser> getAllPlayers() {
        return new SequentialListBuilder<GuildUser>().build(this.users.values());
    }

    private void addPlayerInternal(GuildUser guildUser) {
        this.users.put(guildUser.getUniqueId(), guildUser);

        String playerName = guildUser.getName();
        if (!playerName.equals("null"))
            this.usersByNames.put(playerName.toLowerCase(Locale.ENGLISH), guildUser);
    }

    public void removePlayerInternal(GuildUser guildUser) {
        this.users.remove(guildUser.getUniqueId());
        this.usersByNames.remove(guildUser.getName().toLowerCase(Locale.ENGLISH));
    }
}
