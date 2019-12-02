package oktavia.util

inline fun Int.bytesToBits(): Int = this * 8
inline fun Int.bitsToBytes(): Int = this / 8