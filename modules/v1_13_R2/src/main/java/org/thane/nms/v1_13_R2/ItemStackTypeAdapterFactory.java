package org.thane.nms.v1_13_R2;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.reflect.TypeToken;
import net.minecraft.server.v1_13_R2.AttributeRanged;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.data.BlockData;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.material.MaterialData;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.thane.api.NBT;
import org.thane.nms.v1_13_R2.adapters.blocks.*;
import org.thane.nms.v1_13_R2.adapters.entities.OfflinePlayerAdapter;
import org.thane.nms.v1_13_R2.adapters.entities.effects.PotionEffectAdapter;
import org.thane.nms.v1_13_R2.adapters.enums.EnchantmentAdapter;
import org.thane.nms.v1_13_R2.adapters.entities.effects.PotionEffectTypeAdapter;
import org.thane.nms.v1_13_R2.adapters.items.*;
import org.thane.nms.v1_13_R2.adapters.nms.AttributeRangedAdapter;

public class ItemStackTypeAdapterFactory extends org.thane.api.ItemStackTypeAdapterFactory {
    @SuppressWarnings("unchecked cast")
    public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> type) {
        Class<T> clazz = (Class<T>) type.getRawType();

        if (Block.class.isAssignableFrom(clazz)) {
            return (TypeAdapter<T>) new BlockAdapter(gson);
        } else if (BlockData.class.isAssignableFrom(clazz)) {
            return (TypeAdapter<T>) new BlockDataAdapter();
        } else if (BlockState.class.isAssignableFrom(clazz)) {
            return (TypeAdapter<T>) new BlockStateAdapter(gson);
        } else if (Chunk.class.isAssignableFrom(clazz)) {
            return (TypeAdapter<T>) new ChunkAdapter(gson);
        } else if (Location.class.isAssignableFrom(clazz)) {
            return (TypeAdapter<T>) new LocationAdapter(gson);
        } else if (World.class.isAssignableFrom(clazz)) {
            return (TypeAdapter<T>) new WorldAdapter();
        } else if (OfflinePlayer.class.isAssignableFrom(clazz)) {
            return (TypeAdapter<T>) new OfflinePlayerAdapter();
        } else if (ItemMeta.class.isAssignableFrom(clazz)) {
            return (TypeAdapter<T>) new ItemMetaAdapter(gson);
        } else if (ItemStack.class.isAssignableFrom(clazz)) {
            return (TypeAdapter<T>) new ItemStackAdapter(gson);
        } else if (MaterialData.class.isAssignableFrom(clazz)) {
            return (TypeAdapter<T>) new MaterialDataAdapter(gson);
        } else if (Enchantment.class.isAssignableFrom(clazz)) {
            return (TypeAdapter<T>) new EnchantmentAdapter();
        } else if (Inventory.class.isAssignableFrom(clazz)) {
            return (TypeAdapter<T>) new InventoryAdapter(gson);
        } else if (AttributeModifier.class.isAssignableFrom(clazz)) {
            return (TypeAdapter<T>) new AttributeModifierAdapter(gson);
        } else if (NBT.class.isAssignableFrom(clazz)) {
            return (TypeAdapter<T>) new NBTAdapter(gson);
        } else if (AttributeRanged.class.isAssignableFrom(clazz)) {
            return (TypeAdapter<T>) new AttributeRangedAdapter();
        } else if (PotionEffectType.class.isAssignableFrom(clazz)) {
            return (TypeAdapter<T>) new PotionEffectTypeAdapter();
        } else if (PotionEffect.class.isAssignableFrom(clazz)) {
            return (TypeAdapter<T>) new PotionEffectAdapter(gson);
        } else return null;
    }
}
