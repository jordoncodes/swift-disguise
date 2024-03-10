package me.onlyjordon.swiftdisguise;

import me.onlyjordon.swiftdisguise.api.ISwiftDisguiseAPI;
import me.onlyjordon.swiftdisguise.api.SwiftDisguise;
import me.onlyjordon.swiftdisguise.api.SwiftDisguiseLoader;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
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
    public void onPlayerJoin(PlayerJoinEvent event) {
        ISwiftDisguiseAPI disguise = SwiftDisguise.getAPI(SpigotPlatform.get());
        disguise.refreshPlayerSync(event.getPlayer());
        Bukkit.getOnlinePlayers().forEach(p -> ((SwiftDisguiseSpigot) disguise).sendPrefixSuffix(p, event.getPlayer()));
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        SwiftDisguiseSpigot disguise = (SwiftDisguiseSpigot) SwiftDisguise.getAPI(SpigotPlatform.get());
        disguise.unregisterPlayer(event.getPlayer());
    }
}