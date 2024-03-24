package me.onlyjordon.swiftdisguise.api;

import net.kyori.adventure.text.TextComponent;

public interface ITabPrefixSuffix {
    TextComponent getPrefix();
    TextComponent getSuffix();
    NametagColor getColor();
    int getPriority();


    void setPrefix(TextComponent prefix);
    void setSuffix(TextComponent suffix);
    void setPriority(int priority);
    void setColor(NametagColor color);

    enum NametagColor {
        BLACK,
        DARK_BLUE,
        DARK_GREEN,
        DARK_AQUA,
        DARK_RED,
        DARK_PURPLE,
        GOLD,
        GRAY,
        DARK_GRAY,
        BLUE,
        GREEN,
        AQUA,
        RED,
        LIGHT_PURPLE,
        YELLOW,
        WHITE
    }
}
