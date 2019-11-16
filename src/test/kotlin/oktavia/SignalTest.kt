package oktavia

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.math.PI


class SignalTest {
    @Test fun testFullSlice() {
        val dut = DoubleSignal(100, 10.0f)
        val slicedDut = dut.timeSlice()
        assertEquals(dut.size, slicedDut.size)
    }

    @Test fun testMiddleSlice() {
        val dut = DoubleSignal(100, 10.0f) {
            kotlin.math.sin(2.0 * PI * it/100.0)
        }
        val slicedDut = dut.timeSlice(2.5, 7.5)
        assertApprox(slicedDut.duration(), 5.0)
        assertEquals(slicedDut.size * 2, dut.size)
        assertEquals(slicedDut[0], dut[25])
    }

    @Test fun `signals can be multiplied`() {
        val signal = DoubleSignal(200, 10.0f) { 1.0 }
        val signal2 = signal * 2.0
        assertApprox(signal[0] * 2.0, signal2[0])
    }

    @Test fun `SignalBuffers have right size`() {
        val signal = SampleBuffer(4)
        for (i in 1..6) {
            signal.push(i.toFloat())
        }
        assertEquals(signal.capacity, 4)
        assertEquals(signal.size, 4)
        val expectedOut: Array<Float> = arrayOf(3.0f, 4.0f, 5.0f, 6.0f)
        for (i in 0 until signal.capacity) {
            val stored = signal.pop()
            assertEquals(expectedOut[i], stored)
        }
    }

    @Test fun `SignalBuffers have correct getset order`() {
        val signal = SampleBuffer(3)
        signal.push(1.0f)
        signal.push(2.0f)
        signal.push(3.0f)
        assertEquals(3.0f, signal[0])
        assertEquals(2.0f, signal[1])
        assertEquals(1.0f, signal[2])
    }
}
