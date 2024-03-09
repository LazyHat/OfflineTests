package ksp

import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.symbol.KSAnnotated

internal class BuildConfigProcessor(
    private val fileGenerator: BuildConfigFileGenerator,
    private val options: BuildConfigOptions
) : SymbolProcessor {
    override fun process(resolver: Resolver): List<KSAnnotated> {
        return emptyList()
    }

    override fun finish() {}

    override fun onError() {}
}