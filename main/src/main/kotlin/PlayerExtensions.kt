
import me.onlyjordon.nicknamingapi.Nicknamer
import me.onlyjordon.nicknamingapi.utils.Skin
import me.onlyjordon.nicknamingapi.utils.SkinLayers.SkinLayer
import net.kyori.adventure.text.TextComponent
import org.bukkit.ChatColor
import org.bukkit.entity.Player

class PlayerExtensions {
    companion object {
        fun Player.getNick(): String {
            return Nicknamer.getDisguiser().getNick(this)
        }

        fun Player.setNick(nick: String) {
            Nicknamer.getDisguiser().setNick(this, nick)
        }

        fun Player.getSkin(): Skin {
            return Nicknamer.getDisguiser().getSkin(this)
        }

        fun Player.setSkin(skin: Skin) {
            Nicknamer.getDisguiser().setSkin(this, skin)
        }

        fun Player.setSkin(skin: String) {
            Nicknamer.getDisguiser().setSkin(this, skin)
        }

        fun Player.refresh() {
            Nicknamer.getDisguiser().refreshPlayer(this)
        }

        fun Player.setPrefixSuffix(prefix: TextComponent, suffix: TextComponent, textColor: ChatColor, priority: Int) {
            Nicknamer.getDisguiser().setPrefixSuffix(this, prefix, suffix, textColor, priority)
        }

        fun Player.setSkinLayerVisible(layer: SkinLayer, visible: Boolean) {
            Nicknamer.getDisguiser().setSkinLayerVisible(this, layer, visible)
        }

        fun Player.isSkinLayerVisible(layer: SkinLayer): Boolean {
            return Nicknamer.getDisguiser().isSkinLayerVisible(this, layer)
        }

        fun Player.hide(layer: SkinLayer) {
            Nicknamer.getDisguiser().hide(this, layer)
        }

        fun Player.show(layer: SkinLayer) {
            Nicknamer.getDisguiser().show(this, layer)
        }
    }
}