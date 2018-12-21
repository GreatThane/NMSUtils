package org.thane.nms.v1_13_R2.adapters.items;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import org.thane.api.NBT;

import java.io.IOException;

public class NBTAdapter extends TypeAdapter<NBT> {
    private Gson gson;

    public NBTAdapter(Gson gson) {
        this.gson = gson;
    }

    @Override
    public void write(JsonWriter out, NBT value) throws IOException {
        gson.getAdapter(JsonElement.class).write(out, value.asJsonObject());
    }

    @SuppressWarnings("unchecked")
    @Override
    public NBT read(JsonReader in) throws IOException {
        return new org.thane.nms.v1_13_R2.NBT(gson.getAdapter(JsonObject.class).read(in));
    }
}