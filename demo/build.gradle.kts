plugins {
    application
    alias(libs.plugins.kotlin)
    alias(libs.plugins.javafx)
}

application {
    applicationName = "BitterSweetDemo"
    mainClass.set("com.icuxika.bittersweet.MainAppKt")
}

repositories {
    mavenCentral()
}

javafx {
    version = libs.versions.javafx.version.get()
    modules("javafx.controls", "javafx.fxml")
}

dependencies {
    implementation(platform(libs.kotlin.bom))
    implementation(libs.kotlin.stdlib)
    implementation(project(":lib"))
    implementation(libs.materialfx)

    implementation(libs.log4j.api)
    implementation(libs.log4j.core)
    implementation(libs.log4j.slf4j.impl)
}

testing {
    suites {
        val test by getting(JvmTestSuite::class) {
            useKotlinTest()
        }
    }
}

group = "com.icuxika.bittersweet"
version = libs.versions.project.version.get()
