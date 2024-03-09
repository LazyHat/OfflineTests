import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

val appVersion = properties["app.version"].toString()
val jvmToolChain = libs.versions.jvm.toolchain.get()

plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.compose)
}

group = "ru.lazyhat"
version = appVersion

dependencies {
    implementation(project(":shared"))
    implementation(compose.desktop.currentOs)
    implementation(compose.desktop.uiTestJUnit4)
    implementation(libs.bundles.kotlinx)
    implementation(libs.viewmodel)
}

compose.desktop {
    application {
        mainClass = "MainKt"

        nativeDistributions {
            targetFormats(TargetFormat.Msi, TargetFormat.Deb)

            packageName = "test-guesser"
            packageVersion = appVersion

            //appResourcesRootDir.set(project.layout.projectDirectory.dir("test-guesser/src/main/resources"))
        }
    }
}


tasks.withType<KotlinCompile>{
    kotlinOptions.jvmTarget = jvmToolChain
}

tasks.withType<JavaCompile>{
    sourceCompatibility = jvmToolChain
    targetCompatibility = jvmToolChain
}