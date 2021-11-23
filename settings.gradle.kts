pluginManagement {
    repositories {
        google()
        gradlePluginPortal()
        mavenCentral()
    }
}

rootProject.name = "WalletConnect"
include(":walletconnectv2")
include(":androidDapp")
include(":androidWallet")
