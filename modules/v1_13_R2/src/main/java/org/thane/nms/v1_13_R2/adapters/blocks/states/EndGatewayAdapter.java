package org.thane.nms.v1_13_R2.adapters.blocks.states;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import org.bukkit.Location;
import org.bukkit.block.EndGateway;

import java.io.IOException;

public class EndGatewayAdapter extends TypeAdapter<EndGateway> {
    private Gson gson;

    public EndGatewayAdapter(Gson gson) {
        this.gson = gson;
    }

    @Override
    public void write(JsonWriter out, EndGateway value) throws IOException {
        out.beginObject();
        BlockStateAdapter.writeState(gson, out, value);
        if (value.isPlaced()) {
            out.name("age").value(value.getAge());
            out.name("exit location");
            gson.getAdapter(Location.class).write(out, value.getExitLocation());
            out.name("exact teleport").value(value.isExactTeleport());
        }
        out.endObject();
    }

    @Override
    public EndGateway read(JsonReader in) throws IOException {
        EndGateway state = null;
        in.beginObject();
        while (in.hasNext()) {
            if (in.peek() == JsonToken.NAME) {
                String name = in.nextName();
                switch (name) {
                    case "age":
                        if (state == null) return null;
                        state.setAge(in.nextLong());
                        break;
                    case "exit location":
                        if (state == null) return null;
                        state.setExitLocation(gson.getAdapter(Location.class).read(in));
                        break;
                    case "exact teleport":
                        if (state == null) return null;
                        state.setExactTeleport(in.nextBoolean());
                        break;
                    default:
                        state = (EndGateway) BlockStateAdapter.readState(gson, in, name, state);
                }
            }
        }
        in.endObject();
        return state;
    }
}
