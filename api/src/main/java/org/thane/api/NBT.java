package org.thane.api;

import com.google.gson.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;

import java.io.Serializable;
import java.util.*;

public class NBT implements Serializable {

    private static Gson gson = new Gson();

    private JsonObject nbt;

    public NBT(JsonObject element) {
        nbt = element;
    }

    public void addNBT(String key, JsonElement element) {
        nbt.add(key, element);
    }

    public void removeNBT(String key) {
        nbt.remove(key);
    }

    public void addNBT(String key, Object object) {
        nbt.add(key, gson.toJsonTree(object));
    }

    public JsonElement getNBT(String key) {
        return nbt.get(key);
    }

    public <T> T getNBT(String key, Class<T> type) {
        return gson.fromJson(nbt.get(key), type);
    }

    public boolean hasNBT(String key) {
        return nbt.has(key);
    }

    public boolean isEmpty() {
        return nbt == null || nbt.entrySet().isEmpty();
    }

    public JsonObject asJsonObject() {
        return nbt;
    }

    public String serialize() {
        return gson.toJson(nbt);
    }

    public ItemStack copyOnto(ItemStack stack) {
        return stack;
    }

    public void applyTo(ItemStack stack) throws NoSuchFieldException {
    }

    public void applyTo(Entity entity) {}

    public void applyTo(Block block) {}

    public void combineWith(NBT nbt) {
        for (Map.Entry<String, JsonElement> entry : nbt.asJsonObject().entrySet()) {
            this.asJsonObject().add(entry.getKey(), entry.getValue());
        }
    }

    public NBT withExcludes(String... strings) {
        return withExcludes(new HashSet<>(Arrays.asList(strings)));
    }

    public NBT withExcludes(Set<String> excludes) {
        JsonObject object = deepCopy(nbt);
        excludes.forEach(s -> {
            if (s.contains(".")) {
                String[] array = s.split("\\.");
                JsonObject current = object;
                for (String string : array) {
                    if (current == null) break;
                    if (array[array.length - 1].equals(string)) {
                        current.remove(string);
                    } else current = current.getAsJsonObject(string);
                }
            } else object.remove(s);
        });
        for (Map.Entry<String, JsonElement> entry : object.entrySet()) {
            if (entry.getValue().isJsonObject()) {
                if (entry.getValue().getAsJsonObject().entrySet().isEmpty())
                    object.remove(entry.getKey());
            }
        }
        return new NBT(object);
    }

    public boolean isAppliable() {
        return false;
    }

    public static Gson getGson() {
        return gson;
    }

    public static void setGson(Gson gson) {
        NBT.gson = gson;
    }

    public static JsonObject deepCopy(JsonObject jsonObject) {
        JsonObject result = new JsonObject();
        for (Map.Entry<String, JsonElement> entry : jsonObject.entrySet()) {
            result.add(entry.getKey(), deepCopy(entry.getValue()));
        }
        return result;
    }

    public static JsonArray deepCopy(JsonArray jsonArray) {
        JsonArray result = new JsonArray();
        for (JsonElement e : jsonArray) {
            result.add(deepCopy(e));
        }
        return result;
    }

    public static JsonElement deepCopy(JsonElement jsonElement) {
        if (jsonElement.isJsonPrimitive() || jsonElement.isJsonNull()) {
            return jsonElement;       // these are immutables anyway
        } else if (jsonElement.isJsonObject()) {
            return deepCopy(jsonElement.getAsJsonObject());
        } else if (jsonElement.isJsonArray()) {
            return deepCopy(jsonElement.getAsJsonArray());
        } else {
            throw new UnsupportedOperationException("Unsupported element: " + jsonElement);
        }
    }
}