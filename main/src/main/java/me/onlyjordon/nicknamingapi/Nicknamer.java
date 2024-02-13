package me.onlyjordon.nicknamingapi;

import me.onlyjordon.nicknamingapi.events.*;
import me.onlyjordon.nicknamingapi.utils.Skin;
import me.onlyjordon.nicknamingapi.utils.SkinLayers;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.EnumSet;

public class Nicknamer {

    private final INicknamer nicknamerImpl;
    private final NicknamerAPI plugin;

    public Nicknamer(INicknamer nicknamerImpl, NicknamerAPI plugin) {
        this.nicknamerImpl = nicknamerImpl;
        this.plugin = plugin;
    }

    public void refreshPlayer(@NotNull Player player) {
        if (Bukkit.isPrimaryThread()) {
            Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> refreshPlayer(player));
            return;
        }
        PlayerRefreshEvent event = new PlayerRefreshEvent(player);
        Bukkit.getPluginManager().callEvent(event);
        if (!event.isCancelled())
            nicknamerImpl.refreshPlayer(player);
    }

    public void resetDisguise(@NotNull Player player) {
        nicknamerImpl.resetDisguise(player);
    }

    public boolean setSkin(@NotNull Player player, @NotNull Skin skin) {
        PlayerSkinChangeEvent event = new PlayerSkinChangeEvent(player, nicknamerImpl.getSkin(player), skin);
        Bukkit.getPluginManager().callEvent(event);
        if (event.isCancelled())
            return false;
        return nicknamerImpl.setSkin(player, event.getNewSkin());
    }

    public void setSkin(@NotNull Player player, @NotNull String name) {
        if (Bukkit.isPrimaryThread()) {
            Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> setSkin(player, name));
            return;
        }
        Skin skin = Skin.getSkin(name);
        PlayerSkinChangeEvent event = new PlayerSkinChangeEvent(player, nicknamerImpl.getSkin(player), skin);
        Bukkit.getPluginManager().callEvent(event);
        if (event.isCancelled())
            return;
        Skin newSkin = event.getNewSkin();
        if (newSkin == null) {
            nicknamerImpl.resetSkin(player);
        } else {
            nicknamerImpl.setSkin(player, newSkin);
        }
        refreshPlayer(player); // because this runs async
    }

    public void setSkinLayerVisible(@NotNull Player player, @NotNull SkinLayers.SkinLayer layer, boolean visible) {
        SkinLayers layers = nicknamerImpl.getSkinLayers(player).copy();
        layers.setLayerVisible(layer, visible);
        PlayerSkinLayerChangeEvent event = new PlayerSkinLayerChangeEvent(player, nicknamerImpl.getSkinLayers(player), layers, PlayerSkinLayerChangeEvent.Reason.PLUGIN);
        Bukkit.getPluginManager().callEvent(event);
        if (event.isCancelled())
            return;
        nicknamerImpl.setSkinLayers(player, event.getNewLayers());
    }

    public boolean isSkinLayerVisible(@NotNull Player player, @NotNull SkinLayers.SkinLayer layer) {
        return nicknamerImpl.isSkinLayerVisible(player, layer);
    }

    public SkinLayers getSkinLayers(@NotNull Player player) {
        return nicknamerImpl.getSkinLayers(player);
    }

    public void hide(@NotNull Player player, @NotNull SkinLayers.SkinLayer layer) {
        nicknamerImpl.hide(player, layer);
    }

    public void show(@NotNull Player player, @NotNull SkinLayers.SkinLayer layer) {
        nicknamerImpl.show(player, layer);
    }

    public void setNick(@NotNull Player player, @NotNull String nick) {
        PlayerNickChangeEvent event = new PlayerNickChangeEvent(player, nicknamerImpl.getNick(player), nick);
        Bukkit.getPluginManager().callEvent(event);
        if (event.isCancelled())
            return;
        System.out.println("new nick: " + event.getNewNick() + " // old nick: " + event.getPreviousNick());
        if (event.getNewNick() == null)
            nicknamerImpl.resetNick(player);
        else
            nicknamerImpl.setNick(player, event.getNewNick());
    }

    public void resetNick(@NotNull Player player) {
        PlayerNickChangeEvent event = new PlayerNickChangeEvent(player, nicknamerImpl.getNick(player), null);
        Bukkit.getPluginManager().callEvent(event);
        if (event.isCancelled())
            return;
        if (event.getNewNick() == null) {
            nicknamerImpl.resetNick(player);
        } else {
            nicknamerImpl.setNick(player, event.getNewNick());
        }
    }

    public void setPrefixSuffix(@NotNull Player player, @NotNull TextComponent prefix, @NotNull TextComponent suffix, @NotNull ChatColor color, int priority) {
        PlayerPrefixSuffixChangeEvent event = new PlayerPrefixSuffixChangeEvent(player, getPrefix(player), getSuffix(player), prefix, suffix, color, priority);
        Bukkit.getPluginManager().callEvent(event);
        if (event.isCancelled())
            return;
        TextComponent pref = event.getNewPrefix();
        TextComponent suff = event.getNewSuffix();
        if (pref == null) pref = Component.text("");
        if (suff == null) suff = Component.text("");
        nicknamerImpl.setPrefixSuffix(player, pref, suff, event.getColor(), event.getPriority());
    }

    public TextComponent getPrefix(@NotNull Player player) {
        return nicknamerImpl.getPrefix(player);
    }

    public TextComponent getSuffix(@NotNull Player player) {
        return nicknamerImpl.getSuffix(player);
    }

    public void updatePrefixSuffix(@NotNull Player player) {
        nicknamerImpl.updatePrefixSuffix(player);
    }

    public void disable() {
        nicknamerImpl.disable();
    }

    public void setup() {
        nicknamerImpl.setup();
    }

    public String getNick(@NotNull Player player) {
        return nicknamerImpl.getNick(player);
    }

    @Nullable
    public Player getPlayerWithNick(@NotNull String nick) {
        return nicknamerImpl.getPlayerWithNick(nick);
    }

    public Skin getSkin(@NotNull Player player) {
        return nicknamerImpl.getSkin(player);
    }

    public Skin getSkin(@NotNull String skinName) {
        return nicknamerImpl.getSkin(skinName);
    }

    public void resetSkin(@NotNull Player player) {
        PlayerSkinChangeEvent event = new PlayerSkinChangeEvent(player, nicknamerImpl.getSkin(player), null);
        Bukkit.getPluginManager().callEvent(event);
        if (event.isCancelled())
            return;
        if (event.getNewSkin() == null)
            nicknamerImpl.resetSkin(player);
        else
            nicknamerImpl.setSkin(player, event.getNewSkin());
    }

    public EnumSet<SkinLayers.SkinLayer> getVisibleSkinLayers(@NotNull Player player) {
        return nicknamerImpl.getVisibleSkinLayers(player);
    }

    @Nullable
    public INickData getData(Player player) {
        return nicknamerImpl.getData(player);
    }

    protected INicknamer getImplementation() {
        return nicknamerImpl;
    }
}
