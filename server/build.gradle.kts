import org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL
import org.gradle.api.tasks.testing.logging.TestLogEvent.*
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.dsl.KotlinVersion

plugins {
    alias(libs.plugins.kotlin.jvm)
    application
    alias(libs.plugins.ktor)
    alias(libs.plugins.kotlinx.serialization)
}

repositories {
    mavenCentral()
}

application {
    applicationName = "BitterSweetDemo"
    mainClass.set("com.icuxika.bittersweet.server.MainAppKt")

    // .\gradlew.bat :server:run -Pdevelopment
    val isDevelopment: Boolean = project.ext.has("development")
    println("isDevelopment: $isDevelopment")
    val enableKotlinxCoroutinesDebug = if (isDevelopment) "on" else "off"
    applicationDefaultJvmArgs =
        listOf("-Dkotlinx.coroutines.debug=$enableKotlinxCoroutinesDebug", "-Dio.ktor.development=$isDevelopment")
}

dependencies {
    implementation(libs.kotlin.stdlib)
    implementation(libs.kotlin.reflect)
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.datetime)
    implementation(libs.bundles.ktor.server)
    implementation(libs.bundles.logback)
    testImplementation(libs.kotlin.test.junit5)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.ktor.server.test.host)
    testImplementation(libs.ktor.client.content.negotiation)
    testImplementation(libs.ktor.client.logging)
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

tasks.test {
    useJUnitPlatform()
    testLogging {
        exceptionFormat = FULL
        showExceptions = true
        showStandardStreams = true
        events(PASSED, SKIPPED, FAILED, STANDARD_OUT, STANDARD_ERROR)
    }
}