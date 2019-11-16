package oktavia

import java.nio.ByteBuffer
import java.nio.ByteOrder
import javax.sound.sampled.AudioInputStream
import kotlin.math.pow

private const val DEFAULT_SAMPLE_RATE = 44100.0f

class SignalGroup(bufferCapacity: Int, var sampleRate: Float = DEFAULT_SAMPLE_RATE, numChannels: Int = 1) {
    var currentFrameIdx: Long = 0
        private set
    var bufferCapacity: Int = bufferCapacity
        private set
    val numChannels: Int
        get() = this.channels.size
    private var channels: ArrayList<SampleBuffer> = arrayListOf()

    init {
        for (i in 1..numChannels) {
            this.addChannel()
        }
    }

    fun channel(idx: Int): SampleBuffer {
        return this.channels[idx]
    }

    fun addChannel() {
        this.channels.add(SampleBuffer(bufferCapacity))
    }

    fun read(stream: AudioInputStream, numFrames: Int) {
        this.sampleRate = stream.format.sampleRate
        val frameSize: Int = stream.format.frameSize
        val origFrameIdx = currentFrameIdx
        while (currentFrameIdx < origFrameIdx + numFrames) {
            val byteData = ByteArray(frameSize)
            stream.read(byteData)
            //print(">>> ")
            //byteData.forEach { print(String.format("|%02X", it)) }
            //println("|")
            val channels = stream.format.channels
            for (i in 0 until channels) {
                val sliceRange: IntRange = i * (frameSize/channels) until (i + 1) * (frameSize/channels)
                val slicedByteData: ByteArray = byteData.sliceArray(sliceRange)
                this.loadByteArray(slicedByteData, stream.format.isBigEndian, i)
            }
            // Next frame
            currentFrameIdx++
        }
    }

    fun read(array: FloatArray, numFrames: Int, channel: Int) {
        array.forEach { this.channels[channel].push(it) }
    }

    fun loadByteArray(byteData: ByteArray, isBigEndian: Boolean, channelIndex: Int) {
        // Buffer size has to at least be 4, because that's the size of an int.
        val bufferSize = maxOf(byteData.size, 4)
        val byteBuffer = ByteBuffer.allocate(bufferSize)
        if (isBigEndian) {
            byteBuffer.order(ByteOrder.BIG_ENDIAN)
        } else {
            byteBuffer.order(ByteOrder.LITTLE_ENDIAN)
        }
        val paddedByteData: ByteArray = padByteArray(byteData, bufferSize, isBigEndian)
        byteBuffer.put(paddedByteData)
        byteBuffer.flip()
        val frameMagnitude: Int = byteBuffer.int
        byteBuffer.flip()
        this.channels[channelIndex].push(frameMagnitude.toFloat() / 2.0f.pow(bufferSize * 4 - 1))
    }

    private fun padByteArray(byteData: ByteArray, newSize: Int, isBigEndian: Boolean): ByteArray {
        val dataSize = byteData.size
        return if (isBigEndian) {
            // Pad with the appropriate sign.
            val padValue: Byte = if (byteData[0].toInt() < 0) 0xff.toByte() else 0x00.toByte()
            ByteArray(newSize) {
                if (it >= newSize - dataSize) {
                    byteData[it - newSize + dataSize]
                } else {
                    padValue
                }
            }
        } else {
            // Pad with the appropriate sign.
            val padValue: Byte = if (byteData[byteData.size - 1].toInt() < 0) 0xff.toByte() else 0x00.toByte()
            ByteArray(newSize) {
                if (it < dataSize) {
                    byteData[it]
                }
                else {
                    padValue
                }
            }
        }
    }

    fun pop(): Array<Float> {
        return Array(this.channels.size) { this.channels[it].pop() }
    }

    fun pop(channelIdx: Int): Float {
        return this.channels[channelIdx].pop()
    }

    private fun currentChannelIndex(): Int {
        return (this.currentFrameIdx % this.numChannels.toLong()).toInt()
    }

    /*
    override fun timeSlice(startTime: Double, endTime: Double?): FloatSignal {
        val endIdx: Int = if (endTime == null) {
            this.size - 1
        } else {
            kotlin.math.round(endTime * sampleRate).toInt()
        }
        require(endIdx < this.size) { IllegalArgumentException("endTime beyond length of BufferSignal") }
        val startIdx: Int = kotlin.math.round(startTime * sampleRate).toInt()
        val newSize = endIdx - startIdx
        return FloatSignal(newSize, sampleRate) {
            this.channel()[it + endIdx]
        }
    }
    */
}