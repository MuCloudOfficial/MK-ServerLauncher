plugins{
    kotlin("jvm")
    alias(libs.plugins.kotlin.serialization)
}

group = "me.mucloud"
version = "1.0"

dependencies {
    implementation(libs.bundles.gson)
    implementation(libs.kotlinx.serialization.core)
    implementation(libs.kotlinx.coroutinesSwing)
    implementation(libs.yamlkt)
    implementation(libs.netty.all)
    implementation(libs.okHttp)
    implementation("org.slf4j:slf4j-api:2.0.17")

    implementation(libs.bundles.hikari.sqlite)
    implementation(libs.bundles.exposed)

    testImplementation(libs.bundles.testPack)
}

tasks.test {
    useJUnitPlatform()
}