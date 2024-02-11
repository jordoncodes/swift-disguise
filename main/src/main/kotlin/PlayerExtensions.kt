
import me.onlyjordon.nicknamingapi.NicknamerAPI
import me.onlyjordon.nicknamingapi.utils.SkinLayers.SkinLayer
import net.kyori.adventure.text.TextComponent
import org.bukkit.ChatColor
import org.bukkit.entity.Player

class PlayerExtensions {
    companion object {
        fun Player.getNick(): String {
            return NicknamerAPI.getNicknamer().getNick(this)
        }

        fun Player.setNick(nick: String) {
            NicknamerAPI.getNicknamer().setNick(this, nick)
        }

        fun Player.getSkin(): me.onlyjordon.nicknamingapi.utils.Skin {
            return NicknamerAPI.getNicknamer().getSkin(this)
        }

        fun Player.setSkin(skin: me.onlyjordon.nicknamingapi.utils.Skin) {
            NicknamerAPI.getNicknamer().setSkin(this, skin)
        }

        fun Player.setSkin(skin: String) {
            NicknamerAPI.getNicknamer().setSkin(this, skin)
        }

        fun Player.refresh() {
            NicknamerAPI.getNicknamer().refreshPlayer(this)
        }

        fun Player.setPrefixSuffix(prefix: TextComponent, suffix: TextComponent, textColor: ChatColor, priority: Int) {
            NicknamerAPI.getNicknamer().setPrefixSuffix(this, prefix, suffix, textColor, priority)
        }

        fun Player.setSkinLayerVisible(layer: SkinLayer, visible: Boolean) {
            NicknamerAPI.getNicknamer().setSkinLayerVisible(this, layer, visible)
        }

        fun Player.isSkinLayerVisible(layer: SkinLayer): Boolean {
            return NicknamerAPI.getNicknamer().isSkinLayerVisible(this, layer)
        }

        fun Player.hide(layer: SkinLayer) {
            NicknamerAPI.getNicknamer().hide(this, layer)
        }

        fun Player.show(layer: SkinLayer) {
            NicknamerAPI.getNicknamer().show(this, layer)
        }
    }
}