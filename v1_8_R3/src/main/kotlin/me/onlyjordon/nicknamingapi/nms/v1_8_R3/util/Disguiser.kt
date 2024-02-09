package me.onlyjordon.nicknamingapi.nms.v1_8_R3.util

import PlayerExtensions.Companion.getNick
import com.github.retrooper.packetevents.PacketEvents
import com.github.retrooper.packetevents.event.PacketListener
import com.github.retrooper.packetevents.event.PacketListenerPriority
import com.github.retrooper.packetevents.event.PacketSendEvent
import com.github.retrooper.packetevents.protocol.packettype.PacketType
import com.github.retrooper.packetevents.protocol.player.TextureProperty
import com.github.retrooper.packetevents.protocol.player.UserProfile
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerPlayerInfo
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerSpawnPlayer
import com.mojang.authlib.GameProfile
import com.mojang.authlib.properties.Property
import me.onlyjordon.nicknamingapi.NMSDisguiser
import me.onlyjordon.nicknamingapi.nms.v1_8_R3.util.DisguiserHelper.Companion.destroyPacket
import me.onlyjordon.nicknamingapi.nms.v1_8_R3.util.DisguiserHelper.Companion.playerInfoPacket
import me.onlyjordon.nicknamingapi.nms.v1_8_R3.util.DisguiserHelper.Companion.playerSpawnPacket
import me.onlyjordon.nicknamingapi.nms.v1_8_R3.util.DisguiserHelper.Companion.setFields
import me.onlyjordon.nicknamingapi.nms.v1_8_R3.util.DisguiserHelper.Companion.setPlayerInfoData
import me.onlyjordon.nicknamingapi.nms.v1_8_R3.util.DisguiserHelper.Companion.updateClient
import me.onlyjordon.nicknamingapi.nms.v1_8_R3.util.DisguiserHelper.Companion.worldServer
import me.onlyjordon.nicknamingapi.utils.ReflectionHelper
import me.onlyjordon.nicknamingapi.utils.Skin
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
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerChangedWorldEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.plugin.java.JavaPlugin
import java.util.*


