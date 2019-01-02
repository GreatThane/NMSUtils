package org.thane.nms.v1_13_R2.adapters.items.meta;

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
import org.bukkit.inventory.meta.*;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionEffect;

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
        writeMeta(gson, out, value);
        out.endObject();
    }

    @Override
    public ItemMeta read(JsonReader in) throws IOException {
        ItemMeta meta = Bukkit.getServer().getItemFactory().getItemMeta(Material.WOODEN_AXE);
        in.beginObject();
        while (in.hasNext()) {
            if (in.peek() == JsonToken.NAME) {
                readMeta(gson, in.nextName(), in, meta);
            }
        }
        in.endObject();
        return meta;
    }

    public static void writeMeta(Gson gson, JsonWriter out, ItemMeta value) throws IOException {
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
    }

    @SuppressWarnings("unchecked")
    public static void readMeta(Gson gson, String name, JsonReader in, ItemMeta meta) throws IOException {
        switch (name) {
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
                for (Map.Entry<Attribute, Collection<AttributeModifier>> entry : ((Map<Attribute, Collection<AttributeModifier>>) gson.getAdapter(TypeToken.get(new TypeToken<Map<Attribute, Collection<AttributeModifier>>>() {
                }.getType())).read(in)).entrySet()) {
                    multimap.putAll(entry.getKey(), entry.getValue());
                }
                // this line is to force the "checkAttributeList" method in CraftMetaItem without reflection
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