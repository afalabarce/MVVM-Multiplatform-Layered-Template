import org.gradle.api.JavaVersion

object BuildVersion {
    object environment {
        val applicationId = "io.github.afalabarce.mvvmkmmtemplate"
        val appName = "MVVM KMM Template"
        val appVendor = "Antonio Fdez. Alabarce"
        val menuCategory = "MVVM KMM"
        val appDescription = "Template Project to demonstrate KMP capabilities"
        val appVersion = 100
        val appVersionCode = "1.0.0"
        val javaVersion = JavaVersion.VERSION_17
        val jvmTarget = "17"
        val composeCompilerVersion = "1.5.4"
        val appDatabaseName = "KmmDatabase"
    }

    object android {
        val minSdk = 24
        val compileSdk = 34
    }
}