package me.onlyjordon.swiftdisguise

import com.github.retrooper.packetevents.PacketEvents
import com.github.retrooper.packetevents.manager.server.ServerVersion
import com.github.retrooper.packetevents.protocol.player.ClientVersion
import com.github.retrooper.packetevents.protocol.player.GameMode
import com.github.retrooper.packetevents.protocol.player.TextureProperty
import com.github.retrooper.packetevents.protocol.player.UserProfile
import com.github.retrooper.packetevents.util.Vector3i
import com.github.retrooper.packetevents.wrapper.play.server.*
import me.onlyjordon.swiftdisguise.api.DisguiseData
import me.onlyjordon.swiftdisguise.api.ISwiftDisguiseAPI
import me.onlyjordon.swiftdisguise.api.ITabPrefixSuffix
import me.onlyjordon.swiftdisguise.api.SwiftDisguise
import me.onlyjordon.swiftdisguise.api.SwiftDisguiseConfig
import me.onlyjordon.swiftdisguise.extensions.PacketExtensions
import me.onlyjordon.swiftdisguise.nms.CrossVersionPlayerHelper
import me.onlyjordon.swiftdisguise.nms.RespawnNMS
import me.onlyjordon.swiftdisguise.utils.Util.isPaper
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.Bukkit
import org.bukkit.Bukkit.getLogger
import org.bukkit.Bukkit.isPrimaryThread
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin
import java.util.*


class PlayerRefresher(private val api: ISwiftDisguiseAPI, private val plugin: JavaPlugin): PacketExtensions() {
    private val oldTabPrefixSuffix = HashMap<UUID, ITabPrefixSuffix>()
    var respawnMethod: RespawnMethod

    init {
        respawnMethod = generateRespawnMethod()
        getLogger().info("Using respawn method ${respawnMethod.name}")
    }

