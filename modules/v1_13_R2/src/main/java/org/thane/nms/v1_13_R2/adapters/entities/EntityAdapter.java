package org.thane.nms.v1_13_R2.adapters.entities;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;

import java.io.IOException;
import java.util.UUID;

public class EntityAdapter extends TypeAdapter<Entity> {
    @Override
    public void write(JsonWriter out, Entity value) throws IOException {
        out.value(value.getUniqueId().toString());
    }

    @Override
    public Entity read(JsonReader in) throws IOException {
        return Bukkit.getServer().getEntity(UUID.fromString(in.nextString()));
    }
}
