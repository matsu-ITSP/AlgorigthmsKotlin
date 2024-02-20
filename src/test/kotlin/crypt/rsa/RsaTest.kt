package crypt.rsa

import crypt.rsa.Rsa.Companion.extendedEuclidean
import crypt.rsa.Rsa.Companion.gcd
import crypt.rsa.Rsa.Companion.lcm
import org.junit.Assert.*
import kotlin.test.Test

class RsaTest{

    // gcd: 互いに素
    @Test
    fun testGcd1(){
        val n1 = 20L
        val n2 = 33L
        assertEquals(gcd(n1, n2), 1L)
    }

    // gcd: 共通因数あり
    @Test
    fun testGcd2(){
        val n1 = 180L
        val n2 = 33L
        assertEquals(gcd(n1, n2), 3L)
    }

    // lcm
    // lcm 互いに素
    @Test
    fun testLcm1(){
        val n1 = 20L
        val n2 = 33L
        assertEquals(lcm(n1, n2), 660L)
    }

    // lcm: 共通因数あり
    @Test
    fun testLcm2(){
        val n1 = 180L
        val n2 = 33L
        assertEquals(lcm(n1, n2), 1980L)
    }

    // extendedEuclidean: いくつか
    @Test
    fun testExtendedEuclidean1(){
        val m = 49L
        val n = 33L
        val (x,y) = extendedEuclidean(m,n)
        assertEquals(m * x + n * y, 1)
    }

    @Test
    fun testExtendedEuclidean2(){
        val m = 1024L
        val n = 909L
        val (x,y) = extendedEuclidean(m,n)
        assertEquals(m * x + n * y, 1)
    }

    //ぜんぶ
    @Test
    fun testRSA(){
        val rsa = Rsa()
        val raw = 54321L
        val crypt = rsa.encode(raw)
        println("crypt=$crypt")
        val decoded = rsa.decode(crypt)
        assertEquals(raw, decoded)
    }
}