package com.wolfeiii.agoniaguilds.guild;

import com.google.common.base.Preconditions;
import com.google.common.collect.Sets;
import com.wolfeiii.agoniaguilds.AgoniaGuilds;
import com.wolfeiii.agoniaguilds.guild.builder.GuildBuilder;
import com.wolfeiii.agoniaguilds.guild.privilege.GuildPrivilege;
import com.wolfeiii.agoniaguilds.guild.privilege.PlayerPrivilegeNode;
import com.wolfeiii.agoniaguilds.guild.privilege.PrivilegeNodeAbstract;
import com.wolfeiii.agoniaguilds.guild.roles.PlayerRole;
import com.wolfeiii.agoniaguilds.storage.bridge.DatabaseBridge;
import com.wolfeiii.agoniaguilds.storage.bridge.DatabaseBridgeMode;
import com.wolfeiii.agoniaguilds.storage.bridge.impl.GuildDatabaseBridge;
import com.wolfeiii.agoniaguilds.storage.sql.SQLDatabaseBridge;
import com.wolfeiii.agoniaguilds.user.GuildUser;
import com.wolfeiii.agoniaguilds.utilities.formatters.Formatters;
import com.wolfeiii.agoniaguilds.utilities.logging.Debug;
import com.wolfeiii.agoniaguilds.utilities.logging.Log;
import com.wolfeiii.agoniaguilds.utilities.objects.BukkitExecutor;
import com.wolfeiii.agoniaguilds.utilities.objects.SequentialListBuilder;
import com.wolfeiii.agoniaguilds.utilities.objects.Synchronized;
import com.wolfeiii.agoniaguilds.utilities.sorting.SortingComparators;
import com.wolfeiii.agoniaguilds.utilities.value.Value;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;

public class Guild {

    private static final AgoniaGuilds plugin = AgoniaGuilds.getAgoniaGuilds();

    private final DatabaseBridge databaseBridge;

    private final UUID uuid;
    private final long creationTime;

    private final Synchronized<SortedSet<GuildUser>> members = Synchronized.of(new TreeSet<>(SortingComparators.PLAYER_NAMES_COMPARATOR));
    private final Set<GuildUser> invitedPlayers = Sets.newConcurrentHashSet();
    private final Map<GuildUser, PlayerPrivilegeNode> playerPermissions = new ConcurrentHashMap<>();

    private final AtomicReference<Integer> guildLevel = new AtomicReference<>(0);
    private final Map<GuildPrivilege, PlayerRole> rolePermissions = new ConcurrentHashMap<>();

    private GuildUser owner;
    private String creationTimeDate;

    private final Synchronized<Value<Integer>> teamLimit = Synchronized.of(Value.syncedFixed(-1));

    private volatile long lastTimeUpdate;
    private volatile boolean currentlyActive = false;
    private volatile long lastUpgradeTime = -1L;
    private volatile boolean isTopIslandsIgnored;
    private volatile String guildName;
    private volatile String guildRawName;
    private volatile String description;

    public Guild(GuildBuilder builder) {
        this.uuid = builder.uuid;
        this.owner = builder.owner;

        if (this.owner != null) {
            this.owner.setPlayerGuild(this);
        }

        this.creationTime = builder.creationTime;
        this.guildName = builder.guildName;
        this.guildRawName = Formatters.STRIP_COLOR_FORMATTER.format(this.guildName);
        this.isTopIslandsIgnored = builder.isIgnored;
        this.description = builder.description;
        this.members.write(members -> {
            members.addAll(builder.members);
            members.forEach(member -> member.setPlayerGuild(this));
        });
        this.teamLimit.set(builder.teamLimit);

        this.databaseBridge = new SQLDatabaseBridge();

        updateDatesFormatter();

        this.databaseBridge.setDatabaseBridgeMode(DatabaseBridgeMode.SAVE_DATA);
    }


    public UUID getUniqueId() {
        return uuid;
    }

    public GuildUser getOwner() {
        return owner;
    }

    public void inviteMember(GuildUser guildUser) {
        Preconditions.checkNotNull(guildUser, "guildUser kan inte vara null.");

        invitedPlayers.add(guildUser);
        guildUser.addInvite(this);

        // Ta bort dess invite efter fem minuter.
        BukkitExecutor.sync(() -> revokeInvite(guildUser), 6000L);
    }

    public void revokeInvite(GuildUser guildUser) {
        Preconditions.checkNotNull(guildUser, "guildUser kan inte vara null.");

        invitedPlayers.remove(guildUser);
        guildUser.removeInvite(this);
    }

    public boolean isInvited(GuildUser guildUser) {
        Preconditions.checkNotNull(guildUser, "guildUser kan inte vara null.");
        return invitedPlayers.contains(guildUser);
    }

    public List<GuildUser> getInvitedUsers() {
        return new SequentialListBuilder<GuildUser>().build(this.invitedPlayers);
    }

