package oktavia

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.math.PI


class ArraySignalTest {
    @Test fun testFullSlice() {
        val dut = ArraySignal(100, 10.0)
        val slicedDut = dut.slice()
        assertEquals(dut.size, slicedDut.size)
    }

    @Test fun testMiddleSlice() {
        val dut = ArraySignal(100, 10.0) {
            kotlin.math.sin(2.0 * PI * it/100.0)
        }
        val slicedDut = dut.slice(2.5, 7.5)
        assertApprox(slicedDut.duration(), 5.0)
        assertEquals(slicedDut.size * 2, dut.size)
        assertEquals(slicedDut[0], dut[25])
    }
}
