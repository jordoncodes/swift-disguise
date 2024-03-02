package me.onlyjordon.swiftdisguise.commands.impl;

import me.onlyjordon.swiftdisguise.api.ITabPrefixSuffix;
import me.onlyjordon.swiftdisguise.api.TabPrefixSuffix;
import me.onlyjordon.swiftdisguise.spigot.SpigotPlatform;
import me.onlyjordon.swiftdisguise.api.ISwiftDisguiseAPI;
import me.onlyjordon.swiftdisguise.api.SwiftDisguise;
import me.onlyjordon.swiftdisguise.commands.PlayerOnlyCommand;
import me.onlyjordon.swiftdisguise.utils.Skin;
import me.onlyjordon.swiftdisguise.utils.SkinLayers;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.entity.Player;

import java.awt.*;
import java.util.HashMap;

public class CommandDisguise extends PlayerOnlyCommand {
    public CommandDisguise() {
        super("disguise", "disguise.use");
    }

    @Override
    protected boolean execute(Player player, String[] args) {
        ISwiftDisguiseAPI api = SwiftDisguise.getAPI(SpigotPlatform.get());
        if (args.length == 0) {
            api.setDisguiseName(player, "");
            api.setDisguiseSkin(player, new Skin("", ""));
            api.setDisguisePrefixSuffix(player, new TabPrefixSuffix(
                    Component.text(""),
                    Component.text(player.getName()).color(TextColor.color(0x934593)),
                    ITabPrefixSuffix.NametagColor.WHITE,
                    0
            ));
            api.refreshPlayer(player);
            return true;
        }
        api.setDisguiseName(player, args[0]);
        api.setDisguiseSkin(player, Skin.getSkin(args[0]));
        api.setDisguiseSkinLayers(player, SkinLayers.getFromRaw((byte) 0b0101010));
        api.setDisguisePrefixSuffix(player, new TabPrefixSuffix(
                Component.text("[").color(TextColor.color(Color.GRAY.getRGB())).append(Component.text("Disguised").color(TextColor.color(Color.GREEN.getRGB()))).append(Component.text("] ").color(TextColor.color(Color.GRAY.getRGB()))),
                Component.text(" [Suffix]"),
                ITabPrefixSuffix.NametagColor.WHITE,
                0
        ));
        for (int i = 0; i < 30; i++) {
            api.refreshPlayer(player, true);
        }
        player.sendMessage("You are now disguised as " + args[0] + "!");
        return true;
    }

    @Override
    protected HashMap<String, Integer> completeTab(Player player, String[] args) {
        return new HashMap<String, Integer>() {
            {
                put("test", 0);
            }
        };
    }
}
