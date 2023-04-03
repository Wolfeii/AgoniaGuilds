package com.wolfeiii.agoniaguilds.guild.privilege;

import com.google.common.base.Preconditions;
import com.wolfeiii.agoniaguilds.utilities.logging.Log;
import com.wolfeiii.agoniaguilds.utilities.objects.enums.EnumerateMap;

import java.util.Map;

public abstract class PrivilegeNodeAbstract {

    protected final EnumerateMap<GuildPrivilege, PrivilegeStatus> privileges;

    protected PrivilegeNodeAbstract() {
        this.privileges = new EnumerateMap<>(GuildPrivilege.values());
    }

    protected PrivilegeNodeAbstract(EnumerateMap<GuildPrivilege, PrivilegeStatus> privileges) {
        this.privileges = new EnumerateMap<>(privileges);
    }

    protected void setPermissions(String permissions, boolean checkDefaults) {
        if (!permissions.isEmpty()) {
            String[] permission = permissions.split(";");
            for (String perm : permission) {
                String[] permissionSections = perm.split(":");
                try {
                    GuildPrivilege guildPrivilege = GuildPrivilege.getByName(permissionSections[0]);
                    if (permissionSections.length == 2) {
                        privileges.put(guildPrivilege, PrivilegeStatus.of(permissionSections[1]));
                    } else {
                        if (!checkDefaults || !isDefault(guildPrivilege))
                            privileges.put(guildPrivilege, PrivilegeStatus.ENABLED);
                    }
                } catch (NullPointerException ignored) {
                    // Ignored - invalid privilege.
                } catch (Exception error) {
                    Log.error(error, "An unexpected error while loading permissions for '", perm, "':");
                }
            }
        }
    }

    public abstract boolean hasPermission(GuildPrivilege permission);

    void setPermission(GuildPrivilege guildPrivilege, boolean value) {
        Preconditions.checkNotNull(guildPrivilege, "guildPrivilege parameter cannot be null.");
        this.privileges.put(guildPrivilege, value ? PrivilegeStatus.ENABLED : PrivilegeStatus.DISABLED);
    }

    public Map<GuildPrivilege, Boolean> getCustomPermissions() {
        return this.privileges.collect(GuildPrivilege.values(),
                privilegeStatus -> privilegeStatus == PrivilegeStatus.ENABLED);
    }

    @Override
    public abstract PrivilegeNodeAbstract clone();

    protected boolean isDefault(GuildPrivilege guildPrivilege) {
        return false;
    }

    protected enum PrivilegeStatus {

        ENABLED,
        DISABLED,
        DEFAULT;

        static PrivilegeStatus of(String value) throws IllegalArgumentException {
            switch (value) {
                case "0":
                    return DISABLED;
                case "1":
                    return ENABLED;
                default:
                    return valueOf(value);
            }
        }

        static PrivilegeStatus of(byte value) throws IllegalArgumentException {
            switch (value) {
                case 0:
                    return DISABLED;
                case 1:
                    return ENABLED;
                default:
                    throw new IllegalArgumentException("Invalid privilege status: " + value);
            }
        }

        @Override
        public String toString() {
            switch (this) {
                case ENABLED:
                    return "1";
                case DISABLED:
                    return "0";
                default:
                    return name();
            }
        }

    }


}
