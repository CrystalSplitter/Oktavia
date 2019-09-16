package oktavia

import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin
import java.io.File

class Cmplx(val re: Double = 0.0, val im: Double = 0.0) {
    override fun toString(): String = "ℂ(${re},${im})"
    fun magnitude(): Double = kotlin.math.sqrt(re*re + im*im)
}

operator fun Cmplx.plus(other: Cmplx): Cmplx = Cmplx(this.re + other.re, this.im + other.im)
operator fun Cmplx.minus(other: Cmplx): Cmplx = Cmplx(this.re - other.re, this.im - other.im)
operator fun Cmplx.times(other: Cmplx): Cmplx {
    return Cmplx(
        this.re * other.re - this.im * other.im,
        this.re * other.im + this.im * other.re
    )
}
operator fun Cmplx.times(other: Double): Cmplx = Cmplx(this.re * other, this.im * other)
operator fun Cmplx.div(other: Double): Cmplx = Cmplx(this.re / other, this.im / other)

const val CMPLX_TOLERANCE = 10e-8

fun main(args: Array<String>) {
    val input = arrayListOf<Double>()
    val fidelity = 4
    for (i in 0 until args[0].toInt()*fidelity) {
        when (i % fidelity) {
            0 -> input.add(0.0)
            1 -> input.add(1.0)
            2 -> input.add(0.0)
            3 -> input.add(-1.0)
        }
    }
    val freqDomain = dft(input)
    val timeDomain = invDft(freqDomain)
    val mags = freqDomain.map { it.magnitude() }
    val reals: List<Double> = timeDomain.map { it.re }
    File("my_output.csv").writeText(arrayToCSV(mags))
    File("my_output_2.csv").writeText(arrayToCSV(reals))
}

fun dft(input: List<Double>): ArrayList<Cmplx> {
    val output: ArrayList<Cmplx> = ArrayList<Cmplx>(input.size)
    val inverseInputSize: Double = 1.0/input.size.toDouble()
    for (i in 0 until input.size) {
        var runningReSum: Double = 0.0
        var runningImSum: Double = 0.0
        val iDub: Double = i.toDouble()

        for ((j, inputJ) in input.withIndex()) {
            val jDub: Double = j.toDouble()
            val omega = 2 * PI * jDub * inverseInputSize
            runningReSum += inputJ * cos(omega * iDub)
            runningImSum += inputJ * -sin(omega * iDub)
        }
        output.add(
            Cmplx(
                runningReSum * inverseInputSize,
                runningImSum * inverseInputSize
            )
        )
    }
    return output
}

fun invDft(input: List<Cmplx>): ArrayList<Cmplx> {
    val output: ArrayList<Cmplx> = ArrayList<Cmplx>(input.size)
    val inverse2Pi = 0.5/PI
    val inverseInputSize: Double = 1.0/input.size.toDouble()
    for (i in 0 until input.size) {
        var runningCmplxSum: Cmplx = Cmplx(0.0)
        val iDub: Double = i.toDouble()
        for (j in 0 until input.size) {
            val jDub: Double = j.toDouble()
            val omega = 2 * PI * jDub * inverseInputSize
            runningCmplxSum += Cmplx(cos(omega*iDub),sin(omega*iDub)) * input[j]
        }
        if (kotlin.math.abs(runningCmplxSum.im) > CMPLX_TOLERANCE) {
            println(
                "Input signal not collapsable to Reals. "
                + "Imaginary component was ${runningCmplxSum.im}"
            )
        }
        output.add(runningCmplxSum)
    }
    return output
}

fun arrayToCSV(array: List<Any?>): String {
    var output: String = ""
    for (i in 0 until array.size) {
        output += "${array[i].toString()},\n"
    }
    return output
}
