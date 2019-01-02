package org.thane.nms.v1_13_R2.adapters.blocks.states;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import org.bukkit.DyeColor;
import org.bukkit.block.Banner;

import java.io.IOException;
import java.util.List;

public class BannerAdapter extends TypeAdapter<Banner> {
    private Gson gson;

    public BannerAdapter(Gson gson) {
        this.gson = gson;
    }

    @Override
    public void write(JsonWriter out, Banner value) throws IOException {
        out.beginObject();
        BlockStateAdapter.writeState(gson, out, value);
        out.name("patterns");
        gson.getAdapter(List.class).write(out, value.getPatterns());
        out.name("base color");
        gson.getAdapter(DyeColor.class).write(out, value.getBaseColor());
        out.endObject();
    }

    @SuppressWarnings("unchecked")
    @Override
    public Banner read(JsonReader in) throws IOException {
        Banner state = null;
        in.beginObject();
        while (in.hasNext()) {
            if (in.peek() == JsonToken.NAME) {
                String name = in.nextName();
                switch (name) {
                    case "patterns":
                        if (state == null) return null;
                        state.setPatterns(gson.getAdapter(List.class).read(in));
                        break;
                    case "base color":
                        if (state == null) return null;
                        state.setBaseColor(gson.getAdapter(DyeColor.class).read(in));
                        break;
                    default:
                        state = (Banner) BlockStateAdapter.readState(gson, in, name, state);
                }
            }
        }
        in.endObject();
        return state;
    }
}
