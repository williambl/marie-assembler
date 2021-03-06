enum class Instruction(val opcode: Byte) {
    Load(0x1),
    Store(0x2),
    Add(0x3),
    Subt(0x4),
    Input(0x5),
    Output(0x6),
    Halt(0x7),
    Skipcond(0x8),
    Jump(0x9),
    Clear(0xa),
    AddI(0xb),
    JumpI(0xc),
    LoadI(0xd),
    StoreI(0xe),
    JnS(0x0)
}