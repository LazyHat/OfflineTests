pluginManagement {
    repositories {
        mavenCentral()
        gradlePluginPortal()
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
        google()
    }
    plugins {
        kotlin("jvm").version(extra["kotlin.version"] as String) apply false
        id("org.jetbrains.compose").version(extra["compose.version"] as String) apply false
        id("org.jetbrains.kotlin.plugin.serialization").version(extra["kotlin.version"] as String) apply false
    }
}

rootProject.name = "offline-tests"
include(
    "shared",
    "test-editor",
    "test-guesser"
)