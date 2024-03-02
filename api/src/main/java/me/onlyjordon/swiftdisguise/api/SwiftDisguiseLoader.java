package me.onlyjordon.swiftdisguise.api;

import org.bspfsystems.yamlconfiguration.file.YamlConfiguration;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@ApiStatus.Internal
public class SwiftDisguiseLoader {

    private static SwiftDisguiseConfig config;
    private static YamlConfiguration yamlConfig;
    private static File configFile;

    public static File getConfigFile() {
        return configFile;
    }

    public static SwiftDisguiseConfig getConfig() {
        return config;
    }

    public static YamlConfiguration getYamlConfig() {
        return yamlConfig;
    }

    public static void load(File configFile) {
        SwiftDisguiseLoader.configFile = configFile;
        if (!configFile.exists()) {
            if (!configFile.getParentFile().exists()) {
                configFile.getParentFile().mkdirs();
            }
            try {
                configFile.createNewFile();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        yamlConfig = YamlConfiguration.loadConfiguration(configFile);
        loadDefaultConfigValues();
        SwiftDisguiseConfig.UUIDHidingMode hidingMode = getEnum(yamlConfig, "settings.uuid-hiding-mode", SwiftDisguiseConfig.UUIDHidingMode.class, SwiftDisguiseConfig.UUIDHidingMode.RANDOM);
        SwiftDisguiseConfig.NameMode nameMode = getEnum(yamlConfig, "settings.name-mode", SwiftDisguiseConfig.NameMode.class, SwiftDisguiseConfig.NameMode.WEAK);

        config = SwiftDisguiseConfig.create(hidingMode, nameMode);
    }

    private static <E extends Enum<E>> E getEnum(YamlConfiguration configuration, String path, Class<E> enumClass, E defaultValue) {
        String value = configuration.getString(path);
        E enumValue = null;
        try {
            enumValue = Enum.valueOf(enumClass, configuration.getString(path, defaultValue.toString()));
        } catch (IllegalArgumentException ignored) {}
        if (value == null) {
            System.err.println("Invalid enum value at (" + path + "), using default value: " + defaultValue.toString());
        }
        return Optional.ofNullable(enumValue).orElse(defaultValue);
    }

    private static void setDefaultValue(String path, Object value) {
        if (!yamlConfig.contains(path)) {
            yamlConfig.set(path, value);
        }
    }

    private static void loadDefaultConfigValues() {
        setDefaultValue("settings.uuid-hiding-mode",SwiftDisguiseConfig.UUIDHidingMode.RANDOM.toString());
        setDefaultValue("settings.name-mode",SwiftDisguiseConfig.NameMode.WEAK.toString());
        yamlConfig.setComments("settings", Collections.singletonList("The settings for SwiftDisguise"));
        yamlConfig.setComments("settings.uuid-hiding-mode", getUUIDHidingModeComments());
        yamlConfig.setComments("settings.name-mode", getNameModeComments());
        saveConfig();
    }

    private static void saveConfig() {
        try {
            yamlConfig.save(configFile);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static List<String> getUUIDHidingModeComments() {
        List<String> uuidHidingComments = new ArrayList<>();
        uuidHidingComments.add("The mode to use for hiding UUIDs");
        uuidHidingComments.add("NONE - No UUID hiding will be done");
        uuidHidingComments.add("RANDOM - Random UUIDs for each player");
        return uuidHidingComments;
    }

    @NotNull
    private static List<String> getNameModeComments() {
        List<String> nameModeComments = new ArrayList<>();
        nameModeComments.add("The mode to use for player names");
        nameModeComments.add("WEAK - The server will think the player's name is their real name - commands will be the same as if they weren't nicked");
        nameModeComments.add("STRONG - less compatibility, can have issues as the server thinks your name is the nickname, tab completion + commands show nickname (commands don't work with nickname)");
        return nameModeComments;
    }
}
