package org.thane.nms.v1_13_R2.adapters.blocks.states;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.entity.EntityType;

import java.io.IOException;

public class CreatureSpawnerAdapter extends TypeAdapter<CreatureSpawner> {
    private Gson gson;

    public CreatureSpawnerAdapter(Gson gson) {
        this.gson = gson;
    }

    @Override
    public void write(JsonWriter out, CreatureSpawner value) throws IOException {
        out.beginObject();
        BlockStateAdapter.writeState(gson, out, value);
        out.name("delay").value(value.getDelay());
        out.name("maximum nearby entities").value(value.getMaxNearbyEntities());
        out.name("maximum spawn delay").value(value.getMaxSpawnDelay());
        out.name("minimum spawn delay").value(value.getMinSpawnDelay());
        out.name("player range").value(value.getRequiredPlayerRange());
        out.name("spawn count").value(value.getSpawnCount());
        out.name("spawn type");
        gson.getAdapter(EntityType.class).write(out, value.getSpawnedType());
        out.name("spawn range").value(value.getSpawnRange());
        out.endObject();
    }

    @Override
    public CreatureSpawner read(JsonReader in) throws IOException {
        CreatureSpawner state = null;
        in.beginObject();
        while (in.hasNext()) {
            if (in.peek() == JsonToken.NAME) {
                String name = in.nextName();
                switch (name) {
                    case "delay":
                        if (state == null) return null;
                        state.setDelay(in.nextInt());
                        break;
                    case "maximum nearby entities":
                    case "max nearby entities":
                        if (state == null) return null;
                        state.setMaxNearbyEntities(in.nextInt());
                        break;
                    case "maximum spawn delay":
                    case "max spawn delay":
                        if (state == null) return null;
                        state.setMaxSpawnDelay(in.nextInt());
                        break;
                    case "minimum spawn delay":
                    case "min spawn delay":
                        if (state == null) return null;
                        state.setMinSpawnDelay(in.nextInt());
                        break;
                    case "player range":
                        if (state == null) return null;
                        state.setRequiredPlayerRange(in.nextInt());
                        break;
                    case "spawn count":
                        if (state == null) return null;
                        state.setSpawnCount(in.nextInt());
                        break;
                    case "spawn type":
                        if (state == null) return null;
                        state.setSpawnedType(gson.getAdapter(EntityType.class).read(in));
                        break;
                    case "spawn range":
                        if (state == null) return null;
                        state.setSpawnRange(in.nextInt());
                        break;
                    default:
                        state = (CreatureSpawner) BlockStateAdapter.readState(gson, in, name, state);
                }
            }
        }
        in.endObject();
        return state;
    }
}
