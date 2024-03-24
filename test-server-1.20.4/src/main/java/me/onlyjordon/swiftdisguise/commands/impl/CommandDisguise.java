package me.onlyjordon.swiftdisguise.commands.impl;

import me.onlyjordon.swiftdisguise.SpigotPlatform;
import me.onlyjordon.swiftdisguise.api.ISwiftDisguiseAPI;
import me.onlyjordon.swiftdisguise.api.ITabPrefixSuffix;
import me.onlyjordon.swiftdisguise.api.SwiftDisguise;
import me.onlyjordon.swiftdisguise.api.TabPrefixSuffix;
import me.onlyjordon.swiftdisguise.commands.PlayerOnlyCommand;
import me.onlyjordon.swiftdisguise.utils.Skin;
import me.onlyjordon.swiftdisguise.utils.UUIDFetcher;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;

import java.util.HashMap;

public class CommandDisguise extends PlayerOnlyCommand {
    public CommandDisguise() {
        super("disguise", "disguise.use");
    }

    @Override
    protected boolean execute(Player player, String[] args) {
        ISwiftDisguiseAPI api = SwiftDisguise.getAPI(SpigotPlatform.get());
        api.setDisguiseName(player, args[0]);
        api.setDisguiseSkin(player, Skin.getSkin(args[0]));
        api.setDisguisePrefixSuffix(player, new TabPrefixSuffix(
                Component.text("Disguied "),
                Component.text(" Suffix"),
                ITabPrefixSuffix.NametagColor.WHITE,
                0
        ));
        try {
            api.setDisguiseUniqueId(player, UUIDFetcher.fetchUUID(args[0]).get());
        } catch (Exception e) {
            e.printStackTrace();
        }

        for (int i = 0; i < 30; i++) {
            api.refreshPlayer(player);
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
