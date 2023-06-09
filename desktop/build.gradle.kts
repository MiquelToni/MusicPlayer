import org.jetbrains.compose.compose
import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile


val kotlin_version = "1.8.10"

plugins {
    kotlin("multiplatform")
    id("org.jetbrains.compose")
}

group = "com.mnm"
version = "1.0-SNAPSHOT"

repositories {
    // Include .jar files into the libs folder
    flatDir { dirs("libs") }
}

kotlin {
    jvm {
        compilations.all {
            kotlinOptions.jvmTarget = "11"
        }
        withJava()
    }
    sourceSets {
        val jvmMain by getting {
            dependencies {
                implementation(project(":common"))
                implementation(compose.desktop.currentOs)

                implementation("org.jaudiotagger:jaudiotagger:2.0.1")
                implementation(":minim")
                implementation(":jsminim")
                implementation(":jl1.0.1")
                implementation(":mp3spi1.9.5")
                implementation(":tritonus_aos")
                implementation(":tritonus_share")

            }
        }
        val jvmTest by getting
    }
}

compose.desktop {
    application {
        mainClass = "MainKt"
        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "MusicPlayer"
            packageVersion = "1.0.0"
        }
    }
}
