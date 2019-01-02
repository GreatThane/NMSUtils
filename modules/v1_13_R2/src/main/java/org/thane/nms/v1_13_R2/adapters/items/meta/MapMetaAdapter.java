package org.thane.nms.v1_13_R2.adapters.items.meta;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.inventory.meta.MapMeta;

import java.io.IOException;

public class MapMetaAdapter extends TypeAdapter<MapMeta> {
    private Gson gson;

    public MapMetaAdapter(Gson gson) {
        this.gson = gson;
    }

    @Override
    public void write(JsonWriter out, MapMeta value) throws IOException {
        out.beginObject();
        if (value.hasMapId())
            out.name("id").value(value.getMapId());
        if (value.hasLocationName())
            out.name("location name").value(value.getLocationName());
        if (value.isScaling())
            out.name("scaling").value(value.isScaling());
        if (value.hasColor()) {
            out.name("color");
            gson.getAdapter(Color.class).write(out, value.getColor());
        }
        ItemMetaAdapter.writeMeta(gson, out, value);
        out.endObject();
    }

    @Override
    public MapMeta read(JsonReader in) throws IOException {
        MapMeta meta = (MapMeta) Bukkit.getItemFactory().getItemMeta(Material.FILLED_MAP);
        in.beginObject();
        while (in.hasNext()) {
            if (in.peek() == JsonToken.NAME) {
                String name = in.nextName();
                switch (name) {
                    case "map id":
                    case "id":
                        meta.setMapId(in.nextInt());
                        break;
                    case "location name":
                        meta.setLocationName(in.nextString());
                        break;
                    case "scaling":
                        meta.setScaling(in.nextBoolean());
                        break;
                    case "color":
                    case "map color":
                        meta.setColor(gson.getAdapter(Color.class).read(in));
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
