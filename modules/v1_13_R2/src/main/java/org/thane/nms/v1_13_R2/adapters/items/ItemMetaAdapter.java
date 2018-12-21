package org.thane.nms.v1_13_R2.adapters.items;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.block.BlockState;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.TropicalFish;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.*;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionEffect;
import org.xml.sax.helpers.AttributeListImpl;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class ItemMetaAdapter extends TypeAdapter<ItemMeta> {
    private Gson gson;

    public ItemMetaAdapter(Gson gson) {
        this.gson = gson;
    }

    @Override
    public void write(JsonWriter out, ItemMeta value) throws IOException {
        out.beginObject();
        if (value instanceof BannerMeta) {
            out.name("banner patterns");
            gson.getAdapter(List.class).write(out, ((BannerMeta) value).getPatterns());

        } else if (value instanceof BlockStateMeta) {
            if (((BlockStateMeta) value).hasBlockState()) {
                out.name("block state");
                gson.getAdapter(BlockState.class).write(out, ((BlockStateMeta) value).getBlockState());
            }
        } else if (value instanceof BookMeta) {
            if (((BookMeta) value).hasTitle())
                out.name("title").value(((BookMeta) value).getTitle());
            if (((BookMeta) value).hasAuthor())
                out.name("author").value(((BookMeta) value).getAuthor());
            if (((BookMeta) value).hasGeneration()) {
                out.name("generation");
                gson.getAdapter(BookMeta.Generation.class).write(out, ((BookMeta) value).getGeneration());
            }
            if (((BookMeta) value).hasPages()) {
                out.name("pages");
                gson.getAdapter(List.class).write(out, ((BookMeta) value).getPages());
            }
        } else if (value instanceof EnchantmentStorageMeta) {
            if (((EnchantmentStorageMeta) value).hasStoredEnchants()) {
                out.name("stored enchantments");
                gson.getAdapter(Map.class).write(out, ((EnchantmentStorageMeta) value).getStoredEnchants());
            }
        } else if (value instanceof FireworkEffectMeta) {
            if (((FireworkEffectMeta) value).hasEffect()) {
                out.name("firework effect");
                gson.getAdapter(FireworkEffect.class).write(out, ((FireworkEffectMeta) value).getEffect());
            }
        } else if (value instanceof KnowledgeBookMeta) {
            if (((KnowledgeBookMeta) value).hasRecipes()) {
                out.name("stored recipes");
                gson.getAdapter(List.class).write(out, ((KnowledgeBookMeta) value).getRecipes());
            }
        } else if (value instanceof LeatherArmorMeta) {
            out.name("leather color");
            gson.getAdapter(Color.class).write(out, ((LeatherArmorMeta) value).getColor());
        } else if (value instanceof MapMeta) {
            if (((MapMeta) value).hasMapId())
                out.name("map id").value(((MapMeta) value).getMapId());
            if (((MapMeta) value).hasLocationName())
                out.name("location name").value(((MapMeta) value).getLocationName());
            if (((MapMeta) value).isScaling())
                out.name("scaling").value(((MapMeta) value).isScaling());
            if (((MapMeta) value).hasColor()) {
                out.name("map color");
                gson.getAdapter(Color.class).write(out, ((MapMeta) value).getColor());
            }
        } else if (value instanceof PotionMeta) {
            out.name("base potion data");
            gson.getAdapter(PotionData.class).write(out, ((PotionMeta) value).getBasePotionData());
            if (((PotionMeta) value).hasColor()) {
                out.name("potion color");
                gson.getAdapter(Color.class).write(out, ((PotionMeta) value).getColor());
            }
            if (((PotionMeta) value).hasCustomEffects()) {
                out.name("custom effects");
                gson.getAdapter(List.class).write(out, ((PotionMeta) value).getCustomEffects());
            }
        } else if (value instanceof SkullMeta) {
            if (((SkullMeta) value).hasOwner()) {
                out.name("skull owner");
                gson.getAdapter(OfflinePlayer.class).write(out, ((SkullMeta) value).getOwningPlayer());
            }
        } else if (value instanceof TropicalFishBucketMeta) {
            if (((TropicalFishBucketMeta) value).hasVariant()) {
                out.name("body color");
                gson.getAdapter(DyeColor.class).write(out, ((TropicalFishBucketMeta) value).getBodyColor());
                out.name("pattern");
                gson.getAdapter(TropicalFish.Pattern.class).write(out, ((TropicalFishBucketMeta) value).getPattern());
                out.name("pattern color");
                gson.getAdapter(DyeColor.class).write(out, ((TropicalFishBucketMeta) value).getPatternColor());
            }
        }
        if (value instanceof Damageable) {
            if (((Damageable) value).hasDamage()) {
                out.name("damage").value(((Damageable) value).getDamage());
            }
        }
        if (value.hasDisplayName())
            out.name("display name").value(value.getDisplayName());
        if (value.hasLocalizedName())
            out.name("localized name").value(value.getLocalizedName());
        if (value.hasLore()) {
            out.name("lore");
            gson.getAdapter(List.class).write(out, value.getLore());
        }
        if (value.isUnbreakable())
            out.name("unbreakable").value(value.isUnbreakable());
        if (value.hasEnchants()) {
            out.name("enchantments");
            gson.getAdapter(Map.class).write(out, value.getEnchants());
        }
        if (value.hasAttributeModifiers()) {
            out.name("attribute modifiers");
            gson.getAdapter(Map.class).write(out, value.getAttributeModifiers().asMap());
        }
        out.endObject();
    }

    @SuppressWarnings({"ConstantConditions", "unchecked"})
    @Override
    public ItemMeta read(JsonReader in) throws IOException {
        ItemMeta meta = null;
        in.beginObject();
        while (in.hasNext()) {
            if (in.peek() == JsonToken.NAME) {
                switch (in.nextName()) {
                    case "banner patterns":
                        if (!(meta instanceof BannerMeta))
                            meta = Bukkit.getServer().getItemFactory().getItemMeta(Material.WHITE_BANNER);
                        ((BannerMeta) meta).setPatterns(gson.getAdapter(List.class).read(in));
                        break;
                    case "block state":
                        BlockState state = gson.getAdapter(BlockState.class).read(in);
                        if (!(meta instanceof BlockStateMeta)) {
                            meta = Bukkit.getServer().getItemFactory().getItemMeta(state.getType());
                        }
                        ((BlockStateMeta) meta).setBlockState(state);
                        break;
                    case "title":
                        if (!(meta instanceof BookMeta))
                            meta = Bukkit.getServer().getItemFactory().getItemMeta(Material.WRITTEN_BOOK);
                        ((BookMeta) meta).setTitle(in.nextString());
                        break;
                    case "author":
                        if (!(meta instanceof BookMeta))
                            meta = Bukkit.getServer().getItemFactory().getItemMeta(Material.WRITTEN_BOOK);
                        ((BookMeta) meta).setAuthor(in.nextString());
                        break;
                    case "generation":
                        if (!(meta instanceof BookMeta))
                            meta = Bukkit.getServer().getItemFactory().getItemMeta(Material.WRITTEN_BOOK);
                        ((BookMeta) meta).setGeneration(gson.getAdapter(BookMeta.Generation.class).read(in));
                        break;
                    case "pages":
                        if (!(meta instanceof BookMeta))
                            meta = Bukkit.getServer().getItemFactory().getItemMeta(Material.WRITTEN_BOOK);
                        ((BookMeta) meta).setPages(gson.getAdapter(List.class).read(in));
                        break;
                    case "stored enchantments":
                        if (!(meta instanceof EnchantmentStorageMeta))
                            meta = Bukkit.getServer().getItemFactory().getItemMeta(Material.ENCHANTED_BOOK);
                        for (Map.Entry<Enchantment, Integer> entry : ((Map<Enchantment, Integer>) gson.getAdapter(TypeToken.get(new TypeToken<Map<Enchantment, Integer>>() {
                        }.getType())).read(in)).entrySet()) {
                            ((EnchantmentStorageMeta) meta).addStoredEnchant(entry.getKey(), entry.getValue(), true);
                        }
                        break;
                    case "firework effect":
                        if (!(meta instanceof FireworkEffectMeta))
                            meta = Bukkit.getServer().getItemFactory().getItemMeta(Material.FIREWORK_ROCKET);
                        ((FireworkEffectMeta) meta).setEffect(gson.getAdapter(FireworkEffect.class).read(in));
                        break;
                    case "stored recipes":
                        if (!(meta instanceof KnowledgeBookMeta))
                            meta = Bukkit.getServer().getItemFactory().getItemMeta(Material.KNOWLEDGE_BOOK);
                        ((KnowledgeBookMeta) meta).setRecipes(gson.getAdapter(List.class).read(in));
                        break;
                    case "leather color":
                        if (!(meta instanceof LeatherArmorMeta))
                            meta = Bukkit.getServer().getItemFactory().getItemMeta(Material.LEATHER_CHESTPLATE);
                        ((LeatherArmorMeta) meta).setColor(gson.getAdapter(Color.class).read(in));
                        break;
                    case "map id":
                        if (!(meta instanceof MapMeta))
                            meta = Bukkit.getServer().getItemFactory().getItemMeta(Material.FILLED_MAP);
                        ((MapMeta) meta).setMapId(in.nextInt());
                        break;
                    case "location name":
                        if (!(meta instanceof MapMeta))
                            meta = Bukkit.getServer().getItemFactory().getItemMeta(Material.FILLED_MAP);
                        ((MapMeta) meta).setLocationName(in.nextString());
                        break;
                    case "scaling":
                        if (!(meta instanceof MapMeta))
                            meta = Bukkit.getServer().getItemFactory().getItemMeta(Material.FILLED_MAP);
                        ((MapMeta) meta).setScaling(in.nextBoolean());
                        break;
                    case "map color":
                        if (!(meta instanceof MapMeta))
                            meta = Bukkit.getServer().getItemFactory().getItemMeta(Material.FILLED_MAP);
                        ((MapMeta) meta).setColor(gson.getAdapter(Color.class).read(in));
                        break;
                    case "base potion data":
                        if (!(meta instanceof PotionMeta))
                            meta = Bukkit.getServer().getItemFactory().getItemMeta(Material.POTION);
                        ((PotionMeta) meta).setBasePotionData(gson.getAdapter(PotionData.class).read(in));
                        break;
                    case "potion color":
                        if (!(meta instanceof PotionMeta))
                            meta = Bukkit.getServer().getItemFactory().getItemMeta(Material.POTION);
                        ((PotionMeta) meta).setColor(gson.getAdapter(Color.class).read(in));
                        break;
                    case "custom effects":
                        if (!(meta instanceof PotionMeta))
                            meta = Bukkit.getServer().getItemFactory().getItemMeta(Material.POTION);
                        for (PotionEffect potionEffect : (List<PotionEffect>) gson.getAdapter(TypeToken.get(new TypeToken<List<PotionEffect>>() {
                        }.getType())).read(in)) {
                            ((PotionMeta) meta).addCustomEffect(potionEffect, true);
                        }
                        break;
                    case "skull owner":
                        if (!(meta instanceof SkullMeta))
                            meta = Bukkit.getServer().getItemFactory().getItemMeta(Material.PLAYER_HEAD);
                        ((SkullMeta) meta).setOwningPlayer(gson.getAdapter(OfflinePlayer.class).read(in));
                        break;
                    case "body color":
                        if (!(meta instanceof TropicalFishBucketMeta))
                            meta = Bukkit.getServer().getItemFactory().getItemMeta(Material.TROPICAL_FISH_BUCKET);
                        ((TropicalFishBucketMeta) meta).setBodyColor(gson.getAdapter(DyeColor.class).read(in));
                        break;
                    case "pattern":
                        if (!(meta instanceof TropicalFishBucketMeta))
                            meta = Bukkit.getServer().getItemFactory().getItemMeta(Material.TROPICAL_FISH_BUCKET);
                        ((TropicalFishBucketMeta) meta).setPattern(gson.getAdapter(TropicalFish.Pattern.class).read(in));
                        break;
                    case "pattern color":
                        if (!(meta instanceof TropicalFishBucketMeta))
                            meta = Bukkit.getServer().getItemFactory().getItemMeta(Material.TROPICAL_FISH_BUCKET);
                        ((TropicalFishBucketMeta) meta).setPatternColor(gson.getAdapter(DyeColor.class).read(in));
                        break;
                    case "display name":
                        if (meta == null) meta = Bukkit.getServer().getItemFactory().getItemMeta(Material.WOODEN_AXE);
                        meta.setDisplayName(in.nextString());
                        break;
                    case "localized name":
                        if (meta == null) meta = Bukkit.getServer().getItemFactory().getItemMeta(Material.WOODEN_AXE);
                        meta.setLocalizedName(in.nextString());
                        break;
                    case "lore":
                        if (meta == null) meta = Bukkit.getServer().getItemFactory().getItemMeta(Material.WOODEN_AXE);
                        meta.setLore(gson.getAdapter(List.class).read(in));
                        break;
                    case "unbreakable":
                        if (meta == null) meta = Bukkit.getServer().getItemFactory().getItemMeta(Material.WOODEN_AXE);
                        meta.setUnbreakable(in.nextBoolean());
                        break;
                    case "enchantments":
                        if (meta == null) meta = Bukkit.getServer().getItemFactory().getItemMeta(Material.WOODEN_AXE);
                        for (Map.Entry<Enchantment, Integer> entry : ((Map<Enchantment, Integer>) gson.getAdapter(TypeToken.get(new TypeToken<Map<Enchantment, Integer>>() {
                        }.getType())).read(in)).entrySet()) {
                            meta.addEnchant(entry.getKey(), entry.getValue(), true);
                        }
                        break;
                    case "attribute modifiers":
                        if (meta == null) meta = Bukkit.getServer().getItemFactory().getItemMeta(Material.WOODEN_AXE);
                        Multimap<Attribute, AttributeModifier> multimap = HashMultimap.create();
                        for (Map.Entry<Attribute, Collection<AttributeModifier>> entry : ((Map<Attribute, Collection<AttributeModifier>>) gson.getAdapter(TypeToken.get(new TypeToken<Map<Attribute, Collection<AttributeModifier>>>() {}.getType())).read(in)).entrySet()) {
                            multimap.putAll(entry.getKey(), entry.getValue());
                        }
                        // this line is to force the "checkAttributeList" method in CraftMetaItem without reflection or version dependency
                        meta.addAttributeModifier(Attribute.GENERIC_FOLLOW_RANGE, new AttributeModifier("String", 1, AttributeModifier.Operation.ADD_NUMBER));
                        meta.removeAttributeModifier(Attribute.GENERIC_FOLLOW_RANGE);
                        meta.setAttributeModifiers(multimap);
                        break;
                    case "damage":
                        if (meta == null) meta = Bukkit.getServer().getItemFactory().getItemMeta(Material.WOODEN_AXE);
                        ((Damageable) meta).setDamage(in.nextInt());
                        break;
                }
            }
        }
        in.endObject();
        return meta;
    }
}