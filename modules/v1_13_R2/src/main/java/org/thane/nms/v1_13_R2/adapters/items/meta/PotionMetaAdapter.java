package org.thane.nms.v1_13_R2.adapters.items.meta;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionEffect;

import java.io.IOException;
import java.util.List;

public class PotionMetaAdapter extends TypeAdapter<PotionMeta> {
    private Gson gson;

    public PotionMetaAdapter(Gson gson) {
        this.gson = gson;
    }

    @Override
    public void write(JsonWriter out, PotionMeta value) throws IOException {
        out.beginObject();
        out.name("base potion data");
        gson.getAdapter(PotionData.class).write(out, value.getBasePotionData());
        if (value.hasColor()) {
            out.name("color");
            gson.getAdapter(Color.class).write(out, value.getColor());
        }
        if (value.hasCustomEffects()) {
            out.name("custom effects");
            gson.getAdapter(List.class).write(out, value.getCustomEffects());
        }
        ItemMetaAdapter.writeMeta(gson, out, value);
        out.endObject();
    }

    @SuppressWarnings("unchecked")
    @Override
    public PotionMeta read(JsonReader in) throws IOException {
        PotionMeta meta = (PotionMeta) Bukkit.getItemFactory().getItemMeta(Material.POTION);
        in.beginObject();
        while (in.hasNext()) {
            if (in.peek() == JsonToken.NAME) {
                String name = in.nextName();
                switch (name) {
                    case "base potion data":
                        meta.setBasePotionData(gson.getAdapter(PotionData.class).read(in));
                        break;
                    case "color":
                        meta.setColor(gson.getAdapter(Color.class).read(in));
                        break;
                    case "custom effects":
                        for (PotionEffect potionEffect : (List<PotionEffect>) gson.getAdapter(TypeToken.get(new TypeToken<List<PotionEffect>>() {
                        }.getType())).read(in)) {
                            meta.addCustomEffect(potionEffect, true);
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
