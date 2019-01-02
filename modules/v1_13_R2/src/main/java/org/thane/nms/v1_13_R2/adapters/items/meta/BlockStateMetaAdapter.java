package org.thane.nms.v1_13_R2.adapters.items.meta;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.BlockState;
import org.bukkit.inventory.meta.BlockStateMeta;

import java.io.IOException;

public class BlockStateMetaAdapter extends TypeAdapter<BlockStateMeta> {
    private Gson gson;

    public BlockStateMetaAdapter(Gson gson) {
        this.gson = gson;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void write(JsonWriter out, BlockStateMeta value) throws IOException {
        out.beginObject();
        if (value.hasBlockState()) {
            out.name("block state");
            TypeAdapter  blockStateTypeAdapter = gson.getAdapter(value.getBlockState().getClass());
            blockStateTypeAdapter.write(out, value.getBlockState());
        }
        ItemMetaAdapter.writeMeta(gson, out, value);
        out.endObject();
    }

    @Override
    public BlockStateMeta read(JsonReader in) throws IOException {
        BlockStateMeta meta = (BlockStateMeta) Bukkit.getItemFactory().getItemMeta(Material.CHEST);
        in.beginObject();
        while (in.hasNext()) {
            if (in.peek() == JsonToken.NAME) {
                String name = in.nextName();
                switch (name) {
                    case "block state":
                        BlockState state = gson.getAdapter(meta.getBlockState().getClass()).read(in);
                        meta = (BlockStateMeta) Bukkit.getItemFactory().getItemMeta(state.getType());
                        meta.setBlockState(state);
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
