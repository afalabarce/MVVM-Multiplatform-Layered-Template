package io.github.afalabarce.mvvmkmmtemplate.data.datasources.core.di

import org.koin.android.ext.koin.androidContext
import org.koin.core.module.Module
import org.koin.dsl.module

actual fun getPlatformInjects(): List<Module> = listOf(
    module {
        factory { androidContext() }
    }
)