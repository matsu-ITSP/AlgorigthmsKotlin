package knapsack

import org.junit.Assert.*
import kotlin.test.Test

class KnapsackTest{

    private val item1 = Item(1, 1)
    private val item2 = Item(2, 2)

    /***** test of plus *****/

    // 両方空
    @Test
    fun plusBothEmpty(){
        val k1 = Knapsack(0, emptyMap())
        val k2 = Knapsack(0, emptyMap())
        assertEquals(Knapsack(0, emptyMap()), k1 + k2)
    }

    // this = 空
    @Test
    fun plusThisEmpty(){
        val k1 = Knapsack(0, emptyMap())
        val k2 = Knapsack(1, mapOf(item1 to 1))
        assertEquals(Knapsack(1, mapOf(item1 to 1)), k1 + k2)
    }

    // other = 空
    @Test
    fun plusOtherEmpty(){
        val k1 = Knapsack(1, mapOf(item1 to 1))
        val k2 = Knapsack(0, emptyMap())
        assertEquals(Knapsack(1, mapOf(item1 to 1)), k1 + k2)
    }

    // 重複要素なし
    @Test
    fun plusNoDuplication(){
        val k1 = Knapsack(1, mapOf(item1 to 1))
        val k2 = Knapsack(6, mapOf(item2 to 3))
        assertEquals(Knapsack(7, mapOf(item1 to 1, item2 to 3)), k1 + k2)
    }

    // 重複要素あり
    @Test
    fun plusDuplication(){
        val k1 = Knapsack(1, mapOf(item1 to 1))
        val k2 = Knapsack(7, mapOf(item1 to 1, item2 to 3))
        assertEquals(Knapsack(8, mapOf(item1 to 2, item2 to 3)), k1 + k2)
    }

    // すべて重複
    @Test
    fun plusAllDuplication(){
        val k1 = Knapsack(5, mapOf(item1 to 1, item2 to 2))
        val k2 = Knapsack(11, mapOf(item1 to 3, item2 to 4))
        assertEquals(Knapsack(16, mapOf(item1 to 4, item2 to 6)), k1 + k2)
    }

    /***** test of minus *****/

    // 両方空
    @Test
    fun minusBothEmpty(){
        val k1 = Knapsack(0, emptyMap())
        val k2 = Knapsack(0, emptyMap())
        assertEquals(Knapsack(0, emptyMap()), k1 - k2)
    }

    // this = 空
    @Test
    fun minusThisEmpty(){
        val k1 = Knapsack(0, emptyMap())
        val k2 = Knapsack(1, mapOf(item1 to 1))
        assertEquals(Knapsack(0, emptyMap()), k1 - k2)
    }

    // other = 空
    @Test
    fun minusOtherEmpty(){
        val k1 = Knapsack(1, mapOf(item1 to 1))
        val k2 = Knapsack(0, emptyMap())
        assertEquals(Knapsack(1, mapOf(item1 to 1)), k1 - k2)
    }

    // 重複要素なし
    @Test
    fun minusNoDuplication(){
        val k1 = Knapsack(1, mapOf(item1 to 1))
        val k2 = Knapsack(6, mapOf(item2 to 3))
        assertEquals(k1, k1 - k2)
    }

    // 重複要素あり
    @Test
    fun minusDuplication(){
        val k1 = Knapsack(7, mapOf(item1 to 1, item2 to 3))
        val k2 = Knapsack(1, mapOf(item1 to 1))
        assertEquals(Knapsack(6, mapOf(item2 to 3)), k1 - k2)
    }

    // すべて重複: k1 < k2
    @Test
    fun minusAllDuplication1(){
        val k1 = Knapsack(5, mapOf(item1 to 1, item2 to 2))
        val k2 = Knapsack(11, mapOf(item1 to 3, item2 to 4))
        assertEquals(Knapsack(0, emptyMap()), k1 - k2)
    }

    // すべて重複: k1 = k2
    @Test
    fun minusAllDuplication2(){
        val k1 = Knapsack(11, mapOf(item1 to 3, item2 to 4))
        val k2 = Knapsack(11, mapOf(item1 to 3, item2 to 4))
        assertEquals(Knapsack(0, emptyMap()), k1 - k2)
    }

