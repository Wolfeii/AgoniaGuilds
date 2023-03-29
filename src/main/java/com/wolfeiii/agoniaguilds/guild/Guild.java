package com.wolfeiii.agoniaguilds.guild;

import com.google.common.base.Preconditions;
import com.google.common.collect.Sets;
import com.wolfeiii.agoniaguilds.user.GuildUser;
import com.wolfeiii.agoniaguilds.utilities.formatters.Formatters;
import com.wolfeiii.agoniaguilds.utilities.objects.BukkitExecutor;
import com.wolfeiii.agoniaguilds.utilities.objects.SequentialListBuilder;
import com.wolfeiii.agoniaguilds.utilities.objects.Synchronized;
import com.wolfeiii.agoniaguilds.utilities.sorting.SortingComparators;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

public class Guild {

    private final UUID uuid;
    private final long creationTime;

    private final Synchronized<SortedSet<GuildUser>> members = Synchronized.of(new TreeSet<>(SortingComparators.PLAYER_NAMES_COMPARATOR));
    private final Set<GuildUser> invitedPlayers = Sets.newConcurrentHashSet();

    private final AtomicReference<Integer> guildLevel = new AtomicReference<>(0);

    private GuildUser owner;
    private String creationTimeDate;

    private volatile long lastTimeUpdate;
    private volatile boolean currentlyActive = false;
    private volatile long lastUpgradeTime = -1L;
    private volatile String guildName;
    private volatile String guildRawName;
    private volatile String description;


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

    public void addMember(GuildUser guildUser) {
        Preconditions.checkNotNull(guildUser, "guildUser kan inte vara null.");

        boolean addedNewMember = members.writeAndGet(members -> members.add(guildUser));

        // Denna användare är redan med i denna Guild.
        if (!addedNewMember)
            return;

        guildUser.setPlayerGuild(this);

        updateLastTime();

        if (guildUser.isOnline()) {
            setCurrentlyActive();
        }
    }

    public boolean isMember(GuildUser guildUser) {
        Preconditions.checkNotNull(guildUser, "guildUser kan inte vara null.");
        return owner.equals(guildUser.getGuildOwner());
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
            // TODO: Save to Database.
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

        // TODO: Save to Database.
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

        // TODO: Save to Database.
    }
}
