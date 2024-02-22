package me.onlyjordon.swiftdisguise.api;

import com.github.retrooper.packetevents.protocol.player.TextureProperty;
import com.github.retrooper.packetevents.protocol.player.UserProfile;
import com.google.common.collect.Multimap;
import me.onlyjordon.swiftdisguise.api.utils.NMSUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class PlayerConverter {
    public static UserProfile fromBukkitPlayer(Player player) {

        try {
            Class<?> craftPlayer = Class.forName("org.bukkit.craftbukkit." + NMSUtils.getMinecraftPackage() + ".entity.CraftPlayer");
            Method getProfile = craftPlayer.getDeclaredMethod("getProfile");
            getProfile.setAccessible(true);
            Object profile = getProfile.invoke(player); // GameProfile

            Method getProperties = profile.getClass().getDeclaredMethod("getProperties");
            getProperties.setAccessible(true);
            Multimap properties = (Multimap) getProperties.invoke(profile); // PropertyMap

            Collection<?> textures = properties.get("textures");
            if (textures == null || textures.isEmpty()) return new UserProfile(player.getUniqueId(), player.getName());
            Object texture = textures.iterator().next();

            String value = (String) texture.getClass().getDeclaredMethod("value").invoke(texture);
            String signature = (String) texture.getClass().getDeclaredMethod("signature").invoke(texture);

            ArrayList<TextureProperty> t = new ArrayList<>();
            t.add(new TextureProperty("textures", value, signature));
            return new UserProfile(player.getUniqueId(), player.getName(), t);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
            return null;
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
