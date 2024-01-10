
import org.gradle.api.DefaultTask
import org.gradle.api.file.FileSystemOperations
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.options.Option
import org.gradle.internal.logging.text.StyledTextOutput
import org.gradle.internal.logging.text.StyledTextOutputFactory
import org.gradle.kotlin.dsl.newInstance
import java.io.File
import java.io.IOException
import java.nio.file.FileVisitResult
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.SimpleFileVisitor
import java.nio.file.attribute.BasicFileAttributes
import javax.inject.Inject
import kotlin.io.path.extension
import kotlin.io.path.isRegularFile
import kotlin.io.path.moveTo
import kotlin.io.path.name

abstract class ProjectDeployTask: DefaultTask() {
    private var _sourcePackage: String = BuildVersion.environment.applicationId
    private var _deployPackage: String = ""

    //region task parameters

    @get:Input
    abstract val projectDir: Property<String>
    @Input
    fun getSourcePackage(): String = _sourcePackage

    @Option(
        option = "sourcePackage",
        description = SOURCE_PACKAGE_DESCRIPTION
    )
    fun setSourcePackage(sourcePackage: String){
        _sourcePackage = sourcePackage
    }

    @Input
    fun getDeployPackage(): String = _deployPackage

    @Option(
        option = "deployPackage",
        description = DEPLOY_PACKAGE_DESCRIPTION
    )
    fun setDeployPackage(deployPackage: String){
        _deployPackage = deployPackage
    }

    //endregion

    //region Validation functions

    private fun help(returnValue: Boolean, message: String): Boolean {
        val out: StyledTextOutput = services.get(StyledTextOutputFactory::class.java).create("formattedOutput")
        with(out.withStyle(StyledTextOutput.Style.Description)) {
            println("Project Deploy Task Help")
            println("-------------------------------")
            println("This task can relocate packages and folders from source package to destination deploy package")
            println("- sourcePackage, $SOURCE_PACKAGE_DESCRIPTION")
            println("- deployPackage, $DEPLOY_PACKAGE_DESCRIPTION")
            println("\nUsage:")
            println("./gradlew -q deployKmmProject --sourcePackage=\"io.github.afalabarce.mvvmkmmtemplate\" --deployPackage=\"io.github.afalabarce.awesomeapp\"")
            println("-------------------------------")
        }

        out.withStyle(StyledTextOutput.Style.Failure).println(message)
        return returnValue
    }

    private fun validate(): Boolean {
        var message = ""
        var returnValue = true

        if (_sourcePackage.isEmpty()){
            message = "ERROR: Source package can not be empty"
            returnValue = false
        }

        if (_deployPackage.isEmpty()){
            message = "ERROR: Deploy package can not be empty"
            returnValue = false
        }

        if (_deployPackage.trim().lowercase() == _sourcePackage.trim().lowercase()){
            message = "ERROR: Deploy package can not be equal to Source package (case insensitive)"
            returnValue = false
        }

        return help(returnValue, message)
    }

    //endregion

