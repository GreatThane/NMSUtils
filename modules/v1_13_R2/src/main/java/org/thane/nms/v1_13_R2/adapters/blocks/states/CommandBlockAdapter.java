package org.thane.nms.v1_13_R2.adapters.blocks.states;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import org.bukkit.block.CommandBlock;

import java.io.IOException;

public class CommandBlockAdapter extends TypeAdapter<CommandBlock> {
    private Gson gson;

    public CommandBlockAdapter(Gson gson) {
        this.gson = gson;
    }

    @Override
    public void write(JsonWriter out, CommandBlock value) throws IOException {
        out.beginObject();
        BlockStateAdapter.writeState(gson, out, value);
        if (!value.getName().equalsIgnoreCase("@")) {
            out.name("name").value(value.getName());
        }
        if (!value.getCommand().isEmpty()) {
            out.name("command").value(value.getCommand());
        }
        out.endObject();
    }

    @Override
    public CommandBlock read(JsonReader in) throws IOException {
        CommandBlock state = null;
        in.beginObject();
        while (in.hasNext()) {
            if (in.peek() == JsonToken.NAME) {
                String name = in.nextName();
                switch (name) {
                    case "name":
                        if (state == null) return null;
                        state.setName(in.nextString());
                        break;
                    case "command":
                        if (state == null) return null;
                        state.setCommand(in.nextString());
                        break;
                    default:
                        state = (CommandBlock) BlockStateAdapter.readState(gson, in, name, state);
                }
            }
        }
        in.endObject();
        return state;
    }
}
