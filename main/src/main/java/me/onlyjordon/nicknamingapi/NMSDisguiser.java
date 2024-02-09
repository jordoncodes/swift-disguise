package me.onlyjordon.nicknamingapi;

import me.onlyjordon.nicknamingapi.utils.Skin;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.WeakHashMap;

public abstract class NMSDisguiser {

    protected WeakHashMap<Player, String> nicknames = new WeakHashMap<>();
    protected WeakHashMap<Player, Skin> currentSkins = new WeakHashMap<>();
    protected WeakHashMap<Player, Skin> originalSkins = new WeakHashMap<>();

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
    public void resetSkin(Player player) {
        if (setSkin(player, originalSkins.get(player))) currentSkins.remove(player);
    }


    /**
     * Sets the nickname of a player
     * @param player the player to set the nickname of
     * @param nick the nickname to set
     */
    public abstract void setNick(Player player, String nick);

    /**
     * Resets the nickname of a player
     * @param player the player to reset the nickname of
     */
    public abstract void resetNick(Player player);

    /**
     * Set's the player's prefix & suffix for tab and above the head.
     * @param player the player to set the prefix & suffix of
     * @param prefix the prefix to set
     * @param suffix the suffix to set
     * @param color the color of the name. Applies to prefix, suffix & name
     */
    public abstract void setPrefixSuffix(Player player, TextComponent prefix, TextComponent suffix, ChatColor color);

    @ApiStatus.Internal
    public abstract void disable();

    /**
     * Gets the nickname of a player
     * @param player the player to get the nickname of
     * @return the nickname of the player
     */
    public abstract String getNick(Player player);


    /**
     * Gets the player with a nickname
     * @param nick the nickname of the player
     * @return the player with the nickname
     */
    public abstract Player getPlayerWithNick(String nick);

    @ApiStatus.Internal
    public abstract void setup();

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
