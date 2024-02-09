package me.onlyjordon.nicknamingapi.commands.impl

import me.onlyjordon.nicknamingapi.Nicknamer
import me.onlyjordon.nicknamingapi.commands.PlayerOnlyCommand
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.TextColor
import org.bukkit.ChatColor
import org.bukkit.entity.Player

class CommandDebug : PlayerOnlyCommand("debug", "debug.use") {
    override fun execute(sender: Player, args: Array<String>): Boolean {
        if (args.size >= 2) {
            if ("skin".equals(args[0], ignoreCase = true)) {
                sender.sendMessage("Setting skin...")
                Nicknamer.getDisguiser().setSkin(sender, args[1])
                sender.sendMessage("Skin set!")
                return true
            }
            if ("nick".equals(args[0], ignoreCase = true)) {
                sender.sendMessage("Set nick to " + args[1])
                Nicknamer.getDisguiser().setNick(sender, args[1])
                return true
            }
            if ("nickandskin".equals(args[0], ignoreCase = true)) {
                sender.sendMessage("Setting skin and nick...")
                Nicknamer.getDisguiser().setSkin(sender, args[1])
                Nicknamer.getDisguiser().setNick(sender, args[1])
                sender.sendMessage("Skin and nick set!")
                Nicknamer.getDisguiser().refreshPlayer(sender)
                return true
            }
        }
        if (args.isNotEmpty()) {
            if ("refresh".equals(args[0], ignoreCase = true)) {
                sender.sendMessage("Refreshing...")
                Nicknamer.getDisguiser().refreshPlayer(sender)
                return true
            }
            if ("prefixsuffix".equals(args[0], ignoreCase = true)) {
                Nicknamer.getDisguiser().setPrefixSuffix(sender, Component.text("Admin ").color { TextColor.color(0xff0000).value() }, Component.text(" [Loser]").color { TextColor.color(0xff0000).value() }, ChatColor.WHITE)
                sender.sendMessage("Prefix and suffix set!")
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
            if ("refresh".equals(args[0], ignoreCase = true) || "prefixsuffix".equals(args[0], ignoreCase = true)) return@apply
            put("example", 1)

        }
    }
}
