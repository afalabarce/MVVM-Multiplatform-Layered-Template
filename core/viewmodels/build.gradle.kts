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
            baseName = "core.viewmodels"
            isStatic = true
        }
    }


    sourceSets {
        commonMain.dependencies {
            implementation(project(mapOf("path" to ":core:common")))
            implementation(libs.bundles.layer.core.viewmodels)
        }

        commonTest.dependencies {
            implementation(kotlin("test"))
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
    namespace = "${BuildVersion.environment.applicationId}.core.viewmodels"
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
