plugins {
    alias(libs.plugins.multiplatform)
    alias(libs.plugins.android.library)
    alias(libs.plugins.libres)
    alias(libs.plugins.buildConfig)
    alias(libs.plugins.kotlinx.serialization)
}

kotlin {
    androidTarget {
        compilations.all {
            kotlinOptions {
                jvmTarget = BuildVersion.environment.jvmTarget
            }
        }
    }

    jvm()

    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach {
        it.binaries.framework {
            baseName = "core.di"
            isStatic = true
        }
    }

    sourceSets {
        commonMain.dependencies {
            implementation(project(mapOf("path" to ":core:common")))
            implementation(project(mapOf("path" to ":data:datasources-core")))
            implementation(project(mapOf("path" to ":data:repository")))
            implementation(project(mapOf("path" to ":domain:usecases")))
            implementation(project(mapOf("path" to ":presentation:viewmodels")))
            implementation(libs.bundles.layer.core.common)
        }

        androidMain.dependencies {
            implementation(libs.bundles.android.core)
        }

        jvmMain.dependencies {
        }

        iosMain.dependencies {

        }
    }
}

android {
    namespace = "${BuildVersion.environment.applicationId}.core.di"
    compileSdk = BuildVersion.android.compileSdk
    defaultConfig {
        minSdk = BuildVersion.android.minSdk
    }

    sourceSets["main"].apply {
        manifest.srcFile("src/androidMain/AndroidManifest.xml")
        res.srcDirs("src/androidMain/resources")
    }
    compileOptions {
        sourceCompatibility = BuildVersion.environment.javaVersion
        targetCompatibility = BuildVersion.environment.javaVersion
    }
}