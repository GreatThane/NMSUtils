package org.thane.nms.v1_13_R2.adapters.blocks.states;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import org.bukkit.block.Sign;

import java.io.IOException;

public class SignAdapter extends TypeAdapter<Sign> {
    private Gson gson;

    public SignAdapter(Gson gson) {
        this.gson = gson;
    }

    @Override
    public void write(JsonWriter out, Sign value) throws IOException {
        out.beginObject();
        BlockStateAdapter.writeState(gson, out, value);
        out.name("lines");
        gson.getAdapter(String[].class).write(out, value.getLines());
        out.name("editable").value(value.isEditable());
        out.endObject();
    }

    @Override
    public Sign read(JsonReader in) throws IOException {
        Sign state = null;
        in.beginObject();
        while (in.hasNext()) {
            if (in.peek() == JsonToken.NAME) {
                String name = in.nextName();
                switch (name) {
                    case "lines":
                        if (state == null) return null;
                        String[] array = gson.getAdapter(String[].class).read(in);
                        for (int i = 0; i < array.length; i++) {
                            state.setLine(i, array[i]);
                        }
                        break;
                    case "editable":
                        if (state == null) return null;
                        state.setEditable(in.nextBoolean());
                        break;
                    default:
                        state = (Sign) BlockStateAdapter.readState(gson, in, name, state);
                }
            }
        }
        in.endObject();
        return state;
    }
}
