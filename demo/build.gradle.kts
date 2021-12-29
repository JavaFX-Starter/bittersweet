plugins {
    kotlin("jvm") version "1.6.10"
    application
    id("org.openjfx.javafxplugin") version "0.0.10"
}

group = "com.icuxika.bittersweet"
version = "0.0.1"

application {
    applicationName = "BitterSweetDemo"
    mainClass.set("com.icuxika.bittersweet.MainAppKt")
}

repositories {
    mavenCentral()
}

javafx {
    version = "17"
    modules("javafx.controls", "javafx.fxml")
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation(project(":lib"))
}

testing {
    suites {
        val test by getting(JvmTestSuite::class) {
            useKotlinTest()
        }
    }
}