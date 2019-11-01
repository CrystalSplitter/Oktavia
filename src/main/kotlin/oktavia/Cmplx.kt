package oktavia

import kotlin.math.cos
import kotlin.math.sin

class Cmplx(val re: Double = 0.0, val im: Double = 0.0): Number() {
    companion object {
        /**
         * At what point is it fair to say a component does not exist?
         */
        const val COMPONENT_TOLERANCE = 10e-8
        fun fromPolar(r: Double, theta: Double): Cmplx {
            return Cmplx(r * cos(theta), r * sin(theta))
        }
    }
    constructor(re: Double): this(re, 0.0)

    override fun toByte(): Byte { throw NotImplementedError() }
    override fun toChar(): Char { throw NotImplementedError() }
    override fun toFloat(): Float = re.toFloat()
    override fun toShort(): Short { throw NotImplementedError() }
    override fun toInt(): Int { throw NotImplementedError() }
    override fun toLong(): Long { throw NotImplementedError() }
    override fun toDouble(): Double = re

    fun magnitude(): Double = kotlin.math.sqrt(re*re + im*im)
    fun angle(): Double = kotlin.math.atan2(im, re)
    override fun toString(): String = if (im > 0) "${re}+${im}j" else "${re}${im}j"
    operator fun plus(other: Cmplx): Cmplx = Cmplx(this.re + other.re, this.im + other.im)
    operator fun minus(other: Cmplx): Cmplx = Cmplx(this.re - other.re, this.im - other.im)
    operator fun times(other: Cmplx): Cmplx {
        return Cmplx(
            this.re * other.re - this.im * other.im,
            this.re * other.im + this.im * other.re
        )
    }
    operator fun times(other: Double): Cmplx = Cmplx(this.re * other, this.im * other)
    operator fun div(other: Double): Cmplx = Cmplx(this.re / other, this.im / other)
}

