package encoding.hamming

import java.util.*

/**
 * 値がビットの行列
 * 和は exor で定義される
 */
data class Matrix(val contents: List<List<Bit>>) {
    val column: Int
    val row: Int

    init {
        if (!contents[0].size.let { size -> contents.all { it.size == size } }) {
            throw IllegalArgumentException("Matrix size error.")
        }
        column = contents.size
        row = contents[0].size
    }

    companion object {
        /**
         * 単位行列を作成
         * @param size > 0
         */
        fun createIdentityMatrix(size: Int): Matrix = Matrix(
            (0 until size).map { i ->
                (0 until size).map { k ->
                    Bit(i == k)
                }
            })

        /**
         * int の二次元リストから作成
         */
        fun createFromInt(arg: List<List<Int>>): Matrix = Matrix(
            arg.map { row ->
                row.map {
                    when(it){
                        0 -> Bit(false)
                        1 -> Bit(true)
                        else -> throw IllegalArgumentException()
                    }
                }
            }
        )
    }

    /**
     * 逆行列
     */
    fun turn(): Matrix {
        val result = mutableListOf<List<Bit>>()
        for (i in 0 until row) {
            result.add(contents.map { it[i] })
        }
        return Matrix(result)
    }

    /**
     * 和は exor で計算される行列の積
     */
    operator fun times(other: Matrix): Matrix {
        if (this.row != other.column) {
            throw ArithmeticException("Matrix * Matrix size error.")
        }
        return Matrix(contents.map { row ->
            other.turn().contents.map { column -> timesSingle(row, column) }
        })
    }

    /**
     * 同じ行数の行列を連結する
     */
    fun concatenation(other: Matrix): Matrix {
        if (this.column != other.column) {
            throw ArithmeticException("concatenation called but Matrix column is different.")
        }
        return Matrix(this.contents.mapIndexed { index, list ->
            list + other.contents[index]
        })
    }

    private fun timesSingle(m1: List<Bit>, m2: List<Bit>): Bit {
        if (m1.size != m2.size) {
            throw ArithmeticException("timesSingle called but it is not single result.")
        }
        return (m1.indices).map {
            m1[it] * m2[it]
        }.reduce { acc, bit -> acc + bit }
    }
}
