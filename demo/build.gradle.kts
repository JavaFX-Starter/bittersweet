import org.gradle.internal.os.OperatingSystem

plugins {
    application
    alias(libs.plugins.kotlin)
}

application {
    applicationName = "BitterSweetDemo"
    mainModule.set("bittersweet.demo")
    mainClass.set("com.icuxika.bittersweet.demo.MainAppKt")
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
    implementation(project(":lib"))
    implementation(libs.materialfx)

    implementation(libs.bundles.log4j)

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

testing {
    suites {
        val test by getting(JvmTestSuite::class) {
            useKotlinTest()
        }
    }
}

tasks.processResources {
    filesMatching("application.properties") {
        expand(project.properties)
    }
}

group = "com.icuxika.bittersweet"
version = libs.versions.project.version.get()

tasks.compileJava {
    options.release.set(17)
}

tasks.compileKotlin {
    kotlinOptions {
        jvmTarget = "17"
    }
}
