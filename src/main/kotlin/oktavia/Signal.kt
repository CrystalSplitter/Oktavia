package oktavia

abstract class Signal: Iterable<Number> {
    abstract val size: Int
    abstract var sampleRate: Double

    fun duration(): Double = size.toDouble() / sampleRate

    abstract fun timeSlice(startTime: Double = 0.0, endTime: Double? = null): Signal
}