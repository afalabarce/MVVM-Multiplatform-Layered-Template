package io.github.afalabarce.mvvmkmmtemplate.core.common

import java.awt.Desktop
import java.net.URI

class JvmPlatform: Platform {
    override val name: String = "JvmApp"
}

actual fun getPlatform(): Platform = JvmPlatform()

actual fun openUrl(url: String?) {
    val uri = url?.let { URI.create(it) } ?: return
    Desktop.getDesktop().browse(uri)
}