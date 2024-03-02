package me.onlyjordon.swiftdisguise.api;

import me.onlyjordon.swiftdisguise.utils.Skin;
import me.onlyjordon.swiftdisguise.utils.SkinLayers;
import org.jetbrains.annotations.NotNull;

import java.util.EnumSet;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public abstract class SwiftDisguiseAPI implements ISwiftDisguiseAPI {

    private final Map<Object, IDisguiseData> disguiseDataMap = new ConcurrentHashMap<>();

    @Override
    public IDisguiseData getDisguiseData(Object platformPlayer) {
        checkPlayer(platformPlayer);
        return disguiseDataMap.get(platformPlayer);
    }

    @Override
    public void setDisguiseData(Object platformPlayer, IDisguiseData disguiseData) {
        checkPlayer(platformPlayer);
        disguiseDataMap.put(platformPlayer, disguiseData);
    }

    private DisguiseData getDisguiseDataImpl(Object platformPlayer) {
        return (DisguiseData) getDisguiseData(platformPlayer);
    }

    @Override
    public UUID getRealUUID(Object platformPlayer) {
        checkPlayer(platformPlayer);
        return getDisguiseDataImpl(platformPlayer).getRealUUID();
    }

    @Override
    public String getRealName(Object platformPlayer) {
        checkPlayer(platformPlayer);
        return getDisguiseDataImpl(platformPlayer).getRealName();
    }

    protected Map<Object, IDisguiseData> getDisguiseDataMap() {
        return disguiseDataMap;
    }

    @Override
    public void setDisguiseName(Object platformPlayer, String nick) {
        checkPlayer(platformPlayer);
        getDisguiseDataImpl(platformPlayer).setFakeName(nick);
    }

    @Override
    public void setDisguiseSkin(Object platformPlayer, Skin skin) {
        checkPlayer(platformPlayer);
        getDisguiseDataImpl(platformPlayer).setFakeSkin(skin);
    }

    @Override
    public void setDisguise(Object platformPlayer, String nick, Skin skin) {
        checkPlayer(platformPlayer);
        getDisguiseDataImpl(platformPlayer).setFakeName(nick);
        getDisguiseDataImpl(platformPlayer).setFakeSkin(skin);
    }

    @Override
    public void resetDisguise(Object platformPlayer) {
        checkPlayer(platformPlayer);
        resetDisguiseName(platformPlayer);
        resetSkin(platformPlayer);
    }

    @Override
    public SkinLayers getDisguiseSkinLayers(Object platformPlayer) {
        checkPlayer(platformPlayer);
        return getDisguiseDataImpl(platformPlayer).getFakeSkinLayers();
    }

    @Override
    public void setDisguiseSkinLayers(Object platformPlayer, SkinLayers skinLayers) {
        checkPlayer(platformPlayer);
        getDisguiseDataImpl(platformPlayer).setFakeSkinLayers(skinLayers);
    }

    @Override
    public void setSkinLayerVisible(Object platformPlayer, SkinLayers.SkinLayer skinLayer, boolean visible) {
        checkPlayer(platformPlayer);
        getDisguiseDataImpl(platformPlayer).getFakeSkinLayers().setLayerVisible(skinLayer, visible);
    }

    @Override
    public boolean isSkinLayerVisible(Object platformPlayer, SkinLayers.SkinLayer skinLayer) {
        checkPlayer(platformPlayer);
        return getDisguiseDataImpl(platformPlayer).getFakeSkinLayers().isLayerVisible(skinLayer);
    }

    @Override
    public void show(Object platformPlayer, SkinLayers.SkinLayer layer) {
        checkPlayer(platformPlayer);
        getDisguiseDataImpl(platformPlayer).getFakeSkinLayers().setLayerVisible(layer, true);
    }

    @Override
    public void hide(Object platformPlayer, SkinLayers.SkinLayer layer) {
        checkPlayer(platformPlayer);
        getDisguiseDataImpl(platformPlayer).getFakeSkinLayers().setLayerVisible(layer, false);
    }

    @Override
    public void disguiseAs(Object platformPlayer, Object platformEntity) {
        checkPlayer(platformPlayer);
        setDisguiseName(platformPlayer, getRealName(platformEntity));
        setDisguiseSkin(platformPlayer, getRealSkin(platformEntity));
    }

    @Override
    public void resetDisguiseName(Object platformPlayer) {
        checkPlayer(platformPlayer);
        setDisguiseName(platformPlayer, getRealName(platformPlayer));
    }

    @Override
    public void resetSkin(Object platformPlayer) {
        checkPlayer(platformPlayer);
        setDisguiseSkin(platformPlayer, getRealSkin(platformPlayer));
    }

    @Override
    public void setDisguiseSkin(Object platformPlayer, String skin) {
        checkPlayer(platformPlayer);
        getDisguiseDataImpl(platformPlayer).setFakeSkin(Skin.getSkin(skin));
    }

    @Override
    public Skin getDisguiseSkin(Object platformPlayer) {
        checkPlayer(platformPlayer);
        return getDisguiseData(platformPlayer).getFakeSkin();
    }

    @Override
    public Skin getRealSkin(Object platformPlayer) {
        checkPlayer(platformPlayer);
        return getDisguiseData(platformPlayer).getRealSkin();
    }

    @Override
    public String getDisguiseName(Object platformPlayer) {
        checkPlayer(platformPlayer);
        return getDisguiseData(platformPlayer).getFakeName();
    }

    @Override
    public EnumSet<SkinLayers.SkinLayer> getVisibleSkinLayers(Object platformPlayer) {
        checkPlayer(platformPlayer);
        return getDisguiseData(platformPlayer).getFakeSkinLayers().getVisibleLayers();
    }

    @Override
    public SkinLayers getRealSkinLayers(Object platformPlayer) {
        checkPlayer(platformPlayer);
        return getDisguiseData(platformPlayer).getRealSkinLayers();
    }

    @Override
    public ITabPrefixSuffix getDisguisePrefixSuffix(@NotNull Object platformPlayer) {
        return getDisguiseData(platformPlayer).getPrefixSuffix();
    }

    @Override
    public void setDisguisePrefixSuffix(@NotNull Object platformPlayer, ITabPrefixSuffix prefixSuffix) {
        getDisguiseDataImpl(platformPlayer).setPrefixSuffix(prefixSuffix);
    }

    protected void checkPlayer(Object platformPlayer) {
        if (!validatePlatformPlayer(platformPlayer)) {
            throw new IllegalArgumentException("Invalid Player");
        }
    }


    protected abstract boolean validatePlatformPlayer(Object platformPlayer);
}
