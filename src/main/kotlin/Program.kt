class Program {
    private val labels: MutableMap<String, Line> = mutableMapOf()
    private val lines: MutableList<Line> = mutableListOf()

    fun addLine(string: String) {
        val line = Line.parse(string)
        lines.add(line)
        if (line.label != null) {
            labels[line.label] = line
        }
    }

    fun assemble(): List<UShort> {
        val labelValues = labels.mapValues { (_, v) -> (lines.indexOf(v)+1).toUShort() }

        return lines.map { it.toMachineCode(labelValues) }
    }
}