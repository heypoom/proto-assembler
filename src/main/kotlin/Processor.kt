import java.util.*

enum class Register {
    EIP, // Instruction Pointer aka Program Counter
    SP, // Stack Pointer
    EAX, // Accumulator Counter
    EBX, // Base Counter
}

class Processor {
    val registers = EnumMap<Register, Int>(Register::class.java)
    val memory = HashMap<Int, Int>()

    fun get(reg: Register): Int {
        return registers[reg] ?: 0
    }

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
        val sp = get(Register.SP)
        memory[sp] = value
    }

    fun pop(reg: Register) {
        val sp = get(Register.SP)
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
}

fun isWord(str: String): Boolean {
    return Regex("\\w+").containsMatchIn(str)
}


