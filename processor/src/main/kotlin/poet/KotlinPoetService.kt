package poet

import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.Dependencies
import com.google.devtools.ksp.processing.KSPLogger
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.ParameterSpec
import com.squareup.kotlinpoet.TypeSpec
import domain.Class
import domain.DataClass
import domain.Function

class KotlinPoetService(
    private val classList: List<Class>,
    private val codeGenerator: CodeGenerator,
    private val logger: KSPLogger,
) {

    fun createFile() {
        classList.forEach { classData ->
            val className = "Impl" + classData.name

            val fileSpecBuilder = FileSpec.builder(classData.interfacePackage, className)
                .addType(createClass(classData))

            val fileSpec = fileSpecBuilder.build()
            val file = codeGenerator.createNewFile(
                Dependencies(aggregating = false),
                classData.interfacePackage,
                className,
                "kt"
            )
            file.write(fileSpec.toString().toByteArray())
        }
    }

    private fun createClass(classData: Class): TypeSpec {
        return TypeSpec.classBuilder("Impl" + classData.name)
            .addSuperinterface(ClassName(classData.nameFull, classData.name))
            .addFunctions(classData.functions.map { createFunction(it) })
            .build()
    }

    private fun createFunction(function: Function): FunSpec {
        val functionContent = KotlinFunctionPoetService(logger)
        val funSpecBuilder = FunSpec.builder(function.name)
            .addModifiers(KModifier.OVERRIDE)
            .addParameters(function.parameter.map { createParameter(it) })
            .returns(getReturnType(function.returnType))
        return functionContent.buildAddStatement(function, funSpecBuilder).build()
    }

    private fun getReturnType(returnType: DataClass) = ClassName(returnType.packageName, returnType.name)


    private fun createParameter(parameter: DataClass) = ParameterSpec.builder(parameter.name, ClassName(parameter.packageName, parameter.type)).build()

}