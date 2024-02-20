package knapsack.dp

import knapsack.Item
import knapsack.Knapsack
import knapsack.KnapsackSolver

class DpSolver: KnapsackSolver {

    override fun solve(items: List<Item>, targetWeight: Int): Knapsack {
        // 重さが小さい順、価値が大きい順にソートして、各重さで一番価値が大きいもの以外を除去
        val sortedByWeightItems: List<Item> = items
            .sortedWith(compareBy<Item> { it.weight }.thenByDescending { it.v })
            .distinctBy { it.weight }
        // ナップザック化
        val initKnapsacks: MutableList<Knapsack> = sortedByWeightItems
            .map { item -> Knapsack(item.weight, listOf(item to 1).associate { it }) }
            .toMutableList()
        initKnapsacks.add(0, Knapsack(0, emptyMap()))
        for(i in 0..targetWeight){
            val maxKnapsack = subSolve(initKnapsacks, i)
            if(initKnapsacks.size <= i || initKnapsacks[i].getValue() < maxKnapsack.getValue()){
                initKnapsacks.add(i, maxKnapsack)
            }
        }
        return initKnapsacks[targetWeight]
    }

    /**
     * @param targetWeight 未満は
     * @param knapsacks 完成済みのナップザック
     * @return targetWeight における最適な詰め方のナップザック
     */
    private fun subSolve(knapsacks: List<Knapsack>, targetWeight: Int): Knapsack {
        var maxValue = 0L;
        var maxKnapsack = Knapsack(targetWeight, emptyMap())
        for (i in 1..targetWeight/2) {
            val tempWeight = knapsacks[i].getValue() + knapsacks[targetWeight-i].getValue()
            if(maxValue < tempWeight){
                maxValue = tempWeight
                maxKnapsack = knapsacks[i] + knapsacks[targetWeight-i]
            }
        }
        return maxKnapsack
    }
}