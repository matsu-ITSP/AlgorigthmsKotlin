package encoding.hamming

import kotlin.math.log
import kotlin.math.pow

class Hamming(val messageLength: Int) {

    val hammingLength: Int // n
    val paddedMessageLength: Int // k
    val generateMatrix: Matrix
    val checkMatrix: Matrix

    init {
        val m = decideM(messageLength)
        hammingLength = 2.0.pow(m).toInt() - 1
        paddedMessageLength = hammingLength - m
        generateMatrix = createGenerateMatrix(m)
        checkMatrix = createCheckMatrix(m)
    }

    companion object {
        /*
        len にパディング入れた結果, 長さが k になる ==> k の下限が len ==> k >= len
        n - m >= len ==> n >= m + len
        n = 2^m - 1 ==> 2^m - 1 >= m + len ==> len <= 2^m - m - 1 < 2^m
        ここで k = 2^m - m - 1 より、k = 0, 1, 4, 11, 26, ... , これを k(m) とする
        2^(m-1) < k(m) < 2^m となるのは m >= 4
        よって m >= 4 のとき、すなわち len >= 11 のときは m = log(len) + 1 の前後を調べれば十分
        len < 10 のときは個別で書く必要がある
        */
        fun decideM(messageLength: Int): Int {
            when {
                messageLength < 1 -> throw IllegalArgumentException()
                messageLength == 1 -> return 2
                messageLength in 2..4 -> return 3
                messageLength in 5..11 -> return 4
            }
            val mList = log(base = 2.0, x = messageLength.toDouble()).toInt().let {
                listOf(it, it + 1, it + 2)
            }
            val kList = mList.map { 2.0.pow(it).toInt() - it - 1 }
            return when {
                messageLength <= kList[0] -> mList[0]
                messageLength <= kList[1] -> mList[1]
                messageLength <= kList[2] -> mList[2]
                else -> throw ArithmeticException()
            }
        }

        /**
         * 生成行列の組織符号の単位行列でない部分: 2^(m-1)未満の数のうち、∀n(2^n)以外の数をビット列にし、行に並べたもの
         * @param m > 1
         */
        fun createParity(m: Int): Matrix =
            Matrix(
                (3 until 2.0.pow(m).toInt()).map {
                    String.format("%${m}s", Integer.toBinaryString(it)).replace(" ", "0")
                }.filter { s ->
                    s.filter { c -> c == '1' }.length != 1
                }.map {
                    Bit.createByString(it)
                }
            )

        fun createGenerateMatrix(m: Int): Matrix =
            createParity(m).let { Matrix.createIdentityMatrix(it.column).concatenation(it) }


        fun createCheckMatrix(m: Int): Matrix =
            createParity(m).turn().let { it.concatenation(Matrix.createIdentityMatrix(it.column)) }
    }
    fun encode(message: List<Bit>): List<Bit> =
        (Matrix(listOf(padMessage(message))) * generateMatrix).contents[0]

    private fun padMessage(message: List<Bit>): List<Bit> {
        val pad = mutableListOf<Bit>()
        repeat(paddedMessageLength - message.size){
            pad.add(Bit(false))
        }
        return message + pad
    }

    fun decode(crypt: List<Bit>): List<Bit> {
        val check = Matrix(listOf(crypt)) * checkMatrix.turn()
        if (check.contents[0].all { !it.value }) {
            return crypt.take(messageLength)
        }
        val errorIndex = checkMatrix.contents.indexOf(check.contents[0])
        return crypt.mapIndexed { index, bit ->
            if (index == errorIndex) Bit(!bit.value) else bit
        }.take(messageLength)
    }
}
