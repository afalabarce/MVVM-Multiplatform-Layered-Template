package io.github.afalabarce.mvvmkmmtemplate.presentation.ui.di

import io.github.afalabarce.mvvmkmmtemplate.core.common.di.KoinModuleLoader
import io.github.afalabarce.mvvmkmmtemplate.core.di.CoreDependencyInjection
import org.koin.core.module.Module

object PresentationUiDependencyInjector:KoinModuleLoader {
    override val koinModules: List<Module>
        get() = listOf(
            CoreDependencyInjection.koinModules,
            listOf(
                // TODO Add all presentation ui dependencies, if needed
            )
        ).flatten()

}