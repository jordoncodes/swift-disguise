package me.onlyjordon.nicknamingapi.events;

import me.onlyjordon.nicknamingapi.utils.Skin;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.Nullable;

public class PlayerSkinChangeEvent extends Event implements Cancellable {
    
    private static final HandlerList HANDLERS_LIST = new HandlerList();
    
    private boolean cancelled = false;
    private final Skin previous;
    private Skin current;
    private final Player player;
    
    public PlayerSkinChangeEvent(Player who, Skin prev, Skin current) {
        super(!Bukkit.isPrimaryThread());
        this.player = who;
        this.previous = prev;
        this.current = current;
    }

    public Player getPlayer() {
        return player;
    }

    @Nullable
    public Skin getPreviousSkin() {
        return previous;
    }

    @Nullable
    public Skin getNewSkin() {
        return current;
    }

    public void setCurrent(Skin newSkin) {
        current = newSkin;
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
