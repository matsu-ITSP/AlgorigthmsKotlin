package encoding.hamming

import java.lang.IllegalArgumentException

/**
 * + は exor の bit
 */
data class Bit(val value: Boolean) {
    operator fun plus(other: Bit) = Bit(this.value xor other.value)
    operator fun times(other: Bit) = Bit(this.value && other.value)
    override fun toString(): String {
        return if (value) "1" else "0"
    }

    companion object {
        fun createByString(s: String): List<Bit> = s.map {
            when (it) {
                '0' -> Bit(false)
                '1' -> Bit(true)
                else -> throw IllegalArgumentException()
            }
        }
    }
}