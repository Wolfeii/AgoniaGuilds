package com.wolfeiii.agoniaguilds.guild.privilege;

import com.wolfeiii.agoniaguilds.utilities.objects.ServerVersion;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.Objects;

public class GuildPrivileges {

    public static final GuildPrivilege ALL = register("ALL");
    public static final GuildPrivilege CHANGE_NAME = register("CHANGE_NAME");
    public static final GuildPrivilege DEMOTE_MEMBERS = register("DEMOTE_MEMBERS");
    public static final GuildPrivilege DISBAND_ISLAND = register("DISBAND_GUILD");
    public static final GuildPrivilege INVITE_MEMBER = register("INVITE_MEMBER");
    public static final GuildPrivilege KICK_MEMBER = register("KICK_MEMBER");
    public static final GuildPrivilege PROMOTE_MEMBERS = register("PROMOTE_MEMBERS");
    public static final GuildPrivilege SET_PERMISSION = register("SET_PERMISSION");
    public static final GuildPrivilege SET_ROLE = register("SET_ROLE");

    private GuildPrivileges() {

    }

    public static void registerPrivileges() {
        // Do nothing, only trigger all the register calls
    }

    @NotNull
    private static GuildPrivilege register(String name) {
        return Objects.requireNonNull(register(name, true));
    }

    @Nullable
    private static GuildPrivilege register(String name, boolean registerCheck) {
        if (!registerCheck)
            return null;

        GuildPrivilege.register(name);
        return GuildPrivilege.getByName(name);
    }
}
