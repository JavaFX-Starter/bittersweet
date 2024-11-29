package com.icuxika.bittersweet.demo.util

enum class OS {
    WINDOWS, LINUX, MACOS, OTHER
}

fun getOsName(): String = System.getProperty("os.name")
fun getUserHome(): String = System.getProperty("user.home")

fun getOs(): OS {
    val osName = getOsName().lowercase()
    return when {
        osName.contains("win") -> OS.WINDOWS
        osName.contains("nux") || osName.contains("nix") -> OS.LINUX
        osName.contains("mac") -> OS.MACOS
        else -> OS.OTHER
    }
}

fun isWindows() = getOs() == OS.WINDOWS
fun isLinux() = getOs() == OS.LINUX
fun isMacOS() = getOs() == OS.MACOS