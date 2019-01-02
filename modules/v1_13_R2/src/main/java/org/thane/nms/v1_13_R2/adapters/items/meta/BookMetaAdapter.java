package org.thane.nms.v1_13_R2.adapters.items.meta;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.meta.BookMeta;

import java.io.IOException;
import java.util.List;

public class BookMetaAdapter extends TypeAdapter<BookMeta> {
    private Gson gson;

    public BookMetaAdapter(Gson gson) {
        this.gson = gson;
    }

    @Override
    public void write(JsonWriter out, BookMeta value) throws IOException {
        out.beginObject();
        if (value.hasTitle())
            out.name("title").value(value.getTitle());
        if (value.hasAuthor())
            out.name("author").value(value.getAuthor());
        if (value.hasGeneration()) {
            out.name("generation");
            gson.getAdapter(BookMeta.Generation.class).write(out, value.getGeneration());
        }
        if (value.hasPages()) {
            out.name("pages");
            gson.getAdapter(List.class).write(out, value.getPages());
        }
        ItemMetaAdapter.writeMeta(gson, out, value);
        out.endObject();
    }

    @SuppressWarnings("unchecked")
    @Override
    public BookMeta read(JsonReader in) throws IOException {
        BookMeta meta = (BookMeta) Bukkit.getItemFactory().getItemMeta(Material.WRITTEN_BOOK);
        in.beginObject();
        while (in.hasNext()) {
            if (in.peek() == JsonToken.NAME) {
                String name = in.nextName();
                switch (name) {
                    case "title":
                        meta.setTitle(in.nextString());
                        break;
                    case "author":
                        meta.setAuthor(in.nextString());
                        break;
                    case "generation":
                        meta.setGeneration(gson.getAdapter(BookMeta.Generation.class).read(in));
                        break;
                    case "pages":
                        meta.setPages(gson.getAdapter(List.class).read(in));
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
