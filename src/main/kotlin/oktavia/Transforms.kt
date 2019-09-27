package oktavia

import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

import oktavia.util.rightPad
import oktavia.util.isPower2

const val SAMPLE_MIN: Int = 32


fun fft(input: List<Double>, numSamples: Int): MutableList<Cmplx> {
    require (isPower2(numSamples)) { "numSamples must be a power of 2" }
    return recursiveCooleyTukey(rightPad(input, numSamples))
}

fun ifft(input: List<Cmplx>, sliceLength: Int): MutableList<Cmplx> {
    require (isPower2(input.size)) { "input size must be a power of 2" }
    return invRecursiveCooleyTukey(input).subList(0, sliceLength)
}

fun simpleDFT(input: List<Double>): ArrayList<Cmplx> {
    val output: ArrayList<Cmplx> = ArrayList<Cmplx>(input.size)
    for (i in input.indices) {
        var runningReSum = 0.0
        var runningImSum = 0.0
        val iDub: Double = i.toDouble()

        for ((j, inputJ) in input.withIndex()) {
            val jDub: Double = j.toDouble()
            val omega = 2 * PI * jDub / input.size
            runningReSum += inputJ * cos(omega * iDub)
            runningImSum += inputJ * -sin(omega * iDub)
        }
        output.add(
            Cmplx(
                runningReSum,
                runningImSum
            )
        )
    }
    return output
}

fun invSimpleDFT(input: List<Cmplx>): ArrayList<Cmplx> {
    val output: ArrayList<Cmplx> = ArrayList(input.size)
    val inverseInputSize: Double = 1.0/input.size.toDouble()
    for (i in input.indices) {
        var runningCmplxSum = Cmplx(0.0)
        val iDub: Double = i.toDouble()
        for (j in input.indices) {
            val jDub: Double = j.toDouble()
            val omega = 2 * PI * jDub * inverseInputSize
            runningCmplxSum += Cmplx(cos(omega*iDub),sin(omega*iDub)) * input[j]
        }
        output.add(runningCmplxSum * inverseInputSize)
    }
    return output
}

private fun recursiveCooleyTukey(input: List<Double>): ArrayList<Cmplx> {

    require(input.size % 2 == 0) { "Input must be a power of 2" }

    if (input.size <= SAMPLE_MIN) {
        return simpleDFT(input)
    }

    val output: ArrayList<Cmplx> = ArrayList<Cmplx>(input.size)

    val fftEven = recursiveCooleyTukey(input.slice(0 until input.size step 2))
    val fftOdd = recursiveCooleyTukey(input.slice(1 until input.size step 2))
    val factor: ArrayList<Cmplx> = ArrayList(input.size)
    for (i in input.indices) {
        factor.add(
                Cmplx(
                        cos(2*PI*i.toDouble()/input.size),
                        -sin(2*PI*i.toDouble()/input.size)
                )
        )
    }
    for (i in 0 until input.size/2) {
        output.add((fftEven[i] + factor[i] * fftOdd[i]))
    }
    for (i in 0 until input.size/2) {
        output.add((fftEven[i] + factor[i + input.size/2] * fftOdd[i]))
    }
    return output
}

private fun invRecursiveCooleyTukey(input: List<Cmplx>): ArrayList<Cmplx> {

    require(input.size % 2 == 0) { "Input must be a power of 2" }

    if (input.size <= SAMPLE_MIN) {
        return invSimpleDFT(input)
    }

    val output: ArrayList<Cmplx> = ArrayList(input.size)

    val ifftEven = invRecursiveCooleyTukey(input.slice(0 until input.size step 2))
    val ifftOdd = invRecursiveCooleyTukey(input.slice(1 until input.size step 2))
    val factor: ArrayList<Cmplx> = ArrayList(input.size)

    for (i in 0 until input.size) {
        factor.add(
                Cmplx(
                        cos(2*PI*i.toDouble()/input.size),
                        sin(2*PI*i.toDouble()/input.size)
                )
        )
    }
    for (i in 0 until input.size/2) {
        output.add((ifftEven[i] + factor[i] * ifftOdd[i]) * 0.5)
    }
    for (i in 0 until input.size/2) {
        output.add((ifftEven[i] + factor[i + input.size/2] * ifftOdd[i]) * 0.5)
    }
    return output
}