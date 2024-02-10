# Nicknamer-API
This is a simple API that allows you to nickname a player. It currently supports Spigot 1.8.8 and 1.20.4 and paper for both versions. For 1.8 users, I would recommend using a fork like [PandaSpigot](https://github.com/hpfxd/PandaSpigot).

## Features:
The main features of this API are: changing a player's name, changing a player's skin, giving the player a prefix/suffix using a scoreboard team packet. If you want a new version, feel free to make an issue!

# This is early in development and WILL change!
# You can download in the releases section, on [SpigotMC](https://www.spigotmc.org/resources/nicknamer-api.115002/) or on [PaperMC Hangar](https://hangar.papermc.io/onlyjordon/Nicknamer-API).

## Basic Usage

### maven
An example of the dependency in a maven project is (have the Nicknaming-API.jar in a `libs` folder)
```xml
<dependency>
    <groupId>me.onlyjordon</groupId>
    <artifactId>Nicknaming-API</artifactId>
    <version>1.0-SNAPSHOT</version>
    <scope>system</scope>
    <systemPath>${basedir}/libs/Nicknaming-API.jar</systemPath>
</dependency>
```

After that you can simply:
```java 
// get the api
NMSDisguiser disguiser = Nicknamer.getDisguiser();

// then call:
disguiser.setNick(player, "nickname");
disguiser.setSkin(player, "Notch");
disguiser.setSkinLayerVisible(player, SkinLayers.SkinLayer.CAPE, false); // hide cape
disguiser.refreshPlayer(player);
```

Alternatively, you can use Kotlin extension functions:
```kotlin
val player = ... // get the player, e.g. Bukkit.getPlayer(UUID)
player.setNick("nickname")
val name = player.getNick()
player.setSkin("Notch")
player.setSkinLayerVisible(player, SkinLayers.SkinLayer.CAPE, false); // hide cape
player.refresh()
```

You could also use Nicknamer-API to set prefixes and suffixes for players. This uses scoreboard team packets, and will put the prefix & suffix in the player nametag and in the tablist. Example:
```java
// set the prefix/suffix values:
disguiser.setPrefixSuffix(player, Component.text(ChatColor.RED+"Prefix"), Component.text(ChatColor.GREEN+"Suffix"), ChatColor.WHITE); 
// apply the change:
Nicknamer.getDisguiser().updatePrefixSuffix(player);
```
This would make the name of the player in the tablist and above their head "Prefix {player's nickname} Suffix". This uses [Adventure](https://docs.advntr.dev/index.html).
