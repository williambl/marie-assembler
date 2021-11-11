class Program {
    private val labels: MutableMap<String, Line> = mutableMapOf()
    private val lines: MutableList<Line> = mutableListOf(Line.DummyLine())

    fun addLine(string: String) {
        val line = Line.parse(string)
        if (line != null) {
            lines.add(line)
            if (line.label != null) {
                labels[line.label] = line
            }
        }
    }

    fun assemble(): List<UShort> {
        val labelValues = labels.mapValues { (_, v) -> lines.indexOf(v).toUShort() }

        return lines.map { it.toMachineCode(labelValues) }
    }
}