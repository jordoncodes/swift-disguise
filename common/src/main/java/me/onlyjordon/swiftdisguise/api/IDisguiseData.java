package me.onlyjordon.swiftdisguise.api;

import me.onlyjordon.swiftdisguise.utils.Skin;
import me.onlyjordon.swiftdisguise.utils.SkinLayers;

import java.util.UUID;

public interface IDisguiseData {

    ITabPrefixSuffix getPrefixSuffix();

    UUID getFakeUUID();

    Object getDisguisedAsEntity();

    Skin getFakeSkin();

    Skin getRealSkin();

    String getFakeName();

    String getRealName();

    UUID getRealUUID();

    SkinLayers getFakeSkinLayers();

    SkinLayers getRealSkinLayers();
}
