package oktavia

import oktavia.util.bytesToBits
import java.nio.ByteBuffer
import java.nio.ByteOrder
import javax.sound.sampled.AudioFormat
import javax.sound.sampled.AudioSystem
import javax.sound.sampled.DataLine
import javax.sound.sampled.SourceDataLine
import kotlin.math.pow
import kotlin.math.roundToInt

class SignalPlayer(override val name: String = "SignalPlayer", inputConnection: OutputPort? = null): TargetStage {
    var input: InputPort = InputPort(this, "input")
    override val inputPorts: HashMap<String, InputPort>
        get() = hashMapOf(this.input.name to this.input)
    override val outputPorts: HashMap<String, OutputPort>
        get() = hashMapOf()

    private val outputLine: SourceDataLine
    private val sampleSize: Int = 2
    private val channels: Int = 1
    private val audioFormat: AudioFormat
    private var started: Boolean = false

    private var numStoredSamples: Int = 0
    private var storedSamples: FloatArray = FloatArray(128) { 0.0f }

    init {
        if (inputConnection != null) {
            this.input.connect(inputConnection)
        }
        this.audioFormat = AudioFormat(
                44100.0f,
                sampleSize.bytesToBits(),
                channels,
                true,
                false
        )
        //val convertedStream = AudioSystem.getAudioInputStream(format, audioStream)
        val info: DataLine.Info = DataLine.Info(SourceDataLine::class.java, this.audioFormat)
        this.outputLine = AudioSystem.getLine(info) as SourceDataLine
    }

    override fun pollNewData() {
        if (!started) {
            this.outputLine.open(this.audioFormat)
            outputLine.start()
            started = true
        }
        this.storedSamples[this.numStoredSamples] = this.input.peek()
        this.numStoredSamples++
        if (this.numStoredSamples > 127) {
            val byteArray = this.convertToByteArray(
                    this.storedSamples,
                    this.sampleSize / this.channels,
                    false
            )
            outputLine.write(byteArray, 0, byteArray.size)
            this.numStoredSamples = 0
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