    // すべて重複: k1 > k2
    @Test
    fun minusAllDuplication3(){
        val k1 = Knapsack(11, mapOf(item1 to 3, item2 to 4))
        val k2 = Knapsack(5, mapOf(item1 to 1, item2 to 2))
        assertEquals(Knapsack(6, mapOf(item1 to 2, item2 to 2)), k1 - k2)
    }

    // すべて重複: k1 <> k2
    @Test
    fun minusAllDuplication4(){
        val k1 = Knapsack(5, mapOf(item1 to 3, item2 to 1))
        val k2 = Knapsack(7, mapOf(item1 to 1, item2 to 3))
        assertEquals(Knapsack(2, mapOf(item1 to 2)), k1 - k2)
    }

    /***** test of and *****/

    // 両方空
    @Test
    fun andBothEmpty(){
        val k1 = Knapsack(0, emptyMap())
        val k2 = Knapsack(0, emptyMap())
        assertEquals(Knapsack(0, emptyMap()), k1.and(k2))
    }

    // this = 空
    @Test
    fun andThisEmpty(){
        val k1 = Knapsack(0, emptyMap())
        val k2 = Knapsack(1, mapOf(item1 to 1))
        assertEquals(Knapsack(0, emptyMap()), k1.and(k2))
    }

    // other = 空
    @Test
    fun andOtherEmpty(){
        val k1 = Knapsack(1, mapOf(item1 to 1))
        val k2 = Knapsack(0, emptyMap())
        assertEquals(Knapsack(0, emptyMap()), k1.and(k2))
    }

    // 重複要素なし
    @Test
    fun andNoDuplication(){
        val k1 = Knapsack(1, mapOf(item1 to 1))
        val k2 = Knapsack(6, mapOf(item2 to 3))
        assertEquals(Knapsack(0, emptyMap()), k1.and(k2))
    }

    // 重複要素あり
    @Test
    fun andDuplication(){
        val k1 = Knapsack(2, mapOf(item1 to 2))
        val k2 = Knapsack(7, mapOf(item1 to 1, item2 to 3))
        assertEquals(Knapsack(1, mapOf(item1 to 1)), k1.and(k2))
    }

    // すべて重複
    @Test
    fun andAllDuplication(){
        val k1 = Knapsack(9, mapOf(item1 to 1, item2 to 4))
        val k2 = Knapsack(7, mapOf(item1 to 3, item2 to 2))
        assertEquals(Knapsack(5, mapOf(item1 to 1, item2 to 2)), k1.and(k2))
    }

    /***** test of or *****/

    // 両方空
    @Test
    fun orBothEmpty(){
        val k1 = Knapsack(0, emptyMap())
        val k2 = Knapsack(0, emptyMap())
        assertEquals(Knapsack(0, emptyMap()), k1.or(k2))
    }

    // this = 空
    @Test
    fun orThisEmpty(){
        val k1 = Knapsack(0, emptyMap())
        val k2 = Knapsack(1, mapOf(item1 to 1))
        assertEquals(Knapsack(1, mapOf(item1 to 1)), k1.or(k2))
    }

    // other = 空
    @Test
    fun orOtherEmpty(){
        val k1 = Knapsack(1, mapOf(item1 to 1))
        val k2 = Knapsack(0, emptyMap())
        assertEquals(Knapsack(1, mapOf(item1 to 1)), k1.or(k2))
    }

    // 重複要素なし
    @Test
    fun orNoDuplication(){
        val k1 = Knapsack(1, mapOf(item1 to 1))
        val k2 = Knapsack(6, mapOf(item2 to 3))
        assertEquals(Knapsack(7, mapOf(item1 to 1, item2 to 3)), k1.or(k2))
    }

    // 重複要素あり
    @Test
    fun orDuplication(){
        val k1 = Knapsack(1, mapOf(item1 to 1))
        val k2 = Knapsack(7, mapOf(item1 to 1, item2 to 3))
        assertEquals(Knapsack(7, mapOf(item1 to 1, item2 to 3)), k1.or(k2))
    }

    // すべて重複
    @Test
    fun orAllDuplication(){
        val k1 = Knapsack(9, mapOf(item1 to 1, item2 to 4))
        val k2 = Knapsack(7, mapOf(item1 to 3, item2 to 2))
        assertEquals(Knapsack(11, mapOf(item1 to 3, item2 to 4)), k1.or(k2))
    }
}