plugins {
    `java-library`
    java
    `maven-publish`
    kotlin("jvm") version("1.9.22")
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

allprojects {
    project.ext.set("adventureVersion", "4.15.0")
    project.ext.set("adventureBukkitVersion", "4.3.2")
    apply(plugin = "java")
    apply(plugin = "java-library")
    apply(plugin = "maven-publish")
    group = "me.onlyjordon.swiftdisguise"
    version = "v2.0.0"
    description = ""

    java.sourceCompatibility = JavaVersion.VERSION_1_8
    java.targetCompatibility = JavaVersion.VERSION_1_8

    java {
        withSourcesJar()
        withJavadocJar()
        toolchain.languageVersion.set(JavaLanguageVersion.of(17))
    }

    repositories {
        mavenCentral()
        maven(url = "https://jitpack.io")
        maven(url = "https://repo.papermc.io/repository/maven-public/")
        maven(url = "https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
        maven(url = "https://repo.codemc.io/repository/maven-releases/")
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
}

