
import me.onlyjordon.nicknamingapi.Nicknamer
import me.onlyjordon.nicknamingapi.utils.Skin
import net.kyori.adventure.text.TextComponent
import org.bukkit.ChatColor
import org.bukkit.entity.Player

class PlayerExtensions {
    companion object {
        fun Player.getNick(): String {
            return Nicknamer.getDisguiser().getNick(this);
        }

        fun Player.setNickname(nick: String) {
            Nicknamer.getDisguiser().setNick(this, nick);
        }

        fun Player.getSkin(): Skin {
            return Nicknamer.getDisguiser().getSkin(this);
        }

        fun Player.setSkin(skin: Skin) {
            Nicknamer.getDisguiser().setSkin(this, skin);
        }

        fun Player.setSkin(skin: String) {
            Nicknamer.getDisguiser().setSkin(this, skin);
        }

        fun Player.refresh() {
            Nicknamer.getDisguiser().refreshPlayer(this);
        }

        fun Player.setPrefixSuffix(prefix: TextComponent, suffix: TextComponent, textColor: ChatColor) {
            Nicknamer.getDisguiser().setPrefixSuffix(this, prefix, suffix, textColor);
        }
    }
}