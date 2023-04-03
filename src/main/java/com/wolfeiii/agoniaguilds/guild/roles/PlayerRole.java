package com.wolfeiii.agoniaguilds.guild.roles;

import com.google.common.base.Preconditions;
import com.wolfeiii.agoniaguilds.AgoniaGuilds;
import com.wolfeiii.agoniaguilds.guild.privilege.RolePrivilegeNode;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class PlayerRole {


    private static final AgoniaGuilds plugin = AgoniaGuilds.getAgoniaGuilds();

    private final String name;
    private final String displayName;
    private final int id;
    private final int weight;
    private final RolePrivilegeNode defaultPermissions;

    public PlayerRole(String name, @Nullable String displayName, int id, int weight, List<String> defaultPermissions,
                      PlayerRole previousRole) {
        this.name = name;
        this.displayName = displayName == null ? name : displayName;
        this.id = id;
        this.weight = weight;

        StringBuilder permissions = new StringBuilder();
        defaultPermissions.forEach(perm -> permissions.append(";").append(perm));

        this.defaultPermissions = new RolePrivilegeNode(null,
                previousRole == null ? RolePrivilegeNode.EmptyRolePermissionNode.INSTANCE : previousRole.defaultPermissions,
                permissions.length() == 0 ? "" : permissions.substring(1));
    }

    public static PlayerRole defaultRole() {
        return plugin.getRolesManager().getDefaultRole();
    }

    public static PlayerRole lastRole() {
        return plugin.getRolesManager().getLastRole();
    }

    public static PlayerRole guestRole() {
        return plugin.getRolesManager().getGuestRole();
    }

    public static PlayerRole of(int weight) {
        return plugin.getRolesManager().getPlayerRole(weight);
    }

    public static PlayerRole fromId(int id) {
        return plugin.getRolesManager().getPlayerRoleFromId(id);
    }

    public static PlayerRole of(String name) {
        return plugin.getRolesManager().getPlayerRole(name);
    }

    public static String getValuesString() {
        StringBuilder stringBuilder = new StringBuilder();
        plugin.getRolesManager().getRoles().forEach(playerRole -> stringBuilder.append(", ").append(playerRole.toString().toLowerCase(Locale.ENGLISH)));
        return stringBuilder.substring(2);
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDisplayName() {
        return displayName;
    }

    public int getWeight() {
        return weight;
    }

    public boolean isHigherThan(PlayerRole role) {
        Preconditions.checkNotNull(role, "playerRole parameter cannot be null.");
        return getWeight() > role.getWeight();
    }

    public boolean isLessThan(PlayerRole role) {
        Preconditions.checkNotNull(role, "playerRole parameter cannot be null.");
        return getWeight() < role.getWeight();
    }

    public boolean isFirstRole() {
        return getWeight() == 0;
    }

    public boolean isLastRole() {
        return getWeight() == lastRole().getWeight();
    }

    public boolean isRoleLadder() {
        return getWeight() >= 0 && (getPreviousRole() != null || getNextRole() != null);
    }

    public PlayerRole getNextRole() {
        return getWeight() < 0 ? null : plugin.getRolesManager().getPlayerRole(getWeight() + 1);
    }

    public PlayerRole getPreviousRole() {
        return getWeight() <= 0 ? null : plugin.getRolesManager().getPlayerRole(getWeight() - 1);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PlayerRole that = (PlayerRole) o;
        return id == that.id;
    }

    @Override
    public String toString() {
        return name;
    }

    public RolePrivilegeNode getDefaultPermissions() {
        return defaultPermissions;
    }


}
