import org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL
import org.gradle.api.tasks.testing.logging.TestLogEvent.*
import org.gradle.internal.os.OperatingSystem

plugins {
    alias(libs.plugins.kotlin.jvm)
    `java-library`
    `maven-publish`
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
    testImplementation(libs.kotlin.test.junit5)

    api(libs.slf4j2.api)
    api(libs.kotlinx.coroutines.core)
    api(libs.kotlinx.coroutines.javafx)

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

tasks.test {
    useJUnitPlatform()
    jvmArgs = listOf("-Dfile.encoding=UTF-8", "-Dkotlinx.coroutines.debug", "-Dsun.stdout.encoding=UTF-8")
    testLogging {
        exceptionFormat = FULL
        showExceptions = true
        showStandardStreams = true
        events(PASSED, SKIPPED, FAILED, STANDARD_OUT, STANDARD_ERROR)
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

java {
    withSourcesJar()
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            groupId = project.group.toString()
            artifactId = project.name.lowercase()
            version = project.version.toString()

            from(components["java"])
        }

        repositories {
            maven {
                val releasesRepoUrl = uri(layout.buildDirectory.dir("repos/releases"))
                val snapshotsRepoUrl = uri(layout.buildDirectory.dir("repos/snapshots"))
                url = if (version.toString().endsWith("SNAPSHOT")) snapshotsRepoUrl else releasesRepoUrl
            }
        }
    }
}
