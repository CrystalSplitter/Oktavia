package oktavia


fun assertApprox(expected: Double?, actual: Double?, tolerance: Double = 10e-6) {
    if (expected == null || actual == null) {
        throw AssertionError("$expected ≉ $actual within tolerance $tolerance")
    }
    else if (kotlin.math.abs(expected - actual) > tolerance) {
        throw AssertionError("$expected ≉ $actual within tolerance $tolerance")
    }
}

fun assertApprox(expected: Float?, actual: Float?, tolerance: Float = 10e-6f) {
    if (expected == null || actual == null) {
        throw AssertionError("$expected ≉ $actual within tolerance $tolerance")
    }
    else if (kotlin.math.abs(expected - actual) > tolerance) {
        throw AssertionError("$expected ≉ $actual within tolerance $tolerance")
    }
}
