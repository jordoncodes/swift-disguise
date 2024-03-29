package me.onlyjordon.swiftdisguise.utils;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class UUIDFetcher {

    private static final Cache<String, UUID> cache = CacheBuilder.newBuilder().weakKeys().weakValues().build();

    public static CompletableFuture<UUID> fetchUUID(String playerName) {
        UUID id = cache.getIfPresent(playerName.toLowerCase());
        if(id != null) {
            return CompletableFuture.completedFuture(id);
        }
        CompletableFuture<UUID> future = CompletableFuture.supplyAsync(() -> {
            try {
                // Get response from Mojang API
                URL url = new URL("https://api.mojang.com/users/profiles/minecraft/" + playerName);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.connect();

                if(connection.getResponseCode() == 400) {
                    return UUID.randomUUID();
                }

                InputStream inputStream = connection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

                // Parse JSON response and get UUID
                JsonElement element = new JsonParser().parse(bufferedReader);
                JsonObject object = element.getAsJsonObject();
                String idString = object.get("id").getAsString();

                // Return UUID
                UUID uuid = stringToUUID(idString);
                cache.put(playerName.toLowerCase(), uuid);
                return uuid;
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        });

        return future;
    }


    private static UUID stringToUUID(String uuidAsString) {
        String[] parts = {
                "0x" + uuidAsString.substring(0, 8),
                "0x" + uuidAsString.substring(8, 12),
                "0x" + uuidAsString.substring(12, 16),
                "0x" + uuidAsString.substring(16, 20),
                "0x" + uuidAsString.substring(20, 32)
        };

        long mostSigBits = Long.decode(parts[0]);
        mostSigBits <<= 16;
        mostSigBits |= Long.decode(parts[1]);
        mostSigBits <<= 16;
        mostSigBits |= Long.decode(parts[2]);

        long leastSigBits = Long.decode(parts[3]);
        leastSigBits <<= 48;
        leastSigBits |= Long.decode(parts[4]);

        return new UUID(mostSigBits, leastSigBits);
    }
}
