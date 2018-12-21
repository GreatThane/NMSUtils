package org.thane.nms.v1_13_R2.adapters.blocks;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import org.bukkit.Bukkit;
import org.bukkit.block.data.BlockData;

import java.io.IOException;

public class BlockDataAdapter extends TypeAdapter<BlockData> {

    @Override
    public void write(JsonWriter out, BlockData value) throws IOException {
        out.value(value.getAsString());
    }

    @Override
    public BlockData read(JsonReader in) throws IOException {
        return Bukkit.getServer().createBlockData(in.nextString());
    }
}