    public void addMember(GuildUser guildUser, PlayerRole playerRole) {
        Preconditions.checkNotNull(guildUser, "guildUser kan inte vara null.");

        Log.debug(Debug.ADD_MEMBER, "Guild", "addMember", owner.getName(), guildUser.getName(), playerRole);

        boolean addedNewMember = members.writeAndGet(members -> members.add(guildUser));

        // Denna användare är redan med i denna Guild.
        if (!addedNewMember)
            return;

        guildUser.setPlayerGuild(this);
        guildUser.setPlayerRole(playerRole);

        updateLastTime();

        if (guildUser.isOnline()) {
            setCurrentlyActive();
        }

        GuildDatabaseBridge.addMember(this, guildUser, System.currentTimeMillis());
    }

    public void updateDatesFormatter() {
        this.creationTimeDate = Formatters.DATE_FORMATTER.format(new Date(creationTime * 1000));
    }

    public int getTeamLimit() {
        return this.teamLimit.readAndGet(Value::get);
    }

    public boolean isMember(GuildUser guildUser) {
        Preconditions.checkNotNull(guildUser, "guildUser kan inte vara null.");
        return owner.equals(guildUser.getGuildOwner());
    }

    public List<GuildUser> getGuildMembers(boolean includeOwner) {
        return List.of();
    }

    public Integer getGuildLevel() {
        return guildLevel.get();
    }

    public void updateLastTime() {
        setLastTimeUpdate(System.currentTimeMillis() / 1000);
    }

    public void setCurrentlyActive() {
        setCurrentlyActive(true);
    }

    public void setCurrentlyActive(boolean active) {
        this.currentlyActive = active;
    }

    public boolean isCurrentlyActive() {
        return this.currentlyActive;
    }

    public long getLastTimeUpdate() {
        return this.currentlyActive ? -1 : lastTimeUpdate;
    }

    public void setLastTimeUpdate(long lastTimeUpdate) {
        if (this.lastUpgradeTime == lastTimeUpdate)
            return;

        this.lastTimeUpdate = lastTimeUpdate;

        if (lastTimeUpdate != -1) {
            GuildDatabaseBridge.saveLastTimeUpdate(this);
        }
    }

    public String getName() {
        return guildRawName;
    }

    public void setName(String guildName) {
        Preconditions.checkNotNull(guildName, "guildName kan inte vara null.");

        if (Objects.equals(this.guildName, guildName))
            return;

        this.guildName = guildName;
        this.guildRawName = Formatters.STRIP_COLOR_FORMATTER.format(this.guildName);

        GuildDatabaseBridge.saveName(this);
    }

    public String getRawName() {
        return guildRawName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        Preconditions.checkNotNull(description, "description kan inte vara null.");

        if (Objects.equals(this.description, description))
            return;

        this.description = description;

        GuildDatabaseBridge.saveDescription(this);
    }

    public long getCreationTime() {
        return creationTime;
    }

    public boolean isIgnored() {
        return isTopIslandsIgnored;
    }

    public boolean hasPermission(CommandSender sender, GuildPrivilege guildPrivilege) {
        Preconditions.checkNotNull(sender, "sender parameter cannot be null.");
        Preconditions.checkNotNull(guildPrivilege, "guildPrivilege parameter cannot be null.");

        return sender instanceof ConsoleCommandSender || hasPermission(plugin.getUserManager().getGuildUser(sender), guildPrivilege);
    }

    public boolean hasPermission(GuildUser guildUser, GuildPrivilege guildPrivilege) {
        Preconditions.checkNotNull(guildUser, "guildUser parameter cannot be null.");
        Preconditions.checkNotNull(guildPrivilege, "guildPrivilege parameter cannot be null.");

        PrivilegeNodeAbstract playerNode = getPermissionNode(guildUser);
        return guildUser.hasPermission("superior.admin.bypass.*") ||
                guildUser.hasPermissionWithoutOP("superior.admin.bypass." + guildPrivilege.getName()) ||
                (playerNode != null && playerNode.hasPermission(guildPrivilege));
    }

    public boolean hasPermission(PlayerRole playerRole, GuildPrivilege guildPrivilege) {
        Preconditions.checkNotNull(playerRole, "playerRole parameter cannot be null.");
        Preconditions.checkNotNull(guildPrivilege, "guildPrivilege parameter cannot be null.");

        return getRequiredPlayerRole(guildPrivilege).getWeight() <= playerRole.getWeight();
    }

    public PrivilegeNodeAbstract getPermissionNode(GuildUser guildUser) {
        Preconditions.checkNotNull(guildUser, "guildUser parameter cannot be null.");
        return playerPermissions.getOrDefault(guildUser, new PlayerPrivilegeNode(guildUser, this));
    }

    public PlayerRole getRequiredPlayerRole(GuildPrivilege guildPrivilege) {
        Preconditions.checkNotNull(guildPrivilege, "guildPrivilege parameter cannot be null.");

        PlayerRole playerRole = rolePermissions.get(guildPrivilege);

        if (playerRole != null)
            return playerRole;

        return plugin.getRolesManager().getRoles().stream()
                .filter(_playerRole -> _playerRole.getDefaultPermissions().hasPermission(guildPrivilege))
                .min(Comparator.comparingInt(PlayerRole::getWeight)).orElse(PlayerRole.lastRole());
    }



    public DatabaseBridge getDatabaseBridge() {
        return databaseBridge;
    }
}
