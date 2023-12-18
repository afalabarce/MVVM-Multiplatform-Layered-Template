import androidx.compose.ui.window.ComposeUIViewController
import io.github.afalabarce.mvvmkmmtemplate.presentation.ui.App
import io.github.afalabarce.mvvmkmmtemplate.presentation.ui.di.PresentationUiDependencyInjector
import org.koin.core.context.startKoin
import platform.UIKit.UIViewController

fun MainViewController(): UIViewController = ComposeUIViewController { App() }

fun initKoin(){
    startKoin{
        modules(
            PresentationUiDependencyInjector.koinModules
        )
    }
}
 