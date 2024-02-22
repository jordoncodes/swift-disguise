package me.onlyjordon.swiftdisguise.api.disguise;

import me.onlyjordon.swiftdisguise.api.utils.Skin;
import me.onlyjordon.swiftdisguise.api.utils.SkinLayers;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.ChatColor;

import java.util.UUID;

public interface IDisguiseData {
    ChatColor getPrefixSuffixColor();

    int getTabPriority();

    Skin getCurrentSkin();

    Skin getOriginalSkin();

    SkinLayers getSkinLayers();

    String getNickname();

    TextComponent getPrefix();

    TextComponent getSuffix();

    UUID getFakeUUID();
}
