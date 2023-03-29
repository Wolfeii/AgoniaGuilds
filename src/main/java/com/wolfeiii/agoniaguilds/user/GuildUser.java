package com.wolfeiii.agoniaguilds.user;

import com.google.common.base.Preconditions;
import com.wolfeiii.agoniaguilds.AgoniaGuilds;
import com.wolfeiii.agoniaguilds.guild.Guild;
import com.wolfeiii.agoniaguilds.user.builder.GuildUserBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Consumer;

public class GuildUser {

    private static final AgoniaGuilds plugin = AgoniaGuilds.getAgoniaGuilds();

    private final List<UUID> pendingInvites = new LinkedList<>();

    private final UUID uuid;

    private Guild playerGuild = null;
    private String name;
    private String textureValue;

    private long lastTimeStatus;

    public GuildUser(GuildUserBuilder guildUserBuilder) {
        this.uuid = guildUserBuilder.uuid;
        this.name = guildUserBuilder.name;
        this.textureValue = guildUserBuilder.textureValue;
        this.lastTimeStatus = guildUserBuilder.lastTimeUpdated;
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

        // TODO: Save to Database.
    }

    public void updateLastTimeStatus() {
        setLastTimeStatus(System.currentTimeMillis() / 1000);
    }

    public void setLastTimeStatus(long lastTimeStatus) {
        if (this.lastTimeStatus == lastTimeStatus)
            return;

        this.lastTimeStatus = lastTimeStatus;

        // TODO: Save to Database.
    }

    public long getLastTimeStatus() {
        return lastTimeStatus;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Player asPlayer() {
        return Bukkit.getPlayer(uuid);
    }

    public OfflinePlayer asOfflinePlayer() {
        return Bukkit.getOfflinePlayer(uuid);
    }

    public boolean isOnline() {
        OfflinePlayer offlinePlayer = asOfflinePlayer();
        return offlinePlayer.getName() != null && offlinePlayer.isOnline();
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

    private static String removeTextureValueTimestamp(@Nullable String textureValue) {
        return textureValue == null || textureValue.length() <= 42 ? null : textureValue.substring(0, 35) + textureValue.substring(42);
    }
}
