package me.onlyjordon.swiftdisguise.extensions

import com.github.retrooper.packetevents.PacketEvents
import com.github.retrooper.packetevents.manager.server.ServerVersion
import com.github.retrooper.packetevents.protocol.entity.data.EntityData
import com.github.retrooper.packetevents.protocol.entity.data.EntityDataTypes
import com.github.retrooper.packetevents.protocol.player.ClientVersion
import com.github.retrooper.packetevents.protocol.player.GameMode
import com.github.retrooper.packetevents.protocol.player.User
import com.github.retrooper.packetevents.protocol.player.UserProfile
import com.github.retrooper.packetevents.protocol.world.Difficulty
import com.github.retrooper.packetevents.protocol.world.Dimension
import com.github.retrooper.packetevents.protocol.world.Location
import com.github.retrooper.packetevents.wrapper.PacketWrapper
import com.github.retrooper.packetevents.wrapper.play.server.*
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerChangeGameState.Reason
import io.github.retrooper.packetevents.util.SpigotConversionUtil
import me.onlyjordon.swiftdisguise.nms.CrossVersionPlayerHelper
import org.bukkit.Bukkit
import org.bukkit.World
import org.bukkit.World.Environment
import org.bukkit.entity.Entity
import org.bukkit.entity.Player
import java.util.EnumSet

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
            peUser.profile.name = value.name
            peUser.profile.uuid = value.uuid
            peUser.profile.textureProperties.clear()
            peUser.profile.textureProperties.addAll(value.textureProperties)
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

    private fun setDimension(env: Environment, dim: Dimension) {
        when (env) {
            Environment.NETHER -> dim.dimensionName = "the_nether"
            Environment.THE_END -> dim.dimensionName = "the_end"
            Environment.NORMAL -> dim.dimensionName = "overworld"
            else -> dim.dimensionName = "overworld"
        }
    }

    private val World.peDimension: Dimension
        get() = SpigotConversionUtil.fromBukkitWorld(this)

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
            EnumSet.allOf(WrapperPlayServerPlayerInfoUpdate.Action::class.java), WrapperPlayServerPlayerInfoUpdate.PlayerInfo(
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

    protected val Player.metadataPacket: WrapperPlayServerEntityMetadata
        get() = WrapperPlayServerEntityMetadata(entityId, listOf(EntityData(CrossVersionPlayerHelper.getSkinLayersIndex(PacketEvents.getAPI().serverManager.version), EntityDataTypes.BYTE, 0.toByte()))) // the 0 will be replaced with the correct value in SpigotPacketListener


    protected val Entity.deleteEntityPacket: PacketWrapper<*>
        get() = WrapperPlayServerDestroyEntities(entityId)

    protected val Player.legacySpawnPlayerPacket: PacketWrapper<*>
        get() = WrapperPlayServerSpawnPlayer(entityId, uniqueId, peLocation)

    protected val Player.peLocation: Location
        get() = location.toPacketEvents()

    protected val Player.teleportPacket: PacketWrapper<*>
        get() = WrapperPlayServerEntityTeleport(entityId, peLocation, isOnGround)

    private fun getAnyOtherWorld(world: World): World {
        return Bukkit.getWorlds().firstOrNull { it.uid != world.uid } ?: world
    }

    protected fun Player.teleportToOtherDimensionAndBack() {
        val loc = location
        val otherDimLoc = loc.clone();
        otherDimLoc.world = getAnyOtherWorld(world)
        teleport(otherDimLoc)
        teleport(loc)
    }

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