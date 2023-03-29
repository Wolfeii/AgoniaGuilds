package com.wolfeiii.agoniaguilds.utilities.sorting;

import com.wolfeiii.agoniaguilds.user.GuildUser;

import java.util.Comparator;

public class SortingComparators {

    public final static Comparator<GuildUser> PLAYER_NAMES_COMPARATOR = Comparator.comparing(GuildUser::getName);

}
