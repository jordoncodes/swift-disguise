rootProject.name = "SwiftDisguise"
plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version("0.7.0")
}
include("common")
include("api")
include("platform:spigot")
if (!(System.getenv("JITPACK")).toBoolean()) {
    include("test-server")
}
