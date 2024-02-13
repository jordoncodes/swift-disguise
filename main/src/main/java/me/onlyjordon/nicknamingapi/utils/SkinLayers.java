package me.onlyjordon.nicknamingapi.utils;

import org.jetbrains.annotations.NotNull;

import java.util.EnumSet;

public class SkinLayers {

    private SkinLayers(byte raw) {
        visibleLayers = EnumSet.noneOf(SkinLayer.class);
        for (SkinLayer layer : SkinLayer.values()) {
            if (layer.isEnabled(raw)) {
                visibleLayers.add(layer);
            }
        }
    }

    private EnumSet<SkinLayer> visibleLayers;

    /**
     * Gets the raw byte of the skin layers. This is used in NMS.
     * @return the raw byte
     */
    public byte getRawSkinLayers() {
        byte raw = 0;
        for (SkinLayer layer : visibleLayers) {
            raw = layer.toggleEnabled(raw, true);
        }
        return raw;
    }

    /**
     * Gets the layers that are shown
     * @return the layers that are shown
     */
    public EnumSet<SkinLayer> getVisibleLayers() {
        return visibleLayers;
    }

    /**
     * Sets whether a layer is shown or not
     * @param layer the layer to set
     * @param shown whether the layer should be shown
     */
    public void setLayerVisible(SkinLayer layer, boolean shown) {
        if (shown) {
            visibleLayers.add(layer);
        } else {
            visibleLayers.remove(layer);
        }
    }

    /**
     * Creates a new SkinLayers object from a raw byte
     * @param raw the raw byte. All the layers = 0b1111111; None = 0b0000000
     * @see SkinLayer
     */
    public static SkinLayers getFromRaw(byte raw) {
        return new SkinLayers(raw);
    }

    /**
     * Creates a new SkinLayers object from a set of shown layers
     * @param visibleLayers the layers to show
     * @return the SkinLayers object
     */
    public static SkinLayers getFromVisibleLayers(EnumSet<SkinLayer> visibleLayers) {
        SkinLayers layers = new SkinLayers((byte) 0);
        layers.visibleLayers = visibleLayers;
        return layers;
    }

    /**
     * Creates a new SkinLayers object from an array of shown layers
     * @param visibleLayers the layers to show
     * @return the SkinLayers object
     */
    public static SkinLayers getFromVisibleLayers(SkinLayer... visibleLayers) {
        SkinLayers layers = new SkinLayers((byte) 0);
        layers.visibleLayers = EnumSet.of(visibleLayers[0], visibleLayers);
        return layers;
    }

    public boolean isLayerVisible(@NotNull SkinLayer layer) {
        return visibleLayers.contains(layer);
    }

    @Override
    public String toString() {
        return "SkinLayers{" +
                "visibleLayers=" + visibleLayers +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SkinLayers)) return false;

        SkinLayers that = (SkinLayers) o;

        return visibleLayers.equals(that.visibleLayers);
    }

    @Override
    public int hashCode() {
        return visibleLayers.hashCode();
    }

    public SkinLayers copy() {
        return getFromRaw(getRawSkinLayers());
    }

    public enum SkinLayer {
        CAPE((byte) 0b00000001),
        JACKET((byte) 0b00000010),
        LEFT_SLEEVE((byte) 0b00000100),
        RIGHT_SLEEVE((byte) 0b00001000),
        LEFT_PANTS((byte) 0b00010000),
        RIGHT_PANTS((byte) 0b00100000),
        HAT((byte) 0b01000000);

        private final byte value;

        SkinLayer(byte value) {
            this.value = value;
        }

        public boolean isEnabled(byte raw) {
            return (raw & value) == value;
        }

        public byte toggleEnabled(byte raw, boolean enabled) {
            if (enabled) {
                return (byte) (raw | value);
            } else {
                return (byte) (raw & ~value);
            }
        }
    }

}
