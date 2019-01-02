package org.thane.nms.v1_13_R2.adapters;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import org.bukkit.NamespacedKey;

import java.io.IOException;

public class NamespacedKeyAdapter extends TypeAdapter<NamespacedKey> {
    @Override
    public void write(JsonWriter out, NamespacedKey value) throws IOException {
        out.value(value.toString());
    }

    @Override
    public NamespacedKey read(JsonReader in) throws IOException {
        String[] strings = in.nextString().split(":");
        return new NamespacedKey(strings[0], strings[1]);
    }
}
