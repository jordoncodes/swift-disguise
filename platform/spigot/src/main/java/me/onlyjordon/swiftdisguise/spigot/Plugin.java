package me.onlyjordon.swiftdisguise.spigot;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.protocol.world.Difficulty;
import com.github.retrooper.packetevents.protocol.world.WorldBlockPosition;
import com.github.retrooper.packetevents.resources.ResourceLocation;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerRespawn;
import io.github.retrooper.packetevents.util.SpigotConversionUtil;
import me.onlyjordon.swiftdisguise.api.ISwiftDisguiseAPI;
import me.onlyjordon.swiftdisguise.api.SwiftDisguise;
import me.onlyjordon.swiftdisguise.api.SwiftDisguiseLoader;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public class Plugin extends JavaPlugin implements Listener {

    @Override
    public void onLoad() {
        SwiftDisguiseLoader.load(new File(getDataFolder(), "config.yml"));
        System.out.println("loaded SwiftDisguise!");
    }

    @Override
    public void onEnable() {
        super.onEnable();
        Bukkit.getPluginManager().registerEvents(this, this);
        System.out.println("enabled SwiftDisguise! Config is " + SwiftDisguiseLoader.getConfig().toString());
    }

    @Override
    public void onDisable() {
        super.onDisable();
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        ISwiftDisguiseAPI disguise = SwiftDisguise.getAPI(SpigotPlatform.get());
//        disguise.refreshPlayer(event.getPlayer());
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        SwiftDisguiseSpigot disguise = (SwiftDisguiseSpigot) SwiftDisguise.getAPI(SpigotPlatform.get());
        disguise.unregisterPlayer(event.getPlayer());
    }
}