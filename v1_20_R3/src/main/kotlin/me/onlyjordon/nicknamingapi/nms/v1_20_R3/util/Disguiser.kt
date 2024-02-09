package me.onlyjordon.nicknamingapi.nms.v1_20_R3.util

import PlayerExtensions.Companion.refresh
import com.github.retrooper.packetevents.PacketEvents
import com.github.retrooper.packetevents.event.PacketListener
import com.github.retrooper.packetevents.event.PacketListenerPriority
import com.github.retrooper.packetevents.event.PacketSendEvent
import com.github.retrooper.packetevents.protocol.packettype.PacketType
import com.github.retrooper.packetevents.protocol.player.TextureProperty
import com.github.retrooper.packetevents.protocol.player.UserProfile
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerPlayerInfoRemove
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerPlayerInfoUpdate
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerSpawnEntity
import com.mojang.authlib.properties.Property
import io.netty.buffer.Unpooled
import me.onlyjordon.nicknamingapi.NMSDisguiser
import me.onlyjordon.nicknamingapi.utils.Skin
import net.kyori.adventure.text.TextComponent
import net.kyori.adventure.text.serializer.json.JSONComponentSerializer
import net.minecraft.ChatFormatting
import net.minecraft.network.FriendlyByteBuf
import net.minecraft.network.chat.Component
import net.minecraft.network.protocol.game.*
import net.minecraft.network.protocol.game.ClientboundPlayerInfoUpdatePacket.Action
import net.minecraft.network.protocol.game.ClientboundSetPlayerTeamPacket.Parameters
import net.minecraft.server.level.ServerPlayer
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.Location
import org.bukkit.craftbukkit.v1_20_R3.CraftServer
import org.bukkit.craftbukkit.v1_20_R3.entity.CraftPlayer
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerChangedWorldEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import java.lang.reflect.Constructor
import java.util.*

class Disguiser: Listener,PacketListener,NMSDisguiser() {
    private val fakeUUIDs = WeakHashMap<UUID, UUID>()
    private val prefixSuffix = WeakHashMap<Player, ClientboundSetPlayerTeamPacket>()

    private var setPlayerTeamPacketConstructor: Constructor<ClientboundSetPlayerTeamPacket>? = null

    private fun setFields() {
        if (setPlayerTeamPacketConstructor == null) {
            setPlayerTeamPacketConstructor = ClientboundSetPlayerTeamPacket::class.java.getDeclaredConstructor(
                String::class.java,
                Integer.TYPE,
                Optional::class.java,
                Collection::class.java
            )
            (setPlayerTeamPacketConstructor as Constructor<ClientboundSetPlayerTeamPacket>).isAccessible = true
        }
    }

    private fun getFakeUUID(player: Player): UUID {
        val id = fakeUUIDs.getOrDefault(player.uniqueId, UUID.randomUUID())
        fakeUUIDs[player.uniqueId] = id
        return id
    }

    private fun getFakeUUID(uuid: UUID): UUID {
        val id = fakeUUIDs.getOrDefault(uuid, UUID.randomUUID())
        fakeUUIDs[uuid] = id
        return id
    }

    override fun disable() {
        PacketEvents.getAPI().terminate()
    }

    override fun setup() {
        PacketEvents.getAPI().eventManager.registerListener(this, PacketListenerPriority.LOW)
        PacketEvents.getAPI().init()
        setFields()
    }

    override fun getSkin(player: Player): Skin {
        return currentSkins.getOrDefault(player, originalSkins.getOrDefault(player, Skin(null, null)))
    }

    override fun getSkin(skinName: String): Skin {
        return Skin.getSkin(skinName)
    }

