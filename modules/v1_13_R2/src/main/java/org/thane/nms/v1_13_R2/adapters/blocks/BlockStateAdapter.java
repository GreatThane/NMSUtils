package org.thane.nms.v1_13_R2.adapters.blocks;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import org.bukkit.*;
import org.bukkit.block.*;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.structure.Mirror;
import org.bukkit.block.structure.StructureRotation;
import org.bukkit.block.structure.UsageMode;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;
import org.bukkit.loot.LootTable;
import org.bukkit.loot.Lootable;
import org.bukkit.material.Colorable;
import org.bukkit.material.MaterialData;
import org.bukkit.potion.PotionEffect;
import org.bukkit.util.BlockVector;

import java.io.IOException;
import java.util.List;

public class BlockStateAdapter extends TypeAdapter<BlockState> {
    private Gson gson;

    public BlockStateAdapter(Gson gson) {
        this.gson = gson;
    }

    @SuppressWarnings("deprecation")
    @Override
    public void write(JsonWriter out, BlockState value) throws IOException {
        out.beginObject();
        out.name("material");
        gson.getAdapter(Material.class).write(out, value.getType());
        if (value.isPlaced()) {
            out.name("block");
            gson.getAdapter(Block.class).write(out, value.getBlock());
        }
        out.name("block data");
        gson.getAdapter(BlockData.class).write(out, value.getBlockData());
        out.name("material data");
        gson.getAdapter(MaterialData.class).write(out, value.getData());
        if (value instanceof Banner) {
            out.name("patterns");
            gson.getAdapter(List.class).write(out, ((Banner) value).getPatterns());
            out.name("base color");
            gson.getAdapter(DyeColor.class).write(out, ((Banner) value).getBaseColor());
        }
        if (value instanceof InventoryHolder) {
            out.name("inventory");
            gson.getAdapter(ItemStack[].class).write(out, ((InventoryHolder) value).getInventory().getContents());
        }
        if (value instanceof Beacon) {
            out.name("primary effect");
            gson.getAdapter(PotionEffect.class).write(out, ((Beacon) value).getPrimaryEffect());
            out.name("secondary effect");
            gson.getAdapter(PotionEffect.class).write(out, ((Beacon) value).getSecondaryEffect());
        }
        if (value instanceof Colorable) {
            out.name("color");
            gson.getAdapter(DyeColor.class).write(out, ((Colorable) value).getColor());
        }
        if (value instanceof Lockable && ((Lockable) value).isLocked()) {
            out.name("lock").value(((Lockable) value).getLock());
        }
        if (value instanceof CommandBlock) {
            if (!((CommandBlock) value).getName().equalsIgnoreCase("@")) {
                out.name("name").value(((CommandBlock) value).getName());
            }
            if (!((CommandBlock) value).getCommand().isEmpty()) {
                out.name("command").value(((CommandBlock) value).getCommand());
            }
        }
        if (value instanceof CreatureSpawner) {
            out.name("delay").value(((CreatureSpawner) value).getDelay());
            out.name("maximum nearby entities").value(((CreatureSpawner) value).getMaxNearbyEntities());
            out.name("maximum spawn delay").value(((CreatureSpawner) value).getMaxSpawnDelay());
            out.name("minimum spawn delay").value(((CreatureSpawner) value).getMinSpawnDelay());
            out.name("player range").value(((CreatureSpawner) value).getRequiredPlayerRange());
            out.name("spawn count").value(((CreatureSpawner) value).getSpawnCount());
            out.name("spawn type");
            gson.getAdapter(EntityType.class).write(out, ((CreatureSpawner) value).getSpawnedType());
            out.name("spawn range").value(((CreatureSpawner) value).getSpawnRange());
        }
        if (value instanceof Nameable) {
            out.name("custom name").value(((Nameable) value).getCustomName());
        }
        if (value instanceof Lootable) {
            if (((Lootable) value).getSeed() != 0) {
                out.name("seed").value(((Lootable) value).getSeed());
            }
            out.name("loot table");
            gson.getAdapter(LootTable.class).write(out, ((Lootable) value).getLootTable());
        }
        if (value instanceof EndGateway) {
            out.name("age").value(((EndGateway) value).getAge());
            out.name("exit location");
            gson.getAdapter(Location.class).write(out, ((EndGateway) value).getExitLocation());
            out.name("exact teleport").value(((EndGateway) value).isExactTeleport());
        }
        if (value instanceof Sign) {
            out.name("lines");
            gson.getAdapter(String[].class).write(out, ((Sign) value).getLines());
            out.name("editable").value(((Sign) value).isEditable());
        }
        if (value instanceof Skull && ((Skull) value).hasOwner()) {
            out.name("owning player");
            gson.getAdapter(OfflinePlayer.class).write(out, ((Skull) value).getOwningPlayer());
        }
        if (value instanceof Structure) {
            out.name("author").value(((Structure) value).getAuthor());
            out.name("structure name").value(((Structure) value).getStructureName());
            out.name("integrity").value(((Structure) value).getIntegrity());
            out.name("metadata").value(((Structure) value).getMetadata());
            if (((Structure) value).getSeed() != 0) {
                out.name("seed").value(((Structure) value).getSeed());
            }
            if (value.isPlaced()) {
                out.name("relative position");
                gson.getAdapter(BlockVector.class).write(out, ((Structure) value).getRelativePosition());
                if (((Structure) value).getRotation() != StructureRotation.NONE) {
                    out.name("rotation");
                    gson.getAdapter(StructureRotation.class).write(out, ((Structure) value).getRotation());
                }
                if (((Structure) value).getMirror() != Mirror.NONE) {
                    out.name("mirror");
                    gson.getAdapter(Mirror.class).write(out, ((Structure) value).getMirror());
                }
                out.name("structure size");
                gson.getAdapter(BlockVector.class).write(out, ((Structure) value).getStructureSize());
                out.name("usage mode");
                gson.getAdapter(UsageMode.class).write(out, ((Structure) value).getUsageMode());
                out.name("bounding box visibility").value(((Structure) value).isBoundingBoxVisible());
                out.name("ignore entities").value(((Structure) value).isIgnoreEntities());
                out.name("show air").value(((Structure) value).isShowAir());
            }
        }
        out.endObject();
    }

