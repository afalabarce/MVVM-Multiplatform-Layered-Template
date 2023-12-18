package io.github.afalabarce.mvvmkmmtemplate.domain.usecases.di

import io.github.afalabarce.mvvmkmmtemplate.core.common.di.KoinModuleLoader
import org.koin.core.module.Module

object DomainUseCaseDependencyInjector: KoinModuleLoader {
    override val koinModules: List<Module>
        get() = listOf()
}