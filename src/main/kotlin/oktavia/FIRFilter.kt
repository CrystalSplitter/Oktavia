package oktavia

import javax.sound.sampled.AudioInputStream

class FIRFilter (val coefficients: List<Float>, val batchSize: Int = 1) {
    private val buffer = SignalGroup(coefficients.size, Float.NaN)
    private var inputSource: AudioInputStream? = null
    val sampleRate: Float
        get() = this.buffer.sampleRate

    init {
        this.buffer.channel(0).fill(0.0f)
    }

    /**
     * Connect the input of this filter to a stream source.
     */
    fun connectInput(stream: AudioInputStream) {
        inputSource = stream
        buffer.sampleRate = stream.format.sampleRate
    }

    fun read(frames: Int): FloatArray {
        return FloatArray(frames) { this.read() }
    }

    fun read(): Float {
        val inputSource = this.inputSource ?: throw IllegalStateException("Need input source to read.")
        buffer.read(inputSource, batchSize)
        var out = 0.0f
        coefficients.forEachIndexed { idx, it ->
            out += it * buffer.channel(0)[idx]
        }
        return out
    }
}