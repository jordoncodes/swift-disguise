package me.onlyjordon.nicknamingapi;

import me.onlyjordon.nicknamingapi.utils.Skin;
import me.onlyjordon.nicknamingapi.utils.SkinLayers;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.HashMap;

public class DisguiseBuilder {

    private final Nicknamer nicknamer;
    private String nick,
            skinName = null;
    private Skin skin = null;
    private boolean isReset = false;

    private ChatColor prefixSuffixColor;
    private int priority;

    HashMap<SkinLayers.SkinLayer, Boolean> skinLayerVisibility  = new HashMap<>();

    private TextComponent prefix,
            suffix;

    public DisguiseBuilder(Nicknamer nicknamer) {
        this.nicknamer = nicknamer;
    }

    public DisguiseBuilder setNick(String nick) {
        this.nick = nick;
        return this;
    }

    public DisguiseBuilder setSkin(String skinName) {
        this.skinName = skinName;
        this.skin = null;
        return this;
    }

    public DisguiseBuilder setSkin(Skin skin) {
        this.skin = skin;
        this.skinName = null;
        return this;
    }

    public DisguiseBuilder setPrefixSuffix(TextComponent prefix, TextComponent suffix, ChatColor color, int priority) {
        this.prefix = prefix;
        this.suffix = suffix;
        this.prefixSuffixColor = color;
        this.priority = priority;
        return this;
    }

    public DisguiseBuilder setPrefixSuffix(TextComponent prefix, TextComponent suffix, ChatColor color) {
        setPrefixSuffix(prefix, suffix, color, 0);
        return this;
    }

    public DisguiseBuilder setPrefixSuffix(TextComponent prefix, TextComponent suffix, int priority) {
        setPrefixSuffix(prefix, suffix, ChatColor.WHITE, priority);
        return this;
    }

    public DisguiseBuilder setPrefixSuffix(TextComponent prefix, TextComponent suffix) {
        setPrefixSuffix(prefix, suffix, ChatColor.WHITE, 0);
        return this;
    }

    public DisguiseBuilder setPrefixSuffix(String prefix, String suffix, ChatColor color, int priority) {
        this.prefix = Component.text(prefix);
        this.suffix = Component.text(suffix);
        this.prefixSuffixColor = color;
        this.priority = priority;
        return this;
    }

    public DisguiseBuilder setPrefixSuffix(String prefix, String suffix, ChatColor color) {
        setPrefixSuffix(prefix, suffix, color, 0);
        return this;
    }

    public DisguiseBuilder setPrefixSuffix(String prefix, String suffix, int priority) {
        setPrefixSuffix(prefix, suffix, ChatColor.WHITE, priority);
        return this;
    }

    public DisguiseBuilder setPrefixSuffix(String prefix, String suffix) {
        setPrefixSuffix(prefix, suffix, ChatColor.WHITE, 0);
        return this;
    }

    public DisguiseBuilder setSkinLayerVisible(SkinLayers.SkinLayer layer, boolean visible) {
        skinLayerVisibility.put(layer, visible);
        return this;
    }

    public DisguiseBuilder reset() {
        isReset = true;
        return this;
    }

    public void apply(Player player) {
        if (isReset) {
            nicknamer.resetDisguise(player);
            return;
        }
        if (nick != null) {
            nicknamer.setNick(player, nick);
        }
        if (skinName != null) {
            nicknamer.setSkin(player, skinName);
        }
        if (skin != null) {
            nicknamer.setSkin(player, Skin.getSkin(skinName));
        }
        if (prefix != null && suffix != null && prefixSuffixColor != null) {
            nicknamer.setPrefixSuffix(player, prefix, suffix, prefixSuffixColor, priority);
        }
        for (SkinLayers.SkinLayer layer : skinLayerVisibility.keySet()) {
            nicknamer.setSkinLayerVisible(player, layer, skinLayerVisibility.get(layer));
        }
    }
}
