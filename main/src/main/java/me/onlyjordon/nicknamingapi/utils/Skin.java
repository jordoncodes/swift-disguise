package me.onlyjordon.nicknamingapi.utils;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.concurrent.TimeUnit;

@SuppressWarnings("UnstableApiUsage")
public class Skin {

    private static final String MOJANG_API_URL = "https://api.mojang.com/users/profiles/minecraft/";
    private static final String TEXTURES_API_URL = "https://sessionserver.mojang.com/session/minecraft/profile/";
    private static final Cache<String, Skin> skins = CacheBuilder.newBuilder().expireAfterAccess(30, TimeUnit.MINUTES).build();

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


}
