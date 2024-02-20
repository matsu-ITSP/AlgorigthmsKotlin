package encoding.hamming

import kotlin.test.Test
import kotlin.test.assertEquals

class HammingTest {

    // val hamming = Hamming(4)

    @Test
    fun testLength(){
        // index と length(>1) を合わせるため hammings[0][1] は無視する
        val hammings: MutableList<Hamming> = mutableListOf(Hamming(2), Hamming(2))
        // val hammings: MutableList<Hamming> = mutableListOf(Hamming(12))
        hammings.addAll((2..12).map { Hamming(it) })
        hammings.addAll(listOf(26,27).map { Hamming(it) })
        (2..4).forEach { assertHamming(hammings[it], it, 4, 7) } // m=3
        (5..11).forEach { assertHamming(hammings[it], it, 11, 15) } // m=4
        assertHamming(hammings[12], 12, 26, 31) // m=5
        assertHamming(hammings[13], 26, 26, 31)// m=5
        assertHamming(hammings[14], 27, 57, 63)// m=6
    }

    private fun assertHamming(
        hamming: Hamming,
        messageLength: Int,
        paddedMessageLength: Int,
        hammingLength: Int){

        assertEquals(messageLength, hamming.messageLength)
        assertEquals(paddedMessageLength, hamming.paddedMessageLength)
        assertEquals(hammingLength, hamming.hammingLength)
    }

    @Test
    fun testCreateParity2() {
        val parity2 = Hamming.createParity(2)
        assertEquals(1, parity2.column)
        assertEquals(Matrix.createFromInt(listOf(listOf(1, 1))), parity2)
    }

    @Test
    fun testCreateParity3() {
        val parity3 = Hamming.createParity(3)
        assertEquals(4, parity3.column)
        assertHas(
            Matrix.createFromInt(
                listOf(
                    listOf(0, 1, 1),
                    listOf(1, 0, 1),
                    listOf(1, 1, 0),
                    listOf(1, 1, 1)
                )
            ).contents, parity3.contents
        )
    }

    @Test
    fun testCreateParity4() {
        val parity = Hamming.createParity(4)
        assertEquals(11, parity.column)
        assertHas(
            Matrix.createFromInt(
                listOf(
                    listOf(0, 0, 1, 1),
                    listOf(0, 1, 0, 1),
                    listOf(0, 1, 1, 0),
                    listOf(0, 1, 1, 1),
                    listOf(1, 0, 0, 1),
                    listOf(1, 0, 1, 0),
                    listOf(1, 0, 1, 1),
                    listOf(1, 1, 0, 0),
                    listOf(1, 1, 0, 1),
                    listOf(1, 1, 1, 0),
                    listOf(1, 1, 1, 1)
                )
            ).contents, parity.contents
        )
    }

    private fun <T> assertHas(expected: List<T>, actual: List<T>) {
        expected.forEach {
            assert(actual.contains(it))
        }
    }

    @Test
    fun testCreateGenerateMatrix() {
        val expected = Matrix.createFromInt(
            listOf(
                listOf(1, 0, 0, 0, 0, 1, 1),
                listOf(0, 1, 0, 0, 1, 0, 1),
                listOf(0, 0, 1, 0, 1, 1, 0),
                listOf(0, 0, 0, 1, 1, 1, 1)
            )
        )
        val actual = Hamming.createGenerateMatrix(3)
        assertEquals(expected, actual)
    }

    @Test
    fun testCreateCheckMatrix() {
        val expected = Matrix.createFromInt(
            listOf(
                listOf(0, 1, 1, 1, 1, 0, 0),
                listOf(1, 0, 1, 1, 0, 1, 0),
                listOf(1, 1, 0, 1, 0, 0, 1)
            )
        )
        val actual = Hamming.createCheckMatrix(3)
        assertEquals(expected, actual)
    }

    /**
     * パリティ行列が正しいという前提のもと、符号化が想定の計算通りで求められていることを確認する
     */
    @Test
    fun testEncode() {
        val expected = Bit.createByString("1011010")
        val actual = Hamming(4).encode(Bit.createByString("1011"))
        assertEquals(expected, actual)
    }

    /**
     * 符号化した文字列が復号により戻ることを確認する
     */
    @Test
    fun testDecode() {
        val expected = Bit.createByString("1011")
        val actual = Hamming(4).decode(Bit.createByString("1011010"))
        assertEquals(expected, actual)
    }

    /**
     * 符号化した文字列を一文字変更しても戻ることを確認する
     */
    fun testDecodeDiff() {
        val expected = Bit.createByString("1011")
        val hamming = Hamming(4)
        val actuals = listOf(
            hamming.decode(Bit.createByString("0011010")),
            hamming.decode(Bit.createByString("1111010")),
            hamming.decode(Bit.createByString("1001010")),
            hamming.decode(Bit.createByString("1010010")),
            hamming.decode(Bit.createByString("1011110")),
            hamming.decode(Bit.createByString("1011000")),
            hamming.decode(Bit.createByString("1011011"))
        )
        actuals.forEach {
            assertEquals(expected, it)
        }
    }
}