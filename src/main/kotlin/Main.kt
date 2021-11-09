import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.arguments.multiple
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.types.path
import java.io.File
import java.io.InputStream
import java.io.SequenceInputStream
import java.nio.file.Path
import kotlin.reflect.cast

class Assembler: CliktCommand() {
    private val input by argument().path(mustExist = true, canBeDir = false, mustBeReadable = true).multiple()
    private val output by option(help="File to write assembled binary to", names = arrayOf("-o", "--output")).path(canBeDir = false)

    override fun run() {
        val assembled = Program().also { program ->
            if (input.isEmpty()) {
                while (true) {
                    program.addLine(readLine() ?: break)
                }
            } else {
                input
                    .map(Path::toFile)
                    .map(File::inputStream)
                    .map(InputStream::class::cast)
                    .reduce { acc, fileInputStream -> SequenceInputStream(acc, fileInputStream) }
                    .reader()
                    .forEachLine(program::addLine)
            }
        }.assemble()

        output
            ?.let(Path::toFile)
            ?.also(File::createNewFile)
            ?.writeBytes(assembled.flatMap(UShort::splitToUBytes).map { it.toByte() }.toByteArray())

            ?: assembled.forEach { echo(it.toString(16)) }
    }
}

fun main(args: Array<String>) = Assembler().main(args)

private fun UShort.splitToUBytes() = listOf((this.toUInt() shr 8).toUByte(), this.toUByte())