package knapsack.genetic

import knapsack.Item
import knapsack.Knapsack
import knapsack.KnapsackSolver
import kotlin.math.sqrt

class GeneticSolver : KnapsackSolver {

    /*
    遺伝方法:
    交叉: 重複しない二つのナップザックの共通部分は保持、他の部分は残り重量の分を二つのナップザックどちらかが持っているアイテムを最大数としてランダムに選択していく
    突然変異: 重量のうち10%(?)を超えるまでランダムにナップザックの中身を消し、消えた分をランダムに再生成する

    交叉 : 再生 : 突然変異 = 90 : 9 : 1
    Knapsack[i] の選択率 = sqrt(v[i]) / sum[0..N](sqrt[n])
     */

    private val mutationChangeRate: Double = 0.2

    override fun solve(items: List<Item>, targetWeight: Int): Knapsack {
        // 重さが小さい順、価値が大きい順にソートして、各重さで一番価値が大きいもの以外を除去
        val sortedByWeightItems: List<Item> = items
            .sortedWith(compareBy<Item> { it.weight }.thenByDescending { it.v })
            .distinctBy { it.weight }

        // init
        // 個体数
        val numOfKnapsack = 50
        // 最大世代数 何に比例するべき？
        val maxGeneration = 20
        return temp(items, maxGeneration, numOfKnapsack, targetWeight).maxByOrNull {
            it.getValue()
        } ?: Knapsack(targetWeight, emptyMap())
    }

    /**
     * initialize
     * ランダムなナップザックを生成する
     * @param items 入れる候補の道具
     * @param targetWeight 最大重量
     */
    private fun createRandomKnapsack(items: List<Item>, targetWeight: Int): Knapsack {
        var nowRemainWeight = targetWeight
        val contents: MutableMap<Item, Int> = mutableMapOf()
        while (nowRemainWeight > 0) {
            val selectedItem = items.filter { it.weight <= nowRemainWeight }.random()
            val useItemCount = (1..(nowRemainWeight / selectedItem.weight)).random()
            contents[selectedItem] = (contents[selectedItem] ?: 0) + useItemCount
            nowRemainWeight = targetWeight - contents.map { it.key.weight * it.value }.reduce{ acc, i -> acc + i }
        }
        return Knapsack(targetWeight, contents)
    }

    /**
     * ナップザックの中からランダムに道具を取り出して新しいナップザックを生成する
     * 個数を考慮する必要があるため、createRandomKnapsack(items, targetWeight): Knapsackと共通の処理にはできない
     * @param knapsack 取り出す元のナップザック
     * @param targetWeight 最大重量
     */
    private fun createRandomKnapsack(knapsack: Knapsack, targetWeight: Int): Knapsack {
        var nowRemainWeight = targetWeight
        val items = knapsack.contents.flatMap { item ->
            (0..item.value).map { item.key }
        }.toMutableList()
        val contents: MutableMap<Item, Int> = mutableMapOf()
        while (items.isNotEmpty() && items.minOf { it.weight } > nowRemainWeight) {
            items.removeIf { it.weight <= nowRemainWeight }
            val selectedItem = items.random()
            contents[selectedItem] = (contents[selectedItem] ?: 0) + 1
            nowRemainWeight -= selectedItem.weight
            items.remove(selectedItem)
        }
        return Knapsack(targetWeight, contents)
    }

    /**
     * 指定された世代数だけ遺伝的アルゴリズムを実行し、結果を返す
     * 戻り値は list? set? map?
     * @param items ナップザックに入り得る道具
     * @param generationNum 世代数
     * @param targetWeight ナップザックに入る重量
     */
    private fun temp(
        items: List<Item>,
        generationNum: Int,
        numOfKnapsack: Int,
        targetWeight: Int
    ): List<Knapsack> {
        return if (generationNum == 0) {
            (1..numOfKnapsack).map { createRandomKnapsack(items, targetWeight) }
        } else {
            createNextGeneration(
                items,
                generationNum,
                temp(
                    items,
                    generationNum - 1,
                    numOfKnapsack,
                    targetWeight
                ),
                numOfKnapsack
            )
        }
    }

    /**
     * 前の世代のナップザックを受け取って次の世代のナップザックを作成する
     * 一様交叉、配慮する？
     */
    private fun createNextGeneration(
        items: List<Item>,
        generationNum: Int,
        nowGeneration: List<Knapsack>,
        numOfKnapsack: Int
    ): List<Knapsack> {
        println()
        println("Generation of $generationNum. Average is ${nowGeneration.sumOf { it.getValue() } / nowGeneration.size}")
        // ナップザックが各々の選ばれる確率に比例した数だけ入っているリスト
        val knapsackSelectRatio = nowGeneration.flatMap { knapsack ->  // TODO 変数名
            (0..sqrt(knapsack.getValue().toDouble()).toInt()).map { knapsack }
        }
        // val knapsackSelectRatio = nowGeneration
        if(nowGeneration.distinct().size == 1) {
            return (1..numOfKnapsack).map {
                when(Generation.getRandomGenerationNoCross()){
                    Generation.Keep -> keep(knapsackSelectRatio.random())
                    Generation.Mutation -> mutation(knapsackSelectRatio.random(), items)
                    Generation.Crossover -> throw Exception()
                }
            }
        }
        return (1..numOfKnapsack).map {
            when (Generation.getRandomGeneration()) {
                is Generation.Crossover -> crossover(randomKnapsackPair(knapsackSelectRatio), items)
                    .also { println("Crossover. Value is ${it.getValue()}, contents are ${it.contents}") }
                is Generation.Keep -> keep(knapsackSelectRatio.random())
                    .also { println("Keep. Value is ${it.getValue()}, contents are ${it.contents}") }
                is Generation.Mutation -> mutation(knapsackSelectRatio.random(), items)
                    .also { println("Mutation. Value is ${it.getValue()}, contents are ${it.contents}") }
            }
        }
    }

    private fun randomKnapsackPair(knapsacks: List<Knapsack>): Pair<Knapsack, Knapsack> {
        val fst = knapsacks.random()
        // val snd = knapsacks.filter { it != fst }.random()
        val snd = knapsacks.random()
        return Pair(fst, snd)
    }

    private fun crossover(knapsacks: Pair<Knapsack, Knapsack>, items: List<Item>): Knapsack {
        val (fst, snd) = knapsacks
        if (fst == snd){
            return fst
        }
        val commonPart = fst.and(snd)
        return commonPart + (createRandomKnapsack(items, fst.weight - commonPart.weight))
    }

    private fun keep(knapsack: Knapsack): Knapsack = knapsack

    private fun mutation(knapsack: Knapsack, items: List<Item>): Knapsack {
        if(knapsack.getValue() == 0L){
            return createRandomKnapsack(items, knapsack.weight)
        }
        val mutationWeight = (knapsack.weight * mutationChangeRate).toInt()
        return createRandomKnapsack(
            knapsack,
            knapsack.weight - mutationWeight
        ) +
                createRandomKnapsack(
                    items,
                    mutationWeight
                )
    }

}