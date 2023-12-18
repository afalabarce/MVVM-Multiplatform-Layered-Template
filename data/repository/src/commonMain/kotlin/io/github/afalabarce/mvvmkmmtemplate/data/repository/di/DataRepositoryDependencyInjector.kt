package io.github.afalabarce.mvvmkmmtemplate.data.repository.di

import io.github.afalabarce.mvvmkmmtemplate.core.common.di.KoinModuleLoader
import org.koin.core.module.Module

object DataRepositoryDependencyInjector: KoinModuleLoader {
    override val koinModules: List<Module>
        get() = listOf()
}