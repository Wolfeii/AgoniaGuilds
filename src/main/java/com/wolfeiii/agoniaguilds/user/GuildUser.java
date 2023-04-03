package com.wolfeiii.agoniaguilds.user;

import com.google.common.base.Preconditions;
import com.wolfeiii.agoniaguilds.AgoniaGuilds;
import com.wolfeiii.agoniaguilds.guild.Guild;
import com.wolfeiii.agoniaguilds.guild.privilege.GuildPrivilege;
import com.wolfeiii.agoniaguilds.guild.roles.PlayerRole;
import com.wolfeiii.agoniaguilds.menu.interfaces.view.MenuView;
import com.wolfeiii.agoniaguilds.storage.bridge.DatabaseBridge;
import com.wolfeiii.agoniaguilds.storage.bridge.DatabaseBridgeMode;
import com.wolfeiii.agoniaguilds.storage.bridge.impl.GuildDatabaseBridge;
import com.wolfeiii.agoniaguilds.storage.bridge.impl.PlayersDatabaseBridge;
import com.wolfeiii.agoniaguilds.storage.sql.SQLDatabaseBridge;
import com.wolfeiii.agoniaguilds.user.builder.GuildUserBuilder;
import com.wolfeiii.agoniaguilds.utilities.logging.Debug;
import com.wolfeiii.agoniaguilds.utilities.logging.Log;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.InventoryView;
import org.bukkit.metadata.MetadataValue;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Consumer;

public class GuildUser {

    private static final AgoniaGuilds plugin = AgoniaGuilds.getAgoniaGuilds();

    private final DatabaseBridge databaseBridge;

    private final List<UUID> pendingInvites = new LinkedList<>();

    private final UUID uuid;

    private Guild playerGuild = null;
    private String name;
    private String textureValue;
    private PlayerRole playerRole;

    private long lastTimeStatus;

    public GuildUser(GuildUserBuilder guildUserBuilder) {
        this.uuid = guildUserBuilder.uuid;
        this.name = guildUserBuilder.name;
        this.textureValue = guildUserBuilder.textureValue;
        this.lastTimeStatus = guildUserBuilder.lastTimeUpdated;

        this.databaseBridge = new SQLDatabaseBridge();
        databaseBridge.setDatabaseBridgeMode(DatabaseBridgeMode.SAVE_DATA);
    }

    public UUID getUniqueId() {
        return uuid;
    }

    public String getName() {
        return name;
    }

    public String getTextureValue() {
        return textureValue;
    }

    public void setTextureValue(@NotNull String textureValue) {
        Preconditions.checkNotNull(textureValue, "textureValue kan inte vara null.");

        this.textureValue = textureValue;

        if (Objects.equals(removeTextureValueTimestamp(textureValue), removeTextureValueTimestamp(this.textureValue)))
            return;

        PlayersDatabaseBridge.saveTextureValue(this);
    }

    public void updateLastTimeStatus() {
        setLastTimeStatus(System.currentTimeMillis() / 1000);
    }

    public void setLastTimeStatus(long lastTimeStatus) {
        if (this.lastTimeStatus == lastTimeStatus)
            return;

        this.lastTimeStatus = lastTimeStatus;

        PlayersDatabaseBridge.savePlayerName(this);
    }

    public long getLastTimeStatus() {
        return lastTimeStatus;
    }

    public void setName(String name) {
        Preconditions.checkNotNull(name, "name kan inte vara null.");

        if (Objects.equals(this.name, name))
            return;

        try {
            plugin.getUserManager().removePlayerInternal(this);
            this.name = name;
            PlayersDatabaseBridge.savePlayerName(this);
        } finally {
            plugin.getUserManager().addPlayerInternal(this);
        }
    }

    public void updateName() {
        Player player = asPlayer();
        if (player != null)
            this.setName(player.getName());
    }

    public Player asPlayer() {
        return Bukkit.getPlayer(uuid);
    }

    public OfflinePlayer asOfflinePlayer() {
        return Bukkit.getOfflinePlayer(uuid);
    }

    @Nullable
    public MenuView<?, ?> getOpenedView() {
        Player player = asPlayer();

        if (player != null) {
            InventoryView openInventory = player.getOpenInventory();
            if (openInventory != null && openInventory.getTopInventory() != null) {
                InventoryHolder inventoryHolder = openInventory.getTopInventory().getHolder();
                if (inventoryHolder instanceof MenuView)
                    return (MenuView<?, ?>) inventoryHolder;
            }
        }

        return null;
    }

    public boolean isOnline() {
        OfflinePlayer offlinePlayer = asOfflinePlayer();
        return offlinePlayer.getName() != null && offlinePlayer.isOnline();
    }

    public boolean isShownAsOnline() {
        Player player = asPlayer();
        return player != null && player.getGameMode() != GameMode.SPECTATOR && !isVanished();
    }

    public boolean isVanished() {
        Player player = asPlayer();
        if (player == null || !player.isOnline())
            return false;

        for (MetadataValue meta : player.getMetadata("vanished")) {
            if (meta.asBoolean()) return true;
        }

        return false;
    }

    public boolean hasPermission(GuildPrivilege permission) {
        Preconditions.checkNotNull(permission, "permission kan inte vara null..");
        Guild guild = getGuild();
        return guild != null && guild.hasPermission(this, permission);
    }

    public boolean hasPermission(String permission) {
        Player player = asPlayer();
        return player != null && player.hasPermission(permission);
    }

    public boolean hasPermissionWithoutOP(String permission) {
        return false;
    }

    public void runIfOnline(Consumer<Player> toRun) {
        Player player = asPlayer();
        if (player != null) {
            toRun.accept(player);
        }
    }

    public World getWorld() {
        Location location = getLocation();
        return location == null ? null : location.getWorld();
    }

    public Location getLocation() {
        Player player = asPlayer();
        return player == null ? null : player.getLocation();
    }

    public void setPlayerGuild(Guild playerGuild) {
        this.playerGuild = playerGuild;
    }

    public Guild getGuild() {
        return playerGuild;
    }

    public GuildUser getGuildOwner() {
        return playerGuild == null ? null : playerGuild.getOwner();
    }

    public boolean hasGuild() {
        return playerGuild != null;
    }

    public void addInvite(Guild guild) {
        this.pendingInvites.add(guild.getUniqueId());
    }

    public void removeInvite(Guild guild) {
        this.pendingInvites.remove(guild.getUniqueId());
    }

    public List<Guild> getInvites() {
        return this.pendingInvites.stream()
                .map(uuid -> plugin.getGuildManager().getGuildByUUID(uuid))
                .toList();
    }

    public PlayerRole getPlayerRole() {
        if (playerRole == null) {
            setPlayerRole(PlayerRole.guestRole());
        }

        return playerRole;
    }

    public void setPlayerRole(PlayerRole playerRole) {
        Preconditions.checkNotNull(playerRole, "playerRole kan inte vara null.");

        Log.debug(Debug.SET_PLAYER_ROLE, getName(), playerRole.getName());

        this.playerRole = playerRole;

        Guild guild = getGuild();
        if (guild != null && guild.getOwner() != this)
            GuildDatabaseBridge.saveMemberRole(guild, this);
    }

    private static String removeTextureValueTimestamp(@Nullable String textureValue) {
        return textureValue == null || textureValue.length() <= 42 ? null : textureValue.substring(0, 35) + textureValue.substring(42);
    }

    public DatabaseBridge getDatabaseBridge() {
        return databaseBridge;
    }
}
