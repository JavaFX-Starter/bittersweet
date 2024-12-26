import org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL
import org.gradle.api.tasks.testing.logging.TestLogEvent.*
import org.gradle.internal.os.OperatingSystem
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.dsl.KotlinVersion

plugins {
    alias(libs.plugins.kotlin.jvm)
    application
    alias(libs.plugins.beryx.runtime)
}

application {
    applicationName = "BitterSweetDemo"
    mainClass.set("com.icuxika.bittersweet.demo.MainAppKt")
    applicationDefaultJvmArgs =
        listOf(
            "-XX:+PrintCommandLineFlags",
            "-XX:+UseZGC",
            "-XX:+ZGenerational",
            "-Xlog:gc:logs/gc.log",
            "-Dsun.stdout.encoding=UTF-8",
            "-Dsun.stderr.encoding=UTF-8",
            "-Dkotlinx.coroutines.debug"
        )
}

repositories {
    maven {
        url = uri("https://repo.osgeo.org/repository/release")
    }
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
    implementation(libs.kotlin.stdlib)
    implementation(libs.kotlin.reflect)
    testImplementation(libs.kotlin.test.junit5)
    testImplementation(libs.kotlinx.coroutines.test)
    implementation(libs.kotlinx.dataframe) {
        exclude(group = "org.slf4j", module = "slf4j-simple")
    }
    implementation(libs.kotlinx.datetime.jvm)
    implementation(project(":bittersweet"))

    implementation(libs.materialfx)
    implementation(libs.fxgl)

    implementation(libs.bundles.lets.plot)
    implementation(libs.bundles.logback)
    implementation(libs.bundles.okhttp)

    implementation("org.openjfx:javafx-base:${libs.versions.javafx.version.get()}:${platform}")
    implementation("org.openjfx:javafx-controls:${libs.versions.javafx.version.get()}:${platform}")
    implementation("org.openjfx:javafx-graphics:${libs.versions.javafx.version.get()}:${platform}")
    implementation("org.openjfx:javafx-fxml:${libs.versions.javafx.version.get()}:${platform}")
    implementation("org.openjfx:javafx-swing:${libs.versions.javafx.version.get()}:${platform}")
    implementation("org.openjfx:javafx-media:${libs.versions.javafx.version.get()}:${platform}")
    implementation("org.openjfx:javafx-web:${libs.versions.javafx.version.get()}:${platform}")
}

tasks.test {
    useJUnitPlatform()
    jvmArgs = listOf(
        "-Dfile.encoding=UTF-8",
        "-Dkotlinx.coroutines.debug",
        "-Dsun.stdout.encoding=UTF-8",
        "-Dsun.stderr.encoding=UTF-8"
    )
    testLogging {
        exceptionFormat = FULL
        showExceptions = true
        showStandardStreams = true
        events(PASSED, SKIPPED, FAILED, STANDARD_OUT, STANDARD_ERROR)
    }
}

// .\gradlew.bat :demo:run -Penv=production
tasks.processResources {
    inputs.property("env", project.properties["env"])
    filesMatching("application.properties") {
        expand(project.properties)
    }
}

group = "com.icuxika.bittersweet"
version = libs.versions.project.version.get()

tasks.compileJava {
    options.release.set(libs.versions.jvm.target.get().toInt())
}

kotlin {
    compilerOptions {
        jvmTarget.set(JvmTarget.fromTarget(libs.versions.jvm.target.get()))
        languageVersion.set(KotlinVersion.KOTLIN_2_1)
    }
}

runtime {
    options.set(listOf("--strip-debug", "--compress", "zip-9", "--no-header-files", "--no-man-pages"))
    modules.set(
        listOf(
            "java.desktop",
            "java.xml",
            "jdk.unsupported",
            "jdk.jfr",
            "jdk.unsupported.desktop",
            "java.datatransfer",
            "java.scripting",
            "java.rmi",
            "java.sql",
            "java.naming",
            "java.compiler",
            "java.logging",
            "java.management"
        )
    )

    launcher {
        noConsole = true
        jvmArgs = listOf(
            "-XX:+PrintCommandLineFlags",
            "-XX:+UseZGC",
            "-XX:+ZGenerational",
            "-Dsun.stdout.encoding=UTF-8",
            "-Dsun.stderr.encoding=UTF-8",
        )
    }

    jpackage {
        imageName = application.applicationName

        val currentOS = OperatingSystem.current()
        if (currentOS.isMacOsX) {
            imageOptions.addAll(listOf("--icon", "src/main/resources/application.icns"))
        }
        if (currentOS.isWindows) {
            imageOptions.addAll(listOf("--icon", "src/main/resources/application.ico"))
            installerOptions.addAll(
                listOf(
                    "--win-dir-chooser",
                    "--win-menu",
                    "--win-shortcut",
                    "--install-dir",
                    application.applicationName
                )
            )
        }
        if (currentOS.isLinux) {
            imageOptions.addAll(listOf("--icon", "src/main/resources/application.png"))
            installerOptions.addAll(
                listOf(
                    "--linux-deb-maintainer",
                    "icuxika@outlook.com",
                    "--linux-menu-group",
                    application.applicationName,
                    "--linux-shortcut"
                )
            )
        }
    }
}