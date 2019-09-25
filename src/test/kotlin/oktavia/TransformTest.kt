package oktavia

import java.io.File
import kotlin.test.Test

class TransformTest {
    @Test fun testCooleyTukeyComparison() {
        val signalLength = 1024
        val signal = ArraySignal(signalLength, 10.0) {
            kotlin.math.cos(2*kotlin.math.PI*it/signalLength)
        }
        val d1: ArrayList<Cmplx> = simpleDFT(signal.sampleList())
        val d2: ArrayList<Cmplx> = cooleyTukey(signal.sampleList())
        println(d1.size)
        println(d2.size)
    
        val mags = d2.map { it.magnitude() }
        File("cooley.csv").writeText(arrayToCSV(mags))

        for (i in 0 until d1.size) {
            assertApprox(d1[i].magnitude(), d2[i].magnitude(), tolerance=1e-3)
        }
    }
}
