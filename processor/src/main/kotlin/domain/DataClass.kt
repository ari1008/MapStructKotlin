package domain

data class DataClass(
    val name: String,
    val type: String,
    val packageName: String,
    val property: List<Property>
)
