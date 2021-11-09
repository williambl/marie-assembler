enum class Instruction(val opcode: UByte) {
    Load(0x1u),
    Store(0x2u),
    Add(0x3u),
    Subt(0x4u),
    Input(0x5u),
    Output(0x6u),
    Halt(0x7u),
    SkipCond(0x8u),
    Jump(0x9u)
}