# SwiftDisguise
This is a simple API that allows you to nickname a player. It currently supports Spigot 1.8.8 and 1.20.4 and paper for both versions. For 1.8 users, I would recommend using a fork like [PandaSpigot](https://github.com/hpfxd/PandaSpigot).
Used in: 
- [CapeHider (plugin)](https://github.com/jordoncodes/CapeHider)
- [1hour1life (server)](https://discord.gg/qcUDTArQC7)
## Features:
The main features of this API are: changing a player's name, changing a player's skin, giving the player a prefix/suffix using a scoreboard team packet. If you want a new version, feel free to make an issue!

# This is early in development and WILL change!
# You can download in the releases section, on [SpigotMC](https://www.spigotmc.org/resources/nicknamer-api.115002/) or on [PaperMC Hangar](https://hangar.papermc.io/onlyjordon/Nicknamer-API).

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
        <groupId>com.github.jordoncodes</groupId>
        <artifactId>nicknamer-api</artifactId>
        <version>v1.3.0</version>
        <scope>provided</scope> <!-- without this, Nicknamer.getDisguiser() will give you null -->
    </dependency>
</dependencies>
```

After that you can simply:

```java
Nicknamer namer = NicknamerAPI.getNicknamer();
new DisguiseBuilder(nicknamer)
        .setNick("nickname")
        .setSkin("Notch")
        .setPrefixSuffix(Component.text(ChatColor.RED+"Prefix"), Component.text(ChatColor.GREEN+"Suffix"), ChatColor.WHITE, 0)
        .setSkinLayerVisible(SkinLayers.SkinLayer.CAPE, false)
        .apply(player);
```

You could also use the Nicknamer functions directly, rather than using a builder:
```java 
// get the api
Nicknamer disguiser = NicknamerAPI.getNicknamer();

// then call:
disguiser.setNick(player, "nickname");
disguiser.setSkinLayerVisible(player, SkinLayers.SkinLayer.CAPE, false); // hide cape
disguiser.setSkin(player, "Notch"); // when using a string for the skin (instead of a Skin), it's best to set the
                                    // skin last because it calls refreshPlayer() as it downloads the skin async
                                    // then you don't need to call refreshPlayer()

// a better version of this:
// you could make it not async or get rid of the bukkit task, but it would download the skin synchronously
Bukkit.getScheduler().runTaskAsynchronously(plugin, ()-> {
    Skin skin = Skin.getSkin("Notch"); // get skin
    disguiser.setNick(player, "nickname"); // set nickname
    disguiser.setSkinLayerVisible(player, SkinLayers.SkinLayer.CAPE, false); // hide cape
    disguiser.setPrefixSuffix(player, Component.text(ChatColor.GREEN + "A prefix "), Component.text(""), ChatColor.GRAY, 1); // set prefix & suffix
    disguiser.setSkin(player, skin); // set skin, doing setSkin(Player,String) will create another async task
    disguiser.refreshPlayer(player); // update all of ^
});
```

Alternatively, you can use Kotlin extension functions:
```kotlin
val player = ... // get the player, e.g. Bukkit.getPlayer(UUID)
player.setNick("nickname")
val name = player.getNick()
player.setSkin("Notch")
player.setSkinLayerVisible(player, SkinLayers.SkinLayer.CAPE, false) // hide cape
player.refresh()
```

You could also use Nicknamer-API to set prefixes and suffixes for players. This uses scoreboard team packets, and will put the prefix & suffix in the player nametag and in the tablist. Example:
```java
// set the prefix/suffix values:
disguiser.setPrefixSuffix(player, Component.text(ChatColor.RED+"Prefix"), Component.text(ChatColor.GREEN+"Suffix"), ChatColor.WHITE, 0); // last param (int) is a priority in the tablist, higher priority = lower position in tablist.
// apply the change:
disguiser.updatePrefixSuffix(player);
```

This would make the name of the player in the tablist and above their head "Prefix {player's nickname} Suffix". This uses [Adventure](https://docs.advntr.dev/index.html).

# Events
There are a few events you could use that this plugin makes.

Example of an event to keep the values of setSkinLayerVisible (it changes when the player changes their skin layers by default):
```java
@EventHandler
public void onSkinLayerChange(PlayerSkinLayerChangeEvent event) {
    // Reason.PLAYER = player changed their skinlayers
    // Reason.PLUGIN = plugin changed their skinlayers
    if (e.getReason() == PlayerSkinLayerChangeEvent.Reason.PLAYER) e.setCancelled(true);
}
// or this
@EventHandler
public void onSkinLayerChange(PlayerSkinLayerChangeEvent event) {
    // force hat to be enabled:
    SkinLayers layers = event.getNewLayers();
    layers.setLayerVisible(SkinLayers.SkinLayer.HAT, true);
    event.setNewLayers(layers);
}
```
disable changing nicknames entirely:

```java
@EventHandler
public void onNickChange(PlayerNickChangeEvent event) {
    event.setCancelled(true);
}
```
force everyone's skin to be a certain skin:

```java
@EventHandler
public void onJoin(PlayerJoinEvent event) {
    NicknamerAPI.getNicknamer().setSkin(event.getPlayer(), "Notch");
    NicknamerAPI.getNicknamer().refreshPlayer(event.getPlayer());
}

@EventHandler
public void onSkinChange(PlayerSkinChangeEvent event) {
    event.setNewSkin(Skin.getSkin("Notch"));
}
```

and more in the `me.onlyjordon.nicknamingapi.events` package.

# Errors
## Null Nicknamer
I'm getting a null nicknamer error? What do I do?

You probably have Nicknamer API in your jar. Try following [this](https://github.com/jordoncodes/nicknamer-api?tab=readme-ov-file#maven)

If you're having another issue, and nothing is in console, you could try setting the environment variable NICKNAMER_DEV to true, and it will print out information in chat, and allow you to use `/nickdev`. 
