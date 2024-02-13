package me.onlyjordon.nicknamingapi;

import me.onlyjordon.nicknamingapi.events.*;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.Objects;

public class DevListener implements Listener {

    @EventHandler
    public void onSkinLayerChange(PlayerSkinLayerChangeEvent event) {
        if (!(Objects.equals(event.getPreviousLayers(), event.getNewLayers()))) {
            event.getPlayer().sendMessage("Skin layer change event:");
            event.getPlayer().sendMessage("Previous layers: " + event.getPreviousLayers());
            event.getPlayer().sendMessage("New layers: " + event.getNewLayers());
        }
    }

    @EventHandler
    public void onSkinChange(PlayerSkinChangeEvent event) {
        event.getPlayer().sendMessage("Skin change event:");
        event.getPlayer().sendMessage("Previous skin: " + event.getPreviousSkin());
        event.getPlayer().sendMessage("New skin: " + event.getNewSkin());
    }

    @EventHandler
    public void onNickChange(PlayerNickChangeEvent event) {
        event.getPlayer().sendMessage("Nick change event:");
        event.getPlayer().sendMessage("Previous nick: " + event.getPreviousNick());
        event.getPlayer().sendMessage("New nick: " + event.getNewNick());
        event.getPlayer().sendMessage("isCancelled: " + event.isCancelled());
        event.getPlayer().sendMessage("isAsync: " + event.isAsynchronous());
    }

    @EventHandler
    public void onPrefixSuffixChangeEvent(PlayerPrefixSuffixChangeEvent event) {
        event.getPlayer().sendMessage("Prefix suffix change event:");
        LegacyComponentSerializer serializer = LegacyComponentSerializer.legacySection();
        String previousPrefix = event.getPreviousPrefix() == null ? "null" : serializer.serialize(event.getPreviousPrefix());
        String previousSuffix = event.getPreviousSuffix() == null ? "null" : serializer.serialize(event.getPreviousSuffix());
        String newPrefix = event.getNewPrefix() == null ? "null" : serializer.serialize(event.getNewPrefix());
        String newSuffix = event.getNewSuffix() == null ? "null" : serializer.serialize(event.getNewSuffix());
        event.getPlayer().sendMessage("Previous prefix: " + previousPrefix);
        event.getPlayer().sendMessage("Previous suffix: " + previousSuffix);
        event.getPlayer().sendMessage("New prefix: " + newPrefix);
        event.getPlayer().sendMessage("New suffix: " + newSuffix);
    }

    @EventHandler
    public void onRefresh(PlayerRefreshEvent event) {
        event.getPlayer().sendMessage("Refresh event");
    }

}
