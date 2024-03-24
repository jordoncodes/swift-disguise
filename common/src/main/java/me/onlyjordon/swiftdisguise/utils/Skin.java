package me.onlyjordon.swiftdisguise.utils;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.hash.Hashing;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.bspfsystems.yamlconfiguration.file.FileConfiguration;
import org.bspfsystems.yamlconfiguration.file.YamlConfiguration;
import org.mineskin.MineskinClient;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.file.Files;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

@SuppressWarnings("UnstableApiUsage")
public class Skin {

    private static final String MOJANG_API_URL = "https://api.mojang.com/users/profiles/minecraft/";
    private static final String TEXTURES_API_URL = "https://sessionserver.mojang.com/session/minecraft/profile/";
    private static final Cache<String, Skin> skins = CacheBuilder.newBuilder().expireAfterAccess(30, TimeUnit.MINUTES).build();
    private static final File skinsFile = new File(Util.getDataFolder()+File.separator+"skins.yml");
    private static final FileConfiguration config;

    static {
        if (!skinsFile.exists()) {
            try {
                File parentFile = skinsFile.getParentFile();
                if (!parentFile.exists()) parentFile.mkdirs();
            } catch (Exception ignored) {}
            if (!skinsFile.exists()) {
                try {
                    skinsFile.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                    throw new RuntimeException(e);
                }
            }
        }
        config = YamlConfiguration.loadConfiguration(skinsFile);
    }

    private final String value;
    private final String signature;

    public Skin(String value, String signature) {
        this.value = value;
        this.signature = signature;
    }

    public String getValue() {
        return value;
    }

    public String getSignature() {
        return signature;
    }

    public static Skin getSkin(String name) {
        try {
            Skin skin = skins.getIfPresent(name);
            if (skin != null) return skin;
            URL url = new URL(MOJANG_API_URL + name);
            JsonParser parser = new JsonParser();
            String uuid = parser.parse(new InputStreamReader(url.openStream())).getAsJsonObject().get("id").getAsString();
            url = new URL(TEXTURES_API_URL + uuid + "?unsigned=false");
            JsonObject obj = parser.parse(new InputStreamReader(url.openStream())).getAsJsonObject().get("properties").getAsJsonArray().get(0).getAsJsonObject();
            skin = new Skin(obj.get("value").getAsString(), obj.get("signature").getAsString());
            skins.put(name, skin);
            return skin;
        } catch (IOException e) {
            throw new RuntimeException("Couldn't download skin " + name, e);
        }
    }

    public static Skin getSkin(URL url) {
        MineskinClient client = Util.getMineSkinClient();
        try {
            org.mineskin.data.Skin mineskinSkin = client.generateUrl(url.toString()).get();
            return new Skin(mineskinSkin.data.texture.value, mineskinSkin.data.texture.signature);
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public static Skin getSkin(File file) {
        if (!file.exists()) throw new RuntimeException("Could not get skin from file!", new FileNotFoundException("File " + file + " doesn't exist"));
        String hash = getHash(file);
        if (config.isString(hash)) {
            String[] data = config.getString(hash).split(":");
            return new Skin(data[0], data[1]);
        }
        try {
            org.mineskin.data.Skin skin = getSkinFromFile(file).get();
            config.set(hash, skin.data.texture.value + ":" + skin.data.texture.signature);
            config.save(skinsFile);
            return new Skin(skin.data.texture.value, skin.data.texture.signature);
        } catch (ExecutionException | InterruptedException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static CompletableFuture<org.mineskin.data.Skin> getSkinFromFile(File file) {
        MineskinClient client = Util.getMineSkinClient();
        try {
            return client.generateUpload(file);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private static String getHash(File file) {
        try {
            return Hashing.sha256().hashBytes(Files.readAllBytes(file.toPath())).toString();
        } catch (IOException e) {
            throw new RuntimeException("Couldn't read file " + file, e);
        }
    }

    @Override
    public String toString() {
        return "Skin{" +
                "value='" + value + '\'' +
                ", signature='" + signature + '\'' +
                '}';
    }
}
