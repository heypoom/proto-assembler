import java.util.*

enum class Register {
    EIP, // Instruction Pointer aka Program Counter
    ESP, // Stack Pointer
    EAX, // Accumulator Counter
    EBX, // Base Counter
}

enum class Op {
    NOP, // Do nothing
    MOV, // Move
    JMP, // Jump
    INCR, // Increment
    DECR, // Decrement
    PUSH, // Push to Stack
    POP, // Pop from Stack
    ADD, // Add
    SUB, // Subtract
}

class Processor {
    val registers = EnumMap<Register, Int>(Register::class.java)
    val memory = HashMap<Int, Int>()

    fun get(reg: Register): Int {
        return registers[reg] ?: 0
    }

    fun nop() {}

    fun mov(dst: Register, value: Int) {
        registers[dst] = value
    }

    fun mov(dst: Register, src: Register) {
        mov(dst, get(src))
    }

    fun jmp(address: Int) {
        mov(Register.EIP, address)
    }

    fun push(value: Int) {
        val sp = get(Register.ESP)
        memory[sp] = value
    }

    fun pop(reg: Register) {
        val sp = get(Register.ESP)
        mov(reg, memory[sp] ?: 0)
    }

    fun incr(reg: Register) {
        add(reg, 1)
    }

    fun decr(reg: Register) {
        sub(reg, 1)
    }

    fun add(reg: Register, value: Int) {
        mov(reg, get(reg) + value)
    }

    fun sub(reg: Register, value: Int) {
        add(reg, -value)
    }

    fun xor(reg: Register, value: Int) {
        mov(reg, get(reg).xor(value))
    }
}
