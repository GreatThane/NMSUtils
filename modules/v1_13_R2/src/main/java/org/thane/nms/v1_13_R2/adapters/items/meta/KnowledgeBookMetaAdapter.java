package org.thane.nms.v1_13_R2.adapters.items.meta;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.meta.KnowledgeBookMeta;

import java.io.IOException;
import java.util.List;

public class KnowledgeBookMetaAdapter extends TypeAdapter<KnowledgeBookMeta> {
    private Gson gson;

    public KnowledgeBookMetaAdapter(Gson gson) {
        this.gson = gson;
    }

    @Override
    public void write(JsonWriter out, KnowledgeBookMeta value) throws IOException {
        out.beginObject();
        if (value.hasRecipes()) {
            out.name("stored recipes");
            gson.getAdapter(List.class).write(out, value.getRecipes());
        }
        ItemMetaAdapter.writeMeta(gson, out, value);
        out.endObject();
    }

    @SuppressWarnings("unchecked")
    @Override
    public KnowledgeBookMeta read(JsonReader in) throws IOException {
        KnowledgeBookMeta meta = (KnowledgeBookMeta) Bukkit.getItemFactory().getItemMeta(Material.KNOWLEDGE_BOOK);
        in.beginObject();
        while (in.hasNext()) {
            if (in.peek() == JsonToken.NAME) {
                String name = in.nextName();
                switch (name) {
                    case "stored recipes":
                        meta.setRecipes((List<NamespacedKey>) gson.getAdapter(TypeToken.get(new TypeToken<List<NamespacedKey>>() {
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
