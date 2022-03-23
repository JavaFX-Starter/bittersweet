plugins {
    `java-library`
    alias(libs.plugins.kotlin)
    alias(libs.plugins.javafx)
}

repositories {
    mavenCentral()
}

javafx {
    version = libs.versions.javafx.version.get()
    modules("javafx.controls", "javafx.fxml")
    configuration = "compileOnly"
}

dependencies {
    implementation(platform(libs.kotlin.bom))
    implementation(libs.kotlin.stdlib)
    implementation(libs.kotlin.reflect)
    api(libs.slf4j.api)
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
