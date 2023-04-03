package com.wolfeiii.agoniaguilds.guild.privilege;

import com.google.common.base.Preconditions;
import com.wolfeiii.agoniaguilds.guild.Guild;
import com.wolfeiii.agoniaguilds.guild.roles.PlayerRole;
import com.wolfeiii.agoniaguilds.user.GuildUser;
import com.wolfeiii.agoniaguilds.utilities.objects.enums.EnumerateMap;

import javax.annotation.Nullable;

public class PlayerPrivilegeNode extends PrivilegeNodeAbstract {

    protected final GuildUser guildUser;
    protected Guild guild;

    public PlayerPrivilegeNode(GuildUser guildUser, Guild guild) {
        this.guildUser = guildUser;
        this.guild = guild;
    }

    public PlayerPrivilegeNode(GuildUser guildUser, Guild guild, String permissions) {
        this.guildUser = guildUser;
        this.guild = guild;
        setPermissions(permissions, false);
    }

    private PlayerPrivilegeNode(@Nullable EnumerateMap<GuildPrivilege, PrivilegeStatus> privileges,
                                GuildUser guildUser, Guild guild) {
        super(privileges);
        this.guildUser = guildUser;
        this.guild = guild;
    }

    public void setGuild(Guild guild) {
        this.guild = guild;
    }

    @Override
    public boolean hasPermission(GuildPrivilege guildPrivilege) {
        Preconditions.checkNotNull(guildPrivilege, "guildPrivilege kan inte vara null.");
        return getStatus(GuildPrivileges.ALL) == PrivilegeStatus.ENABLED || getStatus(guildPrivilege) == PrivilegeStatus.ENABLED;
    }


    public void loadPrivilege(GuildPrivilege guildPrivilege, byte status) {
        privileges.put(guildPrivilege, PrivilegeStatus.of(status));
    }

    protected PrivilegeStatus getStatus(GuildPrivilege guildPrivilege) {
        PrivilegeStatus cachedStatus = privileges.getOrDefault(guildPrivilege, PrivilegeStatus.DEFAULT);

        if (cachedStatus != PrivilegeStatus.DEFAULT)
            return cachedStatus;

        PlayerRole playerRole = guild.isMember(guildUser) ? guildUser.getPlayerRole() : PlayerRole.guestRole();

        return guild.hasPermission(playerRole, guildPrivilege) ? PrivilegeStatus.ENABLED : PrivilegeStatus.DISABLED;
    }

    @Override
    public PrivilegeNodeAbstract clone() {
        return new PlayerPrivilegeNode(privileges, guildUser, guild);
    }

    public static class EmptyPlayerPermissionNode extends PlayerPrivilegeNode {

        public static final EmptyPlayerPermissionNode INSTANCE;

        static {
            INSTANCE = new EmptyPlayerPermissionNode();
        }

        EmptyPlayerPermissionNode() {
            this(null, null);
        }

        EmptyPlayerPermissionNode(GuildUser guildUser, Guild guild) {
            super(null, guildUser, guild);
        }

        @Override
        public boolean hasPermission(GuildPrivilege guildPrivilege) {
            Preconditions.checkNotNull(guildPrivilege, "guildPrivilege parameter cannot be null.");
            return guildUser != null && guild != null && super.hasPermission(guildPrivilege);
        }

        protected PrivilegeStatus getStatus(GuildPrivilege guildPrivilege) {
            PlayerRole playerRole = guild.isMember(guildUser) ? guildUser.getPlayerRole() : PlayerRole.guestRole();

            if (guild.hasPermission(playerRole, guildPrivilege))
                return PrivilegeStatus.ENABLED;

            return PrivilegeStatus.DISABLED;
        }

        public void setPermission(GuildPrivilege permission, boolean value) {
            // Do nothing.
        }

    }
}
