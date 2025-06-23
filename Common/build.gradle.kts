plugins{
    kotlin("jvm") version libs.versions.kotlin
}

group = "me.mucloud"
version = "1.0"

dependencies {
    implementation(libs.bundles.gson.pack)
    implementation(libs.bundles.kotlinSerialization)
    implementation(libs.kotlinx.coroutinesSwing)
    implementation("org.slf4j:slf4j-api:2.0.17")

    testImplementation(libs.kotlin.testJunit)
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(11)
}