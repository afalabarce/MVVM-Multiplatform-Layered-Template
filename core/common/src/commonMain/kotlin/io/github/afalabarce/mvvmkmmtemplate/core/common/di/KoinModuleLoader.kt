package io.github.afalabarce.mvvmkmmtemplate.core.common.di

import org.koin.core.module.Module

interface KoinModuleLoader {
    val koinModules: List<Module>
}