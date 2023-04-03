package com.wolfeiii.agoniaguilds.guild.privilege;

import com.google.common.base.Preconditions;
import com.wolfeiii.agoniaguilds.guild.roles.PlayerRole;
import com.wolfeiii.agoniaguilds.utilities.objects.BukkitExecutor;
import com.wolfeiii.agoniaguilds.utilities.objects.enums.EnumerateMap;

public class RolePrivilegeNode extends PrivilegeNodeAbstract {

    private final PlayerRole playerRole;
    private final RolePrivilegeNode previousNode;


    public RolePrivilegeNode(PlayerRole playerRole, PrivilegeNodeAbstract previousNode) {
        this(playerRole, previousNode, "");
    }

    public RolePrivilegeNode(PlayerRole playerRole, PrivilegeNodeAbstract previousNode, String permissions) {
        this.playerRole = playerRole;
        this.previousNode = (RolePrivilegeNode) previousNode;
        BukkitExecutor.sync(() -> setPermissions(permissions, playerRole != null), 1L);
    }

    private RolePrivilegeNode(EnumerateMap<GuildPrivilege, PrivilegeStatus> privileges,
                              PlayerRole playerRole, RolePrivilegeNode previousNode) {
        super(privileges);
        this.playerRole = playerRole;
        this.previousNode = previousNode != null ? (RolePrivilegeNode) previousNode.clone() : null;
    }

    @Override
    public boolean hasPermission(GuildPrivilege permission) {
        Preconditions.checkNotNull(permission, "permission kan inte vara null");

        PrivilegeStatus status = getStatus(permission);

        if (status != PrivilegeStatus.DEFAULT) {
            return status == PrivilegeStatus.ENABLED;
        }

        status = previousNode == null ? PrivilegeStatus.DEFAULT : previousNode.getStatus(permission);

        if (status != PrivilegeStatus.DEFAULT) {
            return status == PrivilegeStatus.ENABLED;
        }

        return playerRole != null && playerRole.getDefaultPermissions().hasPermission(permission);
    }

    @Override
    protected void setPermission(GuildPrivilege guildPrivilege, boolean value) {
        Preconditions.checkNotNull(guildPrivilege, "guildPrivilege kan inte vara null");
        setPermission(guildPrivilege, value, true);
    }

    public PrivilegeStatus getStatus(GuildPrivilege permission) {
        return privileges.getOrDefault(permission, PrivilegeStatus.DEFAULT);
    }

    public void setPermission(GuildPrivilege permission, boolean value, boolean keepDisable) {
        if (!value && !keepDisable) {
            privileges.remove(permission);
        } else {
            super.setPermission(permission, value);
        }

        if (previousNode != null)
            previousNode.setPermission(permission, false, false);
    }


    @Override
    public PrivilegeNodeAbstract clone() {
        return new RolePrivilegeNode(privileges, playerRole, previousNode);
    }

    @Override
    protected boolean isDefault(GuildPrivilege guildPrivilege) {
        if (playerRole != null) {
            return playerRole.getDefaultPermissions().isDefault(guildPrivilege);
        }

        if (previousNode != null && previousNode.isDefault(guildPrivilege))
            return true;

        return privileges.containsKey(guildPrivilege);
    }


    public static class EmptyRolePermissionNode extends RolePrivilegeNode {

        public static final EmptyRolePermissionNode INSTANCE;

        static {
            INSTANCE = new EmptyRolePermissionNode();
        }

        EmptyRolePermissionNode() {
            super(null, null);
        }

        @Override
        public boolean hasPermission(GuildPrivilege permission) {
            return false;
        }

        @Override
        public void setPermission(GuildPrivilege permission, boolean value) {
            // Do nothing.
        }

    }
}
