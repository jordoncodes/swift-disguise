plugins {
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

repositories {
    maven(url = "https://oss.sonatype.org/content/repositories/releases/")
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib:1.9.22")
    api("org.jetbrains:annotations:24.1.0")
    sourceSets.main {
        java.srcDirs("src/main/java", "src/main/kotlin")
    }
    compileOnlyApi("net.kyori:adventure-api:${project.ext.get("adventureVersion")}")
    compileOnlyApi("net.kyori:adventure-text-serializer-gson:${project.ext.get("adventureVersion")}")
    compileOnlyApi("net.kyori:adventure-text-serializer-legacy:${project.ext.get("adventureVersion")}")
    implementation("org.bspfsystems:yamlconfiguration:2.0.1")
    implementation(project(":common"))
}

tasks {
    sourcesJar {
        duplicatesStrategy = DuplicatesStrategy.INCLUDE
    }


    jar {
        archiveClassifier = "original"
    }


    shadowJar {
        archiveClassifier = null
        relocate("org.yaml", "me.onlyjordon.swiftdisguise.libs.yaml")
    }

}

tasks.register("prepareKotlinBuildScriptModel") {}
tasks.register("wrapper") {}