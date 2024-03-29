package me.onlyjordon.swiftdisguise;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.event.PacketListenerPriority;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import me.onlyjordon.swiftdisguise.api.*;
import me.onlyjordon.swiftdisguise.nms.CrossVersionPlayerHelper;
import me.onlyjordon.swiftdisguise.packetevents.SpigotPacketListener;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.ApiStatus;

import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeUnit;

public class SwiftDisguiseSpigot extends SwiftDisguiseAPI {

    public HashMap<Player, IDisguiseData> oldData = new HashMap<>();
    final PlayerRefresher refresher;
    protected Cache<UUID, UUID> tempUniqueIdMap = CacheBuilder.newBuilder().expireAfterWrite(5, TimeUnit.SECONDS).build();
    private final ConcurrentLinkedQueue<Refresh> refreshQueue = new ConcurrentLinkedQueue<>();
    private final BukkitRunnable refreshRunnable;

    public SwiftDisguiseSpigot() {
        PacketEvents.getAPI().init();
        PacketEvents.getAPI().getEventManager().registerListener(new SpigotPacketListener(this), PacketListenerPriority.LOW);
        refresher = new PlayerRefresher(this, JavaPlugin.getProvidingPlugin(getClass()));
        refreshRunnable = new BukkitRunnable() {
            @Override
            public void run() {
                refreshQueue.forEach(refresh -> {
                    if (!refresh.getPlayer().isOnline()) return;
                    if (refresh.shouldRefreshNameSkin()) {
                        refresher.refreshPlayer(refresh.getPlayer());
                    }
                    if (refresh.shouldRefreshSkinLayers()) {
                        refresher.refreshSkinLayers(refresh.getPlayer());
                    }
                    if (refresh.shouldRefreshPrefixSuffix()) {
                        refresher.refreshPrefixSuffix(refresh.getPlayer(), Bukkit.getOnlinePlayers().toArray(new Player[]{}));
                    }
                });
                refreshQueue.clear();
            }
        };
        refreshRunnable.runTaskTimer(JavaPlugin.getProvidingPlugin(getClass()), 1, 1);
    }

    public void disable() {
        PacketEvents.getAPI().terminate();
        refreshRunnable.cancel();
    }

    public void sendPrefixSuffix(Player sender, Player... receiver) {
        refresher.refreshPrefixSuffix(sender, receiver);
    }

    @Override
    public void refreshPlayer(Object platformPlayer) {
        if (!validatePlatformPlayer(platformPlayer)) {
            throw new IllegalArgumentException("Invalid Player!");
        }

        Player player = (Player) platformPlayer;
        DisguiseData old = (DisguiseData) oldData.get(player);
        boolean shouldRefreshNameSkin = (old == null || !old.skinAndNameAndUUIDEquals(getDisguiseData(player)));
        boolean shouldRefreshSkinLayers = (old == null || !old.skinLayersEquals(getDisguiseData(player)));
        boolean shouldRefreshPrefixSuffix = (old == null || !old.prefixSuffixEquals(getDisguiseData(player)));

        if (shouldRefreshNameSkin) {
            refresher.removeUUID(old, player);
        }

        Refresh refresh = new Refresh(player, shouldRefreshNameSkin, shouldRefreshSkinLayers, shouldRefreshPrefixSuffix);
        Refresh other = refreshQueue.stream().filter(r -> r.getPlayer().getUniqueId().equals(player.getUniqueId())).findFirst().orElse(null);
        refreshQueue.remove(other);
        if (other != null) {
            refresh = combine(refresh, other);
        }
        refreshQueue.add(refresh);
        oldData.put(player, ((DisguiseData)getDisguiseData(player)).copy());
    }

    private Refresh combine(Refresh refresh, Refresh other) {
        return new Refresh(refresh.getPlayer(), refresh.shouldRefreshNameSkin() || other.shouldRefreshNameSkin(), refresh.shouldRefreshSkinLayers() || other.shouldRefreshSkinLayers(), refresh.shouldRefreshPrefixSuffix() || other.shouldRefreshPrefixSuffix());
    }

    @Override
    public void refreshPlayerSync(Object platformPlayer) {
        if (!validatePlatformPlayer(platformPlayer)) {
            throw new IllegalArgumentException("Invalid Player!");
        }
        refresher.refreshPlayerSync((Player) platformPlayer);
    }

    @Override
    public IDisguiseData getDisguiseData(Object platformPlayer) {
        if (!validatePlatformPlayer(platformPlayer)) {
            throw new IllegalArgumentException("Invalid Player!");
        }
        if (getDisguiseDataMap().containsKey(platformPlayer))
            return super.getDisguiseData(platformPlayer);
        Player player = (Player) platformPlayer;
        getDisguiseDataMap().put(platformPlayer, new DisguiseData(player.getUniqueId(), (SwiftDisguise.getConfig().hidingMode() == SwiftDisguiseConfig.UUIDHidingMode.RANDOM ? UUID.randomUUID() : player.getUniqueId()), player.getName(), player.getName(), CrossVersionPlayerHelper.skinFromBukkitPlayer(player), CrossVersionPlayerHelper.skinFromBukkitPlayer(player), null, null, new TabPrefixSuffix(), null));
        return getDisguiseData(platformPlayer);
    }

    @Override
    public void setDisguisePrefixSuffix(Object platformPlayer, TabPrefixSuffix prefixSuffix) {
        super.setDisguisePrefixSuffix(platformPlayer, prefixSuffix);
        refresher.refreshPrefixSuffix((Player) platformPlayer, Bukkit.getOnlinePlayers().toArray(new Player[]{}));
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

    @Override
    public boolean isRealPlayer(Object platformPlayer) {
        return platformPlayer != null && getDisguiseDataMap().containsKey(platformPlayer);
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

    private static class Refresh {
        private final boolean shouldRefreshNameSkin,
                shouldRefreshSkinLayers,
                shouldRefreshPrefixSuffix;

        private final Player player;
        public Refresh(Player player, boolean shouldRefreshNameSkin, boolean shouldRefreshSkinLayers, boolean shouldRefreshPrefixSuffix) {
            this.player = player;
            this.shouldRefreshPrefixSuffix = shouldRefreshPrefixSuffix;
            this.shouldRefreshSkinLayers = shouldRefreshSkinLayers;
            this.shouldRefreshNameSkin = shouldRefreshNameSkin;
        }

        public UUID getPlayerId() {
            return player.getUniqueId();
        }

        public Player getPlayer() {
            return player;
        }

        public boolean shouldRefreshNameSkin() {
            return shouldRefreshNameSkin;
        }

        public boolean shouldRefreshPrefixSuffix() {
            return shouldRefreshPrefixSuffix;
        }

        public boolean shouldRefreshSkinLayers() {
            return shouldRefreshSkinLayers;
        }

        public boolean playerEquals(Refresh other) {
            if (other == null) return false;
            return getPlayerId().equals(other.getPlayerId());
        }
    }
}
