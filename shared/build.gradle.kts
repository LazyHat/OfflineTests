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

repositories {
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    google()
}

dependencies {
    implementation(compose.desktop.common)
    implementation(libs.bundles.kotlinx)
    implementation("com.google.devtools.ksp:symbol-processing-api:1.9.22-1.0.16")
    implementation("com.squareup:kotlinpoet:1.16.0")
    implementation("com.squareup:kotlinpoet-ksp:1.16.0")
}

tasks.register("debugEditor"){
    group = "mybuilds"
    doLast { println("DEBUG-EDITOR") }
    finalizedBy(":test-editor:run")
}

tasks.register("releaseEditor"){
    group = "mybuilds"
    doLast { println("RELEASE-EDITOR") }
    finalizedBy(":test-editor:runDistributable")
}

tasks.register("packageEditor"){
    group = "mybuilds"
    doLast { println("PACKAGE-EDITOR") }
    finalizedBy(":test-editor:package")
}

tasks.register("debugGuesser"){
    group = "mybuilds"
    doLast { println("DEBUG-GUESSER") }
    finalizedBy(":test-guesser:run")
}

tasks.register("releaseGuesser"){
    group = "mybuilds"
    doLast { println("RELEASE-GUESSER") }
    finalizedBy(":test-guesser:runDistributable")
}

tasks.register("packageGuesser"){
    group = "mybuilds"
    doLast { println("PACKAGE-GUESSER") }
    finalizedBy(":test-guesser:package")
}

tasks.withType<JavaCompile>{
    sourceCompatibility = jvmToolChain
    targetCompatibility = jvmToolChain
}

tasks.withType<KotlinCompile>{
    kotlinOptions.jvmTarget = jvmToolChain
}
repositories {
    mavenCentral()
}
