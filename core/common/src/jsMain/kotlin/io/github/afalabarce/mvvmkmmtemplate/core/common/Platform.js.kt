package io.github.afalabarce.mvvmkmmtemplate.core.common

import kotlinx.browser.window

class JsPlatform: Platform {
    override val name: String = "WebApp"
}

actual fun getPlatform(): Platform = JsPlatform()

actual fun openUrl(url: String?) {
    url?.let { window.open(it) }
}