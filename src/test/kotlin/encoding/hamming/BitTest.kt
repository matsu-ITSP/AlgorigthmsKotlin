package encoding.hamming

import kotlin.test.Test
import kotlin.test.assertEquals

class BitTest{
    val t = Bit(true)
    val f = Bit(false)
    @Test
    fun testPlus(){
        assertEquals(f, t + t)
        assertEquals(t, f + t)
        assertEquals(t, t + f)
        assertEquals(f, f + f)
    }

    @Test
    fun testTimes(){
        assertEquals(t, t * t)
        assertEquals(f, f * t)
        assertEquals(f, t * f)
        assertEquals(f, f * f)
    }

    @Test
    fun testCreate(){
        assertEquals(listOf(t), Bit.createByString("1"))
        assertEquals(listOf(f), Bit.createByString("0"))
        assertEquals(listOf(t, f), Bit.createByString("10"))
    }
}