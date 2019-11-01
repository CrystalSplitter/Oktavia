package oktavia

import java.io.File
import kotlin.test.Test


class TransformTest {
    @Test fun `fft and simpleDFT should have same output`() {
        val signalLength = 1024
        val signal = DoubleSignal(signalLength, 10.0) {
            kotlin.math.cos(2*kotlin.math.PI*it/signalLength)
        }
        val d1: ArrayList<Cmplx> = simpleDFT(signal.sampleList())
        val d2: ArrayList<Cmplx> = fft(signal.sampleList(), 1024) as ArrayList<Cmplx>
    
        val mags = d2.map { it.magnitude() }
        File("cooley.csv").writeText(arrayToCSV(mags))

        for (i in 0 until d1.size) {
            assertApprox(d1[i].magnitude(), d2[i].magnitude(), tolerance=1e-3)
        }
    }

    @Test fun `inverse dft of a dft should be the same`() {
        val signalLength = 1024
        val signal = DoubleSignal(signalLength, 5.0) {
            kotlin.math.cos(2*kotlin.math.PI*it/signalLength)
        }
        val recreation = invSimpleDFT(simpleDFT(signal.sampleList()))
        for (i in signal.sampleList().indices) {
            assertApprox(recreation[i].re, signal[i])
            assertApprox(recreation[i].im, 0.0)
        }
    }

    @Test fun `inverse dft of a fft should be the same`() {
        val signalLength = 1024
        val signal = DoubleSignal(signalLength, 69.0) {
            kotlin.math.cos(2*kotlin.math.PI*it/signalLength)
        }
        val recreation = invSimpleDFT(fft(signal.sampleList(), 1024))
        for (i in signal.sampleList().indices) {
            assertApprox(recreation[i].re, signal[i])
            assertApprox(recreation[i].im, 0.0)
        }
    }

    @Test fun `ifft of a dft should be the same`() {
        val signalLength = 1024
        val signal = DoubleSignal(signalLength, 80.0) {
            kotlin.math.cos(2*kotlin.math.PI*it/signalLength)
        }
        val recreation = ifft(simpleDFT(signal.sampleList()), 1024)
        for (i in signal.sampleList().indices) {
            assertApprox(recreation[i].re, signal[i])
            assertApprox(recreation[i].im, 0.0)
        }
    }

    @Test fun `ifft of a fft should be the same`() {
        val signalLength = 1024
        val signal = DoubleSignal(signalLength, 42.0) {
            kotlin.math.cos(2*kotlin.math.PI*it/signalLength)
        }
        val recreation = ifft(fft(signal.sampleList(), 2048), 2048)
        for (i in signal.sampleList().indices) {
            assertApprox(recreation[i].re, signal[i])
            assertApprox(recreation[i].im, 0.0)
        }
    }
}
