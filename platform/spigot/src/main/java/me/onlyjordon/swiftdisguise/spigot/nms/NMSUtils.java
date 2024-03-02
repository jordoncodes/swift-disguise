package me.onlyjordon.swiftdisguise.spigot.nms;

import org.bukkit.Bukkit;

public class NMSUtils {

    private static int[] bukkitVersion;
    private static String minecraftPackage;
    public static String getMinecraftPackage() {
        if (minecraftPackage == null) {
            int[] version = getVersion();
            if (version == null)
                throw new IllegalStateException();
            String versionString = "v" + version[0] + "_" + version[1] + "_R";
            String revision = null;
            for (int i = 1; i <= 3; i++) {
                try {
                    Class.forName("org.bukkit.craftbukkit." + versionString + i + ".CraftServer");
                    revision = versionString + i;
                    break;
                } catch (ClassNotFoundException e) {
                }
            }
            if (revision == null)
                throw new IllegalStateException();
            minecraftPackage = revision;
        }
        return minecraftPackage;
    }

    public static int[] getVersion() {
        if (bukkitVersion == null) {
            String version = Bukkit.getBukkitVersion();

            if (version.isEmpty())
                return bukkitVersion = new int[] { 1, 8 };

            String[] parts = version.split("\\.");
            if (parts[1].contains("-")) {
                parts[1] = parts[1].split("-")[0];
            }
            return bukkitVersion = new int[] { Integer.parseInt(parts[0]), Integer.parseInt(parts[1]) };
        }
        return bukkitVersion;
    }
}
