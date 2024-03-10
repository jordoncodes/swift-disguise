package me.onlyjordon.swiftdisguise.utils;

import org.mineskin.MineskinClient;

import java.io.File;
import java.net.URISyntaxException;

public class Util {
    private static final File dataFolder;
    private static final MineskinClient mineSkinClient;
    private static final boolean paper;

    static {
        try {
            dataFolder = new File(new File(Skin.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath()).getParentFile() + File.separator + "SwiftDisguise");
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
        boolean isPaper = false;
        try {
            // Any other works, just the shortest I could find.
            Class.forName("com.destroystokyo.paper.ParticleBuilder");
            isPaper = true;
        } catch (ClassNotFoundException ignored) {
        }
        paper = isPaper;
        mineSkinClient = new MineskinClient("loser");
    }

    public static boolean isPaper() {
        return paper;
    }

    public static MineskinClient getMineSkinClient() {
        return mineSkinClient;
    }

    public static File getDataFolder() {
        return dataFolder;
    }
}
