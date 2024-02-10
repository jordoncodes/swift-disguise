package me.onlyjordon.nicknamingapi;

import me.onlyjordon.nicknamingapi.utils.Skin;
import me.onlyjordon.nicknamingapi.utils.SkinLayers;
import net.kyori.adventure.text.TextComponent;

import java.util.UUID;

public class NickData {
    private Skin originalSkin;
    private Skin currentSkin;
    private String nickname;
    private TextComponent prefix;
    private TextComponent suffix;
    private SkinLayers skinLayers;
    private UUID fakeUUID;

    public NickData(Skin originalSkin, String nickname, TextComponent prefix, TextComponent suffix, SkinLayers skinLayers, UUID fakeUUID) {
        this.originalSkin = originalSkin;
        this.nickname = nickname;
        this.prefix = prefix;
        this.suffix = suffix;
        this.skinLayers = skinLayers;
        this.fakeUUID = fakeUUID;
        this.currentSkin = null;
    }

    public Skin getCurrentSkin() {
        return currentSkin;
    }

    public Skin getOriginalSkin() {
        return originalSkin;
    }

    public SkinLayers getSkinLayers() {
        return skinLayers;
    }

    public String getNickname() {
        return nickname;
    }

    public TextComponent getPrefix() {
        return prefix;
    }

    public TextComponent getSuffix() {
        return suffix;
    }

    public UUID getFakeUUID() {
        return fakeUUID;
    }

    public void setCurrentSkin(Skin currentSkin) {
        this.currentSkin = currentSkin;
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
