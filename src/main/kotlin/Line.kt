sealed class Line(val label: String?) {
    class InstructionLine(private val instruction: Instruction, private val operandLabel: String?, label: String?): Line(label) {
        override fun toMachineCode(labels: Map<String, UShort>): UShort {
            val operand = when {
                operandLabel == null -> 0u
                operandLabel.toUIntOrNull() != null -> operandLabel.toUShort()
                else -> labels[operandLabel] ?: throw IllegalStateException()
            }
            return (instruction.opcode shl 12) or operand
        }

        private infix fun UByte.shl(bitCount: Int): UShort = (instruction.opcode.toUInt() shl bitCount).toUShort()
    }

    class DataLine(private val data: UShort, label: String?): Line(label) {
        override fun toMachineCode(labels: Map<String, UShort>): UShort = data
    }

    class DummyLine(): Line("NULL") {
        override fun toMachineCode(labels: Map<String, UShort>): UShort = 0u
    }

    abstract fun toMachineCode(labels: Map<String, UShort>): UShort

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
                "BIN" -> dataValueString.toUShort(2)
                "DEC" -> dataValueString.toUShort(10)
                "HEX" -> dataValueString.toUShort(16)
                else -> return null
            }

            return DataLine(value, label)
        }

    }
}
