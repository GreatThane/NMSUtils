package org.thane.nms.v1_13_R2.adapters.nms;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import net.minecraft.server.v1_13_R2.AttributeRanged;

import java.io.IOException;
import java.lang.reflect.Field;

public class AttributeRangedAdapter extends TypeAdapter<AttributeRanged> {

    private static Field minimumField;

    static {
        try {
            minimumField = AttributeRanged.class.getDeclaredField("a");
            minimumField.setAccessible(true);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
    }
    @Override
    public void write(JsonWriter out, AttributeRanged value) throws IOException {
        out.beginObject();
        out.name("name").value(value.getName());
        try {
            out.name("minimum").value((double) minimumField.get(value));
            out.name("maximum").value(value.maximum);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        out.name("default").value(value.getDefault());
        out.endObject();
    }

    @Override
    public AttributeRanged read(JsonReader in) throws IOException {
        double minimum = 0;
        double defaultValue = Integer.MAX_VALUE / (double) 2;
        double maximum = Integer.MAX_VALUE;
        String name = "";

        in.beginObject();
        while (in.hasNext()) {
            if (in.peek() == JsonToken.NAME) {
                switch (in.nextName()) {
                    case "name":
                        name = in.nextString();
                        break;
                    case "default":
                        defaultValue = in.nextDouble();
                        break;
                    case "minimum":
                    case "min":
                        minimum = in.nextDouble();
                        break;
                    case "maximum":
                    case "max":
                        maximum = in.nextDouble();
                        break;
                }
            }
        }
        in.endObject();
        return new AttributeRanged(null, name, defaultValue, minimum, maximum);
    }
}
