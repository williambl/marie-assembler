sealed class Line(val label: String?) {
    class InstructionLine(val instruction: Instruction, val addressLabel: String?, label: String?): Line(label)
    class DataLine(val data: UInt, label: String?): Line(label)

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
                "BIN" -> dataValueString.toUInt(2)
                "DEC" -> dataValueString.toUInt(10)
                "HEX" -> dataValueString.toUInt(16)
                else -> return null
            }

            return DataLine(value, label)
        }

    }
}
