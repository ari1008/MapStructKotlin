package domain

data class Function(
    val name: String,
    val parameter: List<DataClass>,
    val annotation: FunctionAnnotation,
    val returnType: DataClass,
)
