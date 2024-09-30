package domain

data class Class(
    val interfacePackage: String,
    val nameFull: String,
    val name: String,
    val functions: List<Function>,
)
