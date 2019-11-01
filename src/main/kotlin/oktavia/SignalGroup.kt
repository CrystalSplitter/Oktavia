package oktavia

import java.nio.ByteBuffer
import java.nio.ByteOrder
import javax.sound.sampled.AudioInputStream
import kotlin.math.pow

class SignalGroup(bufferLength: Int, val numChannels: Int = 1) {
    var currentFrameIdx: Long = 0
        private set
    var channels: Array<SignalBuffer>
        private set

    init {
        this.channels = Array(numChannels) { SignalBuffer(bufferLength) }
    }

    fun readFromAudioStream(stream: AudioInputStream, numFrames: Int) {
        val frameSize: Int = stream.format.frameSize
        //require (this.numChannels == stream.format.channels)
        println("Frame Size $frameSize")

        val origFrameIdx = currentFrameIdx
        while (currentFrameIdx < origFrameIdx + numFrames) {
            val byteData = ByteArray(frameSize)
            stream.read(byteData)
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

    private fun loadByteArray(byteData: ByteArray, isBigEndian: Boolean, channelIndex: Int) {
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
}