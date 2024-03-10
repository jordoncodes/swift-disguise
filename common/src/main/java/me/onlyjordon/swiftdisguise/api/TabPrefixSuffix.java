package me.onlyjordon.swiftdisguise.api;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;

public class TabPrefixSuffix implements ITabPrefixSuffix {

    public TabPrefixSuffix(TextComponent prefix, TextComponent suffix, NametagColor color, int priority) {
        this.prefix = prefix;
        this.suffix = suffix;
        this.color = color;
        this.priority = priority;
    }

    public TabPrefixSuffix() {
        this.prefix = Component.text("");
        this.suffix = Component.text("");
        this.color = NametagColor.WHITE;
        this.priority = 0;
    }

    private TextComponent prefix, suffix;
    private NametagColor color;
    private int priority;

    @Override
    public TextComponent getPrefix() {
        return prefix;
    }

    @Override
    public TextComponent getSuffix() {
        return suffix;
    }

    @Override
    public NametagColor getColor() {
        return color;
    }

    @Override
    public int getPriority() {
        return priority;
    }

    @Override
    public void setPrefix(TextComponent prefix) {
        this.prefix = prefix;
    }

    @Override
    public void setSuffix(TextComponent suffix) {
        this.suffix = suffix;
    }

    @Override
    public void setPriority(int priority) {
        this.priority = priority;
    }

    @Override
    public void setColor(NametagColor color) {
        this.color = color;
    }
}
