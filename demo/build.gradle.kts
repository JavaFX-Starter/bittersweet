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

    implementation(libs.bundles.log4j)
}

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
