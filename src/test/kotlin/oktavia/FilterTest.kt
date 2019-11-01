package oktavia

import java.io.File
import javax.sound.sampled.AudioInputStream
import javax.sound.sampled.AudioSystem
import kotlin.test.Test

class FilterTest {
    @Test fun `should be able to make a mean filter`() {
        val filter = FIRFilter(arrayOf(0.25f, 0.25f, 0.25f, 0.25f))
        val stream = AudioSystem.getAudioInputStream(File("/home/crystal/Documents/testing.wav"))
        filter.connectInput(stream)
        println(filter.read())
    }
}