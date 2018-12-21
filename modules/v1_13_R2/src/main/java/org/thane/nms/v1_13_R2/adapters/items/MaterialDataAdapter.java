package org.thane.nms.v1_13_R2.adapters.items;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import org.bukkit.Material;
import org.bukkit.material.MaterialData;

import java.io.IOException;

@SuppressWarnings("deprecation")
public class MaterialDataAdapter extends TypeAdapter<MaterialData> {
    private Gson gson;

    public MaterialDataAdapter(Gson gson) {
        this.gson = gson;
    }

    @Override
    public void write(JsonWriter out, MaterialData value) throws IOException {
        out.beginObject();
        out.name("material");
        gson.getAdapter(Material.class).write(out, value.getItemType());
        out.name("data").value(value.getData());
        out.endObject();
    }

    @Override
    public MaterialData read(JsonReader in) throws IOException {
        Material material = null;
        Byte data = null;
        in.beginObject();
        while (in.hasNext()) {
            if (in.peek() == JsonToken.NAME) {
                switch (in.nextName()) {
                    case "material":
                        material = gson.getAdapter(Material.class).read(in);
                        break;
                    case "data":
                        data = (byte) in.nextInt();
                        break;
                }
            }
        }
        in.endObject();
        if (material != null) {
            if (data != null) {
                return new MaterialData(material, data);
            } else return new MaterialData(material);
        } else return null;

    }
}