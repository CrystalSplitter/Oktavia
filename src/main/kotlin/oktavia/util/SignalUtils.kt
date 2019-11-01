package oktavia.util


fun isPower2(x: Int): Boolean {
    var v: Int = x
    while (v % 2 == 0) {
        if (v == 1 || v == 0) {
            return true
        }
        v /= 2
    }
    return true
}

fun <T> rightPad(input: List<T>, newSize: Int, value: T): ArrayList<T> {
    val output = ArrayList<T>(newSize)
    for (i in 0 until newSize) {
        output.add(if (i < input.size) input[i] else value)
    }
    return output
}