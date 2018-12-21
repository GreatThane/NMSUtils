package org.thane.nms.v1_13_R2;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import net.minecraft.server.v1_13_R2.*;
import org.bukkit.craftbukkit.v1_13_R2.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class NBT extends org.thane.api.NBT {

    private static Field handleField;
    static {
        try {
            handleField = CraftItemStack.class.getDeclaredField("handle");
            handleField.setAccessible(true);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("ConstantConditions")
    public NBT(ItemStack stack) {
        super(nbtToJsonElement(CraftItemStack.asNMSCopy(stack).getOrCreateTag()).getAsJsonObject());
    }

    @Override
    public ItemStack copyOnto(ItemStack stack) {
        net.minecraft.server.v1_13_R2.ItemStack stack1 = CraftItemStack.asNMSCopy(stack);
        stack1.setTag((NBTTagCompound) jsonElementToNBT(asJsonObject()));
        return CraftItemStack.asBukkitCopy(stack1);
    }

    @Override
    public void applyTo(ItemStack stack) throws IllegalAccessException {
        net.minecraft.server.v1_13_R2.ItemStack stack1 = (net.minecraft.server.v1_13_R2.ItemStack) handleField.get(stack);
        stack1.setTag((NBTTagCompound) jsonElementToNBT(asJsonObject()));
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
                if (number instanceof Integer) {
                    return new NBTTagInt((Integer) number);
                } else if (number instanceof Long) {
                    return new NBTTagLong((Long) number);
                } else if (number instanceof Float) {
                    return new NBTTagFloat((Float) number);
                } else if (number instanceof Double) {
                    return new NBTTagDouble((Double) number);
                } else if (number instanceof Byte) {
                    return new NBTTagByte((Byte) number);
                } else if (number instanceof Short) {
                    return new NBTTagShort((Short) number);
                }
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
        return new NBTTagEnd();
    }

    private static JsonElement nbtToJsonElement(NBTBase nbt) {
        switch (nbt.getTypeId()) {
            case NBTType.END:
                return null;
            case NBTType.BYTE:
                if (((NBTTagByte) nbt).asByte() > 1) {
                    return new JsonPrimitive(((NBTTagByte) nbt).asByte());
                } else return new JsonPrimitive(((NBTTagByte) nbt).asByte() == 1);
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
        return null;
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