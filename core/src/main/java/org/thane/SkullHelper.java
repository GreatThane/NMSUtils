package org.thane;

import com.google.gson.*;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Skull;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;

import java.io.*;
import java.lang.reflect.Field;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.util.UUID;

public class SkullHelper {

    private static final File SKIN_CACHE_FILE = new File(NMSUtils.INSTANCE.getDataFolder().getAbsolutePath() + File.separatorChar + "skin_cache.json");

    private static final String BOUNDARY = "*****";
    private static final String HYPHENS = "--";
    private static final String CRLF = "\r\n";
    private static final URL UPLOAD_URL;

    private static final JsonObject SKIN_CACHE;

    private static final Field ITEM_PROFILE_FIELD;
    private static final Field BLOCK_PROFILE_FIELD;

    static {
        JsonObject SKIN_CACHE1 = null;
        URL UPLOAD_URL1 = null;
        Field ITEM_PROFILE_FIELD1 = null;
        Field BLOCK_PROFILE_FIELD1 = null;
        try {
            if (SKIN_CACHE_FILE.exists()) {
                SKIN_CACHE1 = new JsonParser().parse(new String(Files.readAllBytes(SKIN_CACHE_FILE.toPath()))).getAsJsonObject();
            } else SKIN_CACHE1 = new JsonObject();
            UPLOAD_URL1 = new URL("http://api.mineskin.org/generate/upload");
            ITEM_PROFILE_FIELD1 = Bukkit.getItemFactory().getItemMeta(Material.PLAYER_HEAD).getClass().getDeclaredField("profile");
            ITEM_PROFILE_FIELD1.setAccessible(true);
            BLOCK_PROFILE_FIELD1 = Class.forName("org.bukkit.craftbukkit." + NMSUtils.version + ".block.CraftSkull").getDeclaredField("profile");
        } catch (IOException | NoSuchFieldException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        ITEM_PROFILE_FIELD = ITEM_PROFILE_FIELD1;
        BLOCK_PROFILE_FIELD = BLOCK_PROFILE_FIELD1;
        SKIN_CACHE = SKIN_CACHE1;
        UPLOAD_URL = UPLOAD_URL1;
    }

    public static void cacheSkin(String cachedName, File file, boolean override) {
        if (override || !SKIN_CACHE.has(cachedName)) {
            Runnable runnable = () -> {
                try {
                    byte[] pngBytes = Files.readAllBytes(file.toPath());
                    HttpURLConnection connection = (HttpURLConnection) UPLOAD_URL.openConnection();
                    connection.setUseCaches(false);
                    connection.setDoOutput(true);

                    connection.setRequestMethod("POST");
                    connection.setRequestProperty("Connection", "Keep-Alive");
                    connection.setRequestProperty("Cache-Control", "no-cache");
                    connection.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + BOUNDARY);

                    DataOutputStream request = new DataOutputStream(connection.getOutputStream());
                    request.writeBytes(HYPHENS + BOUNDARY + CRLF);
                    request.writeBytes("Content-Disposition: form-data; name=\""
                            + cachedName + "\";filename=\""
                            + file.getName() + "\"" + CRLF);

                    request.write(pngBytes);

                    request.writeBytes(CRLF);
                    request.writeBytes(HYPHENS + BOUNDARY + HYPHENS + CRLF);

                    request.flush();
                    request.close();

                    connection.setDoInput(true
                    );

                    InputStream responseStream = new BufferedInputStream(connection.getInputStream());
                    BufferedReader responseStreamReader = new BufferedReader(new InputStreamReader(responseStream));
                    String line;
                    StringBuilder builder = new StringBuilder();
                    while ((line = responseStreamReader.readLine()) != null) {
                        builder.append(line).append("\n");
                    }
                    responseStream.close();
                    connection.disconnect();
                    synchronized (SKIN_CACHE) {
                        SKIN_CACHE.add(cachedName, new JsonParser().parse(builder.toString()).getAsJsonObject());
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            };
            if (Bukkit.isPrimaryThread()) {
                Bukkit.getScheduler().runTaskAsynchronously(NMSUtils.INSTANCE, runnable);
            } else runnable.run();
        }
    }

    @SuppressWarnings("Duplicates")
    public static void cacheSkin(String cachedName, String base64, boolean override) {
        if (override || !SKIN_CACHE.has(cachedName)) {
            JsonObject object = new JsonParser().parse(Base64Coder.decodeString(base64)).getAsJsonObject();
            JsonObject finalObject = new JsonObject();
            finalObject.add("name", new JsonPrimitive(""));
            JsonObject data = new JsonObject();
            data.add("uuid", new JsonPrimitive(UUID.randomUUID().toString()));
            data.add("url", object.getAsJsonObject("textures").getAsJsonObject("SKIN").get("url"));
            data.add("value", new JsonPrimitive(base64));
            finalObject.add("data", data);

            SKIN_CACHE.add(cachedName, finalObject);
        }
    }


    public static void cacheSkin(String cachedName, UUID playerUUID, boolean override) {
        if (override || !SKIN_CACHE.has(cachedName))
            SKIN_CACHE.add(cachedName, new JsonPrimitive(playerUUID.toString()));
    }

    public static void cacheSkin(String cachedName, JsonObject object, boolean override) {
        if (override || !SKIN_CACHE.has(cachedName))
            SKIN_CACHE.add(cachedName, object);
    }

    @SuppressWarnings("Duplicates")
    public static void cacheSkin(String cachedName, URL url, boolean override) {
        if (url.getHost().contains("minecraft.net") || url.getHost().contains("mojang.com")) {
            if (override || !SKIN_CACHE.has(cachedName)) {
                JsonObject object = new JsonObject();
                object.add("name", new JsonPrimitive(""));
                JsonObject data = new JsonObject();
                data.add("uuid", new JsonPrimitive(UUID.randomUUID().toString()));
                data.add("url", new JsonPrimitive(url.getProtocol() + "://" + url.getHost() + url.getPath()));
                object.add("data", data);

                SKIN_CACHE.add(cachedName, object);
            }
        }
    }

    @SuppressWarnings("Duplicates")
    public static void setTexture(ItemStack head, String cachedName) {
        SkullMeta meta = (SkullMeta) head.getItemMeta();
        JsonElement element = SKIN_CACHE.get(cachedName);

        if (element instanceof JsonObject) {
            String name = ((JsonObject) element).get("name").getAsString();
            if (name != null && name.isEmpty()) name = null;

            GameProfile profile = new GameProfile(UUID.fromString(((JsonObject) element).getAsJsonObject("data").get("uuid").getAsString()), name);
            profile.getProperties().put("textures", new Property("textures", Base64Coder.encodeString(String.format("{textures:{SKIN:{url:\"%s\"}}}",
                    SKIN_CACHE.get(cachedName).getAsJsonObject().getAsJsonObject("data").getAsJsonObject("texture").get("url").getAsString()))));
            try {
                ITEM_PROFILE_FIELD.set(meta, profile);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        } else if (element instanceof JsonPrimitive && ((JsonPrimitive) element).isString()) {
            meta.setOwningPlayer(Bukkit.getPlayer(UUID.fromString(element.getAsString())));
            head.setItemMeta(meta);
        }
    }

    @SuppressWarnings("Duplicates")
    public static void setTexture(Skull skull, String cachedName) {
        JsonElement element = SKIN_CACHE.get(cachedName);
        if (element instanceof JsonObject) {
            String name = ((JsonObject) element).get("name").getAsString();
            if (name != null && name.isEmpty()) name = null;
            GameProfile profile = new GameProfile(UUID.fromString(((JsonObject) element).getAsJsonObject("data").get("uuid").getAsString()), name);
            profile.getProperties().put("textures", new Property("textures", Base64Coder.encodeString(String.format("{textures:{SKIN:{url:\"%s\"}}}",
                    SKIN_CACHE.get(cachedName).getAsJsonObject().getAsJsonObject("data").getAsJsonObject("texture").get("url").getAsString()))));
            try {
                BLOCK_PROFILE_FIELD.set(skull, profile);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        } else skull.setOwningPlayer(Bukkit.getOfflinePlayer(UUID.fromString(element.getAsString())));
        skull.update(true, false);
    }

    public static void saveCache() {
        saveCache(false);
    }

    public static void saveCache(boolean prettyPrinting) {
        if (!SKIN_CACHE_FILE.exists()) {
            try {
                SKIN_CACHE_FILE.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try (PrintStream out = new PrintStream(new FileOutputStream(SKIN_CACHE_FILE))) {
            if (prettyPrinting) {
                out.print(NMSUtils.getGson().toJson(SKIN_CACHE));
            } else out.print(SKIN_CACHE.toString());

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static JsonObject getSkinCache() {
        return SKIN_CACHE;
    }

    static void init() {
    }
}
