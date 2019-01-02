package org.thane.nms.v1_13_R2.adapters.items.meta;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.inventory.meta.LeatherArmorMeta;

import java.io.IOException;

public class LeatherArmorMetaAdapter extends TypeAdapter<LeatherArmorMeta> {
    private Gson gson;

    public LeatherArmorMetaAdapter(Gson gson) {
        this.gson = gson;
    }

    @Override
    public void write(JsonWriter out, LeatherArmorMeta value) throws IOException {
        out.beginObject();
        out.name("color");
        gson.getAdapter(Color.class).write(out, value.getColor());
        ItemMetaAdapter.writeMeta(gson, out, value);
        out.endObject();
    }

    @Override
    public LeatherArmorMeta read(JsonReader in) throws IOException {
        LeatherArmorMeta meta = (LeatherArmorMeta) Bukkit.getItemFactory().getItemMeta(Material.LEATHER_CHESTPLATE);
        in.beginObject();
        while (in.hasNext()) {
            if (in.peek() == JsonToken.NAME) {
                String name = in.nextName();
                switch (name) {
                    case "leather color":
                    case "color":
                        meta.setColor(gson.getAdapter(Color.class).read(in));
                    default:
                        ItemMetaAdapter.readMeta(gson, name, in, meta);
                }
            }
        }
        in.endObject();
        return meta;
    }
}
