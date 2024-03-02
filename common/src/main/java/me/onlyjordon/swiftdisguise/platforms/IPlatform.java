package me.onlyjordon.swiftdisguise.platforms;

import me.onlyjordon.swiftdisguise.api.ISwiftDisguiseAPI;
import me.onlyjordon.swiftdisguise.api.SwiftDisguiseAPI;

public interface IPlatform {
    SwiftDisguiseAPI getAPI();
}
