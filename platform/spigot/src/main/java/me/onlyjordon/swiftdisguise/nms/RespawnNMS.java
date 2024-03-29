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
    private static final Class<Enum> respawnReason;
    private static final Enum<?> pluginRespawnReason;
    public static final Method respawnMethod1_16;
    public static final Method respawnMethod1_15;
    public static final Class<?> dimensionClass;
    private static final Method getDimensionMethod;
    private static final Method getDimensionTypeMethod;
    private static final boolean isSupported;
    private static final Method legacyRespawnMethod;

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
            Method tempLegacyRespawnMethod = null;

            Method tempGetDimensionMethod;
            try {
                tempGetDimensionMethod = CrossVersionPlayerHelper.getWorldHandleMethod().getReturnType().getDeclaredMethod("o");
            } catch (NoSuchMethodException e) {
                tempGetDimensionMethod = null;
            }
            getDimensionMethod = tempGetDimensionMethod;

            dimensionClass = getDimensionMethod == null ? null : getDimensionMethod.getReturnType();

            getDimensionTypeMethod = dimensionClass == null ? null : dimensionClass.getDeclaredMethod("n");
            dimensionTypeClass = getDimensionTypeMethod == null ? null : getDimensionTypeMethod.getReturnType();

            Class<Enum> tempRespawnReason;
            try {
                tempRespawnReason = (Class<Enum>) Class.forName("org.bukkit.event.player.PlayerRespawnEvent$RespawnReason");
            } catch (Exception e) { tempRespawnReason = null; }
            respawnReason = tempRespawnReason;
            if (respawnReason != null) pluginRespawnReason = Enum.valueOf(respawnReason, "PLUGIN");
            else pluginRespawnReason = null;

            // works 1.16+
            try {
                tempRespawnMethod1_16 = playerListClass.getDeclaredMethod("respawn", CrossVersionPlayerHelper.getServerPlayerClass(), boolean.class);
            } catch (Exception ex) {
                try {
                    tempRespawnMethod1_16 = playerListClass.getDeclaredMethod("respawn", CrossVersionPlayerHelper.getServerPlayerClass(), boolean.class, respawnReason);
                } catch (Exception ignored) {}
            }
            try {
                tempRespawnMethod1_15 = playerListClass.getDeclaredMethod("moveToWorld", CrossVersionPlayerHelper.getServerPlayerClass(), dimensionTypeClass, boolean.class);
            } catch (Exception ignored) {}
            try {
                tempLegacyRespawnMethod = playerListClass.getDeclaredMethod("moveToWorld", CrossVersionPlayerHelper.getServerPlayerClass(), int.class, boolean.class);
            } catch (Exception ignored) {  }
            respawnMethod1_16 = tempRespawnMethod1_16;
            respawnMethod1_15 = tempRespawnMethod1_15;
            legacyRespawnMethod = tempLegacyRespawnMethod;
            isSupported = true;
        } catch (ClassNotFoundException | NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    public static void nmsRespawn(Player player) throws Exception {
        if (respawnMethod1_16 != null) {
            if (respawnReason == null)
                respawnMethod1_16.invoke(getHandle(Bukkit.getServer()), getHandle(player), false);
            else respawnMethod1_16.invoke(getHandle(Bukkit.getServer()), getHandle(player), false, pluginRespawnReason);
        } else if (respawnMethod1_15 != null) {
            respawnMethod1_15.invoke(getHandle(Bukkit.getServer()), getHandle(player), getDimensionType(player.getWorld()), false);
        } else if (legacyRespawnMethod != null) {
            legacyRespawnMethod.invoke(getHandle(Bukkit.getServer()), getHandle(player), player.getWorld().getEnvironment().getId(), false);
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

    public static boolean isSupported() {
        return isSupported;
    }
}
