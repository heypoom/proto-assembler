fun isNumber(str: String): Boolean {
    return str.matches(Regex("^\\d+$"))
}

fun isHex(str: String): Boolean {
    return str.matches(Regex("^0x[0-9A-Fa-f]+$"))
}

fun parseHex(str: String): Int {
    return str.replace("0x", "").toInt(radix = 16)
}

fun showPattern(num: Int): String {
    return num
        .toString(radix = 2)
        .map { if (it == '0') '○' else '●' }
        .mapIndexed { i, x -> if (i % 2 == 0) x else "$x "}
        .joinToString("")
        .trim()
}

private val registers = enumValues<Register>().map { it.name.toLowerCase() }
private val ops = enumValues<Op>().map { it.name.toLowerCase() }

private fun getReg(reg: String): Register? {
    if (reg !in registers) return null

    return enumValueOf<Register>(reg.toUpperCase())
}

private fun getOp(op: String): Op? {
    if (op !in ops) return null

    return enumValueOf<Op>(op.toUpperCase())
}

class Interpreter(private val p: Processor) {
    private val program = mutableListOf<String>()

    private fun getValue(dst: String): Int? {
        val reg = getReg(dst)

        if (reg != null) return p.get(reg)
        if (isHex(dst)) return parseHex(dst)
        if (isNumber(dst)) return dst.toInt()

        return null
    }

    fun run(line: String) {
        val r = line.trim().replace(",", "").split(" ")
        if (r.size < 2) return

        val (op, dstKey) = r.map { it.toLowerCase() }
        val value = getValue(dstKey) ?: return
        execWithValue(op, value)

        when (op) {
            "print" -> return print(value, dstKey)
            "program" -> return println(program)
        }

        val register = getReg(dstKey) ?: return
        execWithRegister(op, register)

        if (r.size < 3) return
        val srcKey = r[2].toLowerCase()
        val srcValue = getValue(srcKey) ?: return
        execWithRegisterAndValue(op, register, srcValue)
    }

    private fun execWithValue(op: String, value: Int) {
        val fn: (Int) -> Unit = when (op) {
            "jmp" -> p::jmp
            "push" -> p::push
            else -> return
        }

        fn(value)
    }

    private fun execWithRegister(op: String, reg: Register) {
        val fn: (Register) -> Unit = when (op) {
            "incr" -> p::incr
            "decr" -> p::decr
            "pop" -> p::pop
            else -> return
        }

        fn(reg)
    }

    private fun execWithRegisterAndValue(op: String, reg: Register, value: Int) {
        val fn: (Register, Int) -> Unit = when (op) {
            "mov" -> p::mov
            "add" -> p::add
            "sub" -> p::sub
            "xor" -> p::xor
            else -> return
        }

        fn(reg, value)
    }

    private fun print(value: Int, dstKey: String) {
        println("$dstKey = $value (${showPattern(value)})")
    }

    fun runLines(lines: String) {
        lines.trimIndent().split("\n").forEach { run(it) }
    }

    fun loop() {
        while (true) {
            print("> ")

            val line = readLine() ?: continue
            run(line)

            println("Register: ${p.registers}")
            println("Memory: ${p.memory}")
        }
    }
}

fun main() {
    val p = Processor()
    val i = Interpreter(p)

    p.mov(Register.EIP, 0)

    i.runLines("""
        mov eax, 20
    """)

    i.loop()
}
