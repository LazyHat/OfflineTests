package ksp

data class BuildConfigOptions(
    private val options: Map<String,String>
){
    val isDebug = options["debug"]
}