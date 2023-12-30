plugins {
    alias(libs.plugins.multiplatform)
    alias(libs.plugins.android.library)
    alias(libs.plugins.libres)
    alias(libs.plugins.buildConfig)
    alias(libs.plugins.kotlinx.serialization)
    alias(libs.plugins.sqlDelight)
    alias(libs.plugins.com.google.ksp)
    alias(libs.plugins.ktorfit)
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
            baseName = "dataDatasourcesCore"
            isStatic = true
        }
    }

    dependencies {
        add("kspCommonMainMetadata", libs.ktorfit.ksp)
        add("kspAndroid", libs.ktorfit.ksp)
        add("kspIosArm64", libs.ktorfit.ksp)
        add("kspIosX64", libs.ktorfit.ksp)
        add("kspIosSimulatorArm64", libs.ktorfit.ksp)
        add("kspJvm", libs.ktorfit.ksp)
    }

    sourceSets {
        commonMain.dependencies {
            implementation(project(mapOf("path" to ":core:common")))
            implementation(project(mapOf("path" to ":data:models")))
            implementation(project(mapOf("path" to ":data:datasources")))

            implementation(libs.bundles.layer.data.datasources.core)
        }

        commonTest.dependencies {
            implementation(kotlin("test"))
        }

        androidMain.dependencies {
            implementation(libs.bundles.android.data.core)
        }

        jvmMain.dependencies {
            implementation(libs.bundles.jvm.data.core)
        }

        iosMain.dependencies {
            implementation(libs.bundles.ios.data.core)
        }

    }
}


android {
    namespace = "${BuildVersion.environment.applicationId}.data.datasources.core"
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

libres {
    // https://github.com/Skeptick/libres#setup
}
tasks.getByPath("jvmSourcesJar").dependsOn("libresGenerateResources")

buildConfig {
    // BuildConfig configuration here.
    // https://github.com/gmazzo/gradle-buildconfig-plugin#usage-in-kts
}

sqldelight {
    databases {
        create(BuildVersion.environment.appDatabaseName) {
            // Database configuration here.
            // https://cashapp.github.io/sqldelight
            packageName.set("${BuildVersion.environment.applicationId}.data.datasources.core.db")
        }
    }
}

task("testClasses").doLast {
    println("This is a dummy testClasses task")
}
