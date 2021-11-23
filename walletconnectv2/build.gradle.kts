plugins {
    kotlin("multiplatform")
    kotlin("native.cocoapods")
    id("com.android.library")
    kotlin("plugin.serialization") version "1.5.31"
}

version = "1.0"

val ktorVersion = "1.6.3"
val kotlinVersion = "1.6.0"
val multiplatformSettings = "0.8.1"

kotlin {
    android()
    iosX64()
    iosArm64()
//    iosSimulatorArm64()

    cocoapods {
        summary = "WalletConnectV2 Kotlin Multiplatform implementation for Android, iOS and JS"
        homepage = "https://beeftechlabs.com"
        ios.deploymentTarget = "14.1"
        podfile = project.file("../iosWallet/Podfile")
        framework {
            baseName = "walletconnectv2"
        }
    }
    
    sourceSets {
        all {
            languageSettings.apply {
                apiVersion = "1.5"
                languageVersion = "1.5"
                progressiveMode = true
                optIn("kotlinx.coroutines.ExperimentalCoroutinesApi")
                optIn("kotlinx.serialization.InternalSerializationApi")
            }
        }
        val commonMain by getting {
            dependencies {
                implementation("org.jetbrains.kotlin:kotlin-stdlib:$kotlinVersion")
                implementation("org.jetbrains.kotlin:kotlin-reflect:$kotlinVersion")

                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core") {
                    version { strictly("1.5.2-native-mt") }
                }
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.3.0")

                implementation("io.ktor:ktor-client-core:$ktorVersion")
                implementation("io.ktor:ktor-client-serialization:$ktorVersion")
                implementation("io.ktor:ktor-client-logging:$ktorVersion")
                implementation("io.ktor:ktor-client-websockets:$ktorVersion")
                implementation("ch.qos.logback:logback-classic:1.2.6")
                implementation("io.ktor:ktor-utils:$ktorVersion")

                implementation("com.russhwolf:multiplatform-settings-no-arg:$multiplatformSettings")
                implementation("com.russhwolf:multiplatform-settings-serialization:$multiplatformSettings")
                implementation("com.russhwolf:multiplatform-settings-coroutines:$multiplatformSettings")

                implementation("com.ionspin.kotlin:multiplatform-crypto-libsodium-bindings:0.8.4")
                implementation("com.soywiz.korlibs.krypto:krypto:2.4.6")
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test-common"))
                implementation(kotlin("test-annotations-common"))
            }
        }
        val androidMain by getting {
            dependencies {
                implementation("io.ktor:ktor-client-cio:$ktorVersion")
                implementation("com.squareup.okhttp3:okhttp:4.9.1")
            }
        }
        val androidTest by getting {
            dependencies {
                implementation(kotlin("test-junit"))
                implementation("junit:junit:4.13.2")
            }
        }

        val iosX64Main by getting
        val iosArm64Main by getting
//        val iosSimulatorArm64Main by getting
        val iosMain by creating {
            dependencies {
                implementation("io.ktor:ktor-client-ios:$ktorVersion")
            }
            dependsOn(commonMain)
            iosX64Main.dependsOn(this)
            iosArm64Main.dependsOn(this)
//            iosSimulatorArm64Main.dependsOn(this)
        }
        val iosX64Test by getting
        val iosArm64Test by getting
//        val iosSimulatorArm64Test by getting
        val iosTest by creating {
            dependsOn(commonTest)
            iosX64Test.dependsOn(this)
            iosArm64Test.dependsOn(this)
//            iosSimulatorArm64Test.dependsOn(this)
        }
    }
}

android {
    compileSdk = 31
    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
    defaultConfig {
        minSdk = 21
        targetSdk = 31
    }
}