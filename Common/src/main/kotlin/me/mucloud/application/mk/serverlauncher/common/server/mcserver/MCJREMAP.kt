package me.mucloud.application.mk.serverlauncher.common.server.mcserver

enum class MCJavaEnvCompMap(
    val lowestCode: Int,
    val recommendCode: Int,
) {
    V1_6_X(49, 50), // Java 5 or 6 at least, Java 6 Recommended. But probably Unsupported.
    V1_16_X(51, 52), // Java 7 at least, Java 8 Recommended.
    V1_20_R2(61, 61), // Java 21 at least
    V1_21_X(65, 65), // Up to Java 23
}

enum class JavaVersion(
    val jVer: String,
    val code: Int,
){
    V1_7("1.7", 51),
    V1_8("1.8", 52),
    V9("9", 53),
    V10("10", 54),
    V11("11", 55),
    V12("12", 56),
    V13("13", 57),
    V14("14", 58),
    V15("15", 59),
    V16("16", 60),
    V17("17", 61),
    V18("18", 62),
    V19("19", 63),
    V20("20", 64),
    V21("21", 65),
    V22("22", 66),
    V23("23", 67),
    V24("24", 68),
    ;

    override fun toString(): String = code.toString()
}