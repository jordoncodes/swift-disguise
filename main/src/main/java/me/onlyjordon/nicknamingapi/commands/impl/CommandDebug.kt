package me.onlyjordon.nicknamingapi.commands.impl

import me.onlyjordon.nicknamingapi.Nicknamer
import me.onlyjordon.nicknamingapi.commands.PlayerOnlyCommand
import me.onlyjordon.nicknamingapi.utils.SkinLayers
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.TextColor
import org.bukkit.ChatColor
import org.bukkit.entity.Player
import org.jetbrains.annotations.ApiStatus.Internal
import java.util.concurrent.ThreadLocalRandom

@Internal
class CommandDebug : PlayerOnlyCommand("debug", "debug.use") {
    override fun execute(player: Player, args: Array<String>): Boolean {
        if (args.size >= 2) {
            if ("skin".equals(args[0], ignoreCase = true)) {
                player.sendMessage("Setting skin...")
                Nicknamer.getDisguiser().setSkin(player, args[1])
                player.sendMessage("Skin set!")
                return true
            }
            if ("nick".equals(args[0], ignoreCase = true)) {
                player.sendMessage("Set nick to " + args[1])
                Nicknamer.getDisguiser().setNick(player, args[1])
                return true
            }
            if ("nickandskin".equals(args[0], ignoreCase = true)) {
                player.sendMessage("Setting skin and nick...")
                Nicknamer.getDisguiser().setSkin(player, args[1])
                Nicknamer.getDisguiser().setNick(player, args[1])
                player.sendMessage("Skin and nick set!")
                Nicknamer.getDisguiser().refreshPlayer(player)
                return true
            }
        }
        if (args.isNotEmpty()) {
            if ("refresh".equals(args[0], ignoreCase = true)) {
                player.sendMessage("Refreshing...")
                Nicknamer.getDisguiser().refreshPlayer(player)
                return true
            }
            if ("prefixsuffix".equals(args[0], ignoreCase = true)) {
                Nicknamer.getDisguiser().setPrefixSuffix(player, Component.text("Admin ").color { TextColor.color(0xff0000).value() }, Component.text(" [Loser]").color { TextColor.color(0xff0000).value() }, ChatColor.WHITE, 1_000_000)
                Nicknamer.getDisguiser().updatePrefixSuffix(player)
                player.sendMessage("Prefix and suffix set!")
                return true
            }
            if ("skinlayers".equals(args[0], ignoreCase = true)) {
                player.sendMessage("Setting random skin layers to 0...")
                println(Nicknamer.getDisguiser().getSkinLayers(player).rawSkinLayers)
                Nicknamer.getDisguiser().setSkinLayerVisible(player, SkinLayers.SkinLayer.entries[ThreadLocalRandom.current().nextInt(SkinLayers.SkinLayer.entries.size)], false)
                println(Nicknamer.getDisguiser().getSkinLayers(player).rawSkinLayers)
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
