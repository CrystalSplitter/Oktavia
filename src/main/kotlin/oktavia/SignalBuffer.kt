package oktavia


class SignalBuffer(capacity: Int): Iterable<Float> {
    var writePos: Int = 0
        private set
    var readPos: Int = 0
        private set
    private var data: Array<Float> = Array(capacity + 1) { Float.NaN }
    val capacity: Int
        get() = this.data.size - 1

    inner class SignalBufferIterator: Iterator<Float> {
        var pos: Int = 0

        override fun hasNext(): Boolean {
            return this.pos != this@SignalBuffer.size()
        }

        override fun next(): Float {
            val output = this@SignalBuffer[pos]
            this.pos++
            return output
        }
    }

    fun push(sample: Float) {
        data[writePos] = sample
        // Special case for wrapping. We are wrapping if we're in the same position as the pop location,
        // and we're not empty.
        if (wrap(writePos + 1) == readPos) {
            incrementPopPos()
        }
        incrementPushPos()
    }

    fun pop(): Float {
        require(readPos != writePos) { "Buffer empty; cannot read." }
        val value = data[readPos]
        incrementPopPos()
        return value
    }

    private fun incrementPushPos() { writePos = wrap(writePos + 1) }
    private fun incrementPopPos() { readPos = wrap(readPos + 1) }

    operator fun get(i: Int): Float {
        return data[wrap(writePos - 1 - i)]
    }

    operator fun set(i: Int, value: Float) {
        data[wrap(writePos - 1 - i)] = value
    }

    override fun iterator(): Iterator<Float> {
        return SignalBufferIterator()
    }

    fun resize(newCapacity: Int) {
        data = Array(newCapacity + 1) { Float.NaN }
        reset()
    }

    fun wrap(value: Int): Int = kotlin.math.abs(value % data.size)

    fun reset() {
        readPos = 0
        writePos = 0
    }

    /**
     * @Returns Number of samples stored in the buffer currently. Maxes out at capacity.
     */
    fun size(): Int {
        if (writePos >= readPos) {
            return writePos - readPos
        }
        return (capacity - readPos) + writePos + 1
    }
}