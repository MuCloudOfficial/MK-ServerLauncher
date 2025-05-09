package me.mucloud.application.MK.ServerLauncher.internal.server.mcserver

enum class MCJavaEnvMap(
    val lowestCode: Int,
    val recommendCode: Int,
) {
    V1_6_X(49, 50), // Java 5 or 6 at least, Java 6 Recommended. But probably Unsupported.
    V1_16_X(51, 52), // Java 7 at least, Java 8 Recommended.
    V1_20_R2(61, 61), // Java 21 at least
    V1_21_X(65, 65), // Up to Java 23
}
