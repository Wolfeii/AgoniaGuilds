package com.wolfeiii.agoniaguilds.guild.privilege;

import com.google.common.base.Preconditions;
import com.wolfeiii.agoniaguilds.utilities.objects.enums.Enumerable;

import java.util.Collection;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class GuildPrivilege implements Enumerable {

    private static final Map<String, GuildPrivilege> guildPrivileges = new HashMap<>();
    private static int ordinalCounter = 0;

    private final String name;
    private final int ordinal;

    private GuildPrivilege(String name) {
        this.name = name.toUpperCase(Locale.ENGLISH);
        this.ordinal = ordinalCounter++;
    }

    @Override
    public int ordinal() {
        return this.ordinal;
    }

    /**
     * Get all the guild privileges.
     */
    public static Collection<GuildPrivilege> values() {
        return guildPrivileges.values();
    }

    /**
     * Get an guild privilege by it's name.
     *
     * @param name The name to check.
     */
    public static GuildPrivilege getByName(String name) {
        Preconditions.checkNotNull(name, "name parameter cannot be null.");

        GuildPrivilege guildPrivilege = guildPrivileges.get(name.toUpperCase(Locale.ENGLISH));

        Preconditions.checkNotNull(guildPrivilege, "Couldn't find an GuildPrivilege with the name " + name + ".");

        return guildPrivilege;
    }

    /**
     * Register a new guild privilege.
     *
     * @param name The name for the guild privilege.
     */
    public static void register(String name) {
        Preconditions.checkNotNull(name, "name parameter cannot be null.");

        name = name.toUpperCase(Locale.ENGLISH);

        Preconditions.checkState(!guildPrivileges.containsKey(name), "GuildPrivilege with the name " + name + " already exists.");

        guildPrivileges.put(name, new GuildPrivilege(name));
    }

    /**
     * Get the name of the guild privilege.
     */
    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "GuildPrivilege{name=" + name + "}";
    }
}
