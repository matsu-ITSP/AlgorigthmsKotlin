package crypt.rsa

import kotlin.test.Test
import kotlin.test.assertEquals

class Matrix22Test {

    // 単位行列 * 単位行列
    @Test
    fun unit() {
        val matrix = Matrix22(1, 0, 0, 1)
        assertEquals(matrix, matrix * matrix)
    }

    // 単位行列 * 1,1,1,1
    @Test
    fun unit2() {
        val matrix1 = Matrix22(1, 0, 0, 1)
        val matrix2 = Matrix22(1, 1, 1, 1)
        assertEquals(matrix2, matrix1 * matrix2)
    }

    // 1,1,1,1 * 単位行列
    @Test
    fun unit3() {
        val matrix1 = Matrix22(1, 0, 0, 1)
        val matrix2 = Matrix22(1, 1, 1, 1)
        assertEquals(matrix2, matrix2 * matrix1)
    }

    // 2,1,1,0 * 0,1,1,-2(逆行列)
    @Test
    fun reverse() {
        val matrix1 = Matrix22(2, 1, 1, 0)
        val matrix2 = Matrix22(0, 1, 1, -2)
        assertEquals(Matrix22(1, 0, 0, 1), matrix1 * matrix2)
    }

    // 1,2,0,3 * 1,1,1,1
    @Test
    fun triangle() {
        val matrix1 = Matrix22(1, 2, 0, 3)
        val matrix2 = Matrix22(1, 1, 1, 1)
        assertEquals(Matrix22(3, 3, 3, 3), matrix1 * matrix2)
    }
}