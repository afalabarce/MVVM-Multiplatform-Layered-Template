@file:Suppress("UnstableApiUsage")

rootProject.name = "MVVM-Multiplatform-Layered-Template"

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

include(":presentation:ui")
include(":presentation:viewmodels")
include(":core:ui")
include(":core:common")
include(":core:di")
include(":core:viewmodels")
include(":domain:usecases")
include(":domain:repository")
include(":domain:models")
include(":data:datasources-core")
include(":data:repository")
include(":data:datasources")
include(":data:models")