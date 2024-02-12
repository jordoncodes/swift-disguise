package me.onlyjordon.nicknamingapi;

import me.onlyjordon.nicknamingapi.utils.Skin;
import me.onlyjordon.nicknamingapi.utils.SkinLayers;
import net.kyori.adventure.text.TextComponent;

import java.util.UUID;

public interface INickData {
    Skin getCurrentSkin();

    Skin getOriginalSkin();

    SkinLayers getSkinLayers();

    String getNickname();

    TextComponent getPrefix();

    TextComponent getSuffix();

    UUID getFakeUUID();
}
