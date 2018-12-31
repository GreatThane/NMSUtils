package org.thane;

import com.google.gson.*;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.material.MaterialData;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.thane.adapters.BooleanSerializer;
import org.thane.api.ItemStackTypeAdapterFactory;
import org.thane.api.NBT;

import java.io.*;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Files;

public class NMSUtils extends JavaPlugin {

    private static String version;
    private static ItemStackTypeAdapterFactory itemStackTypeAdapterFactory;

    private static Gson GSON;

    static {
        try {
            String packageName = Bukkit.getServer().getClass().getPackage().getName();
            version = packageName.substring(packageName.lastIndexOf('.') + 1);

            itemStackTypeAdapterFactory = (ItemStackTypeAdapterFactory) Class.forName("org.thane.nms." + version + ".ItemStackTypeAdapterFactory").getConstructor().newInstance();
            GSON = new GsonBuilder().setPrettyPrinting().enableComplexMapKeySerialization().disableHtmlEscaping().registerTypeAdapterFactory(itemStackTypeAdapterFactory)
                    .registerTypeAdapter(Boolean.class, new BooleanSerializer()).registerTypeAdapter(boolean.class, new BooleanSerializer())
                    .setExclusionStrategies(new ExclusionStrategy() {
                @Override
                public boolean shouldSkipField(FieldAttributes f) {
                    return (f.getDeclaringClass().getPackage().getName().contains("net.minecraft.server") || f.getDeclaringClass().getPackage().getName().contains("org.bukkit.craftbukkit"))
                            && f.getName().equalsIgnoreCase("handle");
                }

                @Override
                public boolean shouldSkipClass(Class<?> clazz) {
                    return false;
                }
            }).create();
            NBT.setGson(GSON);
            //noinspection unchecked
            Class<NBT> nbtClass = (Class<NBT>) Class.forName("org.thane.nms." + version + ".NBT");
            itemNBTConstructor = nbtClass.getConstructor(ItemStack.class);
            jsonNBTConstructor = nbtClass.getConstructor(JsonObject.class);
        } catch (ClassNotFoundException | NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onEnable() {
        // Plugin startup logic
        if (!getDataFolder().exists()) getDataFolder().mkdirs();
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic

    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("test")) {
            if (sender instanceof Player && sender.isOp()) {
                Player player = (Player) sender;
                File file = new File(getDataFolder().getAbsolutePath() + File.separatorChar + player.getUniqueId() + ".json");

                if (args[0].equalsIgnoreCase("write")) {
                    String json = GSON.toJson(((Player) sender).getInventory().getContents());
                    if (!file.exists()) {
                        try {
                            file.createNewFile();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    try (PrintStream out = new PrintStream(new FileOutputStream(file))) {
                        out.print(json);

                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                } else if (args[0].equalsIgnoreCase("item")) {
                    ItemStack itemStack = new ItemStack(Material.DIAMOND_PICKAXE);
                    ItemMeta meta = itemStack.getItemMeta();
                    meta.addAttributeModifier(Attribute.GENERIC_MOVEMENT_SPEED, new AttributeModifier("attribute modifier name", 24, AttributeModifier.Operation.MULTIPLY_SCALAR_1));
                    meta.setLocalizedName("local name");
                    meta.setDisplayName("display name");
                    meta.setUnbreakable(true);
                    meta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
                    itemStack.setItemMeta(meta);
                    NBT nbt = NMSUtils.getNBT(itemStack);
                    nbt.addNBT("this is some nice custom NBT!", "You bet it is!");
                    nbt.addNBT("And this is a custom object", player);
                    player.getInventory().addItem(nbt.copyOnto(itemStack));
                } else {
                    ItemStack[] contents = new ItemStack[0];
                    try {
                        contents = GSON.fromJson(new String(Files.readAllBytes(file.toPath())), ItemStack[].class);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    player.getInventory().setContents(contents);
                }
            }
            return true;
        }
        return false;
    }

    public static Gson getGson() {
        return GSON;
    }

    public static void setGson(Gson gson) {
        GSON = gson;
    }

    public static ItemStackTypeAdapterFactory getItemStackTypeAdapter() {
        return itemStackTypeAdapterFactory;
    }

    private static Constructor<NBT> itemNBTConstructor;
    private static Constructor<NBT> jsonNBTConstructor;

    public static NBT getNBT(ItemStack stack) {
        try {
            return itemNBTConstructor.newInstance(stack);
        } catch (InstantiationException | InvocationTargetException | IllegalAccessException e) {
            e.printStackTrace();
            return new NBT(new JsonObject());
        }
    }

    public static NBT toNBT(JsonObject object) {
        try {
            return jsonNBTConstructor.newInstance(object);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
            return new NBT(new JsonObject());
        }
    }

    public static NBT getEmptyNBT() {
        return toNBT(new JsonObject());
    }
}
