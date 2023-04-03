package com.wolfeiii.agoniaguilds.command;

import com.google.common.base.Preconditions;
import com.wolfeiii.agoniaguilds.AgoniaGuilds;
import com.wolfeiii.agoniaguilds.utilities.objects.SequentialListBuilder;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public abstract class CommandsMap {

    private final Map<String, IGuildCommand> subCommands = new LinkedHashMap<>();
    private final Map<String, IGuildCommand> aliasesToCommand = new HashMap<>();

    private final AgoniaGuilds plugin;

    protected CommandsMap(AgoniaGuilds plugin) {
        this.plugin = plugin;
    }

    public abstract void loadDefaultCommands();

    public void registerCommand(IGuildCommand guildCommand, boolean sort) {
        List<String> aliases = new LinkedList<>(guildCommand.getAliases());
        String label = aliases.get(0).toLowerCase(Locale.ENGLISH);
        aliases.addAll(plugin.getConfiguration().getCommandAliases().getOrDefault(label, Collections.emptyList()));

        if (subCommands.containsKey(label)) {
            subCommands.remove(label);
            aliasesToCommand.values().removeIf(sC -> sC.getAliases().get(0).equals(aliases.get(0)));
        }
        subCommands.put(label, guildCommand);

        for (String alias : aliases) {
            aliasesToCommand.put(alias.toLowerCase(Locale.ENGLISH), guildCommand);
        }

        if (sort) {
            List<IGuildCommand> superiorCommands = new LinkedList<>(subCommands.values());
            superiorCommands.sort(Comparator.comparing(o -> o.getAliases().get(0)));
            subCommands.clear();
            superiorCommands.forEach(s -> subCommands.put(s.getAliases().get(0), s));
        }
    }

    public void unregisterCommand(IGuildCommand guildCommand) {
        Preconditions.checkNotNull(guildCommand, "guildCommand parameter cannot be null.");

        List<String> aliases = new LinkedList<>(guildCommand.getAliases());
        String label = aliases.get(0).toLowerCase(Locale.ENGLISH);
        aliases.addAll(plugin.getConfiguration().getCommandAliases().getOrDefault(label, Collections.emptyList()));

        subCommands.remove(label);
        aliasesToCommand.values().removeIf(sC -> sC.getAliases().get(0).equals(aliases.get(0)));
    }

    @Nullable
    public IGuildCommand getCommand(String label) {
        label = label.toLowerCase(Locale.ENGLISH);
        IGuildCommand guildCommand = subCommands.getOrDefault(label, aliasesToCommand.get(label));
        return guildCommand != null && !isCommandEnabled(guildCommand) ? null : guildCommand;
    }

    public List<IGuildCommand> getSubCommands(boolean includeDisabled) {
        SequentialListBuilder<IGuildCommand> listBuilder = new SequentialListBuilder<>();

        if (includeDisabled)
            listBuilder.filter(this::isCommandEnabled);

        return listBuilder.build(this.subCommands.values());
    }

    private boolean isCommandEnabled(IGuildCommand guildCommand) {
        return guildCommand instanceof IAdminGuildCommand || guildCommand.getAliases().stream()
                .noneMatch(plugin.getConfiguration().getDisabledCommands()::contains);
    }

}