package me.onlyjordon.swiftdisguise.api;

import me.onlyjordon.swiftdisguise.utils.Skin;
import me.onlyjordon.swiftdisguise.utils.SkinLayers;

import java.io.File;
import java.net.URL;
import java.util.EnumSet;
import java.util.UUID;

public interface ISwiftDisguiseAPI {
    void setDisguisePrefixSuffix(Object platformPlayer, TabPrefixSuffix prefixSuffix);

    void setDisguiseName(Object platformPlayer, String nick);

    void setDisguise(Object platformPlayer, String nick, Skin skin);

    void resetDisguise(Object platformPlayer);

    void resetDisguiseName(Object platformPlayer);
    void resetSkin(Object platformPlayer);

    void refreshPlayer(Object platformPlayer);

    void setDisguiseSkin(Object platformPlayer, Skin skin);
    void setDisguiseSkin(Object platformPlayer, String skin);
    void setDisguiseSkin(Object platformPlayer, URL url);
    void setDisguiseSkin(Object platformPlayer, File file);

    Skin getDisguiseSkin(Object platformPlayer);
    Skin getRealSkin(Object platformPlayer);

    String getDisguiseName(Object platformPlayer);


    EnumSet<SkinLayers.SkinLayer> getVisibleSkinLayers(Object platformPlayer);

    SkinLayers getRealSkinLayers(Object platformPlayer);
    SkinLayers getSkinLayers(Object platformPlayer);
    void setSkinLayers(Object platformPlayer, SkinLayers skinLayers);

    void setSkinLayerVisible(Object platformPlayer, SkinLayers.SkinLayer skinLayer, boolean visible);
    boolean isSkinLayerVisible(Object platformPlayer, SkinLayers.SkinLayer skinLayer);

    void show(Object platformPlayer, SkinLayers.SkinLayer layer);
    void hide(Object platformPlayer, SkinLayers.SkinLayer layer);

    void disguiseAs(Object platformPlayer, Object platformEntity);

    IDisguiseData getDisguiseData(Object platformPlayer);
    void setDisguiseData(Object platformPlayer, IDisguiseData disguiseData);

    UUID getRealUUID(Object platformPlayer);
    UUID getFakeUUID(UUID id);

    String getRealName(Object platformPlayer);

    TabPrefixSuffix getDisguisePrefixSuffix(Object platformPlayer);

    void refreshPlayerSync(Object platformPlayer);

    boolean isRealPlayer(Object platformPlayer);
}
