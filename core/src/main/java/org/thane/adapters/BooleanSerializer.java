package org.thane.adapters;

import com.google.gson.*;

import java.lang.reflect.Type;

public class BooleanSerializer implements JsonDeserializer<Boolean> {

    @Override
    public Boolean deserialize(JsonElement arg0, Type arg1, JsonDeserializationContext arg2) throws JsonParseException {
        if (arg0.getAsJsonPrimitive().isBoolean()) {
            return arg0.getAsBoolean();
        } else return arg0.getAsInt() == 1;
    }
}
