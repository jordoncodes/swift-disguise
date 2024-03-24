package me.onlyjordon.swiftdisguise.nms;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import static me.onlyjordon.swiftdisguise.nms.CrossVersionPlayerHelper.getHandle;

public class RespawnNMS {
    public static final Class<?> playerListClass;
    public static final Class<?> dimensionTypeClass;
    public static final Method respawnMethod1_16;
    public static final Method respawnMethod1_15;
    public static final Class<?> dimensionClass;
    private static final Method getDimensionMethod;
    private static final Method getDimensionTypeMethod;

    static {
        try {
            Class<?> plc;
            try {
                plc = Class.forName("net.minecraft.server.players.PlayerList");
            } catch (ClassNotFoundException e) {
                plc = Class.forName("net.minecraft.server."+NMSUtils.getMinecraftPackage()+".PlayerList");
            }
            playerListClass = plc;

            Method tempRespawnMethod1_16 = null;
            Method tempRespawnMethod1_15 = null;

            getDimensionMethod = CrossVersionPlayerHelper.getWorldHandleMethod().getReturnType().getDeclaredMethod("o");
            dimensionClass = getDimensionMethod.getReturnType();

            getDimensionTypeMethod = dimensionClass.getDeclaredMethod("n");
            dimensionTypeClass = getDimensionTypeMethod.getReturnType();

            // works 1.16+
            try {
                tempRespawnMethod1_16 = playerListClass.getDeclaredMethod("a", CrossVersionPlayerHelper.getServerPlayerClass(), boolean.class);
            } catch (Exception e) {}
            try {
                tempRespawnMethod1_15 = playerListClass.getDeclaredMethod("a", CrossVersionPlayerHelper.getServerPlayerClass(), dimensionTypeClass, boolean.class);
            } catch (Exception e) {}
            respawnMethod1_16 = tempRespawnMethod1_16;
            respawnMethod1_15 = tempRespawnMethod1_15;

        } catch (ClassNotFoundException | NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    public static void nmsRespawn(Player player) throws Exception {
        if (respawnMethod1_16 != null) {
            respawnMethod1_16.invoke(getHandle(Bukkit.getServer()), getHandle(player), false);
        } else if (respawnMethod1_15 != null) {
            respawnMethod1_15.invoke(getHandle(Bukkit.getServer()), getHandle(player), getDimensionType(player.getWorld()), false);
        } else {
            throw new RuntimeException("Unsupported server version");
        }
    }

    private static Object getDimensionType(World world) {
        try {
            return getDimensionTypeMethod.invoke(getDimension(world));
        } catch (InvocationTargetException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private static Object getDimension(World world) {
        try {
            Object level = CrossVersionPlayerHelper.getHandle(world);
            return getDimensionMethod.invoke(level);
        } catch (InvocationTargetException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
