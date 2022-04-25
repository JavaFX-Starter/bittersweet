import org.gradle.internal.os.OperatingSystem

plugins {
    `java-library`
    alias(libs.plugins.kotlin)
}

repositories {
    mavenCentral()
}

val platform = when {
    OperatingSystem.current().isWindows -> {
        "win"
    }
    OperatingSystem.current().isMacOsX -> {
        "mac"
    }
    else -> {
        "linux"
    }
}

dependencies {
    implementation(platform(libs.kotlin.bom))
    implementation(libs.kotlin.stdlib)
    implementation(libs.kotlin.reflect)
    api(libs.slf4j.api)

    compileOnly("org.openjfx:javafx-base:${libs.versions.javafx.version.get()}:${platform}")
    compileOnly("org.openjfx:javafx-controls:${libs.versions.javafx.version.get()}:${platform}")
    compileOnly("org.openjfx:javafx-graphics:${libs.versions.javafx.version.get()}:${platform}")
    compileOnly("org.openjfx:javafx-fxml:${libs.versions.javafx.version.get()}:${platform}")
    compileOnly("org.openjfx:javafx-swing:${libs.versions.javafx.version.get()}:${platform}")
    compileOnly("org.openjfx:javafx-media:${libs.versions.javafx.version.get()}:${platform}")
    compileOnly("org.openjfx:javafx-web:${libs.versions.javafx.version.get()}:${platform}")
}

val compileKotlin: org.jetbrains.kotlin.gradle.tasks.KotlinCompile by tasks
val compileJava: JavaCompile by tasks
compileKotlin.destinationDirectory.set(compileJava.destinationDirectory)

testing {
    suites {
        val test by getting(JvmTestSuite::class) {
            useKotlinTest()
        }
    }
}

group = "com.icuxika.bittersweet"
version = libs.versions.project.version.get()

tasks.compileJava {
    options.release.set(libs.versions.jvm.target.get().toInt())
}

tasks.compileKotlin {
    kotlinOptions {
        jvmTarget = libs.versions.jvm.target.get()
    }
}
