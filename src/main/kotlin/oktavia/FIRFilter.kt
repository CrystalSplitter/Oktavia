package oktavia

class FIRFilter (
        val coefficients: List<Float>,
        val batchSize: Int = 1,
        override val name: String = "FIRFilter",
        inputConnection: OutputPort? = null,
        outputConnections: Array<InputPort>? = null
): PipelineStage {
    private val buffer = SignalGroup(coefficients.size, Float.NaN)
    val input: InputPort = InputPort(this, "input")
    val output: OutputPort = OutputPort(this, "output") { this.peek() }
    override val inputPorts: HashMap<String, InputPort>
        get() = hashMapOf(input.name to input)
    override val outputPorts: HashMap<String, OutputPort>
        get() = hashMapOf(output.name to output)

    val sampleRate: Float
        get() = this.buffer.sampleRate

    init {
        if (inputConnection != null) {
            this.input.connect(inputConnection)
        }
        if (outputConnections != null) {
            for (outConnection in outputConnections) {
                this.output.connect(outConnection)
            }
        }
        this.buffer.channel(0).fill(0.0f)
    }

    fun peek(frames: Int): FloatArray {
        return FloatArray(frames) { this.peek() }
    }

    fun peek(): Float {
        buffer.push(input.peek(), 0)
        var out = 0.0f
        coefficients.forEachIndexed { idx, it ->
            out += it * buffer.channel(0)[idx]
        }
        return out
    }
}