    fun refreshSelf(player: Player) {
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


    /**
     * Get the respawn method to use
     * @param exclusions methods to exclude (PACKET_EVENTS cannot be excluded)
     * @return the respawn method to use
     */
    fun generateRespawnMethod(vararg exclusions: RespawnMethod): RespawnMethod {
        return when {
            !exclusions.contains(RespawnMethod.PAPER) && isPaper() -> RespawnMethod.PAPER
            !exclusions.contains(RespawnMethod.REFLECTION) && try { RespawnNMS.isSupported() } catch (e: Exception) { e.printStackTrace(); false } -> RespawnMethod.REFLECTION
            else -> RespawnMethod.PACKET_EVENTS
        }
    }

    enum class RespawnMethod {
        REFLECTION,
        PAPER,
        PACKET_EVENTS
    }

    private fun respawn(player: Player, method: RespawnMethod = respawnMethod) {
        getLogger().info("Refreshing player ${player.name} using respawn method ${respawnMethod.name}")
        if (!isPrimaryThread()) {
            runPrimaryThread {
                respawn(player)
            }
            return
        }
        val loc = player.location.clone()

        val playerIsLegacy = player.peUser.clientVersion.isOlderThan(ClientVersion.V_1_16)
        val serverIsLegacy = PacketEvents.getAPI().serverManager.version.isOlderThan(ServerVersion.V_1_16)
        var method = method
        if (serverIsLegacy) {
            respawnMethod = RespawnMethod.PACKET_EVENTS
            method = respawnMethod
        }

        if (method == RespawnMethod.PAPER && !playerIsLegacy) {
            try {
                val refresh = CrossVersionPlayerHelper.getCraftPlayerClass().getDeclaredMethod("refreshPlayer")
                refresh.isAccessible = true
                refresh.invoke(player)
                return
            } catch (ignored: ClassNotFoundException) {
            } catch (ignored: NoSuchMethodException) {
            } catch (ignored: IllegalAccessException) {
            } catch (ignored: java.lang.reflect.InvocationTargetException) {
                throw RuntimeException("Failed to refresh player with Paper!", ignored)
            }
        }
        if (method == RespawnMethod.PAPER && playerIsLegacy) {
            val rs = generateRespawnMethod(RespawnMethod.PAPER) // Paper's refreshPlayer breaks for legacy players
            respawn(player, rs)
        }

        if (method == RespawnMethod.REFLECTION) {
            try {
                RespawnNMS.nmsRespawn(player) // try respawning with nms
            } catch (ex: Exception) {
                respawnMethod = RespawnMethod.PACKET_EVENTS
                respawn(player)
                throw RuntimeException("Failed to refresh player with Reflection!", ex)
            }
        }

        if (method == RespawnMethod.PACKET_EVENTS) {
            if (!ViaBackwardsFixer.sendRespawnPacketWithVia(
                    player,
                    0L,
                    player.peGameMode.id.toShort(),
                    false
                )
            ) player.sendPacket(player.respawnPacket)
            val l = player.location.clone()
            player.sendPacket(player.teleportPacket)
            player.teleport(player)
            player.teleport(l)
            player.exp = player.exp
            player.health = player.health
            player.healthScale = player.healthScale
            player.foodLevel = player.foodLevel
            player.saturation = player.saturation
            player.allowFlight = player.allowFlight
            if (player.compassTarget != null)
                player.compassTarget = player.compassTarget
            player.exhaustion = player.exhaustion
            player.level = player.level

            val loc = player.world.spawnLocation
            player.sendPacket(WrapperPlayServerTimeUpdate(player.world.fullTime, player.world.time))
            player.sendPacket(WrapperPlayServerSpawnPosition(Vector3i(loc.blockX, loc.blockY, loc.blockZ), 0f))
            player.sendPacket(refreshChunksPacket)
            player.sendPacket(WrapperPlayServerUpdateViewDistance(Bukkit.getServer().viewDistance))
//                player.sendPacket(WrapperPlayServerPlayerAbilities(player.allowFlight, player.isFlying, player.isFlying, player.isFlying, player.flySpeed, player.walkSpeed))
            player.updateInventory()
            player.sendPacket(WrapperPlayServerHeldItemChange(player.inventory.heldItemSlot))
        }

        player.teleport(loc)
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

    fun removeUUID(disguiseData: DisguiseData?, player: Player) {
//        val modernChange = WrapperPlayServerPlayerInfoUpdate(WrapperPlayServerPlayerInfoUpdate.Action.UPDATE_LISTED, PlayerInfo(UserProfile(fakeUUID, fakeName), false, 0, GameMode.defaultGameMode(), null, null))
        val isNull = disguiseData == null
        val disguiseData = disguiseData ?: api.getDisguiseData(player)

        val modernRemove = WrapperPlayServerPlayerInfoRemove(disguiseData.fakeUUID, UUID.fromString("00000000-0000-0000-0000-000000000000"))
        val serverVersion = PacketEvents.getAPI().serverManager.version
        if (serverVersion.isNewerThan(ServerVersion.V_1_19_2)) {
            Bukkit.getOnlinePlayers().forEach { it.sendPacket(modernRemove) }
        }
        player.sendMessage("Refreshing with uuid ${disguiseData.fakeUUID} and name ${disguiseData.fakeName}, isNull $isNull and uuid to remove is ${disguiseData.fakeUUID}")
        val oldRemove = WrapperPlayServerPlayerInfo(
            WrapperPlayServerPlayerInfo.Action.REMOVE_PLAYER,
            WrapperPlayServerPlayerInfo.PlayerData(null,
                UserProfile(if (isNull) disguiseData.realUUID else disguiseData.fakeUUID, if (isNull) disguiseData.realName else disguiseData.fakeName),
                GameMode.defaultGameMode(),
                0
            ),
            WrapperPlayServerPlayerInfo.PlayerData(null,
                UserProfile(UUID.fromString("00000000-0000-0000-0000-000000000000"), "name"),
                GameMode.defaultGameMode(),
                0
            )
        )
        Bukkit.getOnlinePlayers().forEach { it.sendPacket(oldRemove) }

    }
}