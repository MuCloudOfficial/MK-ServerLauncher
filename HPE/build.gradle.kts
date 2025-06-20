plugins {
    kotlin("jvm") version "2.1.20"
    id("io.ktor.plugin") version "3.2.0"
    id("org.jetbrains.kotlin.plugin.serialization") version "2.1.20"
}

group = "me.mucloud"
version = "VoidLand V1.DEV.1"

application {
    mainClass = "io.ktor.server.netty.EngineMain"

    val isDevelopment = /*project.ext.has("development")*/ true
    applicationDefaultJvmArgs = listOf("-Dio.ktor.development=$isDevelopment")
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("io.ktor:ktor-server-core")
    implementation("io.ktor:ktor-server-auth")
    implementation("io.ktor:ktor-server-host-common")
    implementation("io.ktor:ktor-server-status-pages")
    implementation("io.ktor:ktor-server-content-negotiation")
    implementation("io.ktor:ktor-server-websockets")
    implementation("io.ktor:ktor-server-netty")
    implementation("io.ktor:ktor-server-config-yaml")
    implementation("io.ktor:ktor-server-request-validation")
    implementation("io.ktor:ktor-server-cors")
    implementation("io.ktor:ktor-serialization-gson")
    implementation("ch.qos.logback:logback-classic:1.5.13")
    implementation("org.jsoup:jsoup:1.19.1")
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    implementation("org.danilopianini:gson-extras:3.3.0")

    implementation("me.mucloud:Common:1.0")

    testImplementation("io.ktor:ktor-server-test-host")
    testImplementation("io.ktor:ktor-server-test-host-jvm")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit")
}
