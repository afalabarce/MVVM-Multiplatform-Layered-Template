package io.github.afalabarce.mvvmkmmtemplate.core.di

import io.github.afalabarce.mvvmkmmtemplate.core.common.di.KoinModuleLoader
import io.github.afalabarce.mvvmkmmtemplate.data.repository.di.DataRepositoryDependencyInjector
import io.github.afalabarce.mvvmkmmtemplate.data.datasources.core.di.DataSourceCoreDependencyInjector
import io.github.afalabarce.mvvmkmmtemplate.domain.usecases.di.DomainUseCaseDependencyInjector
import io.github.afalabarce.mvvmkmmtemplate.presentation.viewmodels.di.PresentationViewModelsDependencyInjector
import org.koin.core.module.Module

object CoreDependencyInjection : KoinModuleLoader{
    override val koinModules: List<Module>
        get() =
            listOf(
                DataSourceCoreDependencyInjector.koinModules,
                DataRepositoryDependencyInjector.koinModules,
                DomainUseCaseDependencyInjector.koinModules,
                PresentationViewModelsDependencyInjector.koinModules,
            ).flatten()

}