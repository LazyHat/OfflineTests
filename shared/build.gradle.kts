import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

val serializationVersion = properties.get("serialization.version")
val swingVersion = properties.get("swing.version")
val kmpVMVersion = properties.get("kmp.viewmodel.version")
val immutableVersion = properties.get("collections.immutable.version")
val appVersion = properties.get("app.version").toString()
val jvmToolChain = properties.get("jvm.toolchain").toString()

plugins {
    kotlin("jvm")
    id("org.jetbrains.compose")
    id("org.jetbrains.kotlin.plugin.serialization")
}

group = "ru.lazyhat"
version = appVersion

repositories {
    mavenCentral()
    gradlePluginPortal()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    google()
}

dependencies {
    implementation(compose.desktop.common)
    //implementation("org.jetbrains.kotlinx:kotlinx-coroutines-swing:$swingVersion")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:$serializationVersion")
    //implementation("io.github.hoc081098:kmp-viewmodel-koin-compose-jvm:$kmpVMVersion")
    implementation("org.jetbrains.kotlinx:kotlinx-collections-immutable:$immutableVersion")
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


tasks.withType<JavaCompile>(){
    sourceCompatibility = jvmToolChain
    targetCompatibility = jvmToolChain
}

tasks.withType<KotlinCompile>(){
    kotlinOptions.jvmTarget = jvmToolChain
}
