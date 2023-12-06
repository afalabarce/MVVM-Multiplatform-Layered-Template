@file:Suppress("UnstableApiUsage")

include(":core:di")


include(":core:common")


include(":core:viewmodels")


rootProject.name = "MVVM-Multiplatform-Template"

pluginManagement {
    repositories {
        google()
        gradlePluginPortal()
        mavenCentral()
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    }
}

dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    }
}

include(":composeApp")
include(":core:ui")
include(":core:common")
include(":core:di")
include(":core:viewmodels")
