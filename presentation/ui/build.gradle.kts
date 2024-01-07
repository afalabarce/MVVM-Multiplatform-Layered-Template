import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    alias(libs.plugins.multiplatform)
    alias(libs.plugins.compose)
    alias(libs.plugins.android.application)
    alias(libs.plugins.libres)
    alias(libs.plugins.buildConfig)
    alias(libs.plugins.kotlinx.serialization)
    alias(libs.plugins.com.google.ksp)
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
            baseName = "presentationUi"
            isStatic = true
        }
    }

    sourceSets {
        commonMain.dependencies {
            implementation(project(mapOf("path" to ":core:ui")))
            implementation(project(mapOf("path" to ":core:common")))
            implementation(project(mapOf("path" to ":core:di")))
            implementation(project(mapOf("path" to ":presentation:viewmodels")))
            implementation(project(mapOf("path" to ":domain:models")))
            implementation(compose.runtime)
            implementation(compose.material3)
            implementation(compose.materialIconsExtended)
            implementation(libs.bundles.layer.core.ui)
        }

        commonTest.dependencies {
            implementation(kotlin("test"))
        }

        androidMain.dependencies {
            implementation(libs.bundles.android.core.ui)
        }

        jvmMain.dependencies {
            implementation(compose.desktop.common)
            implementation(compose.desktop.currentOs)
        }

        iosMain.dependencies {

        }
    }
}

android {
    namespace = "${BuildVersion.environment.applicationId}.presentation.ui"
    compileSdk = BuildVersion.android.compileSdk

    defaultConfig {
        minSdk = BuildVersion.android.minSdk
        targetSdk = BuildVersion.android.compileSdk

        applicationId = BuildVersion.environment.applicationId
        versionCode = BuildVersion.environment.appVersion
        versionName = BuildVersion.environment.appVersionCode
    }
    sourceSets["main"].apply {
        manifest.srcFile("src/androidMain/AndroidManifest.xml")
        res.srcDirs("src/androidMain/resources")
    }
    compileOptions {
        sourceCompatibility = BuildVersion.environment.javaVersion
        targetCompatibility = BuildVersion.environment.javaVersion
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = BuildVersion.environment.composeCompilerVersion
    }
}

compose.desktop {
    application {
        mainClass = "${BuildVersion.environment.applicationId}.MainKt"
        fromFiles(project.fileTree("libs/") { include("**/*.jar") })

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = BuildVersion.environment.appName
            description = BuildVersion.environment.appDescription
            packageVersion = BuildVersion.environment.appVersionCode
            includeAllModules = true
            javaHome = "/Library/Java/JavaVirtualMachines/jdk-17.0.2.jdk"
            vendor = BuildVersion.environment.appVendor
            appResourcesRootDir.set(project.layout.projectDirectory.dir("resources"))
            windows {
                iconFile.set(project.file("src/jvmMain/libres/images/ic_launcher_ico.ico"))
                dirChooser = true
                //javaHome = "C:\\Program Files\\JetBrains\\IntelliJ IDEA Community Edition 2022.2.2\\jbr"
                menuGroup = BuildVersion.environment.menuCategory
            }

            macOS{
                packageBuildVersion = BuildVersion.environment.appVersionCode
                bundleID = "${BuildVersion.environment.applicationId}.MainKt"
                dockName = BuildVersion.environment.appName
                javaHome = "/Library/Java/JavaVirtualMachines/jdk-17.0.2.jdk"
                iconFile.set(project.file("src/iosMain/libres/images/ic_launcher_icns.icns"))
                mainClass = "${BuildVersion.environment.applicationId}.MainKt"
                appCategory = "public.app-category.developer-tools"
            }
        }
    }
}




libres {
    // https://github.com/Skeptick/libres#setup
    generatedClassName = "MainResources"
    generateNamedArguments = true
}
tasks.getByPath("jvmProcessResources").dependsOn("libresGenerateResources")
tasks.getByPath("jvmSourcesJar").dependsOn("libresGenerateResources")

task("testClasses").doLast {
    println("This is a dummy testClasses task")
}

buildConfig {
    // BuildConfig configuration here.
    // https://github.com/gmazzo/gradle-buildconfig-plugin#usage-in-kts
}
