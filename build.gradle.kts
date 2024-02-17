import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

val koinVersion = properties.get("koin.version")
val serializationVersion = properties.get("serialization.version")
val swingVersion = properties.get("swing.version")
val kmpVMVersion = properties.get("kmp.viewmodel.version")
val immutableVersion = properties.get("collections.immutable.version")
val appVersion = properties.get("app.version").toString()

plugins {
    kotlin("jvm")
    id("org.jetbrains.compose")
    id("org.jetbrains.kotlin.plugin.serialization")
}

group = "ru.lazyhat"
version = appVersion

repositories {
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    google()
}

dependencies {
    implementation(compose.desktop.currentOs)
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-swing:$swingVersion")
    implementation("io.insert-koin:koin-core:$koinVersion")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:$serializationVersion")
    implementation("io.github.hoc081098:kmp-viewmodel-koin-compose-jvm:$kmpVMVersion")
    implementation("org.jetbrains.kotlinx:kotlinx-collections-immutable:$immutableVersion")
}

tasks.withType<KotlinCompile>() {
    kotlinOptions.jvmTarget = "19"
}

compose.desktop {
    application {
        mainClass = "MainKt"

        nativeDistributions {
            targetFormats(TargetFormat.Msi, TargetFormat.Deb)
            packageName = "testeditor"
            packageVersion = appVersion

            appResourcesRootDir.set(project.layout.projectDirectory.dir("src/main/resources"))
        }
    }
}
