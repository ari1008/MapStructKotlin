import com.tschuchort.compiletesting.KotlinCompilation
import com.tschuchort.compiletesting.SourceFile
import com.tschuchort.compiletesting.kspSourcesDir
import com.tschuchort.compiletesting.kspWithCompilation
import com.tschuchort.compiletesting.symbolProcessorProviders
import org.intellij.lang.annotations.Language
import org.jetbrains.kotlin.compiler.plugin.ExperimentalCompilerApi
import org.junit.jupiter.api.Test
import java.io.File
import kotlin.test.assertEquals

class MapperClassTest {

    @Test
    fun `test MapperClass compilation`() {
        val directory = File("src/test/resources/")
        if (directory.exists() && directory.isDirectory) {
            val listTest = directory.listFiles { file -> file.isDirectory }
            listTest?.forEach {
                //if (it.name == "number1") return@forEach
                println("\u001B[33m Run test:  ${it.name}\u001B[0m")
                val directoryStart = it.walkTopDown().filter { it.name == "start" }.firstOrNull()
                val directoryEnd  = it.walkTopDown().filter { it.name == "end" }.firstOrNull()
                if (directoryStart == null) throw Exception("Not found start directory in ${it.name}")
                if (directoryEnd == null) throw Exception("Not found end directory in ${it.name}")
                val fileInterfaceName = findInterfaceInDirectory(directoryStart)?.name
                if (fileInterfaceName.isNullOrBlank()) throw Exception("Not found interface @Mapper in ${directoryStart.name}")
                val fileNameGenerated = "Impl$fileInterfaceName"
                try {
                    val fileFind = File(directoryEnd, fileNameGenerated)
                    assertGeneratedFile(
                        sourcesDirectory = directoryStart,
                        generatedResultFileName = "${it.name}/$fileNameGenerated",
                        generatedSource = fileFind.readText().trimIndent()
                    )
                } catch (_: Exception) {
                    throw Exception("Not found file $fileNameGenerated in ${directoryEnd.name}")
                }
            }
        } else {
            throw Exception("This path is not good")
        }
    }

    @OptIn(ExperimentalCompilerApi::class)
    private fun assertGeneratedFile(
        sourcesDirectory: File,
        generatedResultFileName: String,
        @Language("kotlin") generatedSource: String
    ) {
        val sourceFiles = sourcesDirectory.walkTopDown()
            .filter { it.extension == "kt" }
            .map { file ->
                SourceFile.kotlin(file.name, file.readText().trimIndent())
            }
            .toList()
        val compilation = KotlinCompilation().apply {
            inheritClassPath = true
            kspWithCompilation = true
            sources = sourceFiles
            symbolProcessorProviders = listOf(
                MapperProcessorProvider()
            )
        }
        val result = compilation.compile()
        assertEquals(KotlinCompilation.ExitCode.OK, result.exitCode)

        val generated = File(
            compilation.kspSourcesDir,
            "kotlin/$generatedResultFileName"
        )

        val expectedSource = generatedSource
            .trimIndent()
            .lines()
            .joinToString("\n") { it.trimStart() }
        val actualSource = generated.readText()
            .trimIndent()
            .lines()
            .joinToString("\n") { it.trimStart() }
        assertEquals(
            expectedSource,
            actualSource
        )
    }

    fun findInterfaceInDirectory(directory: File): File? {
        return directory.walkTopDown()
            .filter { isMapperInterface(it) }.firstOrNull()
    }

    fun isMapperInterface(file: File): Boolean {
        if (!file.exists() || !file.isFile) {
            return false
        }

        val content = file.readText()
        val mapperAnnotationRegex = Regex("""@Mapper\(""")
        val interfaceRegex = Regex("""interface\s+\w+""")

        return mapperAnnotationRegex.containsMatchIn(content) && interfaceRegex.containsMatchIn(content)
    }
}