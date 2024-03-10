# SwiftDisguise
This is a simple API that allows you to nickname a player. It currently supports Spigot (& forks) 1.8.8 to 1.20.4. For users running servers that don't run java 11+, I would recommend using a Java 11+ fork like [PandaSpigot (1.8.8)](https://github.com/hpfxd/PandaSpigot).
Used in:
- [CapeHider (plugin)](https://github.com/jordoncodes/CapeHider)
- [1hour1life (server)](https://discord.gg/qcUDTArQC7)
## Features:
The main features of this API are: changing a player's name, changing a player's skin, giving the player a prefix/suffix using a scoreboard team packet. If you want a new version, feel free to make an issue!

# This is early in development and WILL change!
# You can download in the releases section, on [SpigotMC](https://www.spigotmc.org/resources/swiftdisguise.115002/) or on [PaperMC Hangar](https://hangar.papermc.io/onlyjordon/SwiftDisguise).

## Basic Usage
A very basic example plugin you can make yours from: [CapeHider](https://github.com/jordoncodes/CapeHider)

### maven
Include the dependency in a maven project:
```xml
<repositories>
    <!-- other repositories -->
    <repository>
        <id>jitpack</id>
        <url>https://jitpack.io</url>
    </repository>
</repositories>
<dependencies>
    <!-- other dependencies -->
    <dependency>
        <groupId>com.github.jordoncodes.swift-disguise</groupId>
        <artifactId>spigot</artifactId>
        <version>v2.0.0</version>
        <scope>provided</scope>
        <exclusions>
            <exclusion>
                <groupId>org.mineskin</groupId>
                <artifactId>java-client</artifactId>
            </exclusion>
        </exclusions>
    </dependency>
</dependencies>
```

Gradle (Kotlin DSL):
```kotlin
dependencies {
    compileOnly("com.github.jordoncodes.swift-disguise:spigot:v2.0.0") {
        exclude("org.mineskin", "java-client")
    }
}
```

After that you can simply:

```java
SwiftDisguiseAPI api = SwiftDisguise.getAPI(SpigotPlatform.get());
api.setDisguiseSkin(player, "Notch"); // set skin to notch's skin
api.setDisguiseName(player, "Notch"); // set name to notch
api.setSkinLayerVisible(player, SkinLayers.SkinLayer.CAPE, false); // hide cape

// V makes the name of the player in the tablist and above their head "Prefix {player's nickname} Suffix". This uses [Adventure](https://docs.advntr.dev/index.html).
api.setDisguisePrefixSuffix(player, new TabPrefixSuffix(
        Component.text("Prefix "),
        Component.text(" Suffix"),
        ITabPrefixSuffix.NametagColor.WHITE,
        0 // priority in the tablist, higher priority = lower position in tablist.
));
api.refreshPlayer(player); // finally, apply the changes!
```

Alternatively, you can use Kotlin extension functions (player is a Bukkit Player):
```kotlin
player.setDisguiseName("Notch") // set player's name to notch
player.setDisguiseSkin("Notch") // set player's skin to notch's skin
player.setSkinLayerVisible(SkinLayers.SkinLayer.CAPE, false) // hide cape
player.refreshPlayer() // finally, apply the changes!
```


# Events
There is only one event right now, which is PlayerSkinLayerChangeEvent
Example of an event to keep the values of setSkinLayerVisible (it changes when the player changes their skin layers by default):
```java
@EventHandler
public void onSkinLayerChange(PlayerSkinLayerChangeEvent event) {
    // force hat to be enabled:
    SkinLayers layers = event.getNewLayers();
    layers.setLayerVisible(SkinLayers.SkinLayer.HAT, true);
    event.setNewLayers(layers);
}
```
