package org.thane.nms.v1_13_R2.adapters.blocks.states;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import org.bukkit.block.Beacon;
import org.bukkit.potion.PotionEffect;

import java.io.IOException;

public class BeaconAdapter extends TypeAdapter<Beacon> {
    private Gson gson;

    public BeaconAdapter(Gson gson) {
        this.gson = gson;
    }

    @Override
    public void write(JsonWriter out, Beacon value) throws IOException {
        out.beginObject();
        BlockStateAdapter.writeState(gson, out, value);
        out.name("primary effect");
        gson.getAdapter(PotionEffect.class).write(out, value.getPrimaryEffect());
        out.name("secondary effect");
        gson.getAdapter(PotionEffect.class).write(out, value.getSecondaryEffect());
        out.endObject();
    }

    @Override
    public Beacon read(JsonReader in) throws IOException {
        Beacon state = null;
        in.beginObject();
        while (in.hasNext()) {
            if (in.peek() == JsonToken.NAME) {
                String name = in.nextName();
                switch (name) {
                    case "primary effect":
                        if (state == null) return null;
                        state.setPrimaryEffect(gson.getAdapter(PotionEffect.class).read(in).getType());
                        break;
                    case "secondary effect":
                        if (state == null) return null;
                        state.setSecondaryEffect(gson.getAdapter(PotionEffect.class).read(in).getType());
                        break;
                    default:
                        state = (Beacon) BlockStateAdapter.readState(gson, in, name, state);
                }
            }
        }
        in.endObject();
        return state;
    }
}
