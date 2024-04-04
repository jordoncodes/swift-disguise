plugins {
    id("com.github.johnrengelman.shadow") version "8.1.1"
    kotlin("jvm") version "1.9.23"
    `maven-publish`
}

repositories {
    maven(url = "https://repo.viaversion.com")
}

dependencies {
    compileOnly("org.spigotmc:spigot-api:1.8.8-R0.1-SNAPSHOT")
//    compileOnly(fileTree("libs") { include("*.jar") })
    implementation(project(":common"))
    implementation(project(":api"))
    implementation("org.jetbrains.kotlin:kotlin-stdlib:1.9.23")
//    implementation("net.kyori:adventure-api:${project.ext.get("adventureVersion")}")
//    implementation("net.kyori:adventure-text-serializer-gson:${project.ext.get("adventureVersion")}")
//    implementation("net.kyori:adventure-text-serializer-legacy:${project.ext.get("adventureVersion")}")
    implementation("org.bspfsystems:yamlconfiguration:2.0.1")
    implementation("com.google.guava:guava:33.1.0-jre")

    implementation("com.github.retrooper.packetevents:spigot:2.2.1")


    compileOnly("com.viaversion:viabackwards-common:4.9.2-SNAPSHOT") {
        isTransitive = false
    }
    compileOnly("com.viaversion:viaversion:4.9.3-SNAPSHOT") {
        isTransitive = false
    }
}

sourceSets.main {
    java.srcDirs("src/main/java", "src/main/kotlin")
}

tasks {
    jar {
        archiveClassifier = "original"
    }

    sourcesJar {
        duplicatesStrategy = DuplicatesStrategy.INCLUDE
    }

    shadowJar {
        archiveClassifier = null
        relocate("org.yaml", "me.onlyjordon.swiftdisguise.libs.yaml")
        relocate("com.github.retrooper", "me.onlyjordon.swiftdisguise.libs.retrooper")
        relocate("io.github.retrooper", "me.onlyjordon.swiftdisguise.libs.retrooper")
        relocate("net.kyori", "me.onlyjordon.swiftdisguise.libs.kyori")
        relocate("com.google.gson", "me.onlyjordon.swiftdisguise.libs.gson")
    }
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            groupId = project.group.toString()
            artifactId = project.name
            version = project.version.toString()
            from(components["java"])
        }
    }
}

tasks.build.get().finalizedBy(tasks.shadowJar.get())

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

copyTask("copyJarsLegacy", file("${project.rootDir}/legacy-test-server/plugins/"))
copyTask("copyJarsPaper", file("${project.rootDir}/test-server-1.20.4/run/plugins/"))
copyTask("copyJarsSpigot", file("${project.rootDir}/spigot-test-server/plugins/"))

repositories {
    mavenCentral()
}
kotlin {
    jvmToolchain(8)
}