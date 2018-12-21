package org.thane.nms.v1_13_R2.adapters.blocks;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import org.bukkit.Chunk;
import org.bukkit.World;

import java.io.IOException;

public class ChunkAdapter extends TypeAdapter<Chunk> {
    private Gson gson;

    public ChunkAdapter(Gson gson) {
        this.gson = gson;
    }

    @Override
    public void write(JsonWriter out, Chunk value) throws IOException {
        out.beginObject();
        out.name("world");
        gson.getAdapter(World.class).write(out, value.getWorld());
        out.name("x").value(value.getX());
        out.name("z").value(value.getZ());
        out.endObject();
    }

    @Override
    public Chunk read(JsonReader in) throws IOException {
        World world = null;
        Integer x = null;
        Integer z = null;
        in.beginObject();
        while (in.hasNext()) {
            if (in.peek() == JsonToken.NAME) {
                switch (in.nextName()) {
                    case "world":
                        world = gson.getAdapter(World.class).read(in);
                        break;
                    case "x":
                        x = in.nextInt();
                        break;
                    case "z":
                        z = in.nextInt();
                        break;
                }
            }
        }
        in.endObject();
        if (world == null || x == null || z == null) return null;
        return world.getChunkAt(x, z);
    }
}