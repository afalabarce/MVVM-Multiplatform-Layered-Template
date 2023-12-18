package io.github.afalabarce.mvvmkmmtemplate.presentation.viewmodels.di

import io.github.afalabarce.mvvmkmmtemplate.core.common.di.KoinModuleLoader
import org.koin.core.module.Module

object PresentationViewModelsDependencyInjector: KoinModuleLoader {
    override val koinModules: List<Module>
        get() = listOf()
}