    private fun changePackage(projectPath: String){
        val out: StyledTextOutput = services.get(StyledTextOutputFactory::class.java).create("formattedOutput")
        val sourcePackageFolder = _sourcePackage.replace(".", File.separator)
        val deployPackageFolder = _deployPackage.replace(".", File.separator)

        Files.walkFileTree(File(projectPath).toPath(),
            object : SimpleFileVisitor<Path>() {
                @Throws(IOException::class)
                override fun preVisitDirectory(
                    folder: Path,
                    attrs: BasicFileAttributes
                ): FileVisitResult {
                    if (
                        folder.toFile().isDirectory &&
                        folder.toFile().absolutePath.endsWith(_sourcePackage)
                    ) {
                        out.withStyle(StyledTextOutput.Style.Normal).text("· Relocate source package folder $sourcePackageFolder to deploy package folder $deployPackageFolder...")

                        try {
                            val destinationPath = File(
                                folder
                                    .toFile()
                                    .absolutePath
                                    .replace(
                                        oldValue = sourcePackageFolder,
                                        newValue = deployPackageFolder
                                    )
                            ).toPath()

                            folder.moveTo(
                                target = destinationPath,
                                overwrite = true
                            )
                            out.withStyle(StyledTextOutput.Style.Success).println(" [SUCCESS]")
                        }catch (_: Exception){
                            out.withStyle(StyledTextOutput.Style.Failure).println(" [ERROR]")
                        }
                    }


                    return FileVisitResult.CONTINUE
                }

                @Throws(IOException::class)
                override fun visitFile(file: Path, attrs: BasicFileAttributes): FileVisitResult {
                    if (
                        file.isRegularFile() &&
                        file.extension in arrayOf("kt", "xml", "kts")
                    ) {
                        out.withStyle(StyledTextOutput.Style.Normal).text("· [${file.name}] Modifying source package $_sourcePackage to deploy package $_deployPackage...")
                        if (file.toFile().absolutePath.contains(
                                other = ".idea",
                                ignoreCase = true
                            )) {
                            out.withStyle(StyledTextOutput.Style.Info).println(" [SKIPPED]")
                        } else {
                            try {
                                val content = file.toFile().readText(charset = Charsets.UTF_8)
                                if (content.contains(_sourcePackage)){
                                    file.toFile().writeText(content.replace(_sourcePackage, _deployPackage))
                                    out.withStyle(StyledTextOutput.Style.Success).println(" [SUCCESS]")
                                } else {
                                    out.withStyle(StyledTextOutput.Style.Info).println(" <SKIPPED>")
                                }
                            }catch (_: Exception){
                                out.withStyle(StyledTextOutput.Style.Failure).println(" [ERROR]")
                            }
                        }
                    }

                    return FileVisitResult.CONTINUE
                }
            }
        )
    }

    private fun relocatePackageFolders(projectPath: String){
        val out: StyledTextOutput = services.get(StyledTextOutputFactory::class.java).create("formattedOutput")
        val sourcePackageFolder = _sourcePackage.replace(".", File.separator)
        val deployPackageFolder = _deployPackage.replace(".", File.separator)
        Files.walkFileTree(File(projectPath).toPath(),
            object : SimpleFileVisitor<Path>() {
                @Throws(IOException::class)
                override fun visitFile(file: Path, attrs: BasicFileAttributes): FileVisitResult {
                    if (
                        file.toFile().isDirectory &&
                        file.toFile().absolutePath.endsWith(_sourcePackage)
                    ) {
                        out.withStyle(StyledTextOutput.Style.Normal).text("· Relocate source package folder $sourcePackageFolder to deploy package folder $deployPackageFolder...")

                        try {
                            val content = file.toFile().readText(charset = Charsets.UTF_8)
                            if (content.contains(_sourcePackage)){
                                file.toFile().writeText(content.replace(_sourcePackage, _deployPackage))
                                out.withStyle(StyledTextOutput.Style.Success).println(" [SUCCESS]")
                            } else {
                                out.withStyle(StyledTextOutput.Style.Info).println(" <SKIPPED>")
                            }
                        }catch (_: Exception){
                            out.withStyle(StyledTextOutput.Style.Failure).println(" [ERROR]")
                        }
                    }

                    return FileVisitResult.CONTINUE
                }
            }
        )
    }

    @TaskAction
    fun deployKmmProject() {
        check(validate())
        val projectPath = projectDir.get()

        relocatePackageFolders(projectPath)
        changePackage(projectPath)
    }

    companion object {
        private const val SOURCE_PACKAGE_DESCRIPTION =
            "Source package from template, by default io.github.afalabarce.mvvmkmmtemplate"
        private const val DEPLOY_PACKAGE_DESCRIPTION =
            "Deploy package for the template, can't be empty, and not equals to sourcePackage"
    }
}

interface Injected {
    @get:Inject val fs: FileSystemOperations
}