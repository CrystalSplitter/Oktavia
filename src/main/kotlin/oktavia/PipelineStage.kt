package oktavia


class InputPort(val parent: PipelineStage, val name: String)
{
    var connection: OutputPort? = null
        private set

    fun connect(port: OutputPort?) {
        this.connection = port
        if (port != null && !port.connections.contains(this)) {
            port.connect(this)
        }
    }

    fun disconnect() = this.connect(null)

    fun peek(): Float = this.connection?.poll() ?: throw IllegalStateException("Can't peek() disconnected port")
}

/**
 * @param[call] Callback to use to get
 */
class OutputPort(val parent: PipelineStage, val name: String, private val call: () -> Float)
{

    val connections: List<InputPort>
        get() = this._connections.toList()
    private val _connections: ArrayList<InputPort> = arrayListOf()
    private var cachedValue: Float = 0.0f
    private var validCache: Boolean = false

    fun connect(port: InputPort) {
        if (!this._connections.contains(port)) {
            this._connections.add(port)
        }
        if (port.connection != this) {
            port.connect(this)
        }
    }

    fun poll(): Float {
        if (this.validCache) {
            return this.cachedValue
        }
        this.cachedValue = this.call()
        this.validCache = true
        return this.cachedValue
    }

    fun invalidateCache() {
        this.validCache = false
    }
}

interface PipelineStage {
    val inputPorts: HashMap<String, InputPort>
    val outputPorts: HashMap<String, OutputPort>
    val name: String

    fun connectInput(localPortName: String, foreignStage: PipelineStage, foreignPortName: String) {
        this.inputPorts[localPortName]!!.connect(foreignStage.outputPorts[foreignPortName])
    }

    fun connectOutput(localPortName: String, foreignStage: PipelineStage, foreignPortName: String) {
        this.outputPorts[localPortName]!!.connect(foreignStage.inputPorts[foreignPortName]!!)
    }

    fun invalidateCache() {
        this.outputPorts.forEach { (_, port) -> port.invalidateCache() }
    }

    fun halted(): Boolean {
        return false
    }
}

interface SourceStage: PipelineStage {
    fun refreshData()
}

interface TargetStage: PipelineStage {
    fun pollNewData()
}