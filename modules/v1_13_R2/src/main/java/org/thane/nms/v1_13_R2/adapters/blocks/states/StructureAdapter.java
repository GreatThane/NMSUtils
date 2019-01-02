package org.thane.nms.v1_13_R2.adapters.blocks.states;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import org.bukkit.block.Structure;
import org.bukkit.block.structure.Mirror;
import org.bukkit.block.structure.StructureRotation;
import org.bukkit.block.structure.UsageMode;
import org.bukkit.util.BlockVector;

import java.io.IOException;

public class StructureAdapter extends TypeAdapter<Structure> {
    private Gson gson;

    public StructureAdapter(Gson gson) {
        this.gson = gson;
    }

    @Override
    public void write(JsonWriter out, Structure value) throws IOException {
        out.name("author").value(value.getAuthor());
        out.name("structure name").value(value.getStructureName());
        out.name("integrity").value(value.getIntegrity());
        out.name("metadata").value(value.getMetadata());
        if (value.getSeed() != 0) {
            out.name("seed").value(value.getSeed());
        }
        if (value.isPlaced()) {
            out.name("relative position");
            gson.getAdapter(BlockVector.class).write(out, value.getRelativePosition());
            if (value.getRotation() != StructureRotation.NONE) {
                out.name("rotation");
                gson.getAdapter(StructureRotation.class).write(out, value.getRotation());
            }
            if (value.getMirror() != Mirror.NONE) {
                out.name("mirror");
                gson.getAdapter(Mirror.class).write(out, value.getMirror());
            }
            out.name("structure size");
            gson.getAdapter(BlockVector.class).write(out, value.getStructureSize());
            out.name("usage mode");
            gson.getAdapter(UsageMode.class).write(out, value.getUsageMode());
            out.name("bounding box visibility").value(value.isBoundingBoxVisible());
            out.name("ignore entities").value(value.isIgnoreEntities());
            out.name("show air").value(value.isShowAir());
        }
    }

    @Override
    public Structure read(JsonReader in) throws IOException {
        Structure state = null;
        in.beginObject();
        while (in.hasNext()) {
            if (in.peek() == JsonToken.NAME) {
                String name = in.nextName();
                switch (name) {
                    case "author":
                        if (state == null) return null;
                        state.setAuthor(in.nextString());
                        break;
                    case "structure name":
                        if (state == null) return null;
                        state.setStructureName(in.nextString());
                        break;
                    case "integrity":
                        if (state == null) return null;
                        state.setIntegrity((float) in.nextDouble());
                        break;
                    case "metadata":
                        if (state == null) return null;
                        state.setMetadata(in.nextString());
                        break;
                    case "relative position":
                        if (state == null) return null;
                        state.setRelativePosition(gson.getAdapter(BlockVector.class).read(in));
                        break;
                    case "rotation":
                        if (state == null) return null;
                        state.setRotation(gson.getAdapter(StructureRotation.class).read(in));
                        break;
                    case "mirror":
                        if (state == null) return null;
                        state.setMirror(gson.getAdapter(Mirror.class).read(in));
                        break;
                    case "structure size":
                        if (state == null) return null;
                        state.setStructureSize(gson.getAdapter(BlockVector.class).read(in));
                        break;
                    case "usage mode":
                        if (state == null) return null;
                        state.setUsageMode(gson.getAdapter(UsageMode.class).read(in));
                        break;
                    case "bounding box visibility":
                        if (state == null) return null;
                        state.setBoundingBoxVisible(in.nextBoolean());
                        break;
                    case "ignore entities":
                        if (state == null) return null;
                        state.setIgnoreEntities(in.nextBoolean());
                        break;
                    case "show air":
                        if (state == null) return null;
                        state.setShowAir(in.nextBoolean());
                        break;
                    default:
                        state = (Structure) BlockStateAdapter.readState(gson, in, name, state);
                }
            }
        }
        in.endObject();
        return state;
    }
}
