package me.onlyjordon.nicknamingapi.nms.v1_8_R3.util

import PlayerExtensions.Companion.getNick
import com.github.retrooper.packetevents.PacketEvents
import com.github.retrooper.packetevents.event.PacketListener
import com.github.retrooper.packetevents.event.PacketListenerPriority
import com.github.retrooper.packetevents.event.PacketReceiveEvent
import com.github.retrooper.packetevents.event.PacketSendEvent
import com.github.retrooper.packetevents.protocol.packettype.PacketType
import com.github.retrooper.packetevents.protocol.player.TextureProperty
import com.github.retrooper.packetevents.protocol.player.UserProfile
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientSettings
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerEntityMetadata
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerPlayerInfo
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerSpawnPlayer
import com.mojang.authlib.GameProfile
import com.mojang.authlib.properties.Property
import me.onlyjordon.nicknamingapi.NickData
import me.onlyjordon.nicknamingapi.Nicknamer
import me.onlyjordon.nicknamingapi.nms.v1_8_R3.util.DisguiserHelper.destroyPacket
import me.onlyjordon.nicknamingapi.nms.v1_8_R3.util.DisguiserHelper.getSetMetadataPacket
import me.onlyjordon.nicknamingapi.nms.v1_8_R3.util.DisguiserHelper.playerInfoPacket
import me.onlyjordon.nicknamingapi.nms.v1_8_R3.util.DisguiserHelper.playerSpawnPacket
import me.onlyjordon.nicknamingapi.nms.v1_8_R3.util.DisguiserHelper.setFields
import me.onlyjordon.nicknamingapi.nms.v1_8_R3.util.DisguiserHelper.setPlayerInfoData
import me.onlyjordon.nicknamingapi.nms.v1_8_R3.util.DisguiserHelper.updateClient
import me.onlyjordon.nicknamingapi.nms.v1_8_R3.util.DisguiserHelper.worldServer
import me.onlyjordon.nicknamingapi.utils.ReflectionHelper
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.TextComponent
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer
import net.minecraft.server.v1_8_R3.*
import net.minecraft.server.v1_8_R3.PacketPlayOutPlayerInfo.EnumPlayerInfoAction
import net.minecraft.server.v1_8_R3.ScoreboardTeamBase.EnumNameTagVisibility
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.World
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerChangedWorldEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.plugin.java.JavaPlugin
import org.jetbrains.annotations.ApiStatus.Internal
import java.util.*

@Internal
@Suppress("unused")
class Disguiser : PacketListener,Listener, Nicknamer() {

    private val fakeUUIDs = WeakHashMap<Player, UUID>()
    private val prefixSuffix = WeakHashMap<Player, PacketPlayOutScoreboardTeam>()

    private fun getFakeUUID(player: Player): UUID {
        val id = fakeUUIDs.getOrDefault(player, UUID.randomUUID())
        fakeUUIDs[player] = id
        return id
    }

    override fun disable() {
        PacketEvents.getAPI().terminate()
    }

    override fun getNick(player: Player): String {
        return data[player.uniqueId]?.nickname ?: player.name
    }

    override fun getPlayerWithNick(nick: String): Player? {
        return data.entries.firstOrNull { it.value.nickname.lowercase() == nick.lowercase() }?.key?.let {Bukkit.getPlayer(it)}
    }

    override fun setup() {
        PacketEvents.getAPI().eventManager.registerListener(this, PacketListenerPriority.LOW)
        PacketEvents.getAPI().init()
    }

    override fun getSkin(player: Player): me.onlyjordon.nicknamingapi.utils.Skin {
        return data[player.uniqueId]?.currentSkin ?: data[player.uniqueId]?.originalSkin ?: me.onlyjordon.nicknamingapi.utils.Skin(
            null,
            null
        )
    }

    override fun getSkin(skinName: String): me.onlyjordon.nicknamingapi.utils.Skin {
        return me.onlyjordon.nicknamingapi.utils.Skin.getSkin(skinName)
    }


