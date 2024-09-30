import kotlin.reflect.KClass

object MapperClass {
    fun goodClassName(className: String?): String {
        if (className.isNullOrBlank()) return ""

        // Sépare le nom de la classe par les points
        val packageParts = className.split(".")

        // Ajoute "Impl" avant le nom de la classe
        val result = "Impl${packageParts.last()}"

        // Recompose le nom complet avec le bon package
        return (packageParts.dropLast(1).drop(1) + result).joinToString(".")
    }

    fun <T : Any> getMapper(interfaceClass: KClass<T>): T {
        // Trouve le nom de la classe d'implémentation générée
        val implementationClassName = goodClassName(interfaceClass.qualifiedName)
//
//    // Charge dynamiquement la classe d'implémentation
        val implementationClass = Class.forName(implementationClassName)
//
//    // Crée une nouvelle instance de la classe d'implémentation
        return implementationClass.getDeclaredConstructor().newInstance() as T
    }
}
