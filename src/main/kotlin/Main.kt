fun main(args: Array<String>) {
    val program = Program()
    while (true) {
        program.addLine(readLine() ?: break)
    }

    program.assemble().forEach { println(it.toString(16)) }
}