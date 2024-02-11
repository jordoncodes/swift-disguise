package me.onlyjordon.nicknamingapi;

import me.onlyjordon.nicknamingapi.utils.Skin;
import me.onlyjordon.nicknamingapi.utils.SkinLayers;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.UUID;

public abstract class NMSDisguiser {

    protected HashMap<UUID, NickData> data = new HashMap<>();

    /**
     * Refreshes the player, updating their skin, nickname, prefix & suffix
     * @param player the player to refresh
     */
    public abstract void refreshPlayer(Player player);

    /**
     * Resets the whole disguise to the original skin, nickname, prefix & suffix
     * @param player the player to reset the disguise of
     */
    public abstract void resetDisguise(Player player);

    /**
     * Sets the skin of a player
     * @param player the player to set the skin of
     * @param skin the skin to set
     * @return true if the skin was set successfully, false otherwise
     */
    public abstract boolean setSkin(Player player, Skin skin);

    /**
     * Sets the skin of a player
     * @param player the player to set the skin of
     * @param name the name of the player to grab the skin from
     */
    public abstract void setSkin(Player player, String name);

    /**
     * Sets the skin of a player to their original skin.
     * @param player the player to reset the skin of
     */
    public void resetSkin(@NotNull Player player) {
        if (setSkin(player, data.get(player.getUniqueId()).getOriginalSkin())) data.get(player.getUniqueId()).setCurrentSkin(null);
    }

    /**
     * Hides a skin layer for a player
     * @param player the player to hide the skin layer for
     * @param layer the layer to hide
     */
    public void hide(@NotNull Player player, @NotNull SkinLayers.SkinLayer layer) {
        setSkinLayerVisible(player, layer, false);
    }

    /**
     * Shows a skin layer for a player
     * @param player the player to show the skin layer for
     * @param layer the layer to show
     */
    public void show(@NotNull Player player, @NotNull SkinLayers.SkinLayer layer) {
        setSkinLayerVisible(player, layer, true);
    }

    /**
     * Sets whether the skin layer is visible for a player
     * @param player the player to set the skin layer visibility of
     * @param layer the layer to set the visibility of
     * @param visible whether the layer should be visible or not
     */
    public abstract void setSkinLayerVisible(@NotNull Player player, @NotNull SkinLayers.SkinLayer layer, boolean visible);

    /**
     * @param player the player to check
     * @param layer the layer to check
     * @return whether the layer is visible or not
     */
    public abstract boolean isSkinLayerVisible(@NotNull Player player, @NotNull SkinLayers.SkinLayer layer);

    public abstract SkinLayers getSkinLayers(@NotNull Player player);

    public EnumSet<SkinLayers.SkinLayer> getVisibleSkinLayers(@NotNull Player player) {
        return getSkinLayers(player).getVisibleLayers();
    }

    /**
     * Sets the nickname of a player
     * @param player the player to set the nickname of
     * @param nick the nickname to set
     */
    public abstract void setNick(@NotNull Player player, @NotNull String nick);

    /**
     * Resets the nickname of a player
     * @param player the player to reset the nickname of
     */
    public abstract void resetNick(@NotNull Player player);

    /**
     * Set's the player's prefix & suffix for tab and above the head.
     * @param player the player to set the prefix & suffix of
     * @param prefix the prefix to set
     * @param suffix the suffix to set
     * @param color the color of the name. Applies to prefix, suffix & name
     * @param priority the priority of the name. The higher the priority, the lower it will be in the tab list. 0 is the highest priority.
     */
    public abstract void setPrefixSuffix(@NotNull Player player, @NotNull TextComponent prefix, @NotNull TextComponent suffix, @NotNull ChatColor color, int priority);

    /**
     * Sends the prefix/suffix to players
     */
    public abstract void updatePrefixSuffix(@NotNull Player player);

    @ApiStatus.Internal
    public abstract void disable();

    @ApiStatus.Internal
    public abstract void setup();

    /**
     * Gets the nickname of a player
     * @param player the player to get the nickname of
     * @return the nickname of the player
     */
    public abstract String getNick(@NotNull Player player);


    /**
     * Gets the player with a nickname
     * @param nick the nickname of the player
     * @return the player with the nickname
     */
    @Nullable
    public abstract Player getPlayerWithNick(@NotNull String nick);

    /**
     * Gets the skin of a player
     * @param player the player to get the skin of
     * @return the skin of the player
     */
    public abstract Skin getSkin(@NotNull Player player);

    /**
     * Grabs the skin of a player by their name. Makes calls to the Mojang API.
     * @param skinName the name of the player to grab the skin of
     * @return the skin of the player
     */
    public abstract Skin getSkin(@NotNull String skinName);

}
