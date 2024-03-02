plugins {
    id("xyz.jpenilla.run-paper") version "2.2.3"
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

repositories {
}

dependencies {
    compileOnly("org.spigotmc:spigot-api:1.8.8-R0.1-SNAPSHOT")
    compileOnly(project(":platform:spigot"))
    compileOnly(project(":common"))
    compileOnly(project(":api"))
}

tasks {
    runServer {
        minecraftVersion("1.20.4")
        downloadPlugins {
            modrinth("packetevents", "2.2.1")
        }
    }

    jar.get().dependsOn("shadowJar")

    shadowJar {
        archiveClassifier = null
    }

    jar {
        archiveClassifier = "original"
    }

}