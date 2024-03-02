package me.onlyjordon.swiftdisguise.spigot;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.event.PacketListenerPriority;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import me.onlyjordon.swiftdisguise.api.*;
import me.onlyjordon.swiftdisguise.spigot.nms.CrossVersionPlayerHelper;
import me.onlyjordon.swiftdisguise.spigot.packetevents.SpigotPacketListener;
import me.onlyjordon.swiftdisguise.utils.SkinLayers;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.ApiStatus;

import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class SwiftDisguiseSpigot extends SwiftDisguiseAPI {
    protected HashMap<Player, IDisguiseData> oldData = new HashMap<>();
    protected Cache<UUID, UUID> tempUniqueIdMap = CacheBuilder.newBuilder().expireAfterWrite(5, TimeUnit.SECONDS).build();

    private final PlayerRefresher refresher;

    @ApiStatus.Internal
    public SwiftDisguiseSpigot() {
        PacketEvents.getAPI().init();
        PacketEvents.getAPI().getEventManager().registerListener(new SpigotPacketListener(this, SwiftDisguise.getConfig()), PacketListenerPriority.LOW);
        refresher = new PlayerRefresher(JavaPlugin.getProvidingPlugin(getClass()), this);
    }

    public PlayerRefresher getRefresher() {
        return refresher;
    }

    @Override
    public UUID getFakeUUID(UUID uuid) {
        Player player = Bukkit.getPlayer(uuid);
        if (player == null) {
            // so there is a temporary time after the player's left so player info remove doesn't leak the real UUID
            return tempUniqueIdMap.getIfPresent(uuid);
        }
        return getDisguiseData(player).getFakeUUID();
    }

    @ApiStatus.Internal
    public void disable() {
        PacketEvents.getAPI().terminate();
    }

    @Override
    public void refreshPlayer(Object platformPlayer) {
        checkPlayer(platformPlayer);
        Player player = (Player) platformPlayer;
        DisguiseData old = (DisguiseData) oldData.get(player);
        if (old == null || !old.skinAndNameAndUUIDEquals(getDisguiseData(player))) {
            refresher.refreshPlayer(player, false);
        }
        if (old == null || !old.skinLayersEquals(getDisguiseData(player))) {
            refresher.refreshSkinLayers(player);
        }
        oldData.put(player, ((DisguiseData)getDisguiseData(player)).copy());
    }

    @Override
    public void refreshPlayer(Object platformPlayer, boolean force) {
        if (force) forceRefreshPlayer(platformPlayer);
        else refreshPlayer(platformPlayer);
    }

    @Override
    public void forceRefreshPlayer(Object platformPlayer) {
        checkPlayer(platformPlayer);
        refresher.refreshPlayer((Player) platformPlayer, true);
    }

    @Override
    public IDisguiseData getDisguiseData(Object platformPlayer) {
        if (!validatePlatformPlayer(platformPlayer)) {
            throw new IllegalArgumentException("Invalid Player!");
        }
        if (getDisguiseDataMap().containsKey(platformPlayer))
            return super.getDisguiseData(platformPlayer);
        Player player = (Player) platformPlayer;
        setDisguiseData(platformPlayer, new DisguiseData(player.getUniqueId(), SwiftDisguise.getConfig().hidingMode() == SwiftDisguiseConfig.UUIDHidingMode.NONE ? player.getUniqueId() : UUID.randomUUID(), player.getName(), player.getName(), CrossVersionPlayerHelper.skinFromBukkitPlayer(player), CrossVersionPlayerHelper.skinFromBukkitPlayer(player), SkinLayers.getFromRaw((byte)0b1111111), SkinLayers.getFromRaw((byte)0b1111111), new TabPrefixSuffix(Component.empty(), Component.empty(), ITabPrefixSuffix.NametagColor.WHITE, 0), null));
        return getDisguiseData(platformPlayer);
    }

    @Override
    protected boolean validatePlatformPlayer(Object platformPlayer) {
        return platformPlayer instanceof Player;
    }

    @ApiStatus.Internal
    public void unregisterPlayer(Object platformPlayer) {
        checkPlayer(platformPlayer);
        tempUniqueIdMap.put(((Player) platformPlayer).getUniqueId(), UUID.randomUUID());
        getDisguiseDataMap().remove(platformPlayer);
        refresher.unregisterPlayer((Player) platformPlayer);
    }
}
