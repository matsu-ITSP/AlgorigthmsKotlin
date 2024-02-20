package knapsack.genetic

import knapsack.Item
import knapsack.KnapsackSolver
import kotlin.test.Test
import kotlin.test.assertEquals

class GeneticSolverTest{

    val solver: KnapsackSolver = GeneticSolver()
    val item11 = Item(1,1)
    val item23 = Item(3,2)
    val item35 = Item(5,3)

    @Test
    fun test() {
        val items: List<Item> = listOf(item11, item23, item35)
        println()
        println("Result. ${solver.solve(items, 21)}")
    }

}