plugins {
    kotlin("jvm")
    id("io.ktor.plugin") version libs.versions.ktor
    alias(libs.plugins.kotlin.serialization)
}

group = "me.mucloud"
version = "0.0.1"

application {
    mainClass = "io.ktor.server.netty.EngineMain"

    // Development Mode
    val isDevelopment = /*project.ext.has("development")*/ true
    applicationDefaultJvmArgs = listOf("-Dio.ktor.development=$isDevelopment")
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":Common"))
    implementation(libs.bundles.ktor)
    implementation(libs.bundles.gson)

    implementation("ch.qos.logback:logback-classic:1.5.13")
    testImplementation("io.ktor:ktor-server-test-host")
    testImplementation(libs.bundles.testPack)
}
