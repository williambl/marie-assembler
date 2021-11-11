import kotlin.experimental.or

sealed class Line(val label: String?) {
    abstract fun toMachineCode(labels: Map<String, Short>): Short

    class InstructionLine(private val instruction: Instruction, private val operandLabel: String?, label: String?): Line(label) {
        override fun toMachineCode(labels: Map<String, Short>): Short {
            val operand = when {
                operandLabel == null -> 0
                operandLabel.toShortOrNull() != null -> operandLabel.toShort()
                else -> labels[operandLabel] ?: throw IllegalStateException()
            }
            return (instruction.opcode shl 12) or operand
        }

        private infix fun Byte.shl(bitCount: Int): Short = (instruction.opcode.toUInt() shl bitCount).toShort()
    }

    class DataLine(private val data: Short, label: String?): Line(label) {
        override fun toMachineCode(labels: Map<String, Short>): Short = data
    }

    class DummyLine : Line("NULL") {
        override fun toMachineCode(labels: Map<String, Short>): Short = 0
    }

    companion object {
        private val lineRegex = Regex("""^(?:\h*(?:(\w+),)?\h*(\w+)\h*(\w+)?\h*(?:/[^\v]*)?)|(?:/[^\v]*)$""")

        fun parse(line: String): Line? {
            val (label, instruction, operand) = lineRegex.matchEntire(line)?.destructured ?: throw IllegalArgumentException()

            if (instruction.isEmpty()) {
                return null
            }

            return tryParseInstructionLine(label, instruction, operand)
                ?: tryParseDataLine(label, instruction, operand)
                ?: throw IllegalArgumentException()
        }

        private fun tryParseInstructionLine(label: String?, instructionName: String, operandLabel: String): InstructionLine? {
            return InstructionLine(
                Instruction.values().find { it.name == instructionName } ?: return null,
                operandLabel.takeIf(String::isNotEmpty),
                label
            )
        }

        private fun tryParseDataLine(label: String?, dataFormat: String, dataValueString: String): DataLine? {
            val value = when (dataFormat) {
                "BIN" -> dataValueString.toShort(2)
                "DEC" -> dataValueString.toShort(10)
                "HEX" -> dataValueString.toShort(16)
                else -> return null
            }

            return DataLine(value, label)
        }

    }
}
