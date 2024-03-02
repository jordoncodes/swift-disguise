package me.onlyjordon.swiftdisguise.api;

import me.onlyjordon.swiftdisguise.utils.Skin;
import me.onlyjordon.swiftdisguise.utils.SkinLayers;
import org.jetbrains.annotations.NotNull;

import java.util.EnumSet;
import java.util.UUID;

public interface ISwiftDisguiseAPI {

    /**
     * Sets the player's nickname above their head and in tablist. Applies according to the {@link me.onlyjordon.swiftdisguise.api.SwiftDisguiseConfig.NameMode} config in the server.
     * To actually apply the changes, you must call {@link #refreshPlayer(Object)} after making changes.
     * @param platformPlayer The player to set the nickname for, must be a valid player object for the platform
     * @param nick The nickname to set
     *
     */
    void setDisguiseName(Object platformPlayer, String nick);

    /**
     * Set the player's skin to the specified skin.
     * To actually apply the changes, you must call {@link #refreshPlayer(Object)} after making changes.
     * @param platformPlayer The player to set the skin for, must be a valid player object for the platform
     * @param skin The skin to set
     */
    void setDisguiseSkin(Object platformPlayer, Skin skin);

    /**
     * Set the player's skin to the specified skin and nickname.
     * @param platformPlayer The player to set the skin and nickname for, must be a valid player object for the platform
     * @param nick The nickname to set
     * @param skin The skin to set
     */
    void setDisguise(Object platformPlayer, String nick, Skin skin);


    /**
     * Set the player's skin to the specified skin.
     * Should be called asynchronously, as it may make a http request.
     * @param platformPlayer  The player to set the skin for, must be a valid player object for the platform
     * @param skin The skin to set
     */
    void setDisguiseSkin(Object platformPlayer, String skin);

    /**
     * Resets the player's disguise, removing any changes made from this class.
     * To actually apply, you must call {@link #refreshPlayer(Object)} after making changes.
     * @param platformPlayer The player to reset the disguise for, must be a valid player object for the platform
     */
    void resetDisguise(Object platformPlayer);

    /**
     * Resets the player's nickname to their original name.
     * To actually apply, you must call {@link #refreshPlayer(Object)} after making changes.
     * @param platformPlayer The player to reset the nickname for, must be a valid player object for the platform
     */
    void resetDisguiseName(Object platformPlayer);

    /**
     * Resets the player's skin to their original skin.
     * To actually apply, you must call {@link #refreshPlayer(Object)} after making changes.
     * @param platformPlayer The player to reset the skin for, must be a valid player object for the platform
     */
    void resetSkin(Object platformPlayer);

    /**
     * Refreshes the player, updating them with every change made by methods in this class.
     * Does not refresh if no changes were made.
     * @param platformPlayer the player to refresh
     */
    void refreshPlayer(Object platformPlayer);

    /**
     * Refreshes the player, updating them with every change made by methods in this class.
     * @param platformPlayer the player to refresh
     * @param force whether to force the refresh, even if no changes were made
     */
    void refreshPlayer(Object platformPlayer, boolean force);

    /**
     * Like {@link #refreshPlayer(Object)} but forces the refresh, even if no changes were made.
     * @param platformPlayer the player to refresh
     */
    void forceRefreshPlayer(Object platformPlayer);

    /**
     * Gets the player's disguise skin (the skin they are currently disguised as)
     * @param platformPlayer The player to get the disguise skin of, must be a valid player object for the platform
     * @return The player's disguise skin
     */
    Skin getDisguiseSkin(Object platformPlayer);

    /**
     * Gets the player's real skin (the skin they joined with)
     * @param platformPlayer The player to get the real skin of, must be a valid player object for the platform
     * @return The player's real skin
     */
    Skin getRealSkin(Object platformPlayer);

    /**
     * Gets the player's disguise name (the name they are currently disguised as)
     * @param platformPlayer The player to get the disguise name of, must be a valid player object for the platform
     * @return The player's disguise name
     */
    String getDisguiseName(Object platformPlayer);

