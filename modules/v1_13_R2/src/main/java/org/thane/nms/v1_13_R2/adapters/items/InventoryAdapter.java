package org.thane.nms.v1_13_R2.adapters.items;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import org.bukkit.Bukkit;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

import java.io.IOException;

public class InventoryAdapter extends TypeAdapter<Inventory> {
    private Gson gson;

    public InventoryAdapter(Gson gson) {
        this.gson = gson;
    }

    @Override
    public void write(JsonWriter out, Inventory value) throws IOException {
        out.beginObject();
        out.name("title").value(value.getTitle());
        if (value.getType() == InventoryType.CHEST) {
            out.name("size").value(value.getSize());
        } else {
            out.value("type");
            gson.getAdapter(InventoryType.class).write(out, value.getType());
        }
        out.value("contents");
        gson.getAdapter(ItemStack[].class).write(out, value.getContents());
        if (value.getHolder() != null) {
            out.name("owner");
            gson.getAdapter(InventoryHolder.class).write(out, value.getHolder());
        }
        out.endObject();
    }

    @Override
    public Inventory read(JsonReader in) throws IOException {
        InventoryType type = null;
        String title = null;
        Integer size = null;
        ItemStack[] contents = null;
        InventoryHolder owner = null;
        in.beginObject();
        while (in.hasNext()) {
            if (in.peek() == JsonToken.NAME) {
                switch (in.nextName()) {
                    case "title":
                        title = in.nextString();
                        break;
                    case "type":
                        type = gson.getAdapter(InventoryType.class).read(in);
                        break;
                    case "size":
                        size = in.nextInt();
                        break;
                    case "contents":
                        contents = gson.getAdapter(ItemStack[].class).read(in);
                        break;
                    case "owner":
                        owner = gson.getAdapter(InventoryHolder.class).read(in);
                        break;
                }
            }
        }
        in.endObject();
        if (contents == null) return null;
        if (type == null) {
            if (size == null) return null;
            if (title == null) {
                Inventory inventory = Bukkit.createInventory(owner, size);
                inventory.setContents(contents);
                return inventory;
            } else {
                Inventory inventory = Bukkit.createInventory(owner, size, title);
                inventory.setContents(contents);
                return inventory;
            }
        } else if (size != null) {
            if (title == null) {
                Inventory inventory = Bukkit.createInventory(owner, type);
                inventory.setContents(contents);
                return inventory;
            } else {
                Inventory inventory = Bukkit.createInventory(owner, type, title);
                inventory.setContents(contents);
                return inventory;
            }
        } else return null;
    }
}