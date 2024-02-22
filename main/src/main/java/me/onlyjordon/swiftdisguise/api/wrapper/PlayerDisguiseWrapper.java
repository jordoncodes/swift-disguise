package me.onlyjordon.swiftdisguise.api.wrapper;

import me.onlyjordon.swiftdisguise.api.Disguiser;
import me.onlyjordon.swiftdisguise.api.SwiftDisguiseAPI;
import me.onlyjordon.swiftdisguise.api.disguise.IDisguiseData;
import me.onlyjordon.swiftdisguise.api.utils.Skin;
import me.onlyjordon.swiftdisguise.api.utils.SkinLayers;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.EnumSet;

public class PlayerDisguiseWrapper {
    private final Player player;

    private PlayerDisguiseWrapper(Player player) {
        this.player = player;
    }

    public Player getPlayer() {
        return player;
    }

    public String getNick() {
        return getDisguiser().getNick(player);
    }

    public String getRealName() {
        return player.getName();
    }

    @Nullable
    public Skin getRealSkin() {
        return getDisguiser().getOriginalSkin(player);
    }

    public void setNick(String nick) {
        getDisguiser().setNick(player, nick);
    }

    public void resetNick() {
        getDisguiser().resetNick(player);
    }

    public void setSkin(Skin skin) {
        getDisguiser().setSkin(player, skin);
    }

    public void setSkinAsync(String skin) {
        getDisguiser().setSkin(player, skin);
    }

    public void setSkinSync(String skin) {
        getDisguiser().setSkin(player, Skin.getSkin(skin));
    }

    public void resetSkin() {
        getDisguiser().resetSkin(player);
    }

    public void disguiseAsSync(String disguise) {
        setNick(disguise);
        setSkinSync(disguise);
    }

    public void disguiseAsAsync(String disguise) {
        setNick(disguise);
        setSkinAsync(disguise);
    }

    public void refreshPlayer() {
        getDisguiser().refreshPlayer(player);
    }

    public void getSkinLayers() {
        getDisguiser().getSkinLayers(player);
    }

    public void setSkinLayers(boolean cape, boolean jacket, boolean leftSleeve, boolean rightSleeve, boolean leftPants, boolean rightPants, boolean hat) {
        getDisguiser().setSkinLayerVisible(player, SkinLayers.SkinLayer.CAPE, cape);
        getDisguiser().setSkinLayerVisible(player, SkinLayers.SkinLayer.JACKET, jacket);
        getDisguiser().setSkinLayerVisible(player, SkinLayers.SkinLayer.LEFT_SLEEVE, leftSleeve);
        getDisguiser().setSkinLayerVisible(player, SkinLayers.SkinLayer.RIGHT_SLEEVE, rightSleeve);
        getDisguiser().setSkinLayerVisible(player, SkinLayers.SkinLayer.LEFT_PANTS, leftPants);
        getDisguiser().setSkinLayerVisible(player, SkinLayers.SkinLayer.RIGHT_PANTS, rightPants);
        getDisguiser().setSkinLayerVisible(player, SkinLayers.SkinLayer.HAT, hat);
    }

    public void setSkinLayerVisible(SkinLayers.SkinLayer layer, boolean visible) {
        getDisguiser().setSkinLayerVisible(player, layer, visible);
    }

    public void setSkinLayers(SkinLayers layers) {
        getDisguiser().setSkinLayers(player, layers);
    }

    public boolean isSkinLayerVisible(SkinLayers.SkinLayer layer) {
        return getDisguiser().isSkinLayerVisible(player, layer);
    }

    public void hide(SkinLayers.SkinLayer layer) {
        getDisguiser().hide(player, layer);
    }

    public void show(SkinLayers.SkinLayer layer) {
        getDisguiser().show(player, layer);
    }

    public EnumSet<SkinLayers.SkinLayer> getVisibleSkinLayers() {
        return getDisguiser().getVisibleSkinLayers(player);
    }

    public TextComponent getPrefix() {
        return getDisguiser().getPrefix(player);
    }

    public TextComponent getSuffix() {
        return getDisguiser().getSuffix(player);
    }

    public void setPrefix(TextComponent prefix) {
        IDisguiseData data =getDisguiser().getData(player);
        if (data == null) return;
        getDisguiser().setPrefixSuffix(player, prefix, getSuffix(), data.getPrefixSuffixColor(), data.getTabPriority());
    }

    public void setSuffix(TextComponent suffix) {
        IDisguiseData data =getDisguiser().getData(player);
        if (data == null) return;
        getDisguiser().setPrefixSuffix(player, getPrefix(), suffix, data.getPrefixSuffixColor(), data.getTabPriority());
    }

    public void setPrefixSuffix(TextComponent prefix, TextComponent suffix) {
        IDisguiseData data =getDisguiser().getData(player);
        if (data == null) return;
        getDisguiser().setPrefixSuffix(player, prefix, suffix, data.getPrefixSuffixColor(), data.getTabPriority());
    }

    public void setPrefixSuffix(TextComponent prefix, TextComponent suffix, int priority) {
        IDisguiseData data =getDisguiser().getData(player);
        if (data == null) return;
        getDisguiser().setPrefixSuffix(player, prefix, suffix, data.getPrefixSuffixColor(), priority);
    }

    public void setPrefixSuffix(TextComponent prefix, TextComponent suffix, ChatColor color) {
        IDisguiseData data =getDisguiser().getData(player);
        if (data == null) return;
        getDisguiser().setPrefixSuffix(player, prefix, suffix, color, data.getTabPriority());
    }

    public void setPrefixSuffix(TextComponent prefix, TextComponent suffix, ChatColor color, int priority) {
        IDisguiseData data =getDisguiser().getData(player);
        if (data == null) return;
        getDisguiser().setPrefixSuffix(player, prefix, suffix, color, priority);
    }

    public Disguiser getDisguiser() {
        return SwiftDisguiseAPI.getDisguiser();
    }

    public static PlayerDisguiseWrapper of(Player player) {
        return new PlayerDisguiseWrapper(player);
    }
}