    /**
     * Gets the player's real name (the name they joined with)
     * @param platformPlayer The player to get the real name of, must be a valid player object for the platform
     * @return The player's real name
     */
    String getRealName(Object platformPlayer);

    /**
     * Gets the player's visible skin layers
     * @param platformPlayer The player to get the visible skin layers of, must be a valid player object for the platform
     * @return The player's visible skin layers
     */
    EnumSet<SkinLayers.SkinLayer> getVisibleSkinLayers(Object platformPlayer);

    /**
     * Gets the player's real skin layers (the layers they have in their client settings)
     * @param platformPlayer The player to get the real skin layers of, must be a valid player object for the platform
     * @return The player's real skin layers
     */
    SkinLayers getRealSkinLayers(Object platformPlayer);

    /**
     * Gets the player's disguise skin layers (the layers that are currently being shown)
     * @param platformPlayer The player to get the disguise skin layers of, must be a valid player object for the platform
     * @return The player's disguise skin layers
     */
    SkinLayers getDisguiseSkinLayers(Object platformPlayer);

    /**
     * Sets the player's skin layers
     * @param platformPlayer The player to set the skin layers for, must be a valid player object for the platform
     * @param skinLayers The skin layers to set
     */
    void setDisguiseSkinLayers(Object platformPlayer, SkinLayers skinLayers);

    /**
     * Sets the player's skin layer visibility
     * @param platformPlayer The player to set the skin layer visibility for, must be a valid player object for the platform
     * @param skinLayer The skin layer to set the visibility of
     * @param visible Whether the skin layer should be visible
     */
    void setSkinLayerVisible(Object platformPlayer, SkinLayers.SkinLayer skinLayer, boolean visible);

    /**
     * Checks if the player's skin layer is visible
     * @param platformPlayer The player to check the skin layer visibility of, must be a valid player object for the platform
     * @param skinLayer The skin layer to check the visibility of
     * @return Whether the skin layer is visible
     */
    boolean isSkinLayerVisible(Object platformPlayer, SkinLayers.SkinLayer skinLayer);

    /**
     * Shows the player's skin layer
     * @param platformPlayer The player to show the skin layer for, must be a valid player object for the platform
     * @param layer The skin layer to show
     */
    void show(Object platformPlayer, SkinLayers.SkinLayer layer);

    /**
     * Hides the player's skin layer
     * @param platformPlayer The player to hide the skin layer for, must be a valid player object for the platform
     * @param layer The skin layer to hide
     */
    void hide(Object platformPlayer, SkinLayers.SkinLayer layer);

    /**
     * Sets the player's nickname and skin to the specified player's nickname and skin.
     * @param platformPlayer The player to disguise, must be a valid player object for the platform
     * @param platformEntity The entity to disguise as, must be a valid entity object for the platform
     */
    void disguiseAs(Object platformPlayer, Object platformEntity);

    /**
     * Gets the player's disguise data
     * @param platformPlayer The player to get the disguise data of, must be a valid player object for the platform
     * @return The player's disguise data
     */
    IDisguiseData getDisguiseData(Object platformPlayer);

    /**
     * Sets the player's disguise data
     * @param platformPlayer The player to set the disguise data for, must be a valid player object for the platform
     * @param disguiseData The disguise data to set
     */
    void setDisguiseData(Object platformPlayer, IDisguiseData disguiseData);

    /**
     * Gets the player's real UUID (the UUID they joined with)
     * @param platformPlayer The player to get the real UUID of, must be a valid player object for the platform
     * @return The player's real UUID
     */
    UUID getRealUUID(Object platformPlayer);

    /**
     * Gets the player's disguise UUID (the UUID they are currently disguised as)
     * @param uuid A player's uuid
     * @return The player's disguise UUID
     */
    UUID getFakeUUID(UUID uuid);

    /**
     * Gets the player's tab prefix and suffix
     * @param platformPlayer The player, must be a valid player object for the platform
     * @return The player's disguise UUID
     */
    ITabPrefixSuffix getDisguisePrefixSuffix(@NotNull Object platformPlayer);

    void setDisguisePrefixSuffix(@NotNull Object platformPlayer, ITabPrefixSuffix prefixSuffix);
}
