package me.onlyjordon.swiftdisguise.api;

import me.onlyjordon.swiftdisguise.api.utils.Skin;
import me.onlyjordon.swiftdisguise.api.utils.SkinLayers;
import net.kyori.adventure.text.TextComponent;

import java.util.UUID;

public interface IDisguiseData {
    Skin getCurrentSkin();

    Skin getOriginalSkin();

    SkinLayers getSkinLayers();

    String getNickname();

    TextComponent getPrefix();

    TextComponent getSuffix();

    UUID getFakeUUID();
}
