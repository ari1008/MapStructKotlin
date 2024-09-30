import com.google.devtools.ksp.KspExperimental
import com.google.devtools.ksp.getDeclaredFunctions
import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSFunctionDeclaration
import com.google.devtools.ksp.symbol.KSType
import com.google.devtools.ksp.symbol.KSValueArgument
import com.google.devtools.ksp.symbol.KSValueParameter
import domain.AnnotationValue
import domain.Class
import domain.DataClass
import domain.Function
import domain.FunctionAnnotation
import domain.Property
import poet.KotlinPoetService
import kotlin.sequences.filter

class MapperProcessor(
    private val logger: KSPLogger,
    private val codeGenerator: CodeGenerator,
) : SymbolProcessor {
    @OptIn(KspExperimental::class)
    override fun process(resolver: Resolver): List<KSAnnotated> {
        val result = resolver
            .getSymbolsWithAnnotation(Mapper::class.qualifiedName.toString())
            .filterIsInstance<KSClassDeclaration>()
            .map { classType ->
                Class(
                    interfacePackage = classType.packageName.getShortName(),
                    name = classType.simpleName.getShortName(),
                    nameFull = classType.qualifiedName?.getQualifier().toString(),
                    functions = createListFunctionByClass(classType)
                )
            }.toList()
        logger.warn(result.toString())
        val poet = KotlinPoetService(result, codeGenerator, logger)
        poet.createFile()
        return emptyList()
    }




    @OptIn(KspExperimental::class)
    private fun createListFunctionByClass(
        annotatedClass: KSClassDeclaration
    ): List<Function> {
        return annotatedClass
            .getDeclaredFunctions()
            .map { function ->
                val listDataClass = function.parameters.map { createDataclassComplicated(it) }.toList()
                Function(
                    name = function.simpleName.getShortName(),
                    annotation = createFunction(function),
                    parameter = listDataClass,
                    returnType = createDataclassComplicatedD(function.getReturnType(), function.returnType.toString()),
                )
            }.toList()
    }

    fun KSFunctionDeclaration.getReturnType(): KSType {
        val returnTypeRef = this.returnType
        return returnTypeRef?.resolve() ?: throw IllegalStateException("Unable to resolve return type")
    }


    private fun createFunction(
        function: KSFunctionDeclaration
    ): FunctionAnnotation {
        val nameOfFunction = function.simpleName.getShortName()
        val test = function.annotations
        test.filter { annotation -> annotation.annotationType.toString() == "Mapping" }
            .forEach {
                logger.warn("hello ${it.arguments}")
            }
        val listAnnotationValue = function.annotations
            .filter { annotation -> annotation.annotationType.toString() == "Mapping" }
            .map { it.arguments.createArgument() }
            .toList()
        return FunctionAnnotation(name = nameOfFunction, listAnnotationValue)
    }

    private fun List<KSValueArgument>.createArgument(
    ): List<AnnotationValue> {
        return this.map {
            AnnotationValue(it.name?.getShortName()!!, it.value!!.toString())
        }
    }


    private fun createDataclassComplicated(
        dataClass: KSValueParameter
    ): DataClass {
        val paramTypeDeclaration = dataClass.type.resolve().declaration
        val nameDataclass = dataClass.name?.getShortName()
        if (paramTypeDeclaration is KSClassDeclaration) {
            val result = paramTypeDeclaration.getAllProperties().map { property ->
                val propertyName = property.simpleName.asString()
                propertyName
                val propertyType =
                    property.type.resolve().declaration.qualifiedName?.asString() ?: "Unknown type"
                Property(name = propertyName, type = propertyType)
            }.toList()
            return DataClass(
                name = nameDataclass ?: "undefined",
                packageName = paramTypeDeclaration.qualifiedName?.getQualifier() ?: "undefined",
                type = paramTypeDeclaration.simpleName.getShortName(),
                property = result
            )

        }
        return DataClass(
            name = nameDataclass ?: "undefined",
            packageName = paramTypeDeclaration.qualifiedName?.getQualifier() ?: "undefined",
            type = paramTypeDeclaration.simpleName.getShortName(),
            property = emptyList()
        )
    }

    private fun createDataclassComplicatedD(
        dataClass: KSType, nameDataclass: String?
    ): DataClass {
        val paramTypeDeclaration = dataClass.declaration
        if (paramTypeDeclaration is KSClassDeclaration) {
            val result = paramTypeDeclaration.getAllProperties().map { property ->
                val propertyName = property.simpleName.asString()
                val propertyType =
                    paramTypeDeclaration.qualifiedName?.getQualifier().toString()
                Property(name = propertyName, type = propertyType)
            }.toList()
            return DataClass(
                name = nameDataclass ?: "undefined",
                packageName = paramTypeDeclaration.qualifiedName?.getQualifier() ?: "undefined",
                type = paramTypeDeclaration.simpleName.getShortName(),
                property = result
            )

        }
        return DataClass(
            name = nameDataclass ?: "undefined",
            packageName = paramTypeDeclaration.qualifiedName?.getQualifier() ?: "undefined",
            type = paramTypeDeclaration.simpleName.getShortName(),
            property = emptyList()
        )
    }


}