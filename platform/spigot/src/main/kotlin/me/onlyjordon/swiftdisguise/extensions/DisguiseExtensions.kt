package me.onlyjordon.swiftdisguise.extensions

import me.onlyjordon.swiftdisguise.SpigotPlatform
import me.onlyjordon.swiftdisguise.SwiftDisguiseSpigot
import me.onlyjordon.swiftdisguise.api.ITabPrefixSuffix.NametagColor
import me.onlyjordon.swiftdisguise.api.SwiftDisguise
import me.onlyjordon.swiftdisguise.api.TabPrefixSuffix
import me.onlyjordon.swiftdisguise.utils.Skin
import me.onlyjordon.swiftdisguise.utils.SkinLayers
import me.onlyjordon.swiftdisguise.utils.SkinLayers.SkinLayer
import org.bukkit.entity.Player
import java.io.File
import java.net.URL
import java.util.*

object DisguiseExtensions {

    private val api: SwiftDisguiseSpigot = SwiftDisguise.getAPI(SpigotPlatform.get()) as SwiftDisguiseSpigot
    var Player.disguiseSkinLayers: EnumSet<SkinLayer>
        get() = api.getVisibleSkinLayers(this)
        set(value) = api.setDisguiseSkinLayers(this, value)

    var Player.disguiseSkin: Skin
        get() = api.getDisguiseSkin(this)
        set(value) = api.setDisguiseSkin(this, value)

    var Player.disguiseName: String
        get() = api.getDisguiseName(this)
        set(value) = api.setDisguiseName(this, value)

    var Player.disguisePrefixSuffix: TabPrefixSuffix
        get() = api.getDisguisePrefixSuffix(this)
        set(value) = api.setDisguisePrefixSuffix(this, value)

    var Player.disguiseUniqueId: UUID
        get() = api.getDisguiseUniqueId(this)
        set(value) = api.setDisguiseUniqueId(this, value)

    fun Player.refreshPlayer() {
        api.refreshPlayer(this)
    }

    fun Player.refreshPlayerSync() {
        api.refreshPlayerSync(this)
    }

    fun Player.setDisguiseSkin(username: String) {
        api.setDisguiseSkin(this, username)
    }

    fun Player.setDisguiseSkin(url: URL) {
        api.setDisguiseSkin(this, url)
    }

    fun Player.setDisguiseSkin(file: File) {
        api.setDisguiseSkin(this, file)
    }

    fun Player.getDisguiseVisibleSkinLayers(): EnumSet<SkinLayer> {
        return api.getVisibleSkinLayers(this)
    }

    fun Player.resetDisguiseName() {
        api.resetDisguiseName(this)
    }

    fun Player.resetDisguiseSkin() {
        api.resetDisguiseSkin(this)
    }

    fun Player.resetDisguisePrefixSuffix() {
        api.resetDisguisePrefixSuffix(this)
    }

    fun Player.resetDisguise() {
        api.resetDisguise(this)
    }

    fun Player.isSkinLayerVisible(layer: SkinLayer): Boolean {
        return api.isSkinLayerVisible(this, layer)
    }

    fun Player.setSkinLayerVisible(layer: SkinLayer, visible: Boolean) {
        api.setSkinLayerVisible(this, layer, visible)
    }

}