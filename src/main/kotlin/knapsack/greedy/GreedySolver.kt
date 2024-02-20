package knapsack.greedy

import knapsack.Item
import knapsack.Knapsack
import knapsack.KnapsackSolver

class GreedySolver: KnapsackSolver {

    override fun solve(items: List<Item>, targetWeight: Int): Knapsack {
        val sortByEvaluated = items.sortedByDescending { it.getEvaluatedValue() }
        var remainWeight = targetWeight
        val contents = mutableMapOf<Item, Int>()
        sortByEvaluated.forEach {
            if(remainWeight >= it.weight){
                contents[it] = remainWeight / it.weight
                remainWeight %= it.weight
            }
        }
        return Knapsack(targetWeight, contents)
    }
}