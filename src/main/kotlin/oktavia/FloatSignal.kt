package oktavia


class FloatSignal(
        override val size: Int,
        override var sampleRate: Float,
        initFunction: ((it: Int) -> Float)? = null)
    : Signal {

    private val dataArray = FloatArray(size)

    init {
        for (i in 0 until size) {
            if (initFunction != null) {
                dataArray[i] = initFunction(i)
            } else {
                dataArray[i] = 0.0f
            }
        }
    }

    override fun timeSlice(startTime: Double, endTime: Double?): FloatSignal {
        val trueEndTime = endTime ?: duration()
        val startSample = kotlin.math.round(startTime * sampleRate).toInt()
        val endSample = kotlin.math.round(trueEndTime * sampleRate).toInt()
        val newSize = endSample - startSample
        return FloatSignal(newSize, sampleRate) {
            this[it + startSample]
        }
    }

    operator fun times(other: Float): FloatSignal {
        return FloatSignal(this.size, this.sampleRate) { this.dataArray[it] * other }
    }

    override operator fun iterator(): Iterator<Float> {
        return dataArray.iterator()
    }

    operator fun get(it: Int): Float = this.dataArray[it]
    operator fun set(it: Int, value: Float) { this.dataArray[it] = value }
    
    fun sampleList(): List<Float> = this.dataArray.toList()
}
