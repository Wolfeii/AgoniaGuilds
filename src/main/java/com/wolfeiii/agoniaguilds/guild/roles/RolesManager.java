package com.wolfeiii.agoniaguilds.guild.roles;

import com.google.common.base.Preconditions;
import com.wolfeiii.agoniaguilds.AgoniaGuilds;
import com.wolfeiii.agoniaguilds.utilities.objects.SequentialListBuilder;
import org.bukkit.configuration.ConfigurationSection;

import java.util.*;

public class RolesManager {

    private static final int GUEST_ROLE_INDEX = -1;

    private final Map<Integer, PlayerRole> rolesByWeight = new HashMap<>();
    private final Map<Integer, PlayerRole> rolesById = new HashMap<>();
    private final Map<String, PlayerRole> rolesByName = new HashMap<>();

    private int lastRole = Integer.MIN_VALUE;

    private final AgoniaGuilds plugin;

    public RolesManager(AgoniaGuilds plugin) {
        this.plugin = plugin;
    }

    public void initializeData() throws RuntimeException {
        ConfigurationSection rolesSection = plugin.getConfiguration().getGuildRolesSection();

        ConfigurationSection guestSection = rolesSection.getConfigurationSection("guest");

        if (guestSection == null)
            throw new RuntimeException("Missing \"guest\" section for island roles");

        ConfigurationSection coopSection = rolesSection.getConfigurationSection("coop");

        if (coopSection == null)
            throw new RuntimeException("Missing \"coop\" section for island roles");

        PlayerRole previousRole = loadRole(guestSection, GUEST_ROLE_INDEX, null);

        ConfigurationSection laddersSection = rolesSection.getConfigurationSection("ladder");

        if (laddersSection == null)
            throw new RuntimeException("Missing \"ladder\" section for guild roles");

        List<ConfigurationSection> rolesByWeight = new LinkedList<>();
        for (String roleSectionName : laddersSection.getKeys(false))
            rolesByWeight.add(laddersSection.getConfigurationSection(roleSectionName));
        rolesByWeight.sort(Comparator.comparingInt(o -> o.getInt("weight", -1)));

        for (ConfigurationSection roleSection : rolesByWeight)
            previousRole = loadRole(roleSection, previousRole.getWeight() + 1, previousRole);
    }

    public PlayerRole getPlayerRole(int index) {
        return rolesByWeight.get(index);
    }

    public PlayerRole getPlayerRoleFromId(int id) {
        return rolesById.get(id);
    }

    public PlayerRole getPlayerRole(String name) {
        Preconditions.checkNotNull(name, "name kan inte vara null.");
        return rolesByName.get(name.toLowerCase(Locale.ENGLISH));
    }

    public PlayerRole getDefaultRole() {
        return getPlayerRole(0);
    }

    public PlayerRole getLastRole() {
        return getPlayerRole(lastRole);
    }

    public PlayerRole getGuestRole() {
        return getPlayerRole(GUEST_ROLE_INDEX);
    }

    public List<PlayerRole> getRoles() {
        return new SequentialListBuilder<PlayerRole>().build(this.rolesByWeight.values());
    }

    private PlayerRole loadRole(ConfigurationSection section, int expectedWeight, PlayerRole previousRole) throws RuntimeException {
        int weight = section.getInt("weight", expectedWeight);

        if (weight != expectedWeight)
            throw new RuntimeException("Rollen \"" + section.getName() + "\" har en oförväntad vikt: " +
                    weight + ", förväntades: " + expectedWeight);

        int id = section.getInt("id", weight);
        String name = section.getString("name");
        String displayName = section.getString("display-name");

        PlayerRole playerRole = new PlayerRole(name, displayName, id, weight, section.getStringList("permissions"), previousRole);

        this.rolesByWeight.put(playerRole.getWeight(), playerRole);
        this.rolesById.put(playerRole.getId(), playerRole);
        this.rolesByName.put(playerRole.getName().toUpperCase(Locale.ENGLISH), playerRole);

        if (weight > lastRole)
            lastRole = weight;

        return playerRole;
    }
}
