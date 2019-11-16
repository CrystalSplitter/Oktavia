package oktavia

interface Signal: Iterable<Number> {
    val sampleRate: Float
    val size: Int
    fun duration(): Double = size.toDouble() / this.sampleRate
    fun timeSlice(startTime: Double = 0.0, endTime: Double? = null): Signal
}