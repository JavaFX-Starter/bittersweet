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
    implementation(platform("org.jetbrains.kotlin:kotlin-bom"))
    implementation(kotlin("stdlib"))

    implementation(project(":lib"))
    implementation("io.github.palexdev:materialfx:11.12.0")

    implementation("org.apache.logging.log4j:log4j-api:2.17.1")
    implementation("org.apache.logging.log4j:log4j-core:2.17.1")
    implementation("org.apache.logging.log4j:log4j-slf4j-impl:2.17.1")
}

testing {
    suites {
        val test by getting(JvmTestSuite::class) {
            useKotlinTest()
        }
    }
}