package io.github.afalabarce.mvvmkmmtemplate.data.datasources.core.di

import io.github.afalabarce.mvvmkmmtemplate.data.datasources.core.features.preferences.dataStore
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

actual fun getPlatformInjects(): List<Module> = listOf(
    module {
        singleOf(::dataStore)
    }
)