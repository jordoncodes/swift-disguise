package me.onlyjordon.swiftdisguise.extensions

import me.onlyjordon.swiftdisguise.SpigotPlatform
import me.onlyjordon.swiftdisguise.SwiftDisguiseSpigot
import me.onlyjordon.swiftdisguise.api.SwiftDisguise
import me.onlyjordon.swiftdisguise.api.TabPrefixSuffix
import me.onlyjordon.swiftdisguise.utils.Skin
import me.onlyjordon.swiftdisguise.utils.SkinLayers
import me.onlyjordon.swiftdisguise.utils.SkinLayers.SkinLayer
import org.bukkit.entity.Player
import java.io.File
import java.net.URL
import java.util.EnumSet

object DisguiseExtensions {

    private val api: SwiftDisguiseSpigot = SwiftDisguise.getAPI(SpigotPlatform.get()) as SwiftDisguiseSpigot

    fun Player.refreshPlayer() {
        api.refreshPlayer(this)
    }

    fun Player.refreshPlayerSync() {
        api.refreshPlayerSync(this)
    }

    fun Player.setDisguiseName(name: String) {
        api.setDisguiseName(this, name)
    }

    fun Player.setDisguiseSkin(username: String) {
        api.setDisguiseSkin(this, username)
    }

    fun Player.setDisguiseSkin(skin: Skin) {
        api.setDisguiseSkin(this, skin)
    }

    fun Player.setDisguiseSkin(url: URL) {
        api.setDisguiseSkin(this, url)
    }

    fun Player.setDisguiseSkin(file: File) {
        api.setDisguiseSkin(this, file)
    }

    fun Player.setDisguisePrefixSuffix(prefixSuffix: TabPrefixSuffix) {
        api.setDisguisePrefixSuffix(this, prefixSuffix)
    }

    fun Player.setDisguiseSkinLayers(layers: EnumSet<SkinLayer>) {
        api.setDisguiseSkinLayers(this, layers)
    }

    fun Player.setDisguiseSkinLayers(layers: SkinLayers) {
        api.setDisguiseSkinLayers(this, layers)
    }

    fun Player.getDisguiseName(): String {
        return api.getDisguiseName(this)
    }

    fun Player.getDisguiseSkin(): Skin {
        return api.getDisguiseSkin(this)
    }

    fun Player.getDisguisePrefixSuffix(): TabPrefixSuffix {
        return api.getDisguisePrefixSuffix(this)
    }

    fun Player.getDisguiseVisibleSkinLayers(): EnumSet<SkinLayer> {
        return api.getVisibleSkinLayers(this)
    }

    fun Player.getDisguiseSkinLayers(): SkinLayers {
        return api.getDisguiseSkinLayers(this)
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

    fun Player.resetDisguiseSkinLayers() {
        api.resetDisguiseSkinLayers(this)
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