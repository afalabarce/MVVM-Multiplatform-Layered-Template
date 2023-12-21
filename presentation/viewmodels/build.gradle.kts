plugins {
    alias(libs.plugins.multiplatform)
    alias(libs.plugins.android.library)
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
            baseName = "presentation.viewmodels"
            isStatic = true
        }
    }

    sourceSets {
        commonMain.dependencies {
            implementation(project(mapOf("path" to ":core:common")))
            implementation(project(mapOf("path" to ":core:viewmodels")))
            implementation(project(mapOf("path" to ":domain:usecases")))
            implementation(project(mapOf("path" to ":domain:models")))
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
    namespace = "${BuildVersion.environment.applicationId}.presentation.viewmodels"
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

tasks.getByPath("jvmSourcesJar").dependsOn("libresGenerateResources")

buildConfig {
    // BuildConfig configuration here.
    // https://github.com/gmazzo/gradle-buildconfig-plugin#usage-in-kts
}
