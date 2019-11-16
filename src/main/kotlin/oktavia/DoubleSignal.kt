package oktavia

import java.io.InputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder
import javax.sound.sampled.AudioInputStream
import kotlin.math.pow


class DoubleSignal(
        override val size: Int,
        override var sampleRate: Float,
        initFunction: ((it: Int) -> Double)? = null)
    : Signal {

    private val dataArray = DoubleArray(size)

    init {
        for (i in 0 until size) {
            if (initFunction != null) {
                dataArray[i] = initFunction(i)
            } else {
                dataArray[i] = 0.0
            }
        }
    }

    override fun timeSlice(startTime: Double, endTime: Double?): DoubleSignal {
        val trueEndTime = endTime ?: duration()
        val startSample = kotlin.math.round(startTime * sampleRate).toInt()
        val endSample = kotlin.math.round(trueEndTime * sampleRate).toInt()
        val newSize = endSample - startSample
        return DoubleSignal(newSize, sampleRate) {
            this[it + startSample]
        }
    }

    operator fun times(other: Double): DoubleSignal {
        return DoubleSignal(this.size, this.sampleRate) { this.dataArray[it] * other }
    }

    override operator fun iterator(): Iterator<Double> {
        return dataArray.iterator()
    }

    operator fun get(it: Int): Double = this.dataArray[it]
    operator fun set(it: Int, value: Double) { this.dataArray[it] = value }

    fun dot(other: DoubleSignal): DoubleSignal {
        require(other.size == size) { "Signals must be the same size for dot multiplication" }
        return DoubleSignal(size, sampleRate) { this[it] * other[it] }
    }

    fun readAudioStream(stream: AudioInputStream, numFrames: Int? = null) {
        val frameSize: Int = stream.format.frameSize
        val numChannels: Int = stream.format.channels
        println("Frame Size $frameSize")
        if (numFrames != null) {
            // Buffer size has to at least be 4, because that's the size of an int.
            val bufferSize = maxOf(frameSize, 4)
            val byteBuffer = ByteBuffer.allocate(maxOf(frameSize, 4))
            if (stream.format.isBigEndian) {
                byteBuffer.order(ByteOrder.BIG_ENDIAN)
            } else {
                byteBuffer.order(ByteOrder.LITTLE_ENDIAN)
            }
            for (i: Int in 0 until numFrames) {
                val byteData = ByteArray(frameSize)
                stream.read(byteData)

                val paddedByteData: ByteArray = if (stream.format.isBigEndian) {
                    val padValue: Byte = if (byteData[0].toInt() < 0) 0xff.toByte() else 0x00.toByte()
                    ByteArray(bufferSize) {
                        if (it >= bufferSize - frameSize) byteData[it - bufferSize + frameSize] else padValue
                    }
                } else {
                    val padValue: Byte = if (byteData[byteData.size - 1].toInt() < 0) 0xff.toByte() else 0x00.toByte()
                    ByteArray(bufferSize) { if (it < frameSize) byteData[it] else padValue }
                }

                byteBuffer.put(paddedByteData)
                byteBuffer.flip()
                val frameMagnitude: Int = byteBuffer.int
                byteBuffer.flip()
                this[i] = frameMagnitude.toDouble() / 2.0.pow(bufferSize*4-1)
            }
        } else {
            throw IllegalArgumentException("readAudioStream cannot support such a limited number of frames.")
        }
    }

    fun sampleList(): List<Double> = this.dataArray.toList()
}

operator fun Double.times(other: DoubleSignal): DoubleSignal = other * this