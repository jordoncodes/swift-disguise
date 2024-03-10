package me.onlyjordon.swiftdisguise;

import me.onlyjordon.swiftdisguise.api.SwiftDisguiseAPI;
import me.onlyjordon.swiftdisguise.platforms.Platform;

public class SpigotPlatform extends Platform {

    private final SwiftDisguiseSpigot api;
    private static SpigotPlatform instance;

    private SpigotPlatform() {
        api = new SwiftDisguiseSpigot();
    }

    public static SpigotPlatform get() {
        if (instance == null) instance = new SpigotPlatform();
        return instance;
    }

    @Override
    public SwiftDisguiseAPI getAPI() {
        return api;
    }
}
