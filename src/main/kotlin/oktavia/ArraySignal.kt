package oktavia

class ArraySignal(
        val size: Int,
        var sampleRate: Double,
        initFunction: ((it: Int) -> Double)? = null
): Iterable<Double> {
    private val dataArray: ArrayList<Double>
    
    init {
        dataArray = ArrayList<Double>(size)
        for (i in 0 until size) {
            if (initFunction != null) {
                dataArray.add(initFunction(i))
            } else {
                dataArray.add(0.0)
            }
        }
    }

    fun duration(): Double = size.toDouble() * 1.0/sampleRate
    
    fun slice(startTime: Double = 0.0, endTime: Double? = null): ArraySignal {
        val trueEndTime: Double = if (endTime != null) endTime else duration()
        val startSample: Int = kotlin.math.round(startTime * sampleRate).toInt()
        val endSample: Int = kotlin.math.round(trueEndTime * sampleRate).toInt()
        val newSize = endSample - startSample
        return ArraySignal(newSize, sampleRate) {
            this.dataArray[it + startSample]
        }
    }

    override operator fun iterator(): Iterator<Double> {
        return dataArray.iterator()
    }

    operator fun get(it: Int): Double = this.dataArray[it]
    operator fun set(it: Int, value: Double) { this.dataArray[it] = value }
    
    fun sampleList(): List<Double> = this.dataArray.toList()
}
