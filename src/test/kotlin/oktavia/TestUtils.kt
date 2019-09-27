package oktavia


fun assertApprox(left: Double?, right: Double?, tolerance: Double = 10e-6) {
    if (left == null || right == null) {
        throw AssertionError("$left ≉ $right within tolerance $tolerance")
    }
    else if (kotlin.math.abs(left - right) > tolerance) {
        throw AssertionError("$left ≉ $right within tolerance $tolerance")
    }
}

fun assertApprox(left: Float?, right: Float?, tolerance: Float = 10e-6f) {
    if (left == null || right == null) {
        throw AssertionError("$left ≉ $right within tolerance $tolerance")
    }
    else if (kotlin.math.abs(left - right) > tolerance) {
        throw AssertionError("$left ≉ $right within tolerance $tolerance")
    }
}
