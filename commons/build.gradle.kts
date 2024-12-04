import org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL
import org.gradle.api.tasks.testing.logging.TestLogEvent.*

plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlinx.serialization)
    `java-library`
    `maven-publish`
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(libs.kotlin.stdlib)
    implementation(libs.kotlin.reflect)
    testImplementation(libs.kotlin.test.junit5)
    testImplementation(libs.kotlinx.coroutines.test)
    implementation(libs.kotlinx.serialization.json)
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