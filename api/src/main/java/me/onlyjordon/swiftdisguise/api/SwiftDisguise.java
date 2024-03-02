package me.onlyjordon.swiftdisguise.api;

import me.onlyjordon.swiftdisguise.platforms.IPlatform;
import me.onlyjordon.swiftdisguise.platforms.Platform;


public class SwiftDisguise {

    private SwiftDisguise() {
    }

    public static SwiftDisguiseConfig getConfig() {
        return SwiftDisguiseLoader.getConfig();
    }

    /**
     * @param platform The platform to use for the API. (Example: <b><i>SpigotPlatform.get()</i></b>)
     * @return The API for the specified platform.
     */
    public static SwiftDisguiseAPI getAPI(Platform platform) {
        return platform.getAPI();
    }

}
