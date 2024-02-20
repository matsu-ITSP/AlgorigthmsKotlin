package knapsack

import java.lang.Integer.max
import kotlin.math.min

/**
 * @param weight ナップザックに入る容量
 * @param contents そのサイズのナップザックに入っている道具
 */
data class Knapsack(val weight: Int, val contents: Map<Item, Int>) {

    /**
     * ナップザックに入っている道具の総価値を返す
     */
    fun getValue(): Long {
        if (contents.isEmpty()) {
            return 0
        }
        return contents.map { it.key.v.toLong() * it.value.toLong() }.sum()
    }

    /**
     * 二つのナップザックを足す
     * 容量、中の道具、ともに二つのナップザックの合計となる
     */
    operator fun plus(other: Knapsack): Knapsack = Knapsack(
        weight = this.weight + other.weight,
        contents = (this.contents.keys + other.contents.keys).associateWith {
            ((this.contents[it] ?: 0) + (other.contents[it] ?: 0))
        })

    /**
     * 引かれるナップザックから、引くナップザックに入っているものを、入っている分だけ減らす
     * 容量は減った分だけ減らす
     * 引くナップザックにしか入っていないものは減らさない（0個未満にはならない）
     */
    operator fun minus(other: Knapsack): Knapsack{
        val result = this.contents.keys.associateWith {
            (this.contents[it] ?: 0) - (other.contents[it] ?: 0)
        }.filter { it.value > 0 }
        return Knapsack(result.map { it.key.weight * it.value }.sum(), result)
    }

    /**
     * 二つのナップザックの共通部分を取る
     * 両方のナップザックに入っている道具を新しいナップザックに入れ、そのナップザックの容量は道具の合計分となる
     */
    fun and(other: Knapsack): Knapsack{
        val keySet = this.contents.keys.intersect(other.contents.keys)
        val result = keySet.associateWith {
            min(this.contents[it] ?: 0, other.contents[it] ?: 0)
        }.filter { it.value != 0 }
        return Knapsack(result.map { it.key.weight * it.value }.sum(), result)
    }

    /**
     * 二つのナップザックどちらかに入っている部分を取る
     * どちらかのナップザックに入っているものを新しいナップザックに入れる
     * 両方に入っているものは多い方の数だけ入れる
     * そのナップザックの容量は道具の合計分となる
     */
    fun or(other: Knapsack): Knapsack{
        val keySet = this.contents.keys + other.contents.keys
        val result = keySet.associateWith {
            max(this.contents[it] ?: 0, other.contents[it] ?: 0)
        }
        return Knapsack(result.map { it.key.weight * it.value }.sum(), result)
    }

    fun deleteContents(item: Item): Knapsack = Knapsack(
        weight - item.weight * (contents[item] ?: 0),
        contents.minus(item)
    )


}