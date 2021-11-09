class Program {
    private val labels: MutableMap<String, Line> = mutableMapOf()
    private val lines: MutableList<Line> = mutableListOf()

    fun assemble(): List<UShort> {
        val labelValues = labels.mapValues { (_, v) -> lines.indexOf(v).toUShort() }

        return lines.map { it.toMachineCode(labelValues) }
    }
}