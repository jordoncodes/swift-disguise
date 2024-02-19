package me.onlyjordon.nicknamingapi.utils;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.geysermc.floodgate.api.FloodgateApi;

import java.util.UUID;

public class Utils {


    private static boolean isFloodgateInstalled = false;

    public static void checkFloodgate() {
        Plugin floodgatePlugin = Bukkit.getPluginManager().getPlugin("floodgate");
        if (Bukkit.getPluginManager().getPlugin("floodgate") == null) return;
        if (!floodgatePlugin.isEnabled()) return;
        isFloodgateInstalled = true;
    }


    public static boolean isBedrockPlayer(Player player) {
        return isBedrockPlayer(player.getUniqueId());
    }

    public static boolean isBedrockPlayer(UUID id) {
        return isFloodgateInstalled && FloodgateApi.getInstance().isFloodgateId(id);
    }
}
