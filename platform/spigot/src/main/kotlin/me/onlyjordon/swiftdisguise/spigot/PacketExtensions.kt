package me.onlyjordon.swiftdisguise.spigot

import com.github.retrooper.packetevents.PacketEvents
import com.github.retrooper.packetevents.manager.server.ServerVersion
import com.github.retrooper.packetevents.protocol.entity.data.EntityData
import com.github.retrooper.packetevents.protocol.entity.data.EntityDataType
import com.github.retrooper.packetevents.protocol.entity.data.EntityDataTypes
import com.github.retrooper.packetevents.protocol.player.ClientVersion
import com.github.retrooper.packetevents.protocol.player.GameMode
import com.github.retrooper.packetevents.protocol.player.User
import com.github.retrooper.packetevents.protocol.player.UserProfile
import com.github.retrooper.packetevents.protocol.world.Difficulty
import com.github.retrooper.packetevents.protocol.world.Dimension
import com.github.retrooper.packetevents.protocol.world.Location
import com.github.retrooper.packetevents.wrapper.PacketWrapper
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerChangeGameState
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerChangeGameState.Reason
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerDestroyEntities
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerEntityMetadata
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerEntityTeleport
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerPlayerInfo
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerPlayerInfoRemove
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerPlayerInfoUpdate
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerRespawn
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerSpawnPlayer
import io.github.retrooper.packetevents.util.SpigotConversionUtil
import me.onlyjordon.swiftdisguise.spigot.nms.CrossVersionPlayerHelper
import org.bukkit.World
import org.bukkit.World.Environment
import org.bukkit.entity.Entity
import org.bukkit.entity.Player

open class PacketExtensions {
    protected fun Player.sendPacket(packet: PacketWrapper<*>?) {
        if (packet == null) return
        PacketEvents.getAPI().playerManager.sendPacket(this, packet)
    }

    protected val Player.peUser: User
        get() = PacketEvents.getAPI().playerManager.getUser(this)

    protected var Player.peProfile: UserProfile
        get() = peUser.profile
        set(value) {
            val profile = peUser.profile
            profile.name = value.name
            profile.uuid = value.uuid
            profile.textureProperties.clear()
            profile.textureProperties.addAll(value.textureProperties)
            CrossVersionPlayerHelper.setGameProfile(player, profile)
            CrossVersionPlayerHelper.updateNameOnServer(value.name, player)
        }

    protected var Player.peGameMode: GameMode
        get() = SpigotConversionUtil.fromBukkitGameMode(gameMode)
        set(value) {
            gameMode = SpigotConversionUtil.toBukkitGameMode(value)
        }

    protected val World.peDifficulty: Difficulty
        get() = Difficulty.valueOf(difficulty.toString())

    private fun getDimension(player: Player): Dimension {
        val peDimension = player.world.peDimension
        val env = player.world.environment
        val user = player.peUser
        if (peDimension.dimensionName.isBlank()) {
            val dim = if (user.clientVersion.isNewerThan(ClientVersion.V_1_19)) {
                Dimension(0)
            } else {
                Dimension(env.id)
            }
            setDimension(env, dim)
            return dim
        }
        return peDimension
    }

    protected val Player.metadataPacket: WrapperPlayServerEntityMetadata
        get() = WrapperPlayServerEntityMetadata(entityId, listOf(EntityData(CrossVersionPlayerHelper.getSkinLayersIndex(PacketEvents.getAPI().serverManager.version), EntityDataTypes.BYTE, 0.toByte()))) // the 0 will be replaced with the correct value in SpigotPacketListener

    private fun setDimension(env: Environment, dim: Dimension) {
        when (env) {
            Environment.NETHER -> dim.dimensionName = "the_nether"
            Environment.THE_END -> dim.dimensionName = "the_end"
            Environment.NORMAL -> dim.dimensionName = "overworld"
            else -> dim.dimensionName = "overworld"
        }
    }

    private val World.peDimension: Dimension
        get() = try { SpigotConversionUtil.fromBukkitWorld(this) } catch (e: Exception) { Dimension(0) } // need to catch exceptions, because SpigotConversionUtil breaks on some versions

