package me.onlyjordon.swiftdisguise.spigot.nms;

import com.github.retrooper.packetevents.manager.server.ServerVersion;
import com.github.retrooper.packetevents.protocol.player.TextureProperty;
import com.github.retrooper.packetevents.protocol.player.UserProfile;
import com.google.common.collect.Multimap;
import me.onlyjordon.swiftdisguise.utils.Skin;
import org.bukkit.Server;
import org.bukkit.entity.Player;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.UUID;

public class CrossVersionPlayerHelper {

    private static final Class<?> craftPlayer;
    private static final Class<?> playerClass;
    private static final Class<?> gameProfileClass;
    private static final Field getPing;
    private static final Field gameProfileField;
    private static final Method getPlayerHandle;
    private static final Method getServerHandle;
    private static final Constructor<?> gameProfileConstructor;
    private static final Field propertiesField;
    private static final Class<?> propertyClass;
    private static final Constructor<?> propertyConstcutor;
    private static final Class<?> craftServerClass;
    private static final Class<?> playerListClass;
    private static final Field playersByName;
    private static final Method skinTextureValueMethod;
    private static final Method skinTextureSignatureMethod;


    static {
        try {
            craftPlayer = Class.forName("org.bukkit.craftbukkit." + NMSUtils.getMinecraftPackage() + ".entity.CraftPlayer");
            Class<?> playerClass1;
            try {
                playerClass1 = Class.forName("net.minecraft.world.entity.player.EntityHuman");
            } catch (Exception e) {
                playerClass1 = Class.forName("net.minecraft.server." + NMSUtils.getMinecraftPackage() + ".EntityHuman");
            }
            playerClass = playerClass1;

            Field getPing1;
            try {
                System.out.println(playerClass);
                getPing1 = Class.forName("net.minecraft.server." + NMSUtils.getMinecraftPackage() + ".EntityPlayer").getDeclaredField("ping");
            } catch (Exception e) {
                getPing1 = null;
            }
            getPing = getPing1;
            if (getPing != null) getPing.setAccessible(true);

            getPlayerHandle = craftPlayer.getDeclaredMethod("getHandle");
            getPlayerHandle.setAccessible(true);

            Field gameProfile1;
            try {
                gameProfile1 = playerClass.getDeclaredField("cq");
            } catch (Exception e) {
                gameProfile1 = playerClass.getDeclaredField("bH");
            }
            gameProfileField = gameProfile1;
            gameProfileField.setAccessible(true);

            craftServerClass = Class.forName("org.bukkit.craftbukkit." + NMSUtils.getMinecraftPackage() + ".CraftServer");
            getServerHandle = craftServerClass.getDeclaredMethod("getHandle");
            getServerHandle.setAccessible(true);

            gameProfileClass = gameProfileField.getType();
            gameProfileConstructor = gameProfileClass.getConstructor(UUID.class, String.class);

            propertiesField = gameProfileClass.getDeclaredField("properties");
            propertiesField.setAccessible(true);
            propertyClass = Class.forName("com.mojang.authlib.properties.Property");

            propertyConstcutor = propertyClass.getConstructor(String.class, String.class, String.class);

            Class<?> playerListClass1;
            try {
                playerListClass1 = Class.forName("net.minecraft.server.players.PlayerList");
            } catch (Exception e) {
                playerListClass1 = Class.forName("net.minecraft.server." + NMSUtils.getMinecraftPackage() + ".PlayerList");
            }
            playerListClass = playerListClass1;
            playersByName = playerListClass.getDeclaredField("playersByName");
            playersByName.setAccessible(true);

            Method skinTextureValueMethod1;
            Method skinTextureSignatureMethod1;

            try {
                skinTextureValueMethod1 = propertyClass.getDeclaredMethod("getValue");
                skinTextureSignatureMethod1 = propertyClass.getDeclaredMethod("getSignature");
            } catch (NoSuchMethodException e) {
                skinTextureValueMethod1 = propertyClass.getDeclaredMethod("value");
                skinTextureSignatureMethod1 = propertyClass.getDeclaredMethod("signature");
            }

            skinTextureValueMethod = skinTextureValueMethod1;
            skinTextureValueMethod.setAccessible(true);
            skinTextureSignatureMethod = skinTextureSignatureMethod1;
            skinTextureSignatureMethod.setAccessible(true);

        } catch (ClassNotFoundException | NoSuchMethodException | NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
    }

    public static int getPing(Player player) {
        try {
            return (int) getPing.get(getHandle(player));
        } catch (IllegalAccessException | NullPointerException e) {
            try {
                return (int) player.getClass().getDeclaredMethod("getPing").invoke(player);
            } catch (Exception e1) {
                throw new RuntimeException("Couldn't get player ping!", e1);
            }
        }
    }

    public static Object getHandle(Player player) {
        try {
            return getPlayerHandle.invoke(player);
        } catch (InvocationTargetException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public static Object getHandle(Server server) {
        try {
            return getServerHandle.invoke(server);
        } catch (InvocationTargetException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public static Skin skinFromBukkitPlayer(Player player) {
        try {
            Object profile = gameProfileField.get(getHandle(player));
            Multimap properties = (Multimap) propertiesField.get(profile); // PropertyMap

            Collection<?> textures = properties.get("textures");
            if (textures == null || textures.isEmpty()) {
                System.out.println("empty textures, returning null");
                return null;
            }
            Object texture = textures.iterator().next();
            String value = (String) skinTextureValueMethod.invoke(texture);
            String signature = (String) skinTextureSignatureMethod.invoke(texture);
            return new Skin(value, signature);
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void setGameProfile(Player player, UserProfile profile) {
        try {
            Object serverPlayer = getHandle(player); // nms Player
            Object newGameProfile = gameProfileConstructor.newInstance(profile.getUUID(), profile.getName());

            Multimap propertyMap = (Multimap) propertiesField.get(newGameProfile);
            for (TextureProperty textureProperty : profile.getTextureProperties()) {
                Object property = propertyConstcutor.newInstance("textures", textureProperty.getValue(), textureProperty.getSignature());
                propertyMap.put("textures", property);
            }
            gameProfileField.set(serverPlayer, newGameProfile);
        } catch (IllegalAccessException | InvocationTargetException |
                 InstantiationException e) {
            e.printStackTrace();
        }
    }

    /**
     * Updates the player's name on the server, makes Bukkit.getPlayer(name) return the player
     * @param name the new name
     * @param player the player to update
     */
    public static void updateNameOnServer(String name, Player player) {
        try {
            Object server = getHandle(player.getServer());
            Map map = (Map) playersByName.get(server);
            map.remove(player.getName().toLowerCase(java.util.Locale.ROOT));
            map.put(name.toLowerCase(java.util.Locale.ROOT), getHandle(player));
            playersByName.set(server, map);

        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public static int getSkinLayersIndex(ServerVersion version) {
        // not sure how accurate this is, works on 1.20.4 & 1.8.8 though
        if (version.isOlderThan(ServerVersion.V_1_9)) {
            return 10; // 1.8.x
        } else if (version.isOlderThan(ServerVersion.V_1_10)) {
            return 12; // 1.9.x
        } else if (version.isOlderThan(ServerVersion.V_1_13)) {
            return 13; // 1.10.x - 1.12.x
        } else if (version.isOlderThan(ServerVersion.V_1_15)) {
            return 15; // 1.13.x - 1.14.x
        } else if (version.isOlderThan(ServerVersion.V_1_17)) {
            return 16; // 1.15.x - 1.16.x
        } else {
            return 17; // 1.17+
        }
    }
}
