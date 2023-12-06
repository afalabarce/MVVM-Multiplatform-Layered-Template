package io.github.afalabarce.mvvmkmmtemplate.core.ui

import io.github.afalabarce.mvvmkmmtemplate.core.common.Platform
import io.github.afalabarce.mvvmkmmtemplate.core.common.getPlatform

class Greeting {
    private val platform: Platform = getPlatform()

    fun greet(): String {
        return "Hello, ${platform.name}!"
    }
}