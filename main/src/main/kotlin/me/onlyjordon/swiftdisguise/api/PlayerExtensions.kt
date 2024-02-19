package me.onlyjordon.swiftdisguise.api

import me.onlyjordon.swiftdisguise.api.utils.Skin
import me.onlyjordon.swiftdisguise.api.utils.SkinLayers.SkinLayer
import net.kyori.adventure.text.TextComponent
import org.bukkit.ChatColor
import org.bukkit.entity.Player

class PlayerExtensions {
    companion object {
        fun Player.getNick(): String {
            return SwiftDisguiseAPI.getDisguiser().getNick(this)
        }

        fun Player.setNick(nick: String) {
            SwiftDisguiseAPI.getDisguiser().setNick(this, nick)
        }

        fun Player.getSkin(): Skin {
            return SwiftDisguiseAPI.getDisguiser().getSkin(this)
        }

        fun Player.setSkin(skin: Skin) {
            SwiftDisguiseAPI.getDisguiser().setSkin(this, skin)
        }

        fun Player.setSkin(skin: String) {
            SwiftDisguiseAPI.getDisguiser().setSkin(this, skin)
        }

        fun Player.refresh() {
            SwiftDisguiseAPI.getDisguiser().refreshPlayer(this)
        }

        fun Player.setPrefixSuffix(prefix: TextComponent, suffix: TextComponent, textColor: ChatColor, priority: Int) {
            SwiftDisguiseAPI.getDisguiser().setPrefixSuffix(this, prefix, suffix, textColor, priority)
        }

        fun Player.setSkinLayerVisible(layer: SkinLayer, visible: Boolean) {
            SwiftDisguiseAPI.getDisguiser().setSkinLayerVisible(this, layer, visible)
        }

        fun Player.isSkinLayerVisible(layer: SkinLayer): Boolean {
            return SwiftDisguiseAPI.getDisguiser().isSkinLayerVisible(this, layer)
        }

        fun Player.hide(layer: SkinLayer) {
            SwiftDisguiseAPI.getDisguiser().hide(this, layer)
        }

        fun Player.show(layer: SkinLayer) {
            SwiftDisguiseAPI.getDisguiser().show(this, layer)
        }
    }
}