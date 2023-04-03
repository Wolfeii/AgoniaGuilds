package com.wolfeiii.agoniaguilds.utilities.sorting;

import com.wolfeiii.agoniaguilds.guild.Guild;
import com.wolfeiii.agoniaguilds.user.GuildUser;

import java.util.Comparator;

public class SortingComparators {

    public final static Comparator<GuildUser> PLAYER_NAMES_COMPARATOR = Comparator.comparing(GuildUser::getName);

    private final static Comparator<Guild> ISLAND_NAMES_COMPARATOR = (o1, o2) -> {
        String firstName = o1.getName().isEmpty() ? o1.getOwner().getName() : o1.getName();
        String secondName = o2.getName().isEmpty() ? o2.getOwner().getName() : o2.getName();
        return firstName.compareTo(secondName);
    };

    public final static Comparator<Guild> LEVEL_COMPARATOR = (o1, o2) -> {
        int compare = o2.getGuildLevel().compareTo(o1.getGuildLevel());
        return compare == 0 ? ISLAND_NAMES_COMPARATOR.compare(o1, o2) : compare;
    };

    public final static Comparator<Guild> PLAYERS_COMPARATOR = (o1, o2) -> {
        int compare = Integer.compare(o2.getGuildMembers(true).size(), o1.getGuildMembers(true).size());
        return compare == 0 ? ISLAND_NAMES_COMPARATOR.compare(o1, o2) : compare;
    };
}
