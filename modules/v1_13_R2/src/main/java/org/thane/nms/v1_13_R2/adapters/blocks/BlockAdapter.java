package org.thane.nms.v1_13_R2.adapters.blocks;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import org.bukkit.Location;
import org.bukkit.block.Block;

import java.io.IOException;

public class BlockAdapter extends TypeAdapter<Block> {
    private Gson gson;

    public BlockAdapter(Gson gson) {
        this.gson = gson;
    }

    @Override
    public void write(JsonWriter out, Block value) throws IOException {
        gson.getAdapter(Location.class).write(out, value.getLocation());
    }

    @Override
    public Block read(JsonReader in) throws IOException {
        Location location = gson.getAdapter(Location.class).read(in);
        if (location == null) return null;
        return location.getWorld().getBlockAt(location);
    }
}
