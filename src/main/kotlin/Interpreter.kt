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
        .map { it.toString().toInt() }
        .map { if (it == 0) '○' else '●' }
        .joinToString("")
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

        val (op, dstStr) = r.map { it.toLowerCase() }
        val value = getValue(dstStr) ?: return

        when (op) {
            "jmp" -> p.jmp(value)
            "push" -> p.push(value)
            "print" -> print(value, dstStr)
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

    private fun print(value: Int, dstStr: String) {
        println("$dstStr = $value (${showPattern(value)})")
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
        mov eip, 0
    """)

    i.loop()
}
