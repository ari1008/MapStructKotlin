package poet

import com.google.devtools.ksp.processing.KSPLogger
import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.FunSpec
import domain.AnnotationValue
import domain.Function

class KotlinFunctionPoetService(
    private val logger: KSPLogger,
) {

    fun buildAddStatement(function: Function, funSpecBuilder: FunSpec.Builder): FunSpec.Builder {
        val codeBlock = CodeBlock.builder()
        val result = codeBlock.add("return %L(\n", function.returnType.name)
            .buildCodeBlock(
                data = function.annotation.data,
                parameterList = function.parameter.map { it.name },
                returnType = function.returnType.property.map { it.name }
            )
        funSpecBuilder.addCode(result)
        return funSpecBuilder
    }

    private fun CodeBlock.Builder.buildCodeBlock(
        data: List<List<AnnotationValue>>,
        parameterList: List<String>,
        returnType: List<String>,
    ): CodeBlock {
        val result = data.mapNotNull { mappingAnnotation ->
            val parameter = mappingAnnotation.first().argument
            val propertyDataClass = mappingAnnotation.second().argument
            if (parameter in parameterList && propertyDataClass in returnType) {
                propertyDataClass to "    $propertyDataClass = $parameter,\n"
            } else null
        }

        val codeBlockContent = if (result.size == returnType.size) {
            result.map { it.second }
        } else {
            val missing = returnType.filterNot { type -> result.any { it.first == type } }
            missing.map { "    $it = $it,\n" } + result.map { it.second }
        }

        codeBlockContent.forEach { this.add(it) }
        this.add(")")
        return this.build()
    }


    private fun <T> List<T>.second(): T {
        if (isEmpty()) throw NoSuchElementException("List is empty.")
        return this[1]
    }

}