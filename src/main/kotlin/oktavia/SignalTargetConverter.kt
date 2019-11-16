package oktavia

import oktavia.util.bytesToBits
import java.io.File
import java.nio.ByteBuffer
import java.nio.ByteOrder
import javax.sound.sampled.AudioFormat
import javax.sound.sampled.AudioSystem
import javax.sound.sampled.DataLine
import javax.sound.sampled.SourceDataLine
import kotlin.math.pow
import kotlin.math.roundToInt

class PlaySignal() {
    var inputSource: FIRFilter? = null

    /**
     * Connect the input of this filter to a stream source.
     */
    fun connectInput(stream: FIRFilter) {
        inputSource = stream
    }

    fun start() {
        require(inputSource != null) { IllegalStateException("Need input source to read.") }
        val sampleSize = 2
        val channels = 1

        //val audioStream = AudioSystem.getAudioInputStream(File("/home/crystal/Documents/wip_one_channel.wav"))
        println(inputSource!!.sampleRate)
        val format = AudioFormat(
                inputSource!!.sampleRate,
                sampleSize.bytesToBits(),
                channels,
                true,
                false
        )
        //val convertedStream = AudioSystem.getAudioInputStream(format, audioStream)
        val info: DataLine.Info = DataLine.Info(SourceDataLine::class.java, format)
        val outputLine = AudioSystem.getLine(info) as SourceDataLine
        outputLine.open(format)
        outputLine.start()

        while (true) {
            val byteArray = this.convertToByteArray(
                    inputSource!!.read(1000),
                    sampleSize/channels,
                    false
            )
            //print("<-< ")
            //byteArray.forEach { print(String.format("|%02X", it)) }
            //println("|")
            outputLine.write(byteArray, 0, byteArray.size)
        }
    }

    fun convertToByteArray(value: Float, arraySize: Int, isBigEndian: Boolean): ByteArray {
        require(kotlin.math.abs(value) <= 1)
        val rescaled = (value * 2.0.pow(arraySize.bytesToBits() - 1)).roundToInt()
        val bufferSize = kotlin.math.max(arraySize, 4)
        val byteOrder = if (isBigEndian) ByteOrder.BIG_ENDIAN else ByteOrder.LITTLE_ENDIAN
        val byteBuffer = ByteBuffer.allocate(bufferSize).order(byteOrder).putInt(rescaled)
        return byteBuffer.array().slice(0 until arraySize).toByteArray()
    }

    fun convertToByteArray(values: FloatArray, sampleSize: Int, isBigEndian: Boolean): ByteArray {
        val outputList: ArrayList<Byte> = ArrayList(values.size * sampleSize)
        for (value in values) {
            val bound = (1 shl (sampleSize.bytesToBits() - 1)) - 1
            val rescaledInt = (value * bound).roundToInt()
            val boundedInt = kotlin.math.min(bound, kotlin.math.max(-bound, rescaledInt))
            val bufferSize = kotlin.math.max(sampleSize, 4)
            val byteOrder = if (isBigEndian) ByteOrder.BIG_ENDIAN else ByteOrder.LITTLE_ENDIAN
            val byteBuffer = ByteBuffer.allocate(bufferSize).order(byteOrder).putInt(boundedInt)
            outputList.addAll(byteBuffer.array().slice(0 until sampleSize))
        }
        return outputList.toByteArray()
    }
}