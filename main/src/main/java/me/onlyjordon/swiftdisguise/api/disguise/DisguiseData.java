package me.onlyjordon.swiftdisguise.api.disguise;

import me.onlyjordon.swiftdisguise.api.utils.Skin;
import me.onlyjordon.swiftdisguise.api.utils.SkinLayers;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.ChatColor;

import java.util.UUID;

public class DisguiseData implements IDisguiseData {
    private Skin originalSkin;
    private Skin currentSkin;
    private String nickname;

    private TextComponent prefix;
    private TextComponent suffix;
    private ChatColor prefixSuffixColor;
    private int tabPriority;

    private SkinLayers skinLayers;
    private UUID fakeUUID;

    public DisguiseData(Skin originalSkin, String nickname, TextComponent prefix, TextComponent suffix, SkinLayers skinLayers, UUID fakeUUID, ChatColor prefixSuffixColor, int tabPriority) {
        this.originalSkin = originalSkin;
        this.nickname = nickname;
        this.prefix = prefix;
        this.suffix = suffix;
        this.skinLayers = skinLayers;
        this.fakeUUID = fakeUUID;
        this.currentSkin = null;
        this.prefixSuffixColor = prefixSuffixColor;
        this.tabPriority = tabPriority;
    }

    @Override
    public ChatColor getPrefixSuffixColor() {
        return prefixSuffixColor;
    }

    @Override
    public int getTabPriority() {
        return tabPriority;
    }

    public void setPrefixSuffixColor(ChatColor prefixSuffixColor) {
        this.prefixSuffixColor = prefixSuffixColor;
    }

    public void setTabPriority(int tabPriority) {
        this.tabPriority = tabPriority;
    }

    @Override
    public Skin getCurrentSkin() {
        return currentSkin;
    }

    public void setCurrentSkin(Skin currentSkin) {
        this.currentSkin = currentSkin;
    }

    @Override
    public Skin getOriginalSkin() {
        return originalSkin;
    }

    @Override
    public SkinLayers getSkinLayers() {
        return skinLayers;
    }

    @Override
    public String getNickname() {
        return nickname;
    }

    @Override
    public TextComponent getPrefix() {
        return prefix;
    }

    @Override
    public TextComponent getSuffix() {
        return suffix;
    }

    @Override
    public UUID getFakeUUID() {
        return fakeUUID;
    }

    public void setFakeUUID(UUID fakeUUID) {
        this.fakeUUID = fakeUUID;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public void setOriginalSkin(Skin originalSkin) {
        this.originalSkin = originalSkin;
    }

    public void setPrefix(TextComponent prefix) {
        this.prefix = prefix;
    }

    public void setSkinLayers(SkinLayers skinLayers) {
        this.skinLayers = skinLayers;
    }

    public void setSuffix(TextComponent suffix) {
        this.suffix = suffix;
    }
}
