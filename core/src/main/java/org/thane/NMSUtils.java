package org.thane;

import com.google.gson.*;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.banner.Pattern;
import org.bukkit.block.banner.PatternType;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.TropicalFish;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.*;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;
import org.thane.adapters.BooleanSerializer;
import org.thane.api.ItemStackTypeAdapterFactory;
import org.thane.api.NBT;

import java.io.*;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Files;
import java.util.Collections;

@SuppressWarnings("Duplicates")
public class NMSUtils extends JavaPlugin {

    static String VERSION;
    private static ItemStackTypeAdapterFactory itemStackTypeAdapterFactory;

    private static GsonBuilder GSON_BUILDER;
    private static Gson GSON;

    static {
        try {
            String packageName = Bukkit.getServer().getClass().getPackage().getName();
            VERSION = packageName.substring(packageName.lastIndexOf('.') + 1);

            itemStackTypeAdapterFactory = (ItemStackTypeAdapterFactory) Class.forName("org.thane.nms." + VERSION + ".ItemStackTypeAdapterFactory").getConstructor().newInstance();
            GSON_BUILDER = new GsonBuilder().setPrettyPrinting().enableComplexMapKeySerialization().disableHtmlEscaping().registerTypeAdapterFactory(itemStackTypeAdapterFactory)
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
            });
            GSON = GSON_BUILDER.create();
            NBT.setGson(GSON);
            //noinspection unchecked
            Class<NBT> nbtClass = (Class<NBT>) Class.forName("org.thane.nms." + VERSION + ".NBT");
            itemNBTConstructor = nbtClass.getConstructor(ItemStack.class);
            jsonNBTConstructor = nbtClass.getConstructor(JsonObject.class);
            entityNBTConstructor = nbtClass.getConstructor(Entity.class);
            blockNBTConstructor = nbtClass.getConstructor(Block.class);
        } catch (ClassNotFoundException e) {
            Bukkit.getLogger().warning("Unable to find specific version support, switching to reflection fallback classes...");
        } catch (InvocationTargetException e) {
            e.getCause().printStackTrace();
            e.getTargetException().printStackTrace();
        } catch (NoSuchMethodException | InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public NMSUtils() {
        INSTANCE = this;
    }

    public static NMSUtils INSTANCE;

    public static GsonBuilder getBuilder() {
        return GSON_BUILDER;
    }

    public static void instantiateGson() {
        GSON = GSON_BUILDER.create();
        NBT.setGson(GSON);
    }

    @Override
    public void onEnable() {
        // Plugin startup logic
        if (!getDataFolder().exists()) getDataFolder().mkdirs();
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
                    ItemStack stack = new ItemStack(Material.DIAMOND_PICKAXE);
                    ItemMeta meta = stack.getItemMeta();
                    meta.addAttributeModifier(Attribute.GENERIC_MOVEMENT_SPEED, new AttributeModifier("attribute modifier name", 24, AttributeModifier.Operation.MULTIPLY_SCALAR_1));
                    meta.setLocalizedName("local name");
                    meta.setDisplayName("display name");
                    meta.setUnbreakable(true);
                    meta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
                    meta.addEnchant(Enchantment.DIG_SPEED, 5, true);
                    meta.setLore(Collections.singletonList("Some Lore"));
                    ((Damageable) meta).setDamage(94);
                    stack.setItemMeta(meta);
                    NBT nbt = NMSUtils.getNBT(stack);
                    nbt.addNBT("this is some nice custom NBT!", "You bet it is!");
                    nbt.addNBT("And this is a custom object", player);
                    player.getInventory().addItem(nbt.copyOnto(stack));
                    stack = new ItemStack(Material.WHITE_BANNER);
                    meta = stack.getItemMeta();
                    ((BannerMeta) meta).addPattern(new Pattern(DyeColor.RED, PatternType.BRICKS));
                    stack.setItemMeta(meta);
                    player.getInventory().addItem(stack);
                    stack = new ItemStack(Material.CHEST);
                    meta = stack.getItemMeta();
                    BlockState state = ((BlockStateMeta) meta).getBlockState();
                    ((InventoryHolder) state).getInventory().setItem(3, new ItemStack(Material.COBBLESTONE));
                    ((BlockStateMeta) meta).setBlockState(state);
                    stack.setItemMeta(meta);
                    player.getInventory().addItem(stack);
                    stack = new ItemStack(Material.WRITTEN_BOOK);
                    meta = stack.getItemMeta();
                    ((BookMeta) meta).setPages("Some text");
                    ((BookMeta) meta).setGeneration(BookMeta.Generation.COPY_OF_ORIGINAL);
                    ((BookMeta) meta).setAuthor("the author");
                    ((BookMeta) meta).setTitle("title string");
                    stack.setItemMeta(meta);
                    player.getInventory().addItem(stack);
                    stack = new ItemStack(Material.ENCHANTED_BOOK);
                    meta = stack.getItemMeta();
                    ((EnchantmentStorageMeta) meta).addStoredEnchant(Enchantment.ARROW_DAMAGE, 2, true);
                    stack.setItemMeta(meta);
                    player.getInventory().addItem(stack);
                    stack = new ItemStack(Material.FIREWORK_ROCKET);
                    meta = stack.getItemMeta();
                    ((FireworkMeta) meta).addEffect(FireworkEffect.builder().flicker(true).trail(true).withColor(Color.BLUE, Color.FUCHSIA).withFade(Color.ORANGE).with(FireworkEffect.Type.BURST).build());
                    stack.setItemMeta(meta);
                    player.getInventory().addItem(stack);
                    stack = new ItemStack(Material.KNOWLEDGE_BOOK);
                    meta = stack.getItemMeta();
                    ((KnowledgeBookMeta) meta).addRecipe(new NamespacedKey("minecraft", "diamond_hoe"));
                    stack.setItemMeta(meta);
                    player.getInventory().addItem(stack);
                    stack = new ItemStack(Material.LEATHER_CHESTPLATE);
                    meta = stack.getItemMeta();
                    ((LeatherArmorMeta) meta).setColor(Color.GRAY);
                    stack.setItemMeta(meta);
                    player.getInventory().addItem(stack);
                    stack = new ItemStack(Material.FILLED_MAP);
                    meta = stack.getItemMeta();
                    ((MapMeta) meta).setScaling(true);
                    ((MapMeta) meta).setMapId(23);
                    ((MapMeta) meta).setLocationName("Custom location");
                    ((MapMeta) meta).setColor(Color.LIME);
                    stack.setItemMeta(meta);
                    player.getInventory().addItem(stack);
                    stack = new ItemStack(Material.POTION);
                    meta = stack.getItemMeta();
                    ((PotionMeta) meta).setColor(Color.GREEN);
                    ((PotionMeta) meta).setBasePotionData(new PotionData(PotionType.REGEN));
                    ((PotionMeta) meta).addCustomEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, 1, 1, true, true), true);
                    stack.setItemMeta(meta);
                    player.getInventory().addItem(stack);
                    stack = new ItemStack(Material.PLAYER_HEAD);
                    meta = stack.getItemMeta();
                    ((SkullMeta) meta).setOwningPlayer(player);
                    stack.setItemMeta(meta);
                    player.getInventory().addItem(stack);
                    stack = new ItemStack(Material.TROPICAL_FISH_BUCKET);
                    meta = stack.getItemMeta();
                    ((TropicalFishBucketMeta) meta).setPattern(TropicalFish.Pattern.CLAYFISH);
                    ((TropicalFishBucketMeta) meta).setPatternColor(DyeColor.LIME);
                    ((TropicalFishBucketMeta) meta).setBodyColor(DyeColor.MAGENTA);
                    stack.setItemMeta(meta);
                    player.getInventory().addItem(stack);
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
    private static Constructor<NBT> entityNBTConstructor;
    private static Constructor<NBT> blockNBTConstructor;

    public static NBT getNBT(ItemStack stack) {
        try {
            return itemNBTConstructor.newInstance(stack);
        } catch (InvocationTargetException e) {
            e.getCause().printStackTrace();
            e.getTargetException().printStackTrace();
            return new NBT(new JsonObject());
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
            return new NBT(new JsonObject());
        }
    }

    public static NBT getNBT(Entity entity) {
        try {
            return entityNBTConstructor.newInstance(entity);
        } catch (InvocationTargetException e) {
            e.getCause().printStackTrace();
            e.getTargetException().printStackTrace();
            return new NBT(new JsonObject());
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
            return new NBT(new JsonObject());
        }
    }

    public static NBT getNBT(Block block) {
        try {
            return blockNBTConstructor.newInstance(block);
        } catch (InvocationTargetException e) {
            e.getCause().printStackTrace();
            e.getTargetException().printStackTrace();
            return new NBT(new JsonObject());
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
            return new NBT(new JsonObject());
        }
    }

    public static NBT toNBT(JsonObject object) {
        try {
            return jsonNBTConstructor.newInstance(object);
        } catch (InvocationTargetException e) {
            e.getCause().printStackTrace();
            e.getTargetException().printStackTrace();
            return new NBT(new JsonObject());
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
            return new NBT(new JsonObject());
        }
    }

    public static NBT getEmptyNBT() {
        return toNBT(new JsonObject());
    }

    public static NBT deserializeNBT(String string) {
        try {
            return jsonNBTConstructor.newInstance(new JsonParser().parse(string).getAsJsonObject());
        } catch (InvocationTargetException e) {
            e.getCause().printStackTrace();
            e.getTargetException().printStackTrace();
            return new NBT(new JsonObject());
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
            return new NBT(new JsonObject());
        }
    }
}
