package me.onlyjordon.swiftdisguise

import com.github.retrooper.packetevents.protocol.player.ClientVersion
import com.github.retrooper.packetevents.protocol.player.TextureProperty
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerTeams
import me.onlyjordon.swiftdisguise.api.ISwiftDisguiseAPI
import me.onlyjordon.swiftdisguise.api.ITabPrefixSuffix
import me.onlyjordon.swiftdisguise.api.SwiftDisguise
import me.onlyjordon.swiftdisguise.api.SwiftDisguiseConfig
import me.onlyjordon.swiftdisguise.nms.CrossVersionPlayerHelper
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.Bukkit.isPrimaryThread
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin
import java.util.*


class PlayerRefresher(private val api: ISwiftDisguiseAPI, private val plugin: JavaPlugin): PacketExtensions() {

    private val oldTabPrefixSuffix = HashMap<UUID, ITabPrefixSuffix>()

    fun refreshSelf(player: Player) {
        if (isPrimaryThread()) { runAsyncThread { refreshSelf(player) }; return }
        updateGameProfile(player)
        refreshPlayerTab(player, player.peUser.clientVersion, listOf(player))
        respawn(player)
    }

    private fun updateGameProfile(player: Player) {
        if (SwiftDisguise.getConfig().nameMode() == SwiftDisguiseConfig.NameMode.WEAK) return
        val profile = player.peProfile
        val skin = api.getDisguiseSkin(player)
        profile.textureProperties.clear()
        profile.textureProperties.add(TextureProperty("textures", skin.value, skin.signature))
        profile.uuid = player.uniqueId // don't fake uuid here because it will break things
        profile.name = api.getDisguiseName(player)
        CrossVersionPlayerHelper.setGameProfile(player, profile)
        CrossVersionPlayerHelper.updateAllNamesOnServer()
    }

    fun refreshForOthers(player: Player) {
        if (isPrimaryThread()) { runAsyncThread { refreshForOthers(player) }; return }
        val players = ArrayList(player.world.players)
        players.removeIf { it.uniqueId.equals(player.uniqueId) }

        refreshPlayerTab(player, player.peUser.clientVersion, players)
        refreshNameSkinForOthers(player)
    }

    private fun refreshNameSkinForOthers(player: Player, players: Collection<Player> = player.world.players) {
        if (!isPrimaryThread()) {
            runPrimaryThread { refreshNameSkinForOthers(player, players) }
            return
        }
        players.forEach {
            it.hidePlayer(player)
            it.showPlayer(player)
        }
    }

    private fun refreshPlayerTab(player: Player, playerVersion: ClientVersion, players: Collection<Player> = player.world.players) {
        players.forEach {
            it.sendPacket(player.playerInfoRemovePacketFromServerVersion)

            playerVersion.ifOlderThan(ClientVersion.V_1_16_4) {
                if (player == it) {
                    player.sendPacket(player.deleteEntityPacket)
                    player.sendPacket(player.legacySpawnPlayerPacket)
                    runPrimaryThread { player.teleport(player) }
                }
            }

            it.sendPacket(player.playerInfoAddPacketFromServerVersion)
        }
    }

    fun refreshPrefixSuffix(sender: Player, vararg receiver: Player) {
        val prefixSuffix = api.getDisguisePrefixSuffix(sender)
        val colorField = NamedTextColor::class.java.getDeclaredField(prefixSuffix.color.toString())
        val color = colorField.get(null) as NamedTextColor
        val teamInfo = WrapperPlayServerTeams.ScoreBoardTeamInfo(
            Component.empty(),
            prefixSuffix.prefix,
            prefixSuffix.suffix,
            WrapperPlayServerTeams.NameTagVisibility.ALWAYS,
            WrapperPlayServerTeams.CollisionRule.NEVER,
            color,
            WrapperPlayServerTeams.OptionData.FRIENDLY_FIRE
        )
        receiver.forEach {
            if (oldTabPrefixSuffix.containsKey(sender.uniqueId)) {
                val oldPrefixSuffix = oldTabPrefixSuffix[sender.uniqueId]!!
                val oldTeamInfo = WrapperPlayServerTeams.ScoreBoardTeamInfo(
                    Component.empty(),
                    oldPrefixSuffix.prefix,
                    oldPrefixSuffix.suffix,
                    WrapperPlayServerTeams.NameTagVisibility.ALWAYS,
                    WrapperPlayServerTeams.CollisionRule.NEVER,
                    color,
                    WrapperPlayServerTeams.OptionData.FRIENDLY_FIRE
                )
                val deleteOldTeam = WrapperPlayServerTeams((oldPrefixSuffix.priority.toChar()+sender.uniqueId.toString().substring(0, 16)), WrapperPlayServerTeams.TeamMode.REMOVE, oldTeamInfo, api.getDisguiseName(sender))
                it.sendPacket(deleteOldTeam)
            }
            val newTeam = WrapperPlayServerTeams((prefixSuffix.priority.toChar()+sender.uniqueId.toString().substring(0, 16)), WrapperPlayServerTeams.TeamMode.CREATE, teamInfo, api.getDisguiseName(sender))
            it.sendPacket(newTeam)
        }
        oldTabPrefixSuffix[sender.uniqueId] = prefixSuffix
    }


    private fun respawn(player: Player) {
        if (!ViaBackwardsFixer.sendRespawnPacketWithVia(player, 0L, player.peGameMode.id.toShort(), false)) player.sendPacket(player.respawnPacket)
        player.sendPacket(player.teleportPacket)
        runPrimaryThread {
            if (player.isOnline) {
                player.teleport(player)
                player.sendPacket(refreshChunksPacket)
                player.updateInventory()
                player.exp = player.exp
                player.health = player.health
                player.healthScale = player.healthScale
                player.foodLevel = player.foodLevel
                player.saturation = player.saturation
                player.allowFlight = player.allowFlight
                player.compassTarget = player.compassTarget
                player.exhaustion = player.exhaustion
                player.level = player.level
            }
        }
    }

    fun refreshPlayer(player: Player) {
        refreshSelf(player)
        refreshForOthers(player)
    }


    private fun runPrimaryThread(task: () -> Unit) {
        plugin.server.scheduler.runTask(plugin, task)
    }
    private fun runPrimaryThread(delay: Long, task: () -> Unit) {
        plugin.server.scheduler.runTaskLater(plugin, task, delay)
    }

    private fun runAsyncThread(task: () -> Unit) {
        plugin.server.scheduler.runTaskAsynchronously(plugin, task)
    }
    private fun runAsyncThread(delay: Long, task: () -> Unit) {
        plugin.server.scheduler.runTaskLaterAsynchronously(plugin, task, delay)
    }

    fun refreshSkinLayers(player: Player) {
        player.location.world.players.forEach { it.sendPacket(player.metadataPacket) }
    }



    fun unregisterPlayer(player: Player) {
        oldTabPrefixSuffix.remove(player.uniqueId)
    }

    fun refreshPlayerSync(player: Player) {
        updateGameProfile(player)
        refreshPlayerTab(player, player.peUser.clientVersion, listOf(player))
        respawn(player)
        refreshForOthers(player)
    }
}