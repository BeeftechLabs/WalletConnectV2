pluginManagement {
    repositories {
        google()
        gradlePluginPortal()
        mavenCentral()
        mavenLocal()
    }
}

dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
        mavenLocal()
        maven("https://maven.scijava.org/content/repositories/public/")
    }
}

rootProject.name = "walletconnect-kmm"
include(":walletconnect-kmm")