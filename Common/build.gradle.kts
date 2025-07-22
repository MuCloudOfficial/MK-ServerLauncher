plugins{
    kotlin("jvm") version libs.versions.kotlin
    alias(libs.plugins.kotlin.serialization)
}

group = "me.mucloud"
version = "1.0"

dependencies {
    implementation(libs.bundles.gson.pack)
    implementation(libs.kotlinx.serialization.core)
    implementation(libs.kotlinx.coroutinesSwing)
    implementation(libs.yamlkt)
    implementation(libs.netty.all)
    implementation("org.slf4j:slf4j-api:2.0.17")

    testImplementation(libs.kotlin.testJunit)
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(11)
}