    @EventHandler(priority = EventPriority.LOWEST)
    fun onJoin(e: PlayerJoinEvent) {
        val player = e.player
        val skin = (player as CraftPlayer).handle.profile.properties["textures"].firstOrNull()?.let {
            me.onlyjordon.nicknamingapi.utils.Skin(
                it.value,
                it.signature
            )
        } ?: me.onlyjordon.nicknamingapi.utils.Skin(null, null)
        data[player.uniqueId] = NickData(
            skin,
            player.name,
            Component.text(""),
            Component.text(""),
            null,
            UUID.randomUUID()
        )
        prefixSuffix.values.forEach {
            player.handle.playerConnection.sendPacket(it)
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    fun onQuit(e: PlayerQuitEvent) {
        if (plugin.isEnabled) {
            // to allow for info remove packet to be altered
            Bukkit.getServer().scheduler.runTaskLater(plugin, {
                if (!e.player.isOnline)
                    data.remove(e.player.uniqueId)
            }, 2L)
        }
        getPrefixSuffixPacket(e.player, remove = true).let { packet ->
            Bukkit.getOnlinePlayers().forEach {
                (it as CraftPlayer).handle.playerConnection.sendPacket(packet)
            }
        }
        prefixSuffix.remove(e.player)
    }


    private fun getPrefixSuffixPacket(player: Player, prefix: TextComponent = Component.text(""), suffix: TextComponent = Component.text(""), textColor: ChatColor = ChatColor.WHITE, remove: Boolean = false, priority: Int = 0): PacketPlayOutScoreboardTeam {
        val packet = PacketPlayOutScoreboardTeam()
        ReflectionHelper.setFieldValue("a", priority.toChar()+ player.uniqueId.toString().replace('-', Character.MIN_VALUE).substring(0,16), packet) // team name
        ReflectionHelper.setFieldValue("b", priority.toChar()+ player.uniqueId.toString().replace('-', Character.MIN_VALUE).substring(0,16), packet) // team display name
        ReflectionHelper.setFieldValue("c", LegacyComponentSerializer.legacySection().serialize(prefix), packet) // prefix
        ReflectionHelper.setFieldValue("d", LegacyComponentSerializer.legacySection().serialize(suffix), packet) // suffix
        ReflectionHelper.setFieldValue("e", EnumNameTagVisibility.ALWAYS.e, packet) // nametag visibility
        ReflectionHelper.setFieldValue("f", EnumChatFormat.valueOf(textColor.name).b(), packet) // team color
        ReflectionHelper.setFieldValue("g", mutableListOf(player.getNick()) as Collection<String>, packet) // players
        ReflectionHelper.setFieldValue("h", if (remove) 1 else 0, packet) // mode: 0 = add; 1 = remove; 2 = update; 3 = new players; 4 = players removed
        ReflectionHelper.setFieldValue("i", 1, packet) // options: 1 = friendly fire; 2 = see friendly invisibles; 3 = both

        return packet
    }

    @EventHandler(priority = EventPriority.LOWEST)
    fun onWorldChange(e: PlayerChangedWorldEvent) {
        refreshPlayer(e.player)
    }

    private val plugin = JavaPlugin.getProvidingPlugin(this.javaClass)

    private val Player.gameProfile: GameProfile
        get() = GameProfile(player.uniqueId, player.name)

    override fun refreshPlayer(player: Player) { // this mess works
        setFields()
        val craftPlayer = player as CraftPlayer
        val entityPlayer = craftPlayer.handle
        val location = player.getLocation().clone()
        location.pitch = player.getLocation().pitch
        location.yaw = player.getLocation().yaw

        val removePacket =  entityPlayer.playerInfoPacket(EnumPlayerInfoAction.REMOVE_PLAYER)
        val addPacket = entityPlayer.playerInfoPacket(EnumPlayerInfoAction.ADD_PLAYER)
            .setPlayerInfoData(entityPlayer, player.gameProfile)
        player.world.players.forEach {
            (it as CraftPlayer).handle.playerConnection.sendPacket(removePacket)
            if (it.uniqueId == player.uniqueId) return@forEach
            it.handle.playerConnection.sendPacket(entityPlayer.destroyPacket())
        }
        Bukkit.getOnlinePlayers().forEach { other ->
            prefixSuffix[player]?.let { (other as CraftPlayer).handle.playerConnection.sendPacket(it) }
        }
        player.world.players.forEach {
            (it as CraftPlayer).handle.playerConnection.sendPacket(entityPlayer.getSetMetadataPacket())
            it.handle.playerConnection.sendPacket(addPacket)
            if (it.uniqueId == player.uniqueId) return@forEach
            it.handle.playerConnection.sendPacket(entityPlayer.playerSpawnPacket())
        }
        val worldServer = entityPlayer.worldServer

        worldServer.playerChunkMap.removePlayer(entityPlayer)


        val dim = worldServer.world.environment.id
        val playerList = MinecraftServer.getServer().playerList
        val newEnv = World.Environment.entries[dim+2 % World.Environment.entries.size]
        playerList.moveToWorld(entityPlayer, newEnv.id, true)
        entityPlayer.playerConnection.sendPacket(
            PacketPlayOutRespawn(
                dim,
                worldServer.difficulty,
                worldServer.getWorldData().type,
                entityPlayer.playerInteractManager.gameMode
            )
        )

        entityPlayer.worldServer.playerChunkMap.addPlayer(entityPlayer)
        playerList.updateClient(entityPlayer)
        entityPlayer.updateClient()
        player.teleport(location)
    }

    override fun setSkin(player: Player, skin: me.onlyjordon.nicknamingapi.utils.Skin): Boolean {
        val data = data[player.uniqueId] ?: return false
        data.currentSkin = skin
        val profile = (player as CraftPlayer).profile
        profile.properties.clear()
        profile.properties.put("textures", Property("textures", skin.value, skin.signature))
        val gameProfileField = ReflectionHelper.getField("bH", EntityHuman::class.java)
        gameProfileField.isAccessible = true
        gameProfileField.set(player.handle, profile)
        return true
    }

    override fun setSkin(player: Player, name: String) {
        val skin = me.onlyjordon.nicknamingapi.utils.Skin.getSkin(name)
        if (skin != null) {
            setSkin(player, skin)
        }
    }

    override fun setSkinLayerVisible(player: Player, layer: me.onlyjordon.nicknamingapi.utils.SkinLayers.SkinLayer, visible: Boolean) {
        data[player.uniqueId]?.skinLayers?.setLayerVisible(layer, visible)
    }
    override fun isSkinLayerVisible(player: Player, layer: me.onlyjordon.nicknamingapi.utils.SkinLayers.SkinLayer): Boolean {
        return data[player.uniqueId]?.skinLayers?.isLayerVisible(layer) ?: true
    }

    override fun getSkinLayers(player: Player): me.onlyjordon.nicknamingapi.utils.SkinLayers {
        return data[player.uniqueId]?.skinLayers ?: me.onlyjordon.nicknamingapi.utils.SkinLayers.getFromRaw(0b0111111)
    }

    override fun setNick(player: Player, nick: String) {
        data[player.uniqueId]?.nickname = nick
    }

    override fun resetNick(player: Player) {
        data[player.uniqueId]?.nickname = null
    }

    override fun setPrefixSuffix(player: Player, prefix: TextComponent, suffix: TextComponent, textColor: ChatColor, priority: Int) {
        val reset = Component.text(ChatColor.RESET.toString()+textColor.toString())
        var pre = prefix
        var suf = suffix
        if (LegacyComponentSerializer.legacySection().serialize(prefix).length > 12) {
            pre = Component.text(LegacyComponentSerializer.legacySection().serialize(prefix).substring(0, 12))
        }
        if (LegacyComponentSerializer.legacySection().serialize(suffix).length > 12) {
            suf = Component.text(LegacyComponentSerializer.legacySection().serialize(prefix).substring(0, 12))
        }

        val packet = getPrefixSuffixPacket(player, pre.append(reset), reset.append(suf), textColor, remove = false, priority = priority)
        prefixSuffix[player] = packet
    }

    override fun updatePrefixSuffix(player: Player) {
        Bukkit.getOnlinePlayers().forEach {
            (it as CraftPlayer).handle.playerConnection.sendPacket(getPrefixSuffixPacket(player, remove = true))
            it.handle.playerConnection.sendPacket(prefixSuffix[player])
        }
    }

    override fun resetDisguise(player: Player) {
        resetSkin(player)
        resetNick(player)
        getPrefixSuffixPacket(player, remove = true).let { packet ->
            Bukkit.getOnlinePlayers().forEach {
                (it as CraftPlayer).handle.playerConnection.sendPacket(packet)
            }
        }
    }

    override fun onPacketSend(event: PacketSendEvent) {
        if (event.packetType == PacketType.Play.Server.PLAYER_INFO) {
            val wrapper = WrapperPlayServerPlayerInfo(event)
            wrapper.playerDataList.forEach {
                changeProfile(it.userProfile, !(event.player is Player && it.userProfile.uuid == (event.player as Player).uniqueId))
            }
        }
        if (event.packetType == PacketType.Play.Server.SPAWN_PLAYER) {
            val wrapper = WrapperPlayServerSpawnPlayer(event)
            val other = Bukkit.getOfflinePlayer(wrapper.uuid)
            if (other !is Player) return
            val fakeId = getFakeUUID(other)
            if (event.player is Player && other.uniqueId == (event.player as Player).uniqueId) return
            wrapper.uuid = fakeId
        }
        if (event.packetType == PacketType.Play.Server.ENTITY_METADATA) {
            val wrapper = WrapperPlayServerEntityMetadata(event)
            var other: Player? = null
            Bukkit.getOnlinePlayers().forEach { if (it.entityId == wrapper.entityId) other = it }
            if (other == null) return
            wrapper.entityMetadata.forEach {
                if (it.index == 10) it.value = data[other!!.uniqueId]?.skinLayers?.rawSkinLayers ?: it.value
            }
        }
    }

    override fun onPacketReceive(event: PacketReceiveEvent) {
        if (event.player !is Player) return
        val player = event.player as Player
        if (event.packetType == PacketType.Play.Client.CLIENT_SETTINGS) {
            val packet = WrapperPlayClientSettings(event)
            if (data[player.uniqueId]?.skinLayers == null) {
                data[player.uniqueId]?.skinLayers = me.onlyjordon.nicknamingapi.utils.SkinLayers.getFromRaw(packet.visibleSkinSectionMask)
            }
        }
    }
    
    private fun changeProfile(profile: UserProfile, hideUUID: Boolean = true) {
        val uuid = profile.uuid
        val data = data[uuid] ?: return
        val other = Bukkit.getOfflinePlayer(uuid)
        if (!other.isOnline) return
        if (other !is Player) return
        val id = getFakeUUID(other)
        profile.name = data.nickname ?: other.name
        if (hideUUID) profile.uuid = id // fake uuid
        val skin = data.currentSkin ?: data.originalSkin ?: me.onlyjordon.nicknamingapi.utils.Skin(
            null,
            null
        )
        profile.textureProperties = listOf(TextureProperty("textures", skin.value, skin.signature))
    }


}