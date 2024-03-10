package me.onlyjordon.swiftdisguise.packetevents;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.event.PacketListener;
import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.event.PacketSendEvent;
import com.github.retrooper.packetevents.protocol.entity.type.EntityTypes;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.protocol.packettype.PacketTypeCommon;
import com.github.retrooper.packetevents.protocol.player.ClientVersion;
import com.github.retrooper.packetevents.protocol.player.TextureProperty;
import com.github.retrooper.packetevents.protocol.player.UserProfile;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientSettings;
import com.github.retrooper.packetevents.wrapper.play.server.*;
import me.onlyjordon.swiftdisguise.SpigotPlatform;
import me.onlyjordon.swiftdisguise.SwiftDisguiseSpigot;
import me.onlyjordon.swiftdisguise.api.DisguiseData;
import me.onlyjordon.swiftdisguise.events.PlayerSkinLayerChangeEvent;
import me.onlyjordon.swiftdisguise.utils.SkinLayers;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.*;

public class SpigotPacketListener implements PacketListener {

    SwiftDisguiseSpigot api;

    public SpigotPacketListener(SwiftDisguiseSpigot api) {
        this.api = api;
    }

    @Override
    public void onPacketReceive(PacketReceiveEvent event) {
        if (!(event.getPlayer() instanceof Player)) return;
        if (event.getPacketType() == PacketType.Play.Client.CLIENT_SETTINGS) {
            Player player = (Player) event.getPlayer();
            WrapperPlayClientSettings wrapper = new WrapperPlayClientSettings(event);
            ((DisguiseData)api.getDisguiseData(event.getPlayer())).setRealSkinLayers(SkinLayers.getFromRaw(wrapper.getVisibleSkinSectionMask()));
            PlayerSkinLayerChangeEvent e = new PlayerSkinLayerChangeEvent(player, api.getSkinLayers(player), SkinLayers.getFromRaw(wrapper.getVisibleSkinSectionMask()));
            if (!e.isCancelled())
                ((DisguiseData)api.getDisguiseData(event.getPlayer())).setFakeSkinLayers(e.getNewLayers());
        }
    }

    @Override
    public void onPacketSend(PacketSendEvent event) {
        PacketTypeCommon packetType = event.getPacketType();
        Player player = null;
        if (event.getPlayer() instanceof Player) player = (Player) event.getPlayer();
        if (player == null) return;
//        if (!api.isRealPlayer(player)) return;
        if (packetType.equals(PacketType.Play.Server.PLAYER_INFO)) {
            handlePlayerInfoPacket(player, new WrapperPlayServerPlayerInfo(event));
        } else if (packetType.equals(PacketType.Play.Server.PLAYER_INFO_UPDATE)) {
            handlePlayerInfoUpdatePacket(player, new WrapperPlayServerPlayerInfoUpdate(event));
        } else if (packetType.equals(PacketType.Play.Server.PLAYER_INFO_REMOVE)) {
            handlePlayerInfoRemovePacket(player, new WrapperPlayServerPlayerInfoRemove(event));
        } else if (packetType.equals(PacketType.Play.Server.SPAWN_PLAYER)) {
            handleSpawnPlayerPacket(player, new WrapperPlayServerSpawnPlayer(event));
        } else if (packetType.equals(PacketType.Play.Server.SPAWN_ENTITY)) {
            handleSpawnEntityPacket(player, new WrapperPlayServerSpawnEntity(event));
        }
    }

    private void handleSpawnPlayerPacket(Player player, WrapperPlayServerSpawnPlayer wrapper) {
        UUID fakeId = api.getFakeUUID(wrapper.getUUID());
        if (!api.isRealPlayer(Bukkit.getPlayer(wrapper.getUUID()))) return;
        if (!Objects.equals(player.getUniqueId(), wrapper.getUUID())) {
            wrapper.setUUID(fakeId);
        } else {
            wrapper.setUUID(api.getRealUUID(player));
        }
    }

    private void handleSpawnEntityPacket(Player player, WrapperPlayServerSpawnEntity wrapper) {
        if (!wrapper.getUUID().isPresent()) return;
        wrapper.getUUID().ifPresent((uuid) -> {
            if (!Objects.equals(wrapper.getEntityType(), EntityTypes.PLAYER)) return;
            if (!api.isRealPlayer(player)) return;
            UUID fakeId = api.getFakeUUID(uuid);

            if (!Objects.equals(player.getUniqueId(), uuid)) {
                wrapper.setUUID(Optional.of(fakeId));
            } else {
                wrapper.setUUID(Optional.of(api.getRealUUID(player)));
            }
        });
    }


    private void handlePlayerInfoRemovePacket(Player receiver, WrapperPlayServerPlayerInfoRemove wrapper) {
        List<UUID> fakeIds = new ArrayList<>();
        wrapper.getProfileIds().forEach(uuid -> {
            Player player = Bukkit.getPlayer(uuid);
            if (player == null) return;
            if (!api.isRealPlayer(player)) return;
            if (!Objects.equals(receiver.getUniqueId(), player.getUniqueId()))
                fakeIds.add(api.getDisguiseData(player).getFakeUUID());
            else fakeIds.add(player.getUniqueId());
        });
        wrapper.setProfileIds(fakeIds);
    }

    private void handlePlayerInfoUpdatePacket(Player receiver, WrapperPlayServerPlayerInfoUpdate wrapper) {
        wrapper.getEntries().forEach(playerData -> {
            Player player = Bukkit.getPlayer(playerData.getGameProfile().getUUID());
            if (!api.isRealPlayer(player)) return;
            if (player == null) return;
            UserProfile profile = playerData.getGameProfile();
            TextureProperty property = new TextureProperty("textures", api.getDisguiseSkin(player).getValue(), api.getDisguiseSkin(player).getSignature());
            if (!Objects.equals(receiver.getUniqueId(), profile.getUUID())) {
                profile.setUUID(api.getFakeUUID(player.getUniqueId()));
            } else {
                profile.setUUID(player.getUniqueId());
            }
            profile.setName(api.getDisguiseName(player));
            profile.setTextureProperties(new ArrayList<TextureProperty>() { { add(property); } });
            playerData.setGameProfile(profile);
        });
    }

    private void handlePlayerInfoPacket(Player receiver, WrapperPlayServerPlayerInfo wrapper) {
        wrapper.getPlayerDataList().forEach(playerData -> {
            Player player = Bukkit.getPlayer(playerData.getUserProfile().getUUID());
            if (!api.isRealPlayer(player)) return;
            if (player == null) return;
            UserProfile profile = playerData.getUser();
            if (profile == null) return;
            if (!Objects.equals(receiver.getUniqueId(), profile.getUUID())) {
                profile.setUUID(api.getFakeUUID(player.getUniqueId()));
            } else {
                profile.setUUID(player.getUniqueId());
            }
            profile.setName(api.getDisguiseName(player));

            TextureProperty textureProperty = new TextureProperty("textures", api.getDisguiseSkin(player).getValue(), api.getDisguiseSkin(player).getSignature());
            profile.setTextureProperties(new ArrayList<TextureProperty>() { { add(textureProperty); } });
            playerData.setUser(profile);
        });
    }
}
