import kotlin.reflect.KClass

/**
 * This class is used to get the implementation of the interface.
 * @param interfaceClass is the interface that we want to get the implementation.
 * @return the implementation of the interface.
 * @throws ClassNotFoundException if the class is not found.
 */
object MapperClass {
    fun goodClassName(className: String?): String {
        if (className.isNullOrBlank()) return ""
        val packageParts = className.split(".")
        val result = "Impl${packageParts.last()}"
        return (packageParts.dropLast(1).drop(1) + result).joinToString(".")
    }

    fun <T : Any> getMapper(interfaceClass: KClass<T>): T {
        val implementationClassName = goodClassName(interfaceClass.qualifiedName)
        val implementationClass = Class.forName(implementationClassName)
        return implementationClass.getDeclaredConstructor().newInstance() as T
    }
}
