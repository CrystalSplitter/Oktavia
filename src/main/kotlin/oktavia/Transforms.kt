package oktavia

import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

const val SAMPLE_MIN: Int = 32

fun simpleDFT(input: List<Double>): ArrayList<Cmplx> {
    val output: ArrayList<Cmplx> = ArrayList<Cmplx>(input.size)
    for (i in 0 until input.size) {
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

fun cooleyTukey(input: List<Double>): ArrayList<Cmplx> {
    
    if (input.size % 2 != 0) {
        throw IllegalArgumentException("")
    }

    if (input.size <= SAMPLE_MIN) {
        return simpleDFT(input)
    }

    val output: ArrayList<Cmplx> = ArrayList<Cmplx>(input.size)
    
    val fftEven = cooleyTukey(input.slice(0 until input.size step 2))
    val fftOdd = cooleyTukey(input.slice(1 until input.size step 2))
    val factor: ArrayList<Cmplx> = ArrayList(input.size)
    for (i in 0 until input.size) {
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

fun invDFT(input: List<Cmplx>): ArrayList<Cmplx> {
    val output: ArrayList<Cmplx> = ArrayList(input.size)
    val inverseInputSize: Double = 1.0/input.size.toDouble()
    for (i in 0 until input.size) {
        var runningCmplxSum = Cmplx(0.0)
        val iDub: Double = i.toDouble()
        for (j in 0 until input.size) {
            val jDub: Double = j.toDouble()
            val omega = 2 * PI * jDub * inverseInputSize
            runningCmplxSum += Cmplx(cos(omega*iDub),sin(omega*iDub)) * input[j]
        }
        if (kotlin.math.abs(runningCmplxSum.im) > Cmplx.COMPONENT_TOLERANCE) {
            println(
                "Input signal not collapsible to Reals. "
                + "Imaginary component was ${runningCmplxSum.im}"
            )
        }
        output.add(runningCmplxSum)
    }
    return output
}
