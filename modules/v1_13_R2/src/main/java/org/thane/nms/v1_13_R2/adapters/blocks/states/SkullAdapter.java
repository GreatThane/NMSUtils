package org.thane.nms.v1_13_R2.adapters.blocks.states;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Skull;

import java.io.IOException;

public class SkullAdapter extends TypeAdapter<Skull> {
    private Gson gson;

    public SkullAdapter(Gson gson) {
        this.gson = gson;
    }

    @Override
    public void write(JsonWriter out, Skull value) throws IOException {
        out.beginObject();
        BlockStateAdapter.writeState(gson, out, value);
        out.name("owning player");
        gson.getAdapter(OfflinePlayer.class).write(out, value.getOwningPlayer());
        out.endObject();
    }

    @Override
    public Skull read(JsonReader in) throws IOException {
        Skull state = null;
        in.beginObject();
        while (in.hasNext()) {
            if (in.peek() == JsonToken.NAME) {
                String name = in.nextName();
                switch (name) {
                    case "owning player":
                        if (state == null) return null;
                        state.setOwningPlayer(gson.getAdapter(OfflinePlayer.class).read(in));
                        break;
                    default:
                        state = (Skull) BlockStateAdapter.readState(gson, in, name, state);
                }
            }
        }
        in.endObject();
        return state;
    }
}
