package me.onlyjordon.nicknamingapi.events;

import me.onlyjordon.nicknamingapi.utils.SkinLayers;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.Nullable;

public class PlayerSkinLayerChangeEvent extends Event implements Cancellable {

    private static final HandlerList HANDLERS_LIST = new HandlerList();

    private boolean cancelled = false;
    private final SkinLayers previousLayers;
    private SkinLayers currentLayers;
    private final Player player;

    public PlayerSkinLayerChangeEvent(Player who, SkinLayers prev, SkinLayers current) {
        super(!Bukkit.isPrimaryThread());
        this.player = who;
        this.previousLayers = prev;
        this.currentLayers = current;
    }

    public Player getPlayer() {
        return player;
    }

    @Nullable
    public SkinLayers getPreviousLayers() {
        return previousLayers;
    }

    @Nullable
    public SkinLayers getNewLayers() {
        return currentLayers;
    }

    public void setNewLayers(SkinLayers newLayers) {
        currentLayers = newLayers;
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
