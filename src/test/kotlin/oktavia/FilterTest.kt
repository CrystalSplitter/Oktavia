package oktavia

import java.io.File
import javax.sound.sampled.AudioSystem
import kotlin.test.Test
import kotlin.test.assertEquals

class FilterTest {
    private val testSignalPath: String = "/home/crystal/Documents/4_point_signal.wav"

    @Test fun `should be able to make a mean filter`() {
        val filter = FIRFilter(arrayListOf(0.25f, 0.25f, 0.25f, 0.25f))
        val stream = AudioSystem.getAudioInputStream(File(testSignalPath))
        filter.connectInput(stream)
        assertApprox(filter.read(), -0.125f, 10e-3f)
        assertApprox(filter.read(), -0.25f, 10e-3f)
        assertApprox(filter.read(), -0.5f, 10e-3f)
        assertApprox(filter.read(), -0.5f, 10e-3f)
        for (i in 0..10) filter.read()
        assertEquals(0.0f, filter.read())
    }

    @Test fun `should be able to do an array read`() {
        val filter = FIRFilter(arrayListOf(0.25f, 0.25f, 0.25f, 0.25f))
        val stream = AudioSystem.getAudioInputStream(File(testSignalPath))
        filter.connectInput(stream)
        val output: Array<Float> = filter.read(4).toTypedArray()
        val expected = arrayOf(-0.125f, -0.25f, -0.5f, -0.5f)
        output.forEachIndexed { idx, it -> assertApprox(expected[idx], it, 10e-3f) }
    }
}