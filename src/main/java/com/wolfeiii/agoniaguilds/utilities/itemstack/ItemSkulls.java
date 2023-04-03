package com.wolfeiii.agoniaguilds.utilities.itemstack;

import com.wolfeiii.agoniaguilds.AgoniaGuilds;
import com.wolfeiii.agoniaguilds.configuration.CommentedConfiguration;
import com.wolfeiii.agoniaguilds.utilities.logging.Log;
import com.wolfeiii.agoniaguilds.utilities.objects.ServerVersion;
import net.minecraft.nbt.MojangsonParser;
import net.minecraft.nbt.NBTTagCompound;
import org.bukkit.craftbukkit.v1_19_R3.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ItemSkulls {

    private static final String NULL_PLAYER_TEXTURE = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYmFkYzA0OGE3Y2U3OGY3ZGFkNzJhMDdkYTI3ZDg1YzA5MTY4ODFlNTUyMmVlZWQxZTNkYWYyMTdhMzhjMWEifX19";
    private static final Map<String, String> entitySkullTextures = new HashMap<>();

    private ItemSkulls() {

    }

    public static void readTextures(AgoniaGuilds plugin) {
        entitySkullTextures.clear();

        File file = new File(plugin.getDataFolder(), "heads.yml");

        if (!file.exists())
            plugin.saveResource("heads.yml", false);

        CommentedConfiguration cfg = CommentedConfiguration.loadConfiguration(file);

        try {
            cfg.syncWithConfig(file, plugin.getResource("heads.yml"));
        } catch (Exception error) {
            Log.entering("ItemSkulls", "readTextures", "ENTER");
            Log.error(error, "An unexpected error occurred while syncing heads file:");
        }

        for (String entityType : cfg.getConfigurationSection("").getKeys(false))
            entitySkullTextures.put(entityType, cfg.getString(entityType));
    }

    public static ItemStack getPlayerHead(ItemStack itemStack, String texture) {
        net.minecraft.world.item.ItemStack nmsItemStack = CraftItemStack.asNMSCopy(itemStack);

        try {
            String nbtString = String.format(
                    "{SkullOwner:{Id:%s,Properties:{textures:[{Value:\"%s\"}]}}}",
                    serializeUUID(UUID.randomUUID()), texture);

            NBTTagCompound tagCompound = MojangsonParser.a(nbtString);
            nmsItemStack.b(tagCompound);
        } catch (Exception exception) {
            throw new RuntimeException(exception);
        }

        return CraftItemStack.asBukkitCopy(nmsItemStack);
    }

    private static String serializeUUID(UUID uuid) {
        if (ServerVersion.isAtLeast(ServerVersion.v1_16)) {
            long mostSignificantBits = uuid.getMostSignificantBits();
            long leastSignificantBits =  uuid.getLeastSignificantBits();

            return "[I;" +
                    (mostSignificantBits >> 32) + "," +
                    (mostSignificantBits & Integer.MAX_VALUE) + "," +
                    (leastSignificantBits >> 32) + "," +
                    (leastSignificantBits & Integer.MAX_VALUE) + "]";
        } else {
            return '"' + uuid.toString() + '"';
        }
    }

    public static String getNullPlayerTexture() {
        return NULL_PLAYER_TEXTURE;
    }

    public static String getTexture(String entityType) {
        return entitySkullTextures.getOrDefault(entityType, "");
    }

}