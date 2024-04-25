import org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL
import org.gradle.api.tasks.testing.logging.TestLogEvent.*
import org.gradle.internal.os.OperatingSystem

plugins {
    alias(libs.plugins.kotlin.jvm)
    application
}

application {
    applicationName = "BitterSweetDemo"
    mainModule.set("bittersweet.demo")
    mainClass.set("com.icuxika.bittersweet.demo.MainAppKt")
    applicationDefaultJvmArgs = listOf("-Dsun.stdout.encoding=UTF-8", "-Dkotlinx.coroutines.debug")
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
    implementation(libs.kotlin.test.junit5)
    implementation(project(":bittersweet"))
    implementation(libs.materialfx)

    implementation(libs.bundles.logback)

    implementation("org.openjfx:javafx-base:${libs.versions.javafx.version.get()}:${platform}")
    implementation("org.openjfx:javafx-controls:${libs.versions.javafx.version.get()}:${platform}")
    implementation("org.openjfx:javafx-graphics:${libs.versions.javafx.version.get()}:${platform}")
    implementation("org.openjfx:javafx-fxml:${libs.versions.javafx.version.get()}:${platform}")
    implementation("org.openjfx:javafx-swing:${libs.versions.javafx.version.get()}:${platform}")
    implementation("org.openjfx:javafx-media:${libs.versions.javafx.version.get()}:${platform}")
    implementation("org.openjfx:javafx-web:${libs.versions.javafx.version.get()}:${platform}")
}

val compileKotlin: org.jetbrains.kotlin.gradle.tasks.KotlinCompile by tasks
val compileJava: JavaCompile by tasks
compileKotlin.destinationDirectory.set(compileJava.destinationDirectory)

tasks.test {
    useJUnitPlatform()
    jvmArgs = listOf("-Dfile.encoding=UTF-8", "-Dkotlinx.coroutines.debug", "-Dsun.stdout.encoding=UTF-8", "-Dsun.stderr.encoding=UTF-8")
    testLogging {
        exceptionFormat = FULL
        showExceptions = true
        showStandardStreams = true
        events(PASSED, SKIPPED, FAILED, STANDARD_OUT, STANDARD_ERROR)
    }
}

// 未生效的话，重新执行clean build run
tasks.processResources {
    filesMatching("application.properties") {
        expand(project.properties)
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
