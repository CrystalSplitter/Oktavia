package oktavia

import javax.sound.sampled.AudioInputStream

class AudioSource(
        val stream: AudioInputStream,
        val bufferSize: Int = 256,
        val batchSize: Int = 1,
        override val name: String = "AudioSource"
): SourceStage {
    private var halted: Boolean = false
    private val signalGroup: SignalGroup = SignalGroup(
            this.bufferSize,
            this.stream.format.sampleRate,
            numChannels = stream.format.channels
    )

    init {
        if (stream.format.channels > 1) {
            println("WARNING: Can only handle mono channel audio at this time.")
        }
    }

    var output: OutputPort = OutputPort(this, "output") { this.peek() }
    override val inputPorts: HashMap<String, InputPort>
        get() = hashMapOf()
    override val outputPorts: HashMap<String, OutputPort>
        get() = hashMapOf(this.output.name to this.output)

    fun peek(): Float {
        return this.signalGroup.channel(0)[0]
    }

    override fun refreshData() {
        val framesRead: Int = this.signalGroup.push(this.stream, this.batchSize)
        this.halted = framesRead < this.batchSize
    }

    override fun halted(): Boolean {
        return this.halted
    }
}