package me.onlyjordon.nicknamingapi.nms.v1_8_R3.util

import com.mojang.authlib.GameProfile
import me.onlyjordon.nicknamingapi.utils.ReflectionHelper
import net.minecraft.server.v1_8_R3.*
import org.apache.logging.log4j.core.util.ReflectionUtil
import org.bukkit.craftbukkit.v1_8_R3.CraftServer
import java.lang.reflect.Constructor
import java.lang.reflect.Field

class DisguiserHelper {
    companion object {

        private var PLAYER_INFO_LIST: Field? = null

        var scoreboardTeamConstructor: Constructor<PacketPlayOutScoreboardTeam>? = null

        fun setFields() {
            if (PLAYER_INFO_LIST == null) {
                try {
                    PLAYER_INFO_LIST = ReflectionHelper.getField("b", PacketPlayOutPlayerInfo::class.java)
                } catch (e: NoSuchFieldException) {
                    throw RuntimeException("Failed to get PlayerInfoData list", e)
                }
            }
            if (scoreboardTeamConstructor == null) {
                scoreboardTeamConstructor = PacketPlayOutScoreboardTeam::class.java.getDeclaredConstructor()
            }
        }

        fun EntityPlayer.playerInfoPacket(action: PacketPlayOutPlayerInfo.EnumPlayerInfoAction?): PacketPlayOutPlayerInfo {
            return PacketPlayOutPlayerInfo(
                action,
                this,
            )
        }

        fun PacketPlayOutPlayerInfo.setPlayerInfoData(
            entityPlayer: EntityPlayer,
            profile: GameProfile?
        ): PacketPlayOutPlayerInfo {
            ReflectionUtil.setFieldValue(
                PLAYER_INFO_LIST, this,
                listOf(
                    PlayerInfoData(
                        profile,
                        entityPlayer.ping,
                        entityPlayer.playerInteractManager.gameMode,
                        null
                    )
                )
            )
            return this
        }

        fun EntityPlayer.getSetMetadataPacket(): PacketPlayOutEntityMetadata {
            return PacketPlayOutEntityMetadata(id, dataWatcher, true)
        }


        val EntityPlayer.worldServer: WorldServer
            get() = this.u()

        fun EntityPlayer.updateClient() {
            this.updateAbilities()
            this.playerConnection.sendPacket(
                PacketPlayOutUpdateHealth(
                    this.health,
                    this.foodData.getFoodLevel(),
                    this.foodData.getSaturationLevel()
                )
            )
            this.playerConnection.sendPacket(
                PacketPlayOutExperience(
                    this.exp,
                    this.expTotal,
                    this.expLevel
                )
            )
            (this.bukkitEntity.server as CraftServer).handle.updateClient(this)
            this.triggerHealthUpdate()
        }

        fun EntityPlayer.destroyPacket(): PacketPlayOutEntityDestroy {
            return PacketPlayOutEntityDestroy(this.id)
        }

        fun EntityPlayer.playerSpawnPacket(): PacketPlayOutNamedEntitySpawn {
            return PacketPlayOutNamedEntitySpawn(this)
        }
    }

}