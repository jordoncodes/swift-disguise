package me.onlyjordon.nicknamingapi.events;

import net.kyori.adventure.text.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.Nullable;

public class PlayerPrefixSuffixChangeEvent extends Event implements Cancellable {

    private static final HandlerList HANDLERS_LIST = new HandlerList();

    private boolean cancelled = false;
    private final TextComponent previousPrefix;
    private TextComponent currentPrefix;

    private final TextComponent previousSuffix;
    private TextComponent currentSuffix;

    private ChatColor color;
    private int priority;
    private final Player player;

    public PlayerPrefixSuffixChangeEvent(Player who, TextComponent prevPrefix, TextComponent prevSuffix, TextComponent currentPrefix, TextComponent currentSuffix, ChatColor color, int priority) {
        super(!Bukkit.isPrimaryThread());
        this.player = who;
        this.previousPrefix = prevPrefix;
        this.previousSuffix = prevSuffix;
        this.currentPrefix = currentPrefix;
        this.currentSuffix = currentSuffix;
        this.color = color;
        this.priority = priority;
    }

    public Player getPlayer() {
        return player;
    }

    @Nullable
    public TextComponent getNewPrefix() {
        return currentPrefix;
    }

    public void setNewPrefix(TextComponent currentPrefix) {
        this.currentPrefix = currentPrefix;
    }

    @Nullable
    public TextComponent getNewSuffix() {
        return currentSuffix;
    }

    public ChatColor getColor() {
        return color;
    }

    public int getPriority() {
        return priority;
    }

    public void setColor(ChatColor color) {
        this.color = color;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public void setNewSuffix(TextComponent currentSuffix) {
        this.currentSuffix = currentSuffix;
    }

    @Nullable
    public TextComponent getPreviousPrefix() {
        return previousPrefix;
    }

    @Nullable
    public TextComponent getPreviousSuffix() {
        return previousSuffix;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLERS_LIST;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS_LIST;
    }
    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }
}
