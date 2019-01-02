package org.thane.nms.v1_13_R2.adapters.items.meta;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;

import java.io.IOException;
import java.util.Map;

public class EnchantmentStorageMetaAdapter extends TypeAdapter<EnchantmentStorageMeta> {
    private Gson gson;

    public EnchantmentStorageMetaAdapter(Gson gson) {
        this.gson = gson;
    }

    @Override
    public void write(JsonWriter out, EnchantmentStorageMeta value) throws IOException {
        out.beginObject();
        if (value.hasStoredEnchants()) {
            out.name("stored enchantments");
            gson.getAdapter(Map.class).write(out, value.getStoredEnchants());
        }
        ItemMetaAdapter.writeMeta(gson, out, value);
        out.endObject();
    }

    @SuppressWarnings("unchecked")
    @Override
    public EnchantmentStorageMeta read(JsonReader in) throws IOException {
        EnchantmentStorageMeta meta = (EnchantmentStorageMeta) Bukkit.getItemFactory().getItemMeta(Material.ENCHANTED_BOOK);
        in.beginObject();
        while (in.hasNext()) {
            if (in.peek() == JsonToken.NAME) {
                String name = in.nextName();
                switch (name) {
                    case "stored enchantments":
                    case "stored enchants":
                        for (Map.Entry<Enchantment, Integer> entry : ((Map<Enchantment, Integer>) gson.getAdapter(TypeToken.get(new TypeToken<Map<Enchantment, Integer>>() {
                        }.getType())).read(in)).entrySet()) {
                            meta.addStoredEnchant(entry.getKey(), entry.getValue(), true);
                        }
                        break;
                    default:
                        ItemMetaAdapter.readMeta(gson, name, in, meta);
                }
            }
        }
        in.endObject();
        return meta;
    }
}
