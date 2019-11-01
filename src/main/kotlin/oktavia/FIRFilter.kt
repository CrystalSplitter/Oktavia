package oktavia

import javax.sound.sampled.AudioInputStream

class FIRFilter (val coefficients: Array<Float>) {
    private val buffer = SignalGroup(coefficients.size)
    private var inputSource: AudioInputStream? = null

    /**
     * Connect the input of this filter to a stream source.
     */
    fun connectInput(stream: AudioInputStream) {
        inputSource = stream
    }

    fun read(): Float {
        val inputSource = this.inputSource ?: throw IllegalStateException("Need input source to read.")
        buffer.readFromAudioStream(inputSource, 5)

        var out = 0.0f
        coefficients.forEachIndexed { idx, it ->
            out += it * buffer.channels[0][idx]
        }
        return out
    }
}