    protected val Player.peDimension: Dimension
        get() = getDimension(this)
    protected val Player.pingInMs: Int
        get() = CrossVersionPlayerHelper.getPing(this)

    protected val Player.playerInfoAddPacket: PacketWrapper<*>
        get() = WrapperPlayServerPlayerInfo(
            WrapperPlayServerPlayerInfo.Action.ADD_PLAYER, WrapperPlayServerPlayerInfo.PlayerData(
                null, peProfile, peGameMode, pingInMs
            )
        )

    protected val Player.playerInfoAddPacketFromServerVersion: PacketWrapper<*>
        get() = if (PacketEvents.getAPI().serverManager.version.isOlderThan(ServerVersion.V_1_19_4)) {
            playerInfoAddPacket
        } else {
            modernPlayerInfoAddPacket
        }

    protected val Player.playerInfoRemovePacketFromServerVersion: PacketWrapper<*>
        get() = if (PacketEvents.getAPI().serverManager.version.isOlderThan(ServerVersion.V_1_19_4)) {
            playerInfoRemovePacket
        } else {
            modernPlayerInfoRemovePacket
        }

    protected val Player.playerInfoUpdateListedPacketFromServerVersion: PacketWrapper<*>?
        get() = if (PacketEvents.getAPI().serverManager.version.isOlderThan(ServerVersion.V_1_19_4)) {
            null
        } else {
            modernPlayerInfoListPacket
        }

    protected val Player.playerInfoRemovePacket: PacketWrapper<*>
        get() = WrapperPlayServerPlayerInfo(
            WrapperPlayServerPlayerInfo.Action.REMOVE_PLAYER, WrapperPlayServerPlayerInfo.PlayerData(
                null, peProfile, peGameMode, pingInMs
            )
        )

    protected val Player.modernPlayerInfoAddPacket: PacketWrapper<*>
        get() = WrapperPlayServerPlayerInfoUpdate(
            WrapperPlayServerPlayerInfoUpdate.Action.ADD_PLAYER, WrapperPlayServerPlayerInfoUpdate.PlayerInfo(
                peProfile, true, pingInMs, peGameMode, null, null
            )
        )

    protected val Player.modernPlayerInfoRemovePacket: PacketWrapper<*>
        get() = WrapperPlayServerPlayerInfoRemove(uniqueId)

    protected val Player.modernPlayerInfoListPacket: PacketWrapper<*>
        get() = WrapperPlayServerPlayerInfoUpdate(
            WrapperPlayServerPlayerInfoUpdate.Action.UPDATE_LISTED, WrapperPlayServerPlayerInfoUpdate.PlayerInfo(
                peProfile, true, pingInMs, peGameMode, null, null
            )
        )

    protected val Player.respawnPacket: PacketWrapper<*>
        get() = WrapperPlayServerRespawn(
            peDimension,
            world.name,
            world.peDifficulty,
            0,
            peGameMode,
            peGameMode,
            false,
            false,
            true,
            null,
            null,
            0
        )

    protected val Entity.deleteEntityPacket: PacketWrapper<*>
        get() = WrapperPlayServerDestroyEntities(entityId)

    protected val Player.legacySpawnPlayerPacket: PacketWrapper<*>
        get() = WrapperPlayServerSpawnPlayer(entityId, uniqueId, peLocation)

    protected val Player.peLocation: Location
        get() = location.toPacketEvents()

    protected val Player.teleportPacket: PacketWrapper<*>
        get() = WrapperPlayServerEntityTeleport(entityId, peLocation, isOnGround)

    protected val refreshChunksPacket: PacketWrapper<*>
        get() = WrapperPlayServerChangeGameState(Reason.START_LOADING_CHUNKS, 0f)

    protected fun org.bukkit.Location.toPacketEvents(): Location {
        return Location(x, y, z, yaw, pitch)
    }

    protected fun ClientVersion.ifNewerThan(version: ClientVersion, block: () -> Unit) {
        if (this.isNewerThan(version)) {
            block()
        }
    }

    protected fun ClientVersion.ifOlderThan(version: ClientVersion, block: () -> Unit) {
        if (this.isOlderThan(version)) {
            block()
        }
    }
}