package crypt.rsa

/**
 * 2*2行列
 */
data class Matrix22(
    val a11: Long, val a12: Long,
    val a21: Long, val a22: Long
) {
    operator fun times(other: Matrix22): Matrix22{
        return Matrix22(
            this.a11 * other.a11 + this.a12 * other.a21 , this.a11 * other.a12 + this.a12 * other.a22,
            this.a21 * other.a11 + this.a22 * other.a21 , this.a21 * other.a12 + this.a22 * other.a22,
        )
    }
}