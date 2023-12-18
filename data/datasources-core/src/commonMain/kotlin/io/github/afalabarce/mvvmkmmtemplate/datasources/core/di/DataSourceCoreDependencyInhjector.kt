package io.github.afalabarce.mvvmkmmtemplate.datasources.core.di

import io.github.afalabarce.mvvmkmmtemplate.core.common.di.KoinModuleLoader
import org.koin.core.module.Module

object DataSourceCoreDependencyInhjector: KoinModuleLoader {
    override val koinModules: List<Module>
        get() = listOf()
}