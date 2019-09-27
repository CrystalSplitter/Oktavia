package oktavia.util

fun isPower2(x: Int): Boolean {
    var v: Int = x
    while (v % 2 == 0) {
        if (v == 0) {
            return true
        }
        v /= 2
    }
    return true
}

fun rightPad(input: List<Double>, newSize: Int, value: Double = 0.0): ArrayList<Double> {
    val output = ArrayList<Double>(newSize)
    for (i in 0 until newSize) {
        output.add(if (i < input.size) input[i] else value)
    }
    return output
}