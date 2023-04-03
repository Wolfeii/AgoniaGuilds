package com.wolfeiii.agoniaguilds.utilities.sorting;

import com.wolfeiii.agoniaguilds.AgoniaGuilds;
import com.wolfeiii.agoniaguilds.guild.Guild;

import java.util.Comparator;

public class SortingTypes {

    public static final SortingType BY_LEVEL = register("LEVEL", SortingComparators.LEVEL_COMPARATOR, false);
    public static final SortingType BY_PLAYERS = register("PLAYERS", SortingComparators.PLAYERS_COMPARATOR, false);

    private SortingTypes() {

    }

    public static void registerSortingTypes() {
        // Do nothing, only trigger all the register calls
    }

    private static SortingType register(String name, Comparator<Guild> comparator, boolean handleEqualsIslands) {
        SortingType.register(name, comparator, handleEqualsIslands);
        return SortingType.getByName(name);
    }



}
