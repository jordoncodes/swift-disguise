package me.onlyjordon.swiftdisguise.spigot

import com.github.retrooper.packetevents.protocol.player.ClientVersion
import com.github.retrooper.packetevents.protocol.player.TextureProperty
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerTeams
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerTeams.ScoreBoardTeamInfo
import io.github.retrooper.packetevents.util.SpigotConversionUtil
import me.onlyjordon.swiftdisguise.api.ITabPrefixSuffix
import me.onlyjordon.swiftdisguise.api.SwiftDisguise
import me.onlyjordon.swiftdisguise.api.SwiftDisguiseConfig
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.Bukkit
import org.bukkit.Bukkit.isPrimaryThread
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin
import java.util.*
import kotlin.collections.HashMap

class PlayerRefresher(private val plugin: JavaPlugin, private val disguiser: SwiftDisguiseSpigot): PacketExtensions() {

    private val oldTabPrefixSuffix = HashMap<UUID, ITabPrefixSuffix>()

    /**
     * Refreshes the player's name and skin for only themselves.
     * @see refreshForOthers
     * @see refreshPlayer
     */
    fun refreshSelf(player: Player) {
        if (isPrimaryThread()) {
            runAsyncThread { refreshSelf(player) }
            return
        }
        runPrimaryThread {
            if (!player.isOnline) return@runPrimaryThread
            val profile = player.peProfile
            val skin = disguiser.getDisguiseSkin(player)
            profile.textureProperties.add(TextureProperty("textures", skin.value, skin.signature))
            if (SwiftDisguise.getConfig().nameMode() != SwiftDisguiseConfig.NameMode.WEAK) {
                profile.name = disguiser.getDisguiseName(player)
                profile.uuid = player.uniqueId
                profile.textureProperties.clear()
            }
            player.peProfile = profile // sets the nms GameProfile of the player
        }

        refreshPlayerTab(player, player.peUser.clientVersion, listOf(player))
        respawn(player)
    }

    fun unregisterPlayer(player: Player) {
        oldTabPrefixSuffix.remove(player.uniqueId)
    }

    /**
     * Refreshes the player's name and skin for all players in the world.
     * @see refreshSelf
     * @see refreshPlayer
     */
    fun refreshForOthers(player: Player) {
        refreshNameSkinForOthers(player);
    }

    fun refreshSkinLayers(player: Player) {
        player.location.world.players.forEach { it.sendPacket(player.metadataPacket) }
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

            playerVersion.ifNewerThan(ClientVersion.V_1_19_3) {
                if (player == it)
                    player.sendPacket(player.playerInfoUpdateListedPacketFromServerVersion)
            }
        }
    }

    private fun respawn(player: Player) {
        if (!ViaBackwardsFixer.sendRespawnPacketWithVia(player, 0L, player.peGameMode.id.toShort(), false)) player.sendPacket(player.respawnPacket)
        player.sendPacket(player.teleportPacket)
        runPrimaryThread {
            if (player.isOnline)
                player.teleport(player)
            player.sendPacket(refreshChunksPacket)
        }
    }

    fun refreshPlayer(player: Player, force: Boolean = false) {
        refreshSelf(player)
        refreshForOthers(player)
        if (force || !Objects.equals(oldTabPrefixSuffix[player.uniqueId], disguiser.getDisguisePrefixSuffix(player))) {
            refreshPrefixSuffix(player)
        }
    }

    fun refreshPrefixSuffix(player: Player) {
        val prefixSuffix = disguiser.getDisguisePrefixSuffix(player)

        // is there a better way of doing this?
        val colorField = NamedTextColor::class.java.getDeclaredField(prefixSuffix.color.toString())
        val color = colorField.get(null) as NamedTextColor

        val teamInfo = ScoreBoardTeamInfo(Component.empty(), prefixSuffix.prefix, prefixSuffix.suffix, WrapperPlayServerTeams.NameTagVisibility.ALWAYS, WrapperPlayServerTeams.CollisionRule.NEVER, color, WrapperPlayServerTeams.OptionData.FRIENDLY_FIRE)
        Bukkit.getOnlinePlayers().forEach {
            if (oldTabPrefixSuffix.containsKey(player.uniqueId)) {
                val oldPrefixSuffix = oldTabPrefixSuffix[player.uniqueId]!!
                val oldTeamInfo = ScoreBoardTeamInfo(Component.empty(), oldPrefixSuffix.prefix, oldPrefixSuffix.suffix, WrapperPlayServerTeams.NameTagVisibility.ALWAYS, WrapperPlayServerTeams.CollisionRule.NEVER, color, WrapperPlayServerTeams.OptionData.FRIENDLY_FIRE)
                val deleteOldTeam = WrapperPlayServerTeams((oldPrefixSuffix.priority.toChar()+player.uniqueId.toString().substring(0, 16)), WrapperPlayServerTeams.TeamMode.REMOVE, oldTeamInfo, disguiser.getDisguiseName(player))
                it.sendPacket(deleteOldTeam)
            }
            val newTeam = WrapperPlayServerTeams((prefixSuffix.priority.toChar()+player.uniqueId.toString().substring(0, 16)), WrapperPlayServerTeams.TeamMode.CREATE, teamInfo, disguiser.getDisguiseName(player))
            it.sendPacket(newTeam)
        }
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
}