package org.thane.nms.v1_13_R2;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import net.minecraft.server.v1_13_R2.*;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_13_R2.CraftWorld;
import org.bukkit.craftbukkit.v1_13_R2.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_13_R2.inventory.CraftItemStack;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class NBT extends org.thane.api.NBT {

    private static final Field handleField;
    static {
        Field handleField1 = null;
        try {
            handleField1 = CraftItemStack.class.getDeclaredField("handle");
            handleField1.setAccessible(true);
        } catch (NoSuchFieldException ignored) {
        } finally {
            handleField = handleField1;
        }
    }

    @SuppressWarnings("ConstantConditions")
    public NBT(ItemStack stack) {
        super(nbtToJsonElement(CraftItemStack.asNMSCopy(stack).getOrCreateTag()).getAsJsonObject());
    }

    public NBT(JsonObject object) {
        super(object);
    }

    @SuppressWarnings("ConstantConditions")
    public NBT(Entity entity) {
        super(nbtToJsonElement(getEntityTag(entity)).getAsJsonObject());
    }

    @SuppressWarnings("ConstantConditions")
    public NBT(Block block) {
        super(nbtToJsonElement(((CraftWorld) block.getWorld()).getTileEntityAt(block.getX(), block.getY(), block.getZ()).aa_()).getAsJsonObject());
    }

    private static NBTTagCompound getEntityTag(Entity entity) {
        NBTTagCompound compound = new NBTTagCompound();
        ((CraftEntity) entity).getHandle().c(compound);
        return compound;
    }

    @Override
    public ItemStack copyOnto(ItemStack stack) {
        net.minecraft.server.v1_13_R2.ItemStack stack1 = CraftItemStack.asNMSCopy(stack);
        combineWith(new NBT(stack));
        stack1.setTag((NBTTagCompound) jsonElementToNBT(asJsonObject()));
        return CraftItemStack.asBukkitCopy(stack1);
    }

    @Override
    public void applyTo(ItemStack stack) throws NoSuchFieldException {
        if (handleField == null) throw new NoSuchFieldException("Unable to locate 'handle' field in CraftItemStack.class");
        try {
            net.minecraft.server.v1_13_R2.ItemStack stack1 = (net.minecraft.server.v1_13_R2.ItemStack) handleField.get(stack);
            stack1.setTag((NBTTagCompound) jsonElementToNBT(asJsonObject()));
        } catch (IllegalAccessException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void applyTo(Entity entity) {
        ((CraftEntity) entity).getHandle().f((NBTTagCompound) jsonElementToNBT(asJsonObject()));
    }

    @Override
    public void applyTo(Block block) {
        ((CraftWorld) block.getWorld()).getTileEntityAt(block.getX(), block.getY(), block.getZ()).load((NBTTagCompound) jsonElementToNBT(asJsonObject()));
    }

    @Override
    public boolean isAppliable() {
        return true;
    }

    private static NBTBase jsonElementToNBT(JsonElement element) {
        if (element.isJsonPrimitive()) {
            JsonPrimitive primitive = (JsonPrimitive) element;
            if (primitive.isBoolean()) {
                return new NBTTagByte((byte) (primitive.getAsBoolean() ? 1 : 0));
            } else if (primitive.isString()) {
                return new NBTTagString(primitive.getAsString());
            } else if (primitive.isNumber()) {

                Number number = primitive.getAsNumber();
                if (number.doubleValue() % 1 != 0) {
                    if (number.doubleValue() <= Float.MAX_VALUE) {
                        return new NBTTagFloat(number.floatValue());
                    } else return new NBTTagDouble(number.doubleValue());
                } else if (number.longValue() <= Byte.MAX_VALUE) {
                    return new NBTTagByte(number.byteValue());
                } else if (number.longValue() <= Short.MAX_VALUE) {
                    return new NBTTagShort(number.shortValue());
                } else if (number.longValue() <= Integer.MAX_VALUE) {
                    return new NBTTagInt(number.intValue());
                } else return new NBTTagLong(number.longValue());
            }
        } else {
            if (element.isJsonArray()) {
                NBTBase base = jsonElementToNBT(element.getAsJsonArray().get(0));
                switch (base.getTypeId()) {
                    case NBTType.END:
                        return new NBTTagByteArray(new byte[1]);
                    case NBTType.BYTE:
                        List<Byte> bytes = new ArrayList<>();
                        for (JsonElement element1 : element.getAsJsonArray()) {
                            bytes.add(element1.getAsByte());
                        }
                        return new NBTTagByteArray(bytes);
                    case NBTType.SHORT:
                        List<Integer> shorts = new ArrayList<>();
                        for (JsonElement element1 : element.getAsJsonArray()) {
                            shorts.add(element1.getAsInt());
                        }
                        return new NBTTagIntArray(shorts);
                    case NBTType.INT:
                        List<Integer> ints = new ArrayList<>();
                        for (JsonElement element1 : element.getAsJsonArray()) {
                            ints.add(element1.getAsInt());
                        }
                        return new NBTTagIntArray(ints);
                    case NBTType.LONG:
                        List<Long> longs = new ArrayList<>();
                        for (JsonElement element1 : element.getAsJsonArray()) {
                            longs.add(element1.getAsLong());
                        }
                        return new NBTTagLongArray(longs);
                    default:
                        NBTTagList list = new NBTTagList();
                        for (JsonElement element1 : element.getAsJsonArray()) {
                            list.add(jsonElementToNBT(element1));
                        }
                        return list;
                }
            } else if (element.isJsonObject()) {
                NBTTagCompound compound = new NBTTagCompound();
                for (Map.Entry<String, JsonElement> entry : element.getAsJsonObject().entrySet()) {
                    compound.set(entry.getKey(), jsonElementToNBT(entry.getValue()));
                }
                return compound;
            }
        }
        throw new NBTTranslationException("Cannot determine type for Json element '" + element.toString() + "' of class " + element.getClass().getName());
    }

    public static class NBTTranslationException extends RuntimeException {

        public NBTTranslationException(String msg) {
            super(msg);
        }
    }

    private static JsonElement nbtToJsonElement(NBTBase nbt) {
        switch (nbt.getTypeId()) {
            case NBTType.END:
                return null;
            case NBTType.BYTE:
                    return new JsonPrimitive(((NBTTagByte) nbt).asByte());
            case NBTType.SHORT:
                return new JsonPrimitive(((NBTTagShort) nbt).asShort());
            case NBTType.INT:
                return new JsonPrimitive(((NBTTagInt) nbt).asLong());
            case NBTType.LONG:
                return new JsonPrimitive(((NBTTagLong) nbt).asLong());
            case NBTType.FLOAT:
                return new JsonPrimitive(((NBTTagFloat) nbt).asFloat());
            case NBTType.DOUBLE:
                return new JsonPrimitive(((NBTTagDouble) nbt).asDouble());
            case NBTType.BYTE_ARRAY:
                JsonArray byteArray = new JsonArray();
                for (byte bite : ((NBTTagByteArray) nbt).c()) {
                    byteArray.add(bite == 1);
                }
                return byteArray;
            case NBTType.STRING:
                return new JsonPrimitive(((NBTTagString) nbt).asString());
            case NBTType.LIST:
                JsonArray listArray = new JsonArray();
                for (NBTBase nbtBase : ((NBTTagList) nbt)) {
                    listArray.add(nbtToJsonElement(nbtBase));
                }
                return listArray;
            case NBTType.COMPOUND:
                JsonObject jsonObject = new JsonObject();
                for (String string : ((NBTTagCompound) nbt).getKeys()) {
                    jsonObject.add(string, nbtToJsonElement(((NBTTagCompound) nbt).get(string)));
                }
                return jsonObject;
            case NBTType.INT_ARRAY:
                JsonArray intArray = new JsonArray();
                for (int integer : ((NBTTagIntArray) nbt).d()) {
                    intArray.add(integer);
                }
                return intArray;
            case NBTType.LONG_ARRAY:
                JsonArray longArray = new JsonArray();
                for (long longNumber : ((NBTTagLongArray) nbt).d()) {
                    longArray.add(longNumber);
                }
                return longArray;
        }
        throw new NBTTranslationException("Cannot determine type for NBT element '" + nbt.toString() + "' of class " + nbt.getClass().getName());
    }

    private static class NBTType {

        static final byte END = 0;
        static final byte BYTE = 1;
        static final byte SHORT = 2;
        static final byte INT = 3;
        static final byte LONG = 4;
        static final byte FLOAT = 5;
        static final byte DOUBLE = 6;
        static final byte BYTE_ARRAY = 7;
        static final byte STRING = 8;
        static final byte LIST = 9;
        static final byte COMPOUND = 10;
        static final byte INT_ARRAY = 11;
        static final byte LONG_ARRAY = 12;
    }
}