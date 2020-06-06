fun isNumber(str: String): Boolean {
    return str.matches(Regex("^\\d+$"))
}

class Interpreter(private val p: Processor) {
    private val registers = enumValues<Register>().map { it.name.toLowerCase() }

    private fun getReg(reg: String): Register? {
        if (reg !in registers) return null

        return enumValueOf<Register>(reg.toUpperCase())
    }

    private fun getValue(dst: String): Int? {
        val reg = getReg(dst)

        if (reg != null) return p.get(reg)
        if (isNumber(dst)) return dst.toInt()

        return null
    }

    fun run(line: String) {
        val r = line.trim().replace(",", "").split(" ")

        if (r.size < 2) return

        val (op, dstStr) = r.map { it.toLowerCase() }
        val value = getValue(dstStr) ?: return

        when (op) {
            "jmp" -> p.jmp(value)
            "push" -> p.push(value)
        }

        val dstReg = getReg(dstStr) ?: return

        when (op) {
            "incr" -> p.incr(dstReg)
            "decr" -> p.decr(dstReg)
            "pop" -> p.pop(dstReg)
        }

        if (r.size < 3) return
        val srcStr = r[2].toLowerCase()
        val srcReg = getValue(srcStr) ?: return

        when (op) {
            "mov" -> p.mov(dstReg, srcReg)
            "add" -> p.add(dstReg, srcReg)
            "sub" -> p.sub(dstReg, srcReg)
        }
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

    i.runLines("""
        mov eax, 0
        mov ebx, 0
        mov pc, 0
    """)

    i.loop()
}
