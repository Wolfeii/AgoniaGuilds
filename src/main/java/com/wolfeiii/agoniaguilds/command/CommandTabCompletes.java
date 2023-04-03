package com.wolfeiii.agoniaguilds.command;

import com.wolfeiii.agoniaguilds.AgoniaGuilds;
import com.wolfeiii.agoniaguilds.guild.Guild;
import com.wolfeiii.agoniaguilds.user.GuildUser;
import com.wolfeiii.agoniaguilds.utilities.objects.SequentialListBuilder;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import javax.annotation.Nullable;
import java.util.*;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class CommandTabCompletes {

    private CommandTabCompletes() {

    }

    public static List<String> getPlayerGuildsExceptSender(AgoniaGuilds plugin, CommandSender sender, String argument, boolean hideVanish) {
        return getPlayerGuildsExceptSender(plugin, sender, argument, hideVanish, (onlinePlayer, onlineGuild) -> true);
    }

    public static List<String> getPlayerGuildsExceptSender(AgoniaGuilds plugin, CommandSender sender,
                                                            String argument, boolean hideVanish,
                                                            BiPredicate<GuildUser, Guild> guildPredicate) {
        GuildUser guildUser = sender instanceof Player ? plugin.getUserManager().getGuildUser(sender) : null;
        Guild guild = guildUser == null ? null : guildUser.getGuild();
        return getOnlinePlayersWithGuilds(plugin, argument, hideVanish, (onlinePlayer, onlineGuild) ->
                onlineGuild != null && (guildUser == null || guild == null || !guild.equals(onlineGuild)) &&
                        guildPredicate.test(onlinePlayer, onlineGuild));
    }

    public static List<String> getGuildMembers(Guild guild, String argument, Predicate<GuildUser> predicate) {
        return getPlayers(guild.getGuildMembers(false), argument, predicate);
    }

    public static List<String> getGuildMembers(Guild guild, String argument) {
        return getPlayers(guild.getGuildMembers(false), argument);
    }

    public static List<String> getOnlinePlayers(AgoniaGuilds plugin, String argument, boolean hideVanish) {
        String lowerArgument = argument.toLowerCase(Locale.ENGLISH);
        return new SequentialListBuilder<GuildUser>()
                .filter(onlinePlayer -> (!hideVanish || onlinePlayer.isShownAsOnline()) &&
                        onlinePlayer.getName().toLowerCase(Locale.ENGLISH).contains(lowerArgument))
                .map(getOnlineGuildUsers(plugin), GuildUser::getName);
    }

    public static List<String> getOnlinePlayers(AgoniaGuilds plugin, String argument, boolean hideVanish, Predicate<GuildUser> predicate) {
        String lowerArgument = argument.toLowerCase(Locale.ENGLISH);
        return new SequentialListBuilder<GuildUser>()
                .filter(onlinePlayer -> (!hideVanish || onlinePlayer.isShownAsOnline()) &&
                        predicate.test(onlinePlayer) && onlinePlayer.getName().toLowerCase(Locale.ENGLISH).contains(lowerArgument))
                .map(getOnlineGuildUsers(plugin), GuildUser::getName);
    }

    public static List<String> getOnlinePlayersWithGuilds(AgoniaGuilds plugin, String argument,
                                                           boolean hideVanish, @Nullable BiPredicate<GuildUser, Guild> predicate) {
        List<String> tabArguments = new LinkedList<>();
        String lowerArgument = argument.toLowerCase(Locale.ENGLISH);

        for (Player player : Bukkit.getOnlinePlayers()) {
            GuildUser onlinePlayer = plugin.getUserManager().getGuildUser(player);
            if (!hideVanish || onlinePlayer.isShownAsOnline()) {
                Guild onlineGuild = onlinePlayer.getGuild();
                if (predicate == null || predicate.test(onlinePlayer, onlineGuild)) {
                    if (onlinePlayer.getName().toLowerCase(Locale.ENGLISH).contains(lowerArgument))
                        tabArguments.add(onlinePlayer.getName());
                    if (onlineGuild != null && onlineGuild.getName().toLowerCase(Locale.ENGLISH).contains(lowerArgument))
                        tabArguments.add(onlineGuild.getName());
                }
            }
        }

        return Collections.unmodifiableList(tabArguments);
    }

    public static List<String> getCustomComplete(String argument, String... tabVariables) {
        return filterByArgument(Arrays.asList(tabVariables), argument.toLowerCase(Locale.ENGLISH));
    }

    public static List<String> getCustomComplete(String argument, Predicate<String> predicate, String... tabVariables) {
        String lowerArgument = argument.toLowerCase(Locale.ENGLISH);
        return new SequentialListBuilder<String>()
                .filter(var -> var.contains(lowerArgument) && predicate.test(var))
                .build(Arrays.asList(tabVariables));
    }

    public static List<String> getCustomComplete(String argument, IntStream tabVariables) {
        String lowerArgument = argument.toLowerCase(Locale.ENGLISH);
        return new SequentialListBuilder<String>()
                .filter(var -> var.contains(lowerArgument))
                .build(Stream.of(tabVariables).map(i -> i + ""));
    }

    private static List<String> getPlayers(Collection<GuildUser> players, String argument) {
        return filterByArgument(players, GuildUser::getName, argument.toLowerCase(Locale.ENGLISH));
    }

    private static List<String> getPlayers(Collection<GuildUser> players, String argument, Predicate<GuildUser> predicate) {
        String lowerArgument = argument.toLowerCase(Locale.ENGLISH);
        return new SequentialListBuilder<GuildUser>()
                .filter(player -> predicate.test(player) && player.getName().toLowerCase(Locale.ENGLISH).contains(lowerArgument))
                .map(players, GuildUser::getName);
    }

    private static List<GuildUser> getOnlineGuildUsers(AgoniaGuilds plugin) {
        return new SequentialListBuilder<GuildUser>()
                .mutable()
                .build(Bukkit.getOnlinePlayers(), player -> plugin.getUserManager().getGuildUser(player));
    }

    private static List<String> filterByArgument(Collection<String> collection, String argument) {
        return new SequentialListBuilder<String>()
                .filter(name -> name.toLowerCase(Locale.ENGLISH).contains(argument))
                .build(collection);
    }

    private static <E> List<String> filterByArgument(Collection<E> collection, Function<E, String> mapper, String argument) {
        return new SequentialListBuilder<String>()
                .filter(name -> name.toLowerCase(Locale.ENGLISH).contains(argument))
                .build(collection, mapper);
    }

    private static List<String> getFromEnum(Collection<Enum<?>> enums, String argument) {
        String lowerArgument = argument.toLowerCase(Locale.ENGLISH);
        return new SequentialListBuilder<String>()
                .filter(name -> name.contains(lowerArgument))
                .build(enums, enumElement -> enumElement.name().toLowerCase(Locale.ENGLISH));
    }
}
