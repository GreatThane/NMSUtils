package org.thane.nms.v1_13_R2.adapters.items.meta;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.meta.SkullMeta;

import java.io.IOException;

public class SkullMetaAdapter extends TypeAdapter<SkullMeta> {
    private Gson gson;

    public SkullMetaAdapter(Gson gson) {
        this.gson = gson;
    }

    @Override
    public void write(JsonWriter out, SkullMeta value) throws IOException {
        out.beginObject();
        if (value.hasOwner()) {
            out.name("skull owner");
            gson.getAdapter(OfflinePlayer.class).write(out, value.getOwningPlayer());
        }
        ItemMetaAdapter.writeMeta(gson, out, value);
        out.endObject();
    }

    @Override
    public SkullMeta read(JsonReader in) throws IOException {
        SkullMeta meta = (SkullMeta) Bukkit.getItemFactory().getItemMeta(Material.PLAYER_HEAD);
        in.beginObject();
        while (in.hasNext()) {
            if (in.peek() == JsonToken.NAME) {
                String name = in.nextName();
                switch (name) {
                    case "skull owner":
                        meta.setOwningPlayer(gson.getAdapter(OfflinePlayer.class).read(in));
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
