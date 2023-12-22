package io.github.afalabarce.mvvmkmmtemplate.data.datasources.core.di

import io.github.afalabarce.mvvmkmmtemplate.core.common.di.KoinModuleLoader
import org.koin.core.module.Module

expect fun getPlatformInjects(): List<Module>

object DataSourceCoreDependencyInjector: KoinModuleLoader {
    override val koinModules: List<Module>
        get() = getPlatformInjects().union(
            listOf()
        ).toList()
}