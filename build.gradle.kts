plugins {
    `java-library`
    java
    `maven-publish`
    kotlin("jvm") version("1.9.23")
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

allprojects {
    project.ext.set("adventureVersion", "4.16.0")
    project.ext.set("adventureBukkitVersion", "4.3.2")
    apply(plugin = "java")
    apply(plugin = "java-library")
    apply(plugin = "maven-publish")
    group = "me.onlyjordon.swiftdisguise"
    version = "v2.2.0"
    description = ""

    java.sourceCompatibility = JavaVersion.VERSION_1_8
    java.targetCompatibility = JavaVersion.VERSION_1_8

    java {
        withSourcesJar()
        withJavadocJar()
        toolchain.languageVersion.set(JavaLanguageVersion.of(if (System.getenv("JITPACK").toBoolean()) 11 else 17))
    }

    repositories {
        mavenCentral()
        maven(url = "https://jitpack.io")
        maven(url = "https://repo.papermc.io/repository/maven-public/")
        maven(url = "https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
        maven(url = "https://repo.codemc.io/repository/maven-releases/")
        maven(url = "https://repo.inventivetalent.org/repository/public/")
    }

    dependencies {
        implementation("com.google.guava:guava:33.1.0-jre")
    }
}


tasks.create("jitpackBuild", DefaultTask::class) {
    dependsOn(childProjects["common"]!!.tasks.build)
    dependsOn(childProjects["api"]!!.tasks.build)
    dependsOn(childProjects["platform"]!!.childProjects["spigot"]!!.tasks.build)
    group = "build"
}

tasks.build.get().finalizedBy(tasks.shadowJar.get())