@Suppress("unused", "deprecation")
class Disguiser : PacketListener,Listener, NMSDisguiser() {

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
        return nicknames.getOrDefault(player, player.name)
    }

    override fun getPlayerWithNick(nick: String): Player? {
        return nicknames.entries.firstOrNull { it.value.lowercase() == nick.lowercase() }?.key
    }

    override fun setup() {
        PacketEvents.getAPI().eventManager.registerListener(this, PacketListenerPriority.LOW)
        PacketEvents.getAPI().init()
    }

    override fun getSkin(player: Player): Skin {
        return currentSkins.getOrDefault(player, originalSkins.getOrDefault(player, Skin(null, null)))
    }

    override fun getSkin(skinName: String): Skin {
        return Skin.getSkin(skinName)
    }


    @EventHandler
    fun onJoin(e: PlayerJoinEvent) {
        val player = e.player
        setNick(player, getNick(player))
        val craftPlayer = player as CraftPlayer
        val prop = craftPlayer.handle.profile.properties
        setSkin(player, originalSkins.getOrDefault(player, Skin(prop.get("textures").firstOrNull()?.value, prop.get("textures").firstOrNull()?.signature)))
        val entityPlayer = player.handle
        val packet = entityPlayer.playerSpawnPacket()
        entityPlayer.playerConnection.sendPacket(packet)
        player.world.players.forEach {
            if (it.uniqueId == player.uniqueId) return@forEach
            (it as CraftPlayer).handle.playerConnection.sendPacket(packet)
            it.handle.playerConnection.sendPacket(entityPlayer.playerSpawnPacket()) // hack because my code sucks :p
        }
        prefixSuffix.values.forEach {
            player.handle.playerConnection.sendPacket(it)
        }
    }

    @EventHandler
    fun onQuit(e: PlayerQuitEvent) {
        nicknames.remove(e.player)
        currentSkins.remove(e.player)
        originalSkins.remove(e.player)
        getPrefixSuffixRemovePacket(e.player).let { packet ->
            Bukkit.getOnlinePlayers().forEach {
                (it as CraftPlayer).handle.playerConnection.sendPacket(packet)
            }
        }
        prefixSuffix.remove(e.player)
    }

    private fun getPrefixSuffixRemovePacket(player: Player, prefix: TextComponent = Component.text(""), suffix: TextComponent = Component.text(""), textColor: ChatColor = ChatColor.WHITE): PacketPlayOutScoreboardTeam {
        val packet = PacketPlayOutScoreboardTeam()
        ReflectionHelper.setFieldValue("a", player.uniqueId.toString().replace('-', Character.MIN_VALUE).subSequence(0,16).toString(), packet) // team name
        ReflectionHelper.setFieldValue("b", player.uniqueId.toString().replace('-', Character.MIN_VALUE).subSequence(0,16).toString(), packet) // team display name
        ReflectionHelper.setFieldValue("c", LegacyComponentSerializer.legacySection().serialize(prefix), packet) // prefix
        ReflectionHelper.setFieldValue("d", LegacyComponentSerializer.legacySection().serialize(suffix), packet) // suffix
        ReflectionHelper.setFieldValue("e", EnumNameTagVisibility.ALWAYS.e, packet) // nametag visibility
        ReflectionHelper.setFieldValue("f", EnumChatFormat.valueOf(textColor.name).b(), packet) // team color
        ReflectionHelper.setFieldValue("g", mutableListOf(player.getNick()) as Collection<String>, packet) // players
        ReflectionHelper.setFieldValue("h", 0, packet) // mode: 0 = add; 1 = remove; 2 = update; 3 = new players; 4 = players removed
        ReflectionHelper.setFieldValue("i", 1, packet) // options: 1 = friendly fire; 2 = see friendly invisibles; 3 = both
        return packet
    }

    private fun getPrefixSuffixAddPacket(player: Player, prefix: TextComponent = Component.text(""), suffix: TextComponent = Component.text(""), textColor: ChatColor = ChatColor.WHITE): PacketPlayOutScoreboardTeam {
        val packet = PacketPlayOutScoreboardTeam()
        ReflectionHelper.setFieldValue("a", player.uniqueId.toString().replace('-', Character.MIN_VALUE).subSequence(0,16).toString(), packet) // team name
        ReflectionHelper.setFieldValue("b", player.uniqueId.toString().replace('-', Character.MIN_VALUE).subSequence(0,16).toString(), packet) // team display name
        ReflectionHelper.setFieldValue("c", LegacyComponentSerializer.legacySection().serialize(prefix), packet) // prefix
        player.sendMessage(LegacyComponentSerializer.legacySection().serialize(prefix))
        ReflectionHelper.setFieldValue("d", LegacyComponentSerializer.legacySection().serialize(suffix), packet) // suffix
        player.sendMessage(LegacyComponentSerializer.legacySection().serialize(suffix))
        ReflectionHelper.setFieldValue("e", EnumNameTagVisibility.ALWAYS.e, packet) // nametag visibility
        ReflectionHelper.setFieldValue("f", EnumChatFormat.valueOf(textColor.name).b(), packet) // team color
        println(EnumChatFormat.valueOf(textColor.name).b())
        ReflectionHelper.setFieldValue("g", mutableListOf(player.getNick()) as Collection<String>, packet) // players
        ReflectionHelper.setFieldValue("h", 0, packet) // mode: 0 = add; 1 = remove; 2 = update; 3 = new players; 4 = players removed
        ReflectionHelper.setFieldValue("i", 1, packet) // options: 1 = friendly fire; 2 = see friendly invisibles; 3 = both

        return packet
    }

    @EventHandler
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
            (it as CraftPlayer).handle.playerConnection.sendPacket(addPacket)
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

    override fun setSkin(player: Player, skin: Skin): Boolean {
        currentSkins[player] = skin
        val profile = (player as CraftPlayer).profile
        profile.properties.clear()
        profile.properties.put("textures", Property("textures", skin.value, skin.signature))
        val gameProfileField = ReflectionHelper.getField("bH", EntityHuman::class.java)
        gameProfileField.isAccessible = true
        gameProfileField.set(player.handle, profile)
        return true
    }

    override fun setSkin(player: Player, name: String) {
        val skin = Skin.getSkin(name)
        if (skin != null) {
            setSkin(player, skin)
        }
    }

    override fun setNick(player: Player, nick: String) {
        nicknames[player] = nick
    }

    override fun resetNick(player: Player?) {
        nicknames.remove(player)
    }

    override fun setPrefixSuffix(player: Player, prefix: TextComponent, suffix: TextComponent, textColor: ChatColor) {
        val reset = Component.text(ChatColor.RESET.toString()+textColor.toString())
        var pre = prefix
        var suf = suffix
        if (LegacyComponentSerializer.legacySection().serialize(prefix).length > 12) {
            pre = Component.text(LegacyComponentSerializer.legacySection().serialize(prefix).substring(0, 12))
        }
        if (LegacyComponentSerializer.legacySection().serialize(suffix).length > 12) {
            suf = Component.text(LegacyComponentSerializer.legacySection().serialize(prefix).substring(0, 12))
        }

        val packet = getPrefixSuffixAddPacket(player, pre.append(reset), reset.append(suf), textColor)
        prefixSuffix[player] = packet
    }
    override fun resetDisguise(player: Player) {
        resetSkin(player)
        resetNick(player)
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
    }
    
    private fun changeProfile(profile: UserProfile, hideUUID: Boolean = true) {
        val uuid = profile.uuid
        val other = Bukkit.getOfflinePlayer(uuid)
        if (!other.isOnline) return
        if (other !is Player) return
        val id = getFakeUUID(other)
        profile.name = nicknames.getOrDefault(other.player, other.name)
        if (hideUUID) profile.uuid = id // fake uuid
        var skin = currentSkins[other]
        if (skin == null) skin = originalSkins[other]
        if (skin == null) return
        profile.textureProperties = listOf(TextureProperty("textures", skin.value, skin.signature))
    }


}
