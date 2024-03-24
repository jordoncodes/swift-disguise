package me.onlyjordon.swiftdisguise.api;

public class SwiftDisguiseConfig {

    private UUIDHidingMode hidingMode;

    private NameMode nameMode;

    private SwiftDisguiseConfig() {}

    private SwiftDisguiseConfig(UUIDHidingMode hidingMode, NameMode nameMode) {
        this.hidingMode = hidingMode;
        this.nameMode = nameMode;
    }

    public static SwiftDisguiseConfig create(UUIDHidingMode hidingMode, NameMode nameMode) {
        return new SwiftDisguiseConfig(hidingMode, nameMode);
    }

    public NameMode nameMode() {
        return nameMode;
    }

    public UUIDHidingMode hidingMode() {
        return hidingMode;
    }

    @Override
    public String toString() {
        return "SwiftDisguiseConfig{" +
                "hidingMode=" + hidingMode +
                ", nameMode=" + nameMode +
                '}';
    }

    /**
     * The mode to use for hiding UUIDs
     * NONE - No UUID hiding will be done
     * RANDOM - Random UUIDs for each player
     */
    public enum UUIDHidingMode {
        NONE,
        RANDOM
    }

    /**
     * The mode to use for player names
     * WEAK - The server will think the player's name is their real name - commands will be the same as if they weren't nicked
     Util     */
    public enum NameMode {
        WEAK,
        STRONG

    }
}
