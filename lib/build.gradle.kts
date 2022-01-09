plugins {
    kotlin("jvm") version "1.6.10"
    `java-library`
    id("org.openjfx.javafxplugin") version "0.0.10"
}

repositories {
    mavenCentral()
}

javafx {
    version = "17"
    modules("javafx.controls", "javafx.fxml")
    configuration = "compileOnly"
}

dependencies {
    implementation(platform("org.jetbrains.kotlin:kotlin-bom"))
    implementation(kotlin("stdlib"))
    implementation(kotlin("reflect"))
    api("org.slf4j:slf4j-api:1.7.32")
}

testing {
    suites {
        val test by getting(JvmTestSuite::class) {
            useKotlinTest()
        }
    }
}
