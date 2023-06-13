import org.jetbrains.compose.compose

object Versions {
    const val serializationVersion = "1.8.10"
}

plugins {
    kotlin("multiplatform")
    id("org.jetbrains.compose")
    id("com.android.library")
    id("org.jetbrains.kotlin.plugin.serialization") version "1.8.10"
}

group = "com.mnm"
version = "1.0-SNAPSHOT"


val ktorClient = "2.2.3"


repositories {
    // Include .jar files into the libs folder
    flatDir { dirs("libs") }
}

kotlin {
    android()
    jvm("desktop") {
        compilations.all {
            kotlinOptions.jvmTarget = "11"
        }
    }
    sourceSets {
        val commonMain by getting {
            dependencies {
                api(compose.runtime)
                api(compose.foundation)
                api(compose.material)
                api(compose.materialIconsExtended)

                // JSON
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.5.0")
                implementation("io.ktor:ktor-client-content-negotiation:$ktorClient")
                implementation("io.ktor:ktor-serialization-kotlinx-json:$ktorClient")

                implementation("io.ktor:ktor-client-core:$ktorClient")
                implementation("io.ktor:ktor-client-cio:$ktorClient")
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
            }
        }
        val androidMain by getting {
            dependencies {
                api("androidx.appcompat:appcompat:1.5.1")
                api("androidx.core:core-ktx:1.9.0")
            }
        }
        val androidTest by getting {
            dependencies {
                implementation("junit:junit:4.13.2")
            }
        }
        val desktopMain by getting {
            dependencies {
                api(compose.preview)

                implementation("org.jaudiotagger:jaudiotagger:2.0.1")
                implementation(":minim")
                implementation(":jsminim")
                implementation(":jl1.0.1")
                implementation(":mp3spi1.9.5")
                implementation(":tritonus_aos")
                implementation(":tritonus_share")
            }
        }
        val desktopTest by getting
    }
}

android {
    compileSdkVersion(33)
    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
    defaultConfig {
        minSdkVersion(24)
        targetSdkVersion(33)
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}