sealed class Line(val label: String?) {
    class InstructionLine(private val instruction: Instruction, private val operandLabel: String?, label: String?): Line(label) {
        override fun toMachineCode(labels: Map<String, UShort>): UShort {
            val operand = labels[operandLabel] ?: throw IllegalStateException()
            return (instruction.opcode shl 12) or operand
        }

        private infix fun UByte.shl(bitCount: Int): UShort = (instruction.opcode.toUInt() shl bitCount).toUShort()
    }

    class DataLine(private val data: UShort, label: String?): Line(label) {
        override fun toMachineCode(labels: Map<String, UShort>): UShort = data
    }

    abstract fun toMachineCode(labels: Map<String, UShort>): UShort

    companion object {
        fun parse(line: String): Line {
            val label = line.substringBefore(',', "")
            val instructionParts = line.substringAfter(',').trim().split(' ')

            if (instructionParts.size !in 1..2) {
                throw IllegalArgumentException()
            }

            val (firstPart, secondPart) = instructionParts + listOf<String?>(null)

            return tryParseInstructionLine(label, firstPart ?: "", secondPart)
                ?: tryParseDataLine(label, firstPart ?: "", secondPart)
                ?: throw IllegalArgumentException()
        }

        private fun tryParseInstructionLine(label: String, instructionName: String, operandLabel: String?): InstructionLine? {
            return InstructionLine(
                Instruction.values().find { it.name == instructionName } ?: return null,
                operandLabel,
                label
            )
        }

        private fun tryParseDataLine(label: String, dataFormat: String, dataValueString: String?): DataLine? {
            dataValueString ?: return null

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
