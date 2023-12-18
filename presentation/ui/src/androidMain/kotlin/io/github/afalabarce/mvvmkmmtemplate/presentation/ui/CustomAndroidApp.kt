package io.github.afalabarce.mvvmkmmtemplate.presentation.ui

import io.github.afalabarce.mvvmkmmtemplate.core.common.AndroidApp
import io.github.afalabarce.mvvmkmmtemplate.presentation.ui.di.PresentationUiDependencyInjector
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.GlobalContext.startKoin

class CustomAndroidApp: AndroidApp() {
    override fun onCreate() {
        startKoin {
            androidContext(this@CustomAndroidApp)
            // androidModule: refers to Android-specific dependencies
            // appModule: refers to Shared module dependencies
            modules(
                PresentationUiDependencyInjector.koinModules
            )
        }
        super.onCreate()
    }
}