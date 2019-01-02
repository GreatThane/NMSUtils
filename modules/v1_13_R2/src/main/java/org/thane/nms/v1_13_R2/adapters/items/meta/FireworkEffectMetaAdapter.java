package org.thane.nms.v1_13_R2.adapters.items.meta;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import org.bukkit.Bukkit;
import org.bukkit.FireworkEffect;
import org.bukkit.Material;
import org.bukkit.inventory.meta.FireworkEffectMeta;

import java.io.IOException;

public class FireworkEffectMetaAdapter extends TypeAdapter<FireworkEffectMeta> {
    private Gson gson;

    public FireworkEffectMetaAdapter(Gson gson) {
        this.gson = gson;
    }

    @Override
    public void write(JsonWriter out, FireworkEffectMeta value) throws IOException {
        out.beginObject();
        if (value.hasEffect()) {
            out.name("firework effect");
            gson.getAdapter(FireworkEffect.class).write(out, value.getEffect());
        }
        ItemMetaAdapter.writeMeta(gson, out, value);
        out.endObject();
    }

    @Override
    public FireworkEffectMeta read(JsonReader in) throws IOException {
        FireworkEffectMeta meta = (FireworkEffectMeta) Bukkit.getItemFactory().getItemMeta(Material.FIREWORK_ROCKET);
        in.beginObject();
        while (in.hasNext()) {
            if (in.peek() == JsonToken.NAME) {
                String name = in.nextName();
                switch (name) {
                    case "firework effect":
                        meta.setEffect(gson.getAdapter(FireworkEffect.class).read(in));
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
