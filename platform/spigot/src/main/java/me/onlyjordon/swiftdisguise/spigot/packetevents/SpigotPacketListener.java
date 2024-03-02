package me.onlyjordon.swiftdisguise.spigot.packetevents;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.event.PacketListener;
import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.event.PacketSendEvent;
import com.github.retrooper.packetevents.manager.server.ServerVersion;
import com.github.retrooper.packetevents.protocol.entity.type.EntityTypes;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.protocol.packettype.PacketTypeCommon;
import com.github.retrooper.packetevents.protocol.player.ClientVersion;
import com.github.retrooper.packetevents.protocol.player.TextureProperty;
import com.github.retrooper.packetevents.protocol.player.UserProfile;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientChatCommand;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientPlayerBlockPlacement;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientSettings;
import com.github.retrooper.packetevents.wrapper.play.server.*;
import me.onlyjordon.swiftdisguise.api.SwiftDisguiseConfig;
import me.onlyjordon.swiftdisguise.spigot.SwiftDisguiseSpigot;
import me.onlyjordon.swiftdisguise.api.DisguiseData;
import me.onlyjordon.swiftdisguise.spigot.events.PlayerSkinLayerChangeEvent;
import me.onlyjordon.swiftdisguise.spigot.nms.CrossVersionPlayerHelper;
import me.onlyjordon.swiftdisguise.utils.SkinLayers;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.stream.Collectors;

public class SpigotPacketListener implements PacketListener {

    private final SwiftDisguiseSpigot api;
    private final SwiftDisguiseConfig config;

    public SpigotPacketListener(SwiftDisguiseSpigot api, SwiftDisguiseConfig config) {
        this.api = api;
        this.config = config;
    }

    @Override
    public void onPacketReceive(PacketReceiveEvent event) {
        if (!(event.getPlayer() instanceof Player)) return;
        if (event.getPacketType() == PacketType.Play.Client.CLIENT_SETTINGS) {
            Player player = (Player) event.getPlayer();
            WrapperPlayClientSettings wrapper = new WrapperPlayClientSettings(event);
            ((DisguiseData)api.getDisguiseData(event.getPlayer())).setRealSkinLayers(SkinLayers.getFromRaw(wrapper.getVisibleSkinSectionMask()));
            PlayerSkinLayerChangeEvent e = new PlayerSkinLayerChangeEvent(player, api.getDisguiseSkinLayers(player), SkinLayers.getFromRaw(wrapper.getVisibleSkinSectionMask()));
            if (!e.isCancelled())
                ((DisguiseData)api.getDisguiseData(event.getPlayer())).setFakeSkinLayers(e.getNewLayers());
        }
//        if (event.getPacketType() == PacketType.Play.Client.CHAT_COMMAND) {
//            Player sender = (Player) event.getPlayer();
//            WrapperPlayClientChatCommand wrapper = new WrapperPlayClientChatCommand(event);
//
//            String[] args = wrapper.getCommand().split(" ");
//            if (args.length == 0 || args.length == 1) return;
//            System.out.println(config.nameMode() + " / " + config.hidingMode());
//            if (config.nameMode() == SwiftDisguiseConfig.NameMode.FULL) {
//                System.out.println("replacing names for " + sender.getName());
//                replaceName(sender, args, wrapper);
//            }
//            System.out.println(wrapper.getCommand());
//
//        }
    }
//
//    public void replaceName(Player sender, String[] args, WrapperPlayClientChatCommand wrapper) {
//        for (Player canSee : Bukkit.getOnlinePlayers().stream().filter(p -> sender.canSee(p) && p.getUniqueId() != sender.getUniqueId()).collect(Collectors.toList())) {
//            for (String arg : args) {
//                System.out.println(arg);
//                if (!arg.equalsIgnoreCase(api.getRealName(canSee))) return;
//                wrapper.setCommand(wrapper.getCommand().replace(arg, api.getRealName(canSee)));
//                System.out.println("replaced " + arg + " with " + api.getRealName(canSee));
//            }
//        }
//    }

