package org.thane.nms.v1_13_R2.adapters.entities.effects;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.io.IOException;

public class PotionEffectAdapter extends TypeAdapter<PotionEffect> {
    private Gson gson;

    public PotionEffectAdapter(Gson gson) {
        this.gson = gson;
    }

    @Override
    public void write(JsonWriter out, PotionEffect value) throws IOException {
        if (value == null) {
            out.nullValue();
            return;
        }
        out.beginObject();
        out.name("type");
        gson.getAdapter(PotionEffectType.class).write(out, value.getType());
        out.name("duration").value(value.getDuration());
        if (value.getAmplifier() > 0)
            out.name("amplifier").value(value.getAmplifier());
        if (!value.hasParticles())
            out.name("particles").value(value.hasParticles());
        if (value.isAmbient())
            out.name("ambient").value(value.isAmbient());
        if (value.hasIcon())
            out.name("icon").value(value.hasIcon());
        out.endObject();
    }

    @Override
    public PotionEffect read(JsonReader in) throws IOException {
        PotionEffectType type = null;
        int duration = 0;
        int amplifier = 0;
        boolean particles = true;
        boolean ambient = false;
        boolean icon = false;
        in.beginObject();
        while (in.hasNext()) {
            if (in.peek() == JsonToken.NAME) {
                switch (in.nextName()) {
                    case "type":
                        type = gson.getAdapter(PotionEffectType.class).read(in);
                        break;
                    case "duration":
                        duration = in.nextInt();
                        break;
                    case "amplifier":
                        amplifier = in.nextInt();
                        break;
                    case "particles":
                        particles = in.nextBoolean();
                        break;
                    case "ambient":
                        ambient = in.nextBoolean();
                        break;
                    case "icon":
                        icon = in.nextBoolean();
                        break;
                }
            }
        }
        in.endObject();
        if (type == null || duration == 0) return null;
        return new PotionEffect(type, duration, amplifier, ambient, particles, icon);
    }
}
