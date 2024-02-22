package me.onlyjordon.swiftdisguise.debug

import me.onlyjordon.swiftdisguise.api.DisguiseBuilder
import me.onlyjordon.swiftdisguise.api.SwiftDisguiseAPI
import me.onlyjordon.swiftdisguise.api.utils.SkinLayers
import me.onlyjordon.swiftdisguise.api.wrapper.PlayerDisguiseWrapper
import me.onlyjordon.swiftdisguise.commands.PlayerOnlyCommand
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.TextColor
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer
import org.bukkit.ChatColor
import org.bukkit.entity.Player
import java.util.concurrent.ThreadLocalRandom

class CommandDev : PlayerOnlyCommand("nickdev", "swiftdisguise.dev") {
    override fun execute(player: Player, args: Array<String>): Boolean {
        if (args.size >= 2) {
            if ("skin".equals(args[0], ignoreCase = true)) {
                player.sendMessage("Setting skin...")
                SwiftDisguiseAPI.getDisguiser().setSkin(player, args[1])
                player.sendMessage("Skin set!")
                return true
            }
            if ("nick".equals(args[0], ignoreCase = true)) {
                player.sendMessage("Set nick to " + args[1])
                SwiftDisguiseAPI.getDisguiser().setNick(player, args[1])
                SwiftDisguiseAPI.getDisguiser().refreshPlayer(player)
                return true
            }
            if ("nickandskin".equals(args[0], ignoreCase = true)) {
                SwiftDisguiseAPI.getDisguiser().setNick(player, args[1])
                SwiftDisguiseAPI.getDisguiser().setSkin(player, args[1])
                player.sendMessage("Skin and nick are being set!")
                return true
            }
        }
        if (args.isNotEmpty()) {
            if ("refresh".equals(args[0], ignoreCase = true)) {
                player.sendMessage("Refreshing...")
                SwiftDisguiseAPI.getDisguiser().refreshPlayer(player)
                return true
            }
            if ("prefixsuffix".equals(args[0], ignoreCase = true)) {
                SwiftDisguiseAPI.getDisguiser().setPrefixSuffix(player, Component.text("Admin ").color { TextColor.color(0xff0000).value() }, Component.text(" [Loser]").color { TextColor.color(0xff0000).value() }, ChatColor.WHITE, 1_000_000)
                SwiftDisguiseAPI.getDisguiser().updatePrefixSuffix(player)
                player.sendMessage("Prefix and suffix set!")
                return true
            }
            if ("skinlayers".equals(args[0], ignoreCase = true)) {
                player.sendMessage("Setting random skin layers to 0...")
                SwiftDisguiseAPI.getDisguiser().setSkinLayerVisible(player, SkinLayers.SkinLayer.entries[ThreadLocalRandom.current().nextInt(
                    SkinLayers.SkinLayer.entries.size)], false)
                player.sendMessage("Skin layers set!")
                return true
            }
            if ("builder".equals(args[0], ignoreCase = true)) {
                DisguiseBuilder(SwiftDisguiseAPI.getDisguiser())
                    .setNick("example")
                    .setSkin("phoave")
                    .setPrefixSuffix("Admin ", " Test")
                    .setSkinLayerVisible(SkinLayers.SkinLayer.CAPE, false)
                    .apply(player)
            }
            if ("wrapper".equals(args[0], ignoreCase = true)) {
                val wrapper = PlayerDisguiseWrapper.of(player)
                wrapper.disguiseAsSync("example")
                wrapper.setPrefixSuffix(Component.text("Prefix ").color { TextColor.color(0xff0000).value() }, Component.text(" Suffix").color { TextColor.color(0xff0000).value() }, ChatColor.WHITE, 1_000_000)
                wrapper.refreshPlayer()
                player.sendMessage("real name is ${wrapper.realName}; disguised as ${wrapper.nick} with prefix \"${LegacyComponentSerializer.legacySection().serialize(wrapper.prefix)}\" and suffix \"${LegacyComponentSerializer.legacySection().serialize(wrapper.suffix)}\"")
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
            put("builder", 0)
            put("example", 1)

        }
    }
}
