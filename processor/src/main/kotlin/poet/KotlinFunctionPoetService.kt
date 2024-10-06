package poet

import com.google.devtools.ksp.processing.KSPLogger
import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.FunSpec
import domain.AnnotationValue
import domain.DataClass
import domain.Function
import domain.FunctionAnnotation

class KotlinFunctionPoetService(
    private val logger: KSPLogger,
) {

    fun buildAddStatement(function: Function, funSpecBuilder: FunSpec.Builder): FunSpec.Builder {
        val codeBlock = CodeBlock.builder()
        val allPossibility = createAllPossibility(function.parameter)
        val returnType = function.returnType.property.map { it.name }
        val hello = annotation(function.annotation)
        val test = change(hello, returnType)
        val listBlockString = createCodeBlock(allPossibility, test.first)
        val change = codeBlock.add("return %L(\n", function.returnType.name)
            .buildCodeBlock2(listBlockString + test.second)

        val result = codeBlock.add("return %L(\n", function.returnType.name)
            .buildCodeBlock(
                data = function.annotation.data,
                parameterList = function.parameter.map { it.name },
                returnType = function.returnType.property.map { it.name }
            )
        funSpecBuilder.addCode(change)
        return funSpecBuilder
    }

    private fun CodeBlock.Builder.buildCodeBlock2(codeBlockContent: List<String>): CodeBlock {
        codeBlockContent.forEach { this.add(it) }
        this.add(")")
        return this.build()
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

    private fun createAllPossibility(parameter: List<DataClass>): List<String> {
        return createProperty(parameter) + createForDataClass(parameter)
    }

    private fun createForDataClass(parameter: List<DataClass>): List<String> {
        return parameter
            .filter { it.property.isNotEmpty() }
            .map { dataClass ->
                dataClass.property.map { properties ->
                    "${dataClass.name}.${properties.name}"
                }
            }.flatten()

    }

    private fun createProperty(parameter: List<DataClass>): List<String> {
        return parameter.map { it.name }
    }

    private fun createCodeBlock(listString: List<String>, returnTypeList: List<String>): List<String> {
        return returnTypeList
            .mapNotNull { returnType ->
                val result = listString.find { it.contains(returnType) }
                if (result != null) {
                    return@mapNotNull "    $returnType = $result,\n"
                } else {
                    return@mapNotNull null
                }
            }.toList()
    }


    private fun annotation(annotation: FunctionAnnotation): Map<String, String> {
        return annotation.data.map {
            val parameter = it.first().argument
            val propertyDataClass = it.second().argument
            propertyDataClass to "    $propertyDataClass = $parameter,\n"
        }.toMap()

    }

    private fun change(mapString: Map<String, String>, returnType: List<String>): Pair<List<String>, List<String>> {
        val filteredList = returnType.filterNot { it in mapString.keys }
        val result = mapString.filter { it.key in returnType }.map { it.value }
        return filteredList to result
    }


}

