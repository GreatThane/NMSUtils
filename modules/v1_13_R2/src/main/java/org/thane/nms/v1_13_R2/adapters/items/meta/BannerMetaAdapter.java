package org.thane.nms.v1_13_R2.adapters.items.meta;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.banner.Pattern;
import org.bukkit.inventory.meta.BannerMeta;

import java.io.IOException;
import java.util.List;

public class BannerMetaAdapter extends TypeAdapter<BannerMeta> {
    private Gson gson;

    public BannerMetaAdapter(Gson gson) {
        this.gson = gson;
    }

    @Override
    public void write(JsonWriter out, BannerMeta value) throws IOException {
        out.beginObject();
        out.name("banner patterns");
        gson.getAdapter(List.class).write(out, value.getPatterns());
        ItemMetaAdapter.writeMeta(gson, out, value);
        out.endObject();
    }

    @SuppressWarnings("unchecked")
    @Override
    public BannerMeta read(JsonReader in) throws IOException {
        BannerMeta meta = (BannerMeta) Bukkit.getItemFactory().getItemMeta(Material.WHITE_BANNER);
        in.beginObject();
        while (in.hasNext()) {
            if (in.peek() == JsonToken.NAME) {
                String name = in.nextName();
                switch (name) {
                    case "banner patterns":
                        meta.setPatterns((List<Pattern>) gson.getAdapter(TypeToken.get(new TypeToken<List<Pattern>>() {
                        }.getType())).read(in));
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
