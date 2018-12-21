package org.thane.nms.v1_13_R2.adapters.items;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.inventory.EquipmentSlot;

import java.io.IOException;
import java.util.UUID;

public class AttributeModifierAdapter extends TypeAdapter<AttributeModifier> {
    private Gson gson;

    public AttributeModifierAdapter(Gson gson) {
        this.gson = gson;
    }

    @Override
    public void write(JsonWriter out, AttributeModifier value) throws IOException {
        out.beginObject();
        out.name("name").value(value.getName());
        out.name("amount").value(value.getAmount());
        out.name("operation");
        gson.getAdapter(AttributeModifier.Operation.class).write(out, value.getOperation());
        if (value.getSlot() != null) {
            out.name("slot");
            gson.getAdapter(EquipmentSlot.class).write(out, value.getSlot());
        }
        out.name("uuid").value(value.getUniqueId().toString());
        out.endObject();
    }

    @Override
    public AttributeModifier read(JsonReader in) throws IOException {
        String name = null;
        Double amount = null;
        AttributeModifier.Operation operation = null;
        EquipmentSlot slot = null;
        UUID uuid = null;
        in.beginObject();
        while (in.hasNext()) {
            if (in.peek() == JsonToken.NAME) {
                switch (in.nextName()) {
                    case "name":
                        name = in.nextString();
                        break;
                    case "amount":
                        amount = in.nextDouble();
                        break;
                    case "operation":
                        operation = gson.getAdapter(AttributeModifier.Operation.class).read(in);
                        break;
                    case "slot":
                        slot = gson.getAdapter(EquipmentSlot.class).read(in);
                        break;
                    case "uuid":
                        uuid = UUID.fromString(in.nextString());
                        break;
                }
            }
        }
        in.endObject();
        if (amount == null || name == null || operation == null) return null;
        if (uuid == null) uuid = UUID.randomUUID();
        return new AttributeModifier(uuid, name, amount, operation, slot);
    }
}