    @SuppressWarnings({"deprecation", "unchecked"})
    @Override
    public BlockState read(JsonReader in) throws IOException {
        BlockState state = null;
        in.beginObject();
        while (in.hasNext()) {
            if (in.peek() == JsonToken.NAME) {
                switch (in.nextName()) {
                    case "material":
                        state = ((BlockStateMeta) new ItemStack(gson.getAdapter(Material.class).read(in)).getItemMeta()).getBlockState();
                        break;
                    case "material data":
                        if (state == null) return null;
                        state.setData(gson.getAdapter(MaterialData.class).read(in));
                        break;
                    case "block data":
                        if (state == null) return null;
                        state.setBlockData(gson.getAdapter(BlockData.class).read(in));
                        break;
                    case "block":
                        state = gson.getAdapter(Block.class).read(in).getState();
                        break;
                    case "patterns":
                        if (state == null) return null;
                        ((Banner) state).setPatterns(gson.getAdapter(List.class).read(in));
                        break;
                    case "base color":
                        if (state == null) return null;
                        ((Banner) state).setBaseColor(gson.getAdapter(DyeColor.class).read(in));
                        break;
                    case "inventory":
                        if (state == null) return null;
                        ((InventoryHolder) state).getInventory().setContents(gson.getAdapter(ItemStack[].class).read(in));
                        break;
                    case "primary effect":
                        if (state == null) return null;
                        ((Beacon) state).setPrimaryEffect(gson.getAdapter(PotionEffect.class).read(in).getType());
                        break;
                    case "secondary effect":
                        if (state == null) return null;
                        ((Beacon) state).setSecondaryEffect(gson.getAdapter(PotionEffect.class).read(in).getType());
                        break;
                    case "color":
                        if (state == null) return null;
                        ((Colorable) state).setColor(gson.getAdapter(DyeColor.class).read(in));
                        break;
                    case "lock":
                        if (state == null) return null;
                        ((Lockable) state).setLock(in.nextString());
                        break;
                    case "name":
                        if (state == null) return null;
                        ((CommandBlock) state).setName(in.nextString());
                        break;
                    case "command":
                        if (state == null) return null;
                        ((CommandBlock) state).setCommand(in.nextString());
                        break;
                    case "delay":
                        if (state == null) return null;
                        ((CreatureSpawner) state).setDelay(in.nextInt());
                        break;
                    case "maximum nearby entities":
                    case "max nearby entities":
                        if (state == null) return null;
                        ((CreatureSpawner) state).setMaxNearbyEntities(in.nextInt());
                        break;
                    case "maximum spawn delay":
                    case "max spawn delay":
                        if (state == null) return null;
                        ((CreatureSpawner) state).setMaxSpawnDelay(in.nextInt());
                        break;
                    case "minimum spawn delay":
                    case "min spawn delay":
                        if (state == null) return null;
                        ((CreatureSpawner) state).setMinSpawnDelay(in.nextInt());
                        break;
                    case "player range":
                        if (state == null) return null;
                        ((CreatureSpawner) state).setRequiredPlayerRange(in.nextInt());
                        break;
                    case "spawn count":
                        if (state == null) return null;
                        ((CreatureSpawner) state).setSpawnCount(in.nextInt());
                        break;
                    case "spawn type":
                        if (state == null) return null;
                        ((CreatureSpawner) state).setSpawnedType(gson.getAdapter(EntityType.class).read(in));
                        break;
                    case "spawn range":
                        if (state == null) return null;
                        ((CreatureSpawner) state).setSpawnRange(in.nextInt());
                        break;
                    case "custom name":
                        if (state == null) return null;
                        ((Nameable) state).setCustomName(in.nextString());
                        break;
                    case "seed":
                        if (state == null) return null;
                        if (state instanceof Lootable) {
                            ((Lootable) state).setSeed(in.nextLong());
                        } else ((Structure) state).setSeed(in.nextLong());
                        break;
                    case "loot table":
                        if (state == null) return null;
                        ((Lootable) state).setLootTable(gson.getAdapter(LootTable.class).read(in));
                        break;
                    case "age":
                        if (state == null) return null;
                        ((EndGateway) state).setAge(in.nextLong());
                        break;
                    case "exit location":
                        if (state == null) return null;
                        ((EndGateway) state).setExitLocation(gson.getAdapter(Location.class).read(in));
                        break;
                    case "exact teleport":
                        if (state == null) return null;
                        ((EndGateway) state).setExactTeleport(in.nextBoolean());
                        break;
                    case "lines":
                        if (state == null) return null;
                        String[] array = gson.getAdapter(String[].class).read(in);
                        for (int i = 0; i < array.length; i++) {
                            ((Sign) state).setLine(i, array[i]);
                        }
                        break;
                    case "editable":
                        if (state == null) return null;
                        ((Sign) state).setEditable(in.nextBoolean());
                        break;
                    case "owning player":
                        if (state == null) return null;
                        ((Skull) state).setOwningPlayer(gson.getAdapter(OfflinePlayer.class).read(in));
                        break;
                    case "author":
                        if (state == null) return null;
                        ((Structure) state).setAuthor(in.nextString());
                        break;
                    case "structure name":
                        if (state == null) return null;
                        ((Structure) state).setStructureName(in.nextString());
                        break;
                    case "integrity":
                        if (state == null) return null;
                        ((Structure) state).setIntegrity((float) in.nextDouble());
                        break;
                    case "metadata":
                        if (state == null) return null;
                        ((Structure) state).setMetadata(in.nextString());
                        break;
                    case "relative position":
                        if (state == null) return null;
                        ((Structure) state).setRelativePosition(gson.getAdapter(BlockVector.class).read(in));
                        break;
                    case "rotation":
                        if (state == null) return null;
                        ((Structure) state).setRotation(gson.getAdapter(StructureRotation.class).read(in));
                        break;
                    case "mirror":
                        if (state == null) return null;
                        ((Structure) state).setMirror(gson.getAdapter(Mirror.class).read(in));
                        break;
                    case "structure size":
                        if (state == null) return null;
                        ((Structure) state).setStructureSize(gson.getAdapter(BlockVector.class).read(in));
                        break;
                    case "usage mode":
                        if (state == null) return null;
                        ((Structure) state).setUsageMode(gson.getAdapter(UsageMode.class).read(in));
                        break;
                    case "bounding box visibility":
                        if (state == null) return null;
                        ((Structure) state).setBoundingBoxVisible(in.nextBoolean());
                        break;
                    case "ignore entities":
                        if (state == null) return null;
                        ((Structure) state).setIgnoreEntities(in.nextBoolean());
                        break;
                    case "show air":
                        if (state == null) return null;
                        ((Structure) state).setShowAir(in.nextBoolean());
                        break;
                }
            }
        }
        in.endObject();
        return state;
    }
}
