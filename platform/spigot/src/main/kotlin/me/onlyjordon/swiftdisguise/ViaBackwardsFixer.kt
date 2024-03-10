package me.onlyjordon.swiftdisguise

import com.github.retrooper.packetevents.PacketEvents
import com.github.retrooper.packetevents.manager.server.ServerVersion
import com.github.retrooper.packetevents.protocol.player.ClientVersion
import com.viaversion.viabackwards.protocol.protocol1_15_2to1_16.Protocol1_15_2To1_16
import com.viaversion.viaversion.api.Via
import com.viaversion.viaversion.api.connection.UserConnection
import com.viaversion.viaversion.api.protocol.packet.PacketWrapper
import com.viaversion.viaversion.api.type.Type
import com.viaversion.viaversion.protocols.protocol1_15to1_14_4.ClientboundPackets1_15
import org.bukkit.Bukkit
import org.bukkit.entity.Player


object ViaBackwardsFixer {

    /**
     * used to avoid Via sending a second respawn packet to the player
     * @return true if the packet has been sent with Via, else false
     */
    fun sendRespawnPacketWithVia(player: Player, seed: Long, gamemode: Short, isFlat: Boolean): Boolean {
        val shouldUseVia = Bukkit.getPluginManager().getPlugin("ViaVersion") != null && Bukkit.getPluginManager().getPlugin("ViaBackwards") != null
        val isClientOld = PacketEvents.getAPI().playerManager.getClientVersion(player).isOlderThan(ClientVersion.V_1_16)
        val isServerOld = PacketEvents.getAPI().serverManager.version.isOlderThan(ServerVersion.V_1_16)
        if (shouldUseVia && isClientOld && !isServerOld) {
            sendRespawnPacketWithViaImpl(player, seed, gamemode, isFlat)
            println("sending packet with Via* to ${player.name} who is on version ${PacketEvents.getAPI().playerManager.getClientVersion(player)}")
            return true
        } else {
            return false
        }
    }


    private fun sendRespawnPacketWithViaImpl(player: Player, seed: Long, gamemode: Short, isFlat: Boolean) {
        val connection: UserConnection? = Via.getManager().connectionManager.getConnectedClient(player.uniqueId)
        val packet: PacketWrapper = PacketWrapper.create(ClientboundPackets1_15.RESPAWN, connection)

        packet.write(Type.INT, player.world.environment.id)
        packet.write(Type.LONG, seed)
        packet.write(Type.UNSIGNED_BYTE, gamemode)
        packet.write(Type.STRING, if (isFlat) "flat" else "default")
        try {
            packet.send(Protocol1_15_2To1_16::class.java)
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }
}