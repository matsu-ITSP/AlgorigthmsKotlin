package knapsack

import knapsack.greedy.GreedySolver
import knapsack.dp.DpSolver
import kotlin.test.Test

class ComparisonTest {

    private val dpSolver: KnapsackSolver = DpSolver()
    private val greedySolver: KnapsackSolver = GreedySolver()

    /*
    item.count = 1000, 2000, 4000, 8000
    knapsack.weight = 10000, 100000, 1000000
    item.weight = 1~10000
    item.v = 1~100000
    s.t. item[i].weight <= item[j].weight ==> item[i].v <= item[j].v
    */

    @Test
    fun test(){
        val counts = setOf(100, 200, 400)
        val weights = setOf(1000, 2000, 4000)
        val pairs = counts.flatMap { count -> weights.map { weight -> count to weight } }
        pairs.forEach {
            val (count, weight) = it
            val items = createItems(count)
            val dpResult = runTest(dpSolver, items, weight)
            val greedyResult = runTest(greedySolver, items, weight)
            println("Count = $count, Weight = $weight")
            println("Dp Result: value = ${dpResult.first}, time = ${dpResult.second}ms.")
            println("Greedy Result: value = ${greedyResult.first}, time = ${greedyResult.second}ms.")
            println()
        }
    }

    @Test
    fun testCount1000Weight10000(){
        val count = 1000
        val weight = 10000
        val items = createItems(count)
        val dpResult = runTest(dpSolver, items, weight)
        val greedyResult = runTest(greedySolver, items, weight)
        println("Dp Result: value = ${dpResult.first}, time = ${dpResult.second}ms.")
        println("Greedy Result: value = ${greedyResult.first}, time = ${greedyResult.second}ms.")
        // println("items: $items")
    }

    private fun createItems(count: Int): List<Item>{
        val weights = (1..10000).shuffled().take(count).sorted()
        val values = (1..100000).shuffled().take(count).sorted()
        return weights.mapIndexed { index: Int, it: Int ->  Item(values[index], it)}
    }

    private fun runTest(solver: KnapsackSolver, items: List<Item>, weight: Int): Pair<Result, MilliTime>{
        val begin: MilliTime = System.nanoTime()
        val result: Result = solver.solve(items, weight).getValue()
        val end: MilliTime = System.nanoTime()
        return result to (end - begin) / 1000 / 1000
    }

}

typealias Result = Long
typealias MilliTime = Long