package org.thane.nms.v1_13_R2.adapters.items.meta;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import org.bukkit.Bukkit;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.entity.TropicalFish;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.TropicalFishBucketMeta;

import java.io.IOException;

public class TropicalFishBucketMetaAdapter extends TypeAdapter<TropicalFishBucketMeta> {
    private Gson gson;

    public TropicalFishBucketMetaAdapter(Gson gson) {
        this.gson = gson;
    }

    @Override
    public void write(JsonWriter out, TropicalFishBucketMeta value) throws IOException {
        out.beginObject();
        if (value.hasVariant()) {
            out.name("pattern");
            gson.getAdapter(TropicalFish.Pattern.class).write(out, value.getPattern());
            out.name("body color");
            gson.getAdapter(DyeColor.class).write(out, value.getBodyColor());
            out.name("pattern color");
            gson.getAdapter(DyeColor.class).write(out, value.getPatternColor());
        }
        ItemMetaAdapter.writeMeta(gson, out, value);
        out.endObject();
    }

    @Override
    public TropicalFishBucketMeta read(JsonReader in) throws IOException {
        TropicalFishBucketMeta meta = (TropicalFishBucketMeta) Bukkit.getItemFactory().getItemMeta(Material.TROPICAL_FISH_BUCKET);
        in.beginObject();
        while (in.hasNext()) {
            if (in.peek() == JsonToken.NAME) {
                String name = in.nextName();
                switch (name) {
                    case "body color":
                        meta.setBodyColor(gson.getAdapter(DyeColor.class).read(in));
                        break;
                    case "pattern":
                        meta.setPattern(gson.getAdapter(TropicalFish.Pattern.class).read(in));
                        break;
                    case "pattern color":
                        meta.setPatternColor(gson.getAdapter(DyeColor.class).read(in));
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
