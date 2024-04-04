import java.util.stream.Collectors

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
            hangar("ViaVersion", "4.9.4-SNAPSHOT+276")
            hangar("ViaBackwards", "4.9.3-SNAPSHOT+150")
            hangar("ViaRewind", "3.0.7-SNAPSHOT+108")
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

tasks.shadowJar.get().dependsOn(":platform:spigot:shadowJar")

fun copyTask(name: String, intoFile: File) {
    tasks.register(name, Copy::class) {
        from(tasks.shadowJar.get().destinationDirectory.get())
        into(intoFile)
        exclude("${project.name}-${project.version}-javadoc.jar")
        exclude("${project.name}-${project.version}-${tasks.jar.get().archiveClassifier.get()}.jar")
        exclude("${project.name}-${project.version}-sources.jar")
    }
    tasks.shadowJar.get().finalizedBy(tasks.getByName(name))
}

//copyTask("copyJarsLegacy", file("${project.rootDir}/legacy-test-server/plugins/"))
//copyTask("copyJarsSpigot", file("${project.rootDir}/spigot-test-server/plugins/"))