    override fun onPacketSend(event: PacketSendEvent) {
        if (event.packetType == PacketType.Play.Server.PLAYER_INFO_UPDATE) {
            val packet = WrapperPlayServerPlayerInfoUpdate(event)
            packet.entries.forEach {
                changeProfile(it.gameProfile, !(event.player is Player && it.gameProfile.uuid == (event.player as Player).uniqueId))
            }
        }
        if (event.packetType == PacketType.Play.Server.PLAYER_INFO_REMOVE) {
            val packet = WrapperPlayServerPlayerInfoRemove(event)
            val toRemove = HashSet<UUID>()
            val toAdd = HashSet<UUID>()
            packet.profileIds.forEach {
                if (event.player is Player && it == (event.player as Player).uniqueId) return@forEach
                toRemove.add(it)
                toAdd.add(getFakeUUID(it))
            }
            toRemove.forEach { packet.profileIds.remove(it) }
            toAdd.forEach { packet.profileIds.add(it) }
        }
        if (event.packetType == PacketType.Play.Server.SPAWN_ENTITY) {
            val packet = WrapperPlayServerSpawnEntity(event)
            packet.uuid.ifPresent {
                val player = Bukkit.getPlayer(it) ?: return@ifPresent
                if (event.player is Player && it == (event.player as Player).uniqueId) return@ifPresent
                val id = getFakeUUID(player)
                packet.uuid = Optional.of(id)
            }
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

    @EventHandler
    fun onJoin(e: PlayerJoinEvent) {
        val player = e.player
        setNick(player, getNick(player))
        originalSkins[player] = (player as CraftPlayer).profile.properties.get("textures").firstOrNull()?.let { Skin(it.value, it.signature) }
        prefixSuffix.values.forEach {
            player.handle.connection.send(it)
        }
    }

    @EventHandler
    fun onQuit(e: PlayerQuitEvent) {
        val player = e.player
        originalSkins.remove(player)
        currentSkins.remove(player)
        nicknames.remove(player)
        Bukkit.getOnlinePlayers().forEach {
            sendPacketRemove(player, it)
        }
        prefixSuffix.remove(player)
    }

    private fun sendPacketRemove(toRemove: Player, toSend: Player) {
        getTeamPacket(toRemove, net.kyori.adventure.text.Component.text(""), net.kyori.adventure.text.Component.text(""), ChatColor.WHITE, 1).let { packet ->
            (toSend as CraftPlayer).handle.connection.send(packet)
        }
    }

    @EventHandler
    fun onWorldChange(e: PlayerChangedWorldEvent) {
        e.player.refresh()
    }

    override fun refreshPlayer(player: Player) {
        player.location.world.players.forEach {
            if (it != player) {
                it.hidePlayer(player)
                it.showPlayer(player)
            }
        }
        if (!player.isOnline) return
        for (other in player.location.world.players) {
            val entityPlayer = (other as CraftPlayer).handle
            entityPlayer.sendPlayerInfoRemovePacket(player)
            entityPlayer.sendPlayerInfoUpdatePacket(player, Action.ADD_PLAYER)
            entityPlayer.sendPlayerInfoUpdatePacket(player, Action.UPDATE_LISTED)
            entityPlayer.sendSetEntityMetadataPacket(player)
            prefixSuffix[player]?.let { other.handle.connection.send(it) }
        }
        val entityPlayer = (player as CraftPlayer).handle

        entityPlayer.sendRespawnPacket()
        entityPlayer.sendTeleportPacket(player.location)
        entityPlayer.sendLevelInfo()
        entityPlayer.sendAllPlayerInfo()
    }

    override fun setSkin(player: Player, skin: Skin): Boolean { // maybe replace with Paper's API for it?
        val craftPlayer = player as CraftPlayer
        craftPlayer.profile.properties.removeAll("textures")
        craftPlayer.profile.properties.put("textures", Property("textures", skin.value, skin.signature))
        currentSkins[player] = skin
        return true
    }

    override fun setSkin(player: Player, name: String) {
        setSkin(player, Skin.getSkin(name))
    }

    override fun setNick(player: Player, nick: String) {
        nicknames[player] = nick
    }

    override fun resetNick(player: Player?) {
        nicknames.remove(player)
    }

    override fun resetDisguise(player: Player) {
        resetSkin(player)
        resetNick(player)
        prefixSuffix.remove(player)
    }

    private fun getParameters(player: Player, prefix: TextComponent, suffix: TextComponent, color: ChatFormatting): Parameters {
        // better way of doing this?
        val byteBuf = FriendlyByteBuf(Unpooled.buffer()).
            writeComponent(Component.literal(player.uniqueId.toString().replace('-', Character.MIN_VALUE).subSequence(0,16).toString())).
            writeByte(0b01).
            writeUtf("always").
            writeUtf("never").
            writeEnum(color).
            writeComponent(Component.Serializer.fromJson((JSONComponentSerializer.json().serialize(prefix.asComponent())))).
            writeComponent(Component.Serializer.fromJson((JSONComponentSerializer.json().serialize(suffix.asComponent()))))
        return Parameters(byteBuf)
    }

    private fun getTeamPacket(player: Player, prefix: TextComponent, suffix: TextComponent, textColor: ChatColor, method: Int): ClientboundSetPlayerTeamPacket {
        return setPlayerTeamPacketConstructor!!.newInstance(
            player.uniqueId.toString().replace('-', Character.MIN_VALUE).subSequence(0, 16),
            method,
            Optional.of(getParameters(player, prefix, suffix, ChatFormatting.getByCode(textColor.char)!!)),
            listOf(getNick(player))
        )
    }

    override fun setPrefixSuffix(player: Player, prefix: TextComponent, suffix: TextComponent, textColor: ChatColor) {
        setFields()
        prefixSuffix[player] = getTeamPacket(player, prefix, suffix, textColor, 0)
    }

    override fun getNick(player: Player): String {
        return nicknames.getOrDefault(player, player.name)
    }

    override fun getPlayerWithNick(nick: String): Player? {
        return nicknames.entries.firstOrNull { it.value.lowercase() == nick.lowercase() }?.key
    }


    private fun ServerPlayer.sendAllPlayerInfo() {
        (Bukkit.getServer() as CraftServer).handle.sendAllPlayerInfo(this)
    }

    private fun ServerPlayer.sendLevelInfo() {
        (Bukkit.getServer() as CraftServer).handle.sendLevelInfo(this, this.serverLevel())
    }

    private fun ServerPlayer.sendRespawnPacket() {
        connection.send(ClientboundRespawnPacket(
                CommonPlayerSpawnInfo(
                    this.serverLevel().dimensionTypeId(),
                    this.serverLevel().dimension(),
                    0L, // seed
                    this.gameMode.gameModeForPlayer,
                    this.gameMode.gameModeForPlayer,
                    false, //isDebug
                    false, //isFlat
                    Optional.empty(),
                    0 // portal cooldown
                ),
                0x02.toByte()
            )
        )
    }

    private fun ServerPlayer.sendTeleportPacket(loc: Location) {
        connection.send(ClientboundPlayerPositionPacket(loc.x, loc.y, loc.z, loc.yaw, loc.pitch, Collections.emptySet(), 0))
    }

    private fun ServerPlayer.sendPlayerInfoRemovePacket(updatingPlayer: Player) {
        connection.send(ClientboundPlayerInfoRemovePacket(listOf(updatingPlayer.uniqueId))) // fake UUID is applied in packet listener
    }

    private fun ServerPlayer.sendPlayerInfoUpdatePacket(updatingPlayer: Player, action: Action) {
        connection.send(ClientboundPlayerInfoUpdatePacket(action, (updatingPlayer as CraftPlayer).handle))
    }

    private fun ServerPlayer.sendSetEntityMetadataPacket(updatingPlayer: Player) {
        connection.send(ClientboundSetEntityDataPacket(
            updatingPlayer.entityId,
            (updatingPlayer as CraftPlayer).handle.entityData.nonDefaultValues
        ))
    }

}
