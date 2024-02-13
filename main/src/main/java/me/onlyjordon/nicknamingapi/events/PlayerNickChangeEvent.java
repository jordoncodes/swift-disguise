package me.onlyjordon.nicknamingapi.events;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.Nullable;

public class PlayerNickChangeEvent extends Event implements Cancellable {

    private static final HandlerList HANDLERS_LIST = new HandlerList();

    private boolean cancelled = false;
    private final String previous;
    private String current;
    private final Player player;

    public PlayerNickChangeEvent(Player who, String prev, String current) {
        super(!Bukkit.isPrimaryThread());
        this.player = who;
        this.previous = prev;
        this.current = current;
    }

    public Player getPlayer() {
        return player;
    }

    @Nullable
    public String getPreviousNick() {
        return previous;
    }

    public String getNewNick() {
        return current;
    }

    public void setNewNick(String newNick) {
        current = newNick;
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
