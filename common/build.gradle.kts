dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib:1.9.22")
    api("org.jetbrains:annotations:24.1.0")
    implementation("com.google.code.gson:gson:2.10.1")
    implementation("com.google.guava:guava:33.0.0-jre")

    compileOnlyApi("net.kyori:adventure-api:${project.ext.get("adventureVersion")}")
    compileOnlyApi("net.kyori:adventure-text-serializer-gson:${project.ext.get("adventureVersion")}")
    compileOnlyApi("net.kyori:adventure-text-serializer-legacy:${project.ext.get("adventureVersion")}")
}
