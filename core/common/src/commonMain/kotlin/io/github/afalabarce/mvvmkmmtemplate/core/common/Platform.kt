package io.github.afalabarce.mvvmkmmtemplate.core.common

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform

expect fun openUrl(url: String?)