package io.github.afalabarce.mvvmkmmtemplate.data.datasources.core.di

import de.jensklingenberg.ktorfit.Ktorfit
import io.github.afalabarce.mvvmkmmtemplate.core.common.di.KoinModuleLoader
import io.github.afalabarce.mvvmkmmtemplate.data.datasources.core.db.Database
import io.github.afalabarce.mvvmkmmtemplate.data.datasources.core.db.DriverFactory
import io.github.afalabarce.mvvmkmmtemplate.data.datasources.core.features.preferences.AppPreferencesImpl
import io.github.afalabarce.mvvmkmmtemplate.data.datasources.core.remote.ApiService
import io.github.afalabarce.mvvmkmmtemplate.data.datasources.features.preferences.AppPreferences
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

expect fun getPlatformInjects(): List<Module>

object DataSourceCoreDependencyInjector: KoinModuleLoader {
    override val koinModules: List<Module>
        get() = getPlatformInjects().union(
            listOf(
                module {
                    single <ApiService>{
                        Ktorfit
                            .Builder()
                            .baseUrl(ApiService.API_URL)
                            .build()
                            .create()
                    }
                    singleOf(::Database)
                    single<AppPreferences>{ AppPreferencesImpl(get()) }
                }
            )
        ).toList()
}