package me.onlyjordon.swiftdisguise.api;

import me.onlyjordon.swiftdisguise.utils.Skin;
import me.onlyjordon.swiftdisguise.utils.SkinLayers;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class DisguiseData implements IDisguiseData {

    private UUID realUUID, fakeUUID;
    private String realName, fakeName;
    private Skin realSkin, fakeSkin;
    private SkinLayers realSkinLayers, fakeSkinLayers;
    private ITabPrefixSuffix prefixSuffix;
    private Object disguisedAsEntity;

    public DisguiseData(@Nullable UUID realUUID, @Nullable UUID fakeUUID, @Nullable String realName, @Nullable String fakeName, @Nullable Skin realSkin, @Nullable Skin fakeSkin, @Nullable SkinLayers realSkinLayers, @Nullable SkinLayers fakeSkinLayers, @Nullable ITabPrefixSuffix prefixSuffix, @Nullable Object disguisedAsEntity) {
        this.realUUID = realUUID;
        this.fakeUUID = fakeUUID;
        this.realName = realName;
        this.fakeName = fakeName;
        this.realSkin = realSkin;
        this.fakeSkin = fakeSkin;
        this.realSkinLayers = realSkinLayers;
        this.fakeSkinLayers = fakeSkinLayers;
        this.prefixSuffix = prefixSuffix;
        this.disguisedAsEntity = disguisedAsEntity;
    }

    @Override
    public ITabPrefixSuffix getPrefixSuffix() {
        return prefixSuffix;
    }

    public void setPrefixSuffix(ITabPrefixSuffix prefixSuffix) {
        this.prefixSuffix = prefixSuffix;
    }

    @Override
    public UUID getFakeUUID() {
        return fakeUUID;
    }

    @Override
    public Object getDisguisedAsEntity() {
        return disguisedAsEntity;
    }

    @Override
    public Skin getFakeSkin() {
        return fakeSkin;
    }

    @Override
    public Skin getRealSkin() {
        return realSkin;
    }

    @Override
    public String getFakeName() {
        return fakeName;
    }

    @Override
    public String getRealName() {
        return realName;
    }

    @Override
    public UUID getRealUUID() {
        return realUUID;
    }

    @Override
    public SkinLayers getFakeSkinLayers() {
        return fakeSkinLayers;
    }

    @Override
    public SkinLayers getRealSkinLayers() {
        return realSkinLayers;
    }

    public void setFakeSkinLayers(SkinLayers fakeSkinLayers) {
        this.fakeSkinLayers = fakeSkinLayers;
    }

    public void setRealSkinLayers(SkinLayers realSkinLayers) {
        this.realSkinLayers = realSkinLayers;
    }

    public void setFakeUUID(UUID fakeUUID) {
        this.fakeUUID = fakeUUID;
    }

    public void setDisguisedAsEntity(Object disguisedAsEntity) {
        this.disguisedAsEntity = disguisedAsEntity;
    }

    public void setFakeName(String fakeName) {
        this.fakeName = fakeName;
    }

    public void setFakeSkin(Skin fakeSkin) {
        this.fakeSkin = fakeSkin;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    public void setRealSkin(Skin realSkin) {
        this.realSkin = realSkin;
    }

    public void setRealUUID(UUID realUUID) {
        this.realUUID = realUUID;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof IDisguiseData)) return false;
        IDisguiseData that = (IDisguiseData) o;

        if (getRealUUID() != null ? !getRealUUID().equals(that.getRealUUID()) : that.getRealUUID() != null)
            return false;
        if (getFakeUUID() != null ? !getFakeUUID().equals(that.getFakeUUID()) : that.getFakeUUID() != null)
            return false;
        if (getRealName() != null ? !getRealName().equals(that.getRealName()) : that.getRealName() != null)
            return false;
        if (getFakeName() != null ? !getFakeName().equals(that.getFakeName()) : that.getFakeName() != null)
            return false;
        if (getRealSkin() != null ? !getRealSkin().equals(that.getRealSkin()) : that.getRealSkin() != null)
            return false;
        if (getFakeSkin() != null ? !getFakeSkin().equals(that.getFakeSkin()) : that.getFakeSkin() != null)
            return false;
        if (getRealSkinLayers() != null ? !getRealSkinLayers().equals(that.getRealSkinLayers()) : that.getRealSkinLayers() != null)
            return false;
        if (getFakeSkinLayers() != null ? !getFakeSkinLayers().equals(that.getFakeSkinLayers()) : that.getFakeSkinLayers() != null)
            return false;
        return getDisguisedAsEntity() != null ? getDisguisedAsEntity().equals(that.getDisguisedAsEntity()) : that.getDisguisedAsEntity() == null;
    }

    public boolean skinAndNameAndUUIDEquals(Object o) {
        if (this == o) return true;
        if (!(o instanceof IDisguiseData)) return false;
        IDisguiseData that = (IDisguiseData) o;

        if (getRealUUID() != null ? !getRealUUID().equals(that.getRealUUID()) : that.getRealUUID() != null)
            return false;
        if (getFakeUUID() != null ? !getFakeUUID().equals(that.getFakeUUID()) : that.getFakeUUID() != null)
            return false;
        if (getRealName() != null ? !getRealName().equals(that.getRealName()) : that.getRealName() != null)
            return false;
        if (getFakeName() != null ? !getFakeName().equals(that.getFakeName()) : that.getFakeName() != null)
            return false;
        if (getRealSkin() != null ? !getRealSkin().equals(that.getRealSkin()) : that.getRealSkin() != null)
            return false;
        return getFakeSkin() != null ? getFakeSkin().equals(that.getFakeSkin()) : that.getFakeSkin() == null;
    }

    public boolean skinLayersEquals(Object o) {
        if (this == o) return true;
        if (!(o instanceof IDisguiseData)) return false;
        IDisguiseData that = (IDisguiseData) o;
        return getFakeSkinLayers() != null ? getFakeSkinLayers().equals(that.getFakeSkinLayers()) : that.getFakeSkinLayers() == null;
    }

    @Override
    public int hashCode() {
        int result = getRealUUID() != null ? getRealUUID().hashCode() : 0;
        result = 31 * result + (getFakeUUID() != null ? getFakeUUID().hashCode() : 0);
        result = 31 * result + (getRealName() != null ? getRealName().hashCode() : 0);
        result = 31 * result + (getFakeName() != null ? getFakeName().hashCode() : 0);
        result = 31 * result + (getRealSkin() != null ? getRealSkin().hashCode() : 0);
        result = 31 * result + (getFakeSkin() != null ? getFakeSkin().hashCode() : 0);
        result = 31 * result + (getRealSkinLayers() != null ? getRealSkinLayers().hashCode() : 0);
        result = 31 * result + (getFakeSkinLayers() != null ? getFakeSkinLayers().hashCode() : 0);
        result = 31 * result + (getDisguisedAsEntity() != null ? getDisguisedAsEntity().hashCode() : 0);
        return result;
    }

    public DisguiseData copy() {
        return new DisguiseData(realUUID, fakeUUID, realName, fakeName, realSkin, fakeSkin, realSkinLayers, fakeSkinLayers, prefixSuffix, disguisedAsEntity);
    }
}
