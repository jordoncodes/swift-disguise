plugins {
    id("com.github.johnrengelman.shadow") version "8.1.1"
}


repositories {
    mavenCentral()
}

dependencies {
    api("org.jetbrains:annotations:24.1.0")
    implementation("com.google.code.gson:gson:2.10.1")
    implementation("org.bspfsystems:yamlconfiguration:2.0.1")
    implementation("org.mineskin:java-client:1.2.2-SNAPSHOT")
    compileOnlyApi("net.kyori:adventure-api:${project.ext.get("adventureVersion")}")
    compileOnlyApi("net.kyori:adventure-text-serializer-gson:${project.ext.get("adventureVersion")}")
    compileOnlyApi("net.kyori:adventure-text-serializer-legacy:${project.ext.get("adventureVersion")}")
    implementation("com.google.guava:guava:33.1.0-jre")
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