    @Override
    public void onPacketSend(PacketSendEvent event) {
        PacketTypeCommon packetType = event.getPacketType();
        Player player = null;
        if (event.getPlayer() instanceof Player) player = (Player) event.getPlayer();
        if (player == null) return;
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
        } else if (packetType.equals(PacketType.Play.Server.ENTITY_METADATA)) {
            handleEntityMetadataPacket(player, new WrapperPlayServerEntityMetadata(event));
        }
    }

    private void handlePlayerInfoRemovePacket(Player receiver, WrapperPlayServerPlayerInfoRemove wrapper) {
        List<UUID> fakeIds = new ArrayList<>();
        wrapper.getProfileIds().forEach(uuid -> {
            Player player = Bukkit.getPlayer(uuid);
            if (player == null) return;
            if (!Objects.equals(receiver.getUniqueId(), player.getUniqueId()))
                fakeIds.add(api.getDisguiseData(player).getFakeUUID());
            else fakeIds.add(player.getUniqueId());
        });
        wrapper.setProfileIds(fakeIds);
    }

    private void handlePlayerInfoUpdatePacket(Player receiver, WrapperPlayServerPlayerInfoUpdate wrapper) {
        wrapper.getEntries().forEach(playerData -> {
            Player player = Bukkit.getPlayer(playerData.getGameProfile().getUUID());
            if (player == null) return;
            UserProfile profile = playerData.getGameProfile();
            TextureProperty property = new TextureProperty("textures", api.getDisguiseSkin(player).getValue(), api.getDisguiseSkin(player).getSignature());
            if (!Objects.equals(receiver.getUniqueId(), profile.getUUID())) {
                profile.setName(api.getDisguiseName(player));
                if (config.hidingMode() != SwiftDisguiseConfig.UUIDHidingMode.NONE)
                    profile.setUUID(api.getFakeUUID(player.getUniqueId()));

            } else {
                profile.setUUID(api.getRealUUID(receiver));
            }
            TextureProperty finalProperty = property;
            profile.setTextureProperties(new ArrayList<TextureProperty>() { { add(finalProperty); } });
            playerData.setGameProfile(profile);
        });
    }

    private void handlePlayerInfoPacket(Player receiver, WrapperPlayServerPlayerInfo wrapper) {
        wrapper.getPlayerDataList().forEach(playerData -> {
            Player player = Bukkit.getPlayer(playerData.getUserProfile().getUUID());
            if (player == null) return;
            UserProfile profile = playerData.getUser();
            if (profile == null) return;
            if (!Objects.equals(receiver.getUniqueId(), profile.getUUID())) {
                profile.setName(api.getDisguiseName(player));
                if (config.hidingMode() != SwiftDisguiseConfig.UUIDHidingMode.NONE)
                    profile.setUUID(api.getFakeUUID(player.getUniqueId()));
                else {
                    profile.setUUID(api.getRealUUID(receiver));
                }
            }

            TextureProperty textureProperty = new TextureProperty("textures", api.getDisguiseSkin(player).getValue(), api.getDisguiseSkin(player).getSignature());
            profile.setTextureProperties(new ArrayList<TextureProperty>() { { add(textureProperty); } });
            playerData.setUser(profile);
        });
    }

    private void handleSpawnPlayerPacket(Player receiver, WrapperPlayServerSpawnPlayer wrapper) {
        if (config.hidingMode() == SwiftDisguiseConfig.UUIDHidingMode.NONE) return;
        UUID fakeId = api.getFakeUUID(wrapper.getUUID());
        if (!Objects.equals(receiver.getUniqueId(), wrapper.getUUID())) {
            wrapper.setUUID(fakeId);
        } else {
            wrapper.setUUID(api.getRealUUID(receiver));
        }
    }

    private void handleSpawnEntityPacket(Player receiver, WrapperPlayServerSpawnEntity wrapper) {
        if (config.hidingMode() == SwiftDisguiseConfig.UUIDHidingMode.NONE) return;
        wrapper.getUUID().ifPresent((uuid) -> {
            if (!Objects.equals(wrapper.getEntityType(), EntityTypes.PLAYER)) return;
            UUID fakeId = api.getFakeUUID(uuid);
            if (!Objects.equals(receiver.getUniqueId(), uuid)) {
                wrapper.setUUID(Optional.of(fakeId));
            } else {
                wrapper.setUUID(Optional.of(api.getRealUUID(receiver)));
            }
        });
    }

    private void handleEntityMetadataPacket(Player receiver, WrapperPlayServerEntityMetadata wrapper) {
        wrapper.getEntityMetadata().forEach((data) -> {
            int index = data.getIndex();
            if (index == CrossVersionPlayerHelper.getSkinLayersIndex(PacketEvents.getAPI().getServerManager().getVersion())) {
                byte d = api.getDisguiseSkinLayers(receiver).getRawSkinLayers();
                data.setValue(d);
            }
        });
    }
}
