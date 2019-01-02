package org.thane.nms.v1_13_R2;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.reflect.TypeToken;
import net.minecraft.server.v1_13_R2.AttributeRanged;
import org.bukkit.*;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.block.*;
import org.bukkit.block.data.BlockData;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.*;
import org.bukkit.material.MaterialData;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.thane.api.NBT;
import org.thane.nms.v1_13_R2.adapters.NamespacedKeyAdapter;
import org.thane.nms.v1_13_R2.adapters.blocks.*;
import org.thane.nms.v1_13_R2.adapters.blocks.states.*;
import org.thane.nms.v1_13_R2.adapters.entities.EntityAdapter;
import org.thane.nms.v1_13_R2.adapters.entities.OfflinePlayerAdapter;
import org.thane.nms.v1_13_R2.adapters.entities.effects.PotionEffectAdapter;
import org.thane.nms.v1_13_R2.adapters.enums.EnchantmentAdapter;
import org.thane.nms.v1_13_R2.adapters.entities.effects.PotionEffectTypeAdapter;
import org.thane.nms.v1_13_R2.adapters.items.*;
import org.thane.nms.v1_13_R2.adapters.items.meta.*;
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
            if (Banner.class.isAssignableFrom(clazz)) {
                return (TypeAdapter<T>) new BannerAdapter(gson);
            } else if (Beacon.class.isAssignableFrom(clazz)) {
                return (TypeAdapter<T>) new BeaconAdapter(gson);
            } else if (CommandBlock.class.isAssignableFrom(clazz)) {
                return (TypeAdapter<T>) new CommandBlockAdapter(gson);
            } else if (CreatureSpawner.class.isAssignableFrom(clazz)) {
                return (TypeAdapter<T>) new CreatureSpawnerAdapter(gson);
            } else if (EndGateway.class.isAssignableFrom(clazz)) {
                return (TypeAdapter<T>) new EndGatewayAdapter(gson);
            } else if (Sign.class.isAssignableFrom(clazz)) {
                return (TypeAdapter<T>) new SignAdapter(gson);
            } else if (Skull.class.isAssignableFrom(clazz)) {
                return (TypeAdapter<T>) new SkullAdapter(gson);
            } else if (Structure.class.isAssignableFrom(clazz)) {
                return (TypeAdapter<T>) new StructureAdapter(gson);
            } else return (TypeAdapter<T>) new BlockStateAdapter(gson);
        } else if (Chunk.class.isAssignableFrom(clazz)) {
            return (TypeAdapter<T>) new ChunkAdapter(gson);
        } else if (Location.class.isAssignableFrom(clazz)) {
            return (TypeAdapter<T>) new LocationAdapter(gson);
        } else if (World.class.isAssignableFrom(clazz)) {
            return (TypeAdapter<T>) new WorldAdapter();
        } else if (OfflinePlayer.class.isAssignableFrom(clazz)) {
            return (TypeAdapter<T>) new OfflinePlayerAdapter();
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
        } else if (Entity.class.isAssignableFrom(clazz)) {
            return (TypeAdapter<T>) new EntityAdapter();
        } else if (ItemMeta.class.isAssignableFrom(clazz)) {
            if (BannerMeta.class.isAssignableFrom(clazz)) {
                return (TypeAdapter<T>) new BannerMetaAdapter(gson);
            }  else if (BlockStateMeta.class.isAssignableFrom(clazz)) {
                return (TypeAdapter<T>) new BlockStateMetaAdapter(gson);
            } else if (BookMeta.class.isAssignableFrom(clazz)) {
                return (TypeAdapter<T>) new BookMetaAdapter(gson);
            } else if (EnchantmentStorageMeta.class.isAssignableFrom(clazz)) {
                return (TypeAdapter<T>) new EnchantmentStorageMetaAdapter(gson);
            } else if (FireworkEffectMeta.class.isAssignableFrom(clazz)) {
                return (TypeAdapter<T>) new FireworkEffectMetaAdapter(gson);
            } else if (KnowledgeBookMeta.class.isAssignableFrom(clazz)) {
                return (TypeAdapter<T>) new KnowledgeBookMetaAdapter(gson);
            } else if (LeatherArmorMeta.class.isAssignableFrom(clazz)) {
                return (TypeAdapter<T>) new LeatherArmorMetaAdapter(gson);
            } else if (MapMeta.class.isAssignableFrom(clazz)) {
                return (TypeAdapter<T>) new MapMetaAdapter(gson);
            } else if (PotionMeta.class.isAssignableFrom(clazz)) {
                return (TypeAdapter<T>) new PotionMetaAdapter(gson);
            } else if (SkullMeta.class.isAssignableFrom(clazz)) {
                return (TypeAdapter<T>) new SkullMetaAdapter(gson);
            } else if (TropicalFishBucketMeta.class.isAssignableFrom(clazz)) {
                return (TypeAdapter<T>) new TropicalFishBucketMetaAdapter(gson);
            } else return (TypeAdapter<T>) new ItemMetaAdapter(gson);
        } else if (NamespacedKey.class.isAssignableFrom(clazz)) {
            return (TypeAdapter<T>) new NamespacedKeyAdapter();
        } else return null;
    }
}
