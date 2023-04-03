package com.wolfeiii.agoniaguilds.utilities;

import com.wolfeiii.agoniaguilds.utilities.objects.ServerVersion;
import org.bukkit.Bukkit;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.command.defaults.BukkitCommand;
import org.bukkit.craftbukkit.v1_19_R3.CraftServer;

import java.lang.reflect.Field;

public class Utilities {

    public static void registerCommand(BukkitCommand pluginCommand) {
        SimpleCommandMap simpleCommandMap;

        try {
            String bukkitVersion = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
            Class<?> craftServerClass = Class.forName("org.bukkit.craftbukkit." + bukkitVersion + ".CraftServer");

            Object craftServerObject = craftServerClass.cast(Bukkit.getServer());

            Field commandMapField = craftServerClass.getDeclaredField("commandMap");
            commandMapField.setAccessible(true);

            simpleCommandMap = (SimpleCommandMap) commandMapField.get(craftServerObject);
            simpleCommandMap.register("agoniaguilds", pluginCommand);
        } catch (ClassNotFoundException | NoSuchFieldException | IllegalAccessException error) {
            error.printStackTrace();
        }
    }


}
