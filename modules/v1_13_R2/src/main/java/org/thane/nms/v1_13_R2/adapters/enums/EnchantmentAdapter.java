package org.thane.nms.v1_13_R2.adapters.enums;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;

import java.io.IOException;

public class EnchantmentAdapter extends TypeAdapter<Enchantment> {
    @Override
    public void write(JsonWriter out, Enchantment value) throws IOException {
        out.value(value.getKey().getKey());
    }

    @Override
    public Enchantment read(JsonReader in) throws IOException {
        return Enchantment.getByKey(new NamespacedKey(NamespacedKey.MINECRAFT, in.nextString()));
    }
}