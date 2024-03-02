package me.onlyjordon.swiftdisguise.api;

import me.onlyjordon.swiftdisguise.utils.Skin;
import me.onlyjordon.swiftdisguise.utils.SkinLayers;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public interface IDisguiseData {

    UUID getFakeUUID();

    Object getDisguisedAsEntity();

    Skin getFakeSkin();

    Skin getRealSkin();

    String getFakeName();

    String getRealName();

    UUID getRealUUID();

    ITabPrefixSuffix getPrefixSuffix();

    SkinLayers getFakeSkinLayers();

    SkinLayers getRealSkinLayers();
}
