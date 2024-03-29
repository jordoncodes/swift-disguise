package me.onlyjordon.swiftdisguise;

import me.onlyjordon.swiftdisguise.api.DisguiseData;
import me.onlyjordon.swiftdisguise.api.SwiftDisguise;
import me.onlyjordon.swiftdisguise.api.SwiftDisguiseLoader;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class Plugin extends JavaPlugin implements Listener {
    @Override
    public void onEnable() {
        super.onEnable();
        Bukkit.getPluginManager().registerEvents(this, this);
        SwiftDisguiseLoader.load();
    }

    @Override
    public void onDisable() {
        ((SwiftDisguiseSpigot)SwiftDisguise.getAPI(SpigotPlatform.get())).disable();
        super.onDisable();
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerJoin(PlayerJoinEvent e) {
        SwiftDisguiseSpigot disguise = (SwiftDisguiseSpigot) SwiftDisguise.getAPI(SpigotPlatform.get());
        disguise.refresher.removeUUID((DisguiseData) disguise.getDisguiseData(e.getPlayer()), e.getPlayer());
        disguise.refreshPlayerSync(e.getPlayer());
        disguise.oldData.put(e.getPlayer(), ((DisguiseData)disguise.getDisguiseData(e.getPlayer())).copy());
        Bukkit.getOnlinePlayers().forEach(p -> disguise.sendPrefixSuffix(p, e.getPlayer()));
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onQuit(PlayerQuitEvent e) {
        SwiftDisguiseSpigot disguise = (SwiftDisguiseSpigot) SwiftDisguise.getAPI(SpigotPlatform.get());
        disguise.unregisterPlayer(e.getPlayer());
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onRespawn(PlayerRespawnEvent e) {
        SwiftDisguiseSpigot disguise = (SwiftDisguiseSpigot) SwiftDisguise.getAPI(SpigotPlatform.get());
        if (disguise.refresher.getRespawnMethod() == PlayerRefresher.RespawnMethod.PACKET_EVENTS && e.getPlayer().isDead()) {
            disguise.refreshPlayerSync(e.getPlayer());
            Bukkit.getScheduler().runTaskLater(JavaPlugin.getProvidingPlugin(getClass()), () -> disguise.refreshPlayerSync(e.getPlayer()), 1);
        }
    }
}