package me.onlyjordon.swiftdisguise.events;

import me.onlyjordon.swiftdisguise.utils.SkinLayers;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;


public class PlayerSkinLayerChangeEvent extends Event implements Cancellable {
    private static final HandlerList handlers = new HandlerList();
    private boolean cancelled;

    private final SkinLayers oldLayers;
    private SkinLayers newLayers;
    private final Player player;

    public PlayerSkinLayerChangeEvent(Player player, SkinLayers oldLayers, SkinLayers newLayers) {
        super(!Bukkit.isPrimaryThread());
        this.player = player;
        this.oldLayers = oldLayers;
        this.newLayers = newLayers;
    }

    public Player getPlayer() {
        return player;
    }

    public SkinLayers getNewLayers() {
        return newLayers;
    }

    public void setNewLayers(SkinLayers newLayers) {
        this.newLayers = newLayers;
    }

    public SkinLayers getOldLayers() {
        return oldLayers;
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public boolean isCancelled() {
        return cancelled;
    }

    public void setCancelled(boolean cancel) {
        cancelled = cancel;
    }
}
