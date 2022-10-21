plugins {
    kotlin("multiplatform")
    id("com.android.library")
    id("maven-publish")
    kotlin("plugin.serialization") version "1.6.21"
}

group = "com.beeftechlabs"
version = "1.0"
val artifact = "walletconnect-kmm"

kotlin {
    android {
        publishLibraryVariants("release", "debug")
    }

    listOf(
        iosX64 {
            compilations.getByName("main") {
                val WalletConnect by cinterops.creating {
                    // Path to .def file
                    defFile("src/nativeInterop/cinterop/WalletConnect.def")

                    compilerOpts(
                        "-framework",
                        "WalletConnect",
                        "-F/Users/alex/devel/WalletConnectSwiftV2/Build/Release-iphoneos/PackageFrameworks/WalletConnect.framework"
                    )
                }
            }

            binaries.all {
                // Tell the linker where the framework is located.
                linkerOpts(
                    "-framework",
                    "WalletConnect",
                    "-F/Users/alex/devel/WalletConnectSwiftV2/Build/Release-iphoneos/PackageFrameworks/WalletConnect.framework"
                )
            }
        },
        iosArm64(),
        iosSimulatorArm64()
    ).forEach {
        it.binaries.framework {
            baseName = "shared"
        }
    }
    
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.4")
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.4.0")
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
            }
        }
        val androidMain by getting {
            dependencies {
                implementation("com.walletconnect:sign:2.0.0-rc.5")
                implementation("com.walletconnect:android-core:1.0.0")
            }
        }
        val androidTest by getting
        val iosX64Main by getting
        val iosArm64Main by getting
        val iosSimulatorArm64Main by getting
        val iosMain by creating {
            dependsOn(commonMain)
            iosX64Main.dependsOn(this)
            iosArm64Main.dependsOn(this)
            iosSimulatorArm64Main.dependsOn(this)
        }
        val iosX64Test by getting
        val iosArm64Test by getting
        val iosSimulatorArm64Test by getting
        val iosTest by creating {
            dependsOn(commonTest)
            iosX64Test.dependsOn(this)
            iosArm64Test.dependsOn(this)
            iosSimulatorArm64Test.dependsOn(this)
        }
    }
}

android {
    namespace = "com.beeftechlabs.walletconnect"
    compileSdk = 32
    defaultConfig {
        minSdk = 26
        targetSdk = 32
    }
}

kotlin.targets.withType(org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget::class.java) {
    binaries.all {
        binaryOptions["memoryModel"] = "experimental"
    }
}
