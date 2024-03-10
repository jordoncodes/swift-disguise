plugins {
    id("com.github.johnrengelman.shadow") version "8.1.1"
    kotlin("jvm") version "1.9.23"
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
    compileOnlyApi("com.github.retrooper.packetevents:spigot:2.2.1")
    implementation("org.bspfsystems:yamlconfiguration:2.0.1")
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

tasks.register("copyJars", Copy::class) {
    from(tasks.shadowJar.get().destinationDirectory.get())
    into(file("${project.rootDir}/test-server-1.20.4/run/plugins/"))
    exclude("${project.name}-${project.version}-javadoc.jar")
    exclude("${project.name}-${project.version}-${tasks.jar.get().archiveClassifier.get()}.jar")
    exclude("${project.name}-${project.version}-sources.jar")
}

tasks.shadowJar.get().finalizedBy(tasks.getByName("copyJars"))

repositories {
    mavenCentral()
}
kotlin {
    jvmToolchain(8)
}