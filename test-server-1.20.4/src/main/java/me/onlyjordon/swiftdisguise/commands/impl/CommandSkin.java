package me.onlyjordon.swiftdisguise.commands.impl;

import me.onlyjordon.swiftdisguise.api.SwiftDisguiseAPI;
import me.onlyjordon.swiftdisguise.commands.PlayerOnlyCommand;
import me.onlyjordon.swiftdisguise.utils.Skin;
import me.onlyjordon.swiftdisguise.utils.Util;
import org.bukkit.entity.Player;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;

public class CommandSkin extends PlayerOnlyCommand {

    private final SwiftDisguiseAPI api;

    public CommandSkin(SwiftDisguiseAPI api) {
        super("skin", "skin.use");
        this.api = api;
    }

    @Override
    protected boolean execute(Player player, String[] args) {
        if (args.length == 0) {
            player.sendMessage("Usage: /skin <url/player/filename>");
            return true;
        }
        try {
            URL url = new URL(args[0]); // check if the argument is a valid URL
            api.setDisguiseSkin(player, Skin.getSkin(url));
            api.refreshPlayer(player);
            return true;
        } catch (MalformedURLException e) {
            if (args[0].endsWith(".png")) {
                File file = new File(Util.getDataFolder()+File.separator+"skins"+File.separator+args[0]);
                Skin skin = Skin.getSkin(file);
                api.setDisguiseSkin(player, skin);
                api.refreshPlayer(player);
                return true;
            }
            Skin skin = Skin.getSkin(args[0]);
            api.setDisguiseSkin(player, skin);
            api.refreshPlayer(player);
            return true;
        }
    }

    @Override
    protected HashMap<String, Integer> completeTab(Player player, String[] args) {
        return null;
    }
}
