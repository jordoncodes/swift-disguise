package me.onlyjordon.swiftdisguise;

import me.onlyjordon.swiftdisguise.api.ISwiftDisguiseAPI;
import me.onlyjordon.swiftdisguise.api.SwiftDisguise;
import me.onlyjordon.swiftdisguise.api.SwiftDisguiseLoader;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
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

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        SwiftDisguiseSpigot disguise = (SwiftDisguiseSpigot) SwiftDisguise.getAPI(SpigotPlatform.get());
        disguise.refreshPlayerSync(e.getPlayer());
        Bukkit.getOnlinePlayers().forEach(p -> disguise.sendPrefixSuffix(p, e.getPlayer()));
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        SwiftDisguiseSpigot disguise = (SwiftDisguiseSpigot) SwiftDisguise.getAPI(SpigotPlatform.get());
        disguise.unregisterPlayer(e.getPlayer());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onRespawn(PlayerRespawnEvent e) {
        SwiftDisguiseSpigot disguise = (SwiftDisguiseSpigot) SwiftDisguise.getAPI(SpigotPlatform.get());
        Bukkit.getScheduler().runTaskLater(JavaPlugin.getProvidingPlugin(getClass()), () -> disguise.refreshPlayerSync(e.getPlayer()), 1);
    }
}