package me.onlyjordon.nicknamingapi.debug

import me.onlyjordon.nicknamingapi.NicknamerAPI
import me.onlyjordon.nicknamingapi.commands.PlayerOnlyCommand
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.TextColor
import org.bukkit.ChatColor
import org.bukkit.entity.Player
import java.util.concurrent.ThreadLocalRandom

class CommandDev : PlayerOnlyCommand("nickdev", "nicknamerapi.dev") {
    override fun execute(player: Player, args: Array<String>): Boolean {
        if (args.size >= 2) {
            if ("skin".equals(args[0], ignoreCase = true)) {
                player.sendMessage("Setting skin...")
                NicknamerAPI.getNicknamer().setSkin(player, args[1])
                player.sendMessage("Skin set!")
                return true
            }
            if ("nick".equals(args[0], ignoreCase = true)) {
                player.sendMessage("Set nick to " + args[1])
                NicknamerAPI.getNicknamer().setNick(player, args[1])
                return true
            }
            if ("nickandskin".equals(args[0], ignoreCase = true)) {
                player.sendMessage("Setting skin and nick...")
                NicknamerAPI.getNicknamer().setSkin(player, args[1])
                NicknamerAPI.getNicknamer().setNick(player, args[1])
                player.sendMessage("Skin and nick set!")
                NicknamerAPI.getNicknamer().refreshPlayer(player)
                return true
            }
        }
        if (args.isNotEmpty()) {
            if ("refresh".equals(args[0], ignoreCase = true)) {
                player.sendMessage("Refreshing...")
                NicknamerAPI.getNicknamer().refreshPlayer(player)
                return true
            }
            if ("prefixsuffix".equals(args[0], ignoreCase = true)) {
                NicknamerAPI.getNicknamer().setPrefixSuffix(player, Component.text("Admin ").color { TextColor.color(0xff0000).value() }, Component.text(" [Loser]").color { TextColor.color(0xff0000).value() }, ChatColor.WHITE, 1_000_000)
                NicknamerAPI.getNicknamer().updatePrefixSuffix(player)
                player.sendMessage("Prefix and suffix set!")
                return true
            }
            if ("skinlayers".equals(args[0], ignoreCase = true)) {
                player.sendMessage("Setting random skin layers to 0...")
                NicknamerAPI.getNicknamer().setSkinLayerVisible(player, me.onlyjordon.nicknamingapi.utils.SkinLayers.SkinLayer.entries[ThreadLocalRandom.current().nextInt(
                    me.onlyjordon.nicknamingapi.utils.SkinLayers.SkinLayer.entries.size)], false)
                player.sendMessage("Skin layers set!")
                return true
            }

        }
        return false
    }

    override fun completeTab(sender: Player, args: Array<String>): HashMap<String, Int> {
        return HashMap<String, Int>().apply {
            put("skin", 0)
            put("nick", 0)
            put("nickandskin", 0)
            put("refresh", 0)
            put("prefixsuffix", 0)
            put("skinlayers", 0)
            put("example", 1)

        }
    }
}
