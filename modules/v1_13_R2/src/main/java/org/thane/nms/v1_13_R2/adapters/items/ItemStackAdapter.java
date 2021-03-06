package org.thane.nms.v1_13_R2.adapters.items;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import org.apache.commons.io.output.StringBuilderWriter;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.material.MaterialData;
import org.thane.api.NBT;

import java.io.IOException;

public class ItemStackAdapter extends TypeAdapter<ItemStack> {

    private Gson gson;

    public ItemStackAdapter(Gson gson) {
        this.gson = gson;
    }

    @SuppressWarnings({"deprecation", "unchecked"})
    @Override
    public void write(JsonWriter out, ItemStack value) throws IOException {
        if (value == null) {
            out.nullValue();
            return;

        }
        out.beginObject();
        out.name("material");
        gson.getAdapter(Material.class).write(out, value.getType());
        out.name("amount").value(value.getAmount());
        if (value.getData() != null && value.getData().getData() != 0) {
            out.name("data");
            gson.getAdapter(MaterialData.class).write(out, value.getData());
        }
        if (value.hasItemMeta()) {
            StringBuilderWriter stringWriter = new StringBuilderWriter();
            JsonWriter writer = new JsonWriter(stringWriter);
            TypeAdapter metaTypeAdapter = gson.getAdapter(Bukkit.getItemFactory().getItemMeta(value.getType()).getClass());
            metaTypeAdapter.write(writer, value.getItemMeta());
            if (!stringWriter.getBuilder().toString().contains("{}")) {
                out.name("meta");
                metaTypeAdapter.write(out, value.getItemMeta());
            }
            writer.flush();
            writer.close();
        }
        NBT nbt = new org.thane.nms.v1_13_R2.NBT(value).withExcludes("Unbreakable",
                "HideFlags", "display", "Damage", "AttributeModifiers", "BlockEntityTag.Items", "BlockEntityTag.Lock", "BlockEntityTag.id", "Enchantments",
                "BlockEntityTag.Command", "BlockEntityTag.CustomName", "BlockEntityTag.Primary", "BlockEntityTag.Secondary", "CustomPotionColor", "CustomPotionEffects", "Potion",
                "StoredEnchantments", "BlockEntityTag.Patterns", "BlockEntityTag.x", "BlockEntityTag.y", "BlockEntityTag.z", "generation", "pages", "author", "title", "Fireworks",
                "Recipes", "map_is_scaling", "map", "SkullOwner", "Name", "BucketVariantTag");
        if (!nbt.isEmpty()) {
            out.name("nbt");
            gson.getAdapter(NBT.class).write(out, nbt);
        }
        out.endObject();
    }

    @SuppressWarnings("deprecation")
    @Override
    public ItemStack read(JsonReader in) throws IOException {
        if (in.peek() == JsonToken.NULL) {
            in.nextNull();
            return null;
        }
        ItemStack stack = null;
        in.beginObject();
        while (in.hasNext()) {
            if (in.peek() == JsonToken.NAME) {
                switch (in.nextName()) {
                    case "material":
                        stack = new ItemStack(gson.getAdapter(Material.class).read(in));
                        break;
                    case "amount":
                        if (stack == null) return null;
                        stack.setAmount(in.nextInt());
                        break;
                    case "meta":
                        if (stack == null) return null;
                        stack.setItemMeta(gson.getAdapter(Bukkit.getItemFactory().getItemMeta(stack.getType()).getClass()).read(in));
                        break;
                    case "data":
                        if (stack == null) return null;
                        stack.setData(gson.getAdapter(MaterialData.class).read(in));
                        break;
                    case "nbt":
                        if (stack == null) return null;
                        stack = gson.getAdapter(NBT.class).read(in).copyOnto(stack);
                        break;
                }
            }
        }
        in.endObject();
        return stack;
    }
}