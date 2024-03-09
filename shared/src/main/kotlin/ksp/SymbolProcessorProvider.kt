import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import ksp.BuildConfigFileGenerator
import ksp.BuildConfigOptions
import ksp.BuildConfigProcessor

internal class BuildConfigProcessorProvider {
    fun create(environment: SymbolProcessorEnvironment): BuildConfigProcessor {
        return BuildConfigProcessor(
            BuildConfigFileGenerator(environment.codeGenerator),
            BuildConfigOptions(environment.options)
        )
    }
}