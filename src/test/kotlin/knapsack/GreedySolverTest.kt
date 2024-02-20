package knapsack

import knapsack.greedy.GreedySolver
import kotlin.test.Test
import kotlin.test.assertEquals

class GreedySolverTest {

    val solver: KnapsackSolver = GreedySolver()
    val item11 = Item(1,1)
    val item21 = Item(1,2)
    val item23 = Item(3,2)

    @Test
    fun test() {
        val items: List<Item> = listOf(Item(1, 1), Item(3, 2))
        assertEquals(1, solver.solve(items, 1).getValue())
        assertEquals(3, solver.solve(items, 2).getValue())
        assertEquals(4, solver.solve(items, 3).getValue())
        assertEquals(6, solver.solve(items, 4).getValue())
    }

    // item なしのとき
    @Test
    fun noItem() {
        val items: List<Item> = emptyList()
        assertEquals(0, solver.solve(items, 1).getValue())
        assertEquals(0, solver.solve(items, 2).getValue())
        assertEquals(0, solver.solve(items, 3).getValue())
        assertEquals(0, solver.solve(items, 4).getValue())
    }

    // item 1つのとき
    @Test
    fun oneItem() {
        val items: List<Item> = listOf(item11)
        assertEquals(1, solver.solve(items, 1).getValue())
        assertEquals(2, solver.solve(items, 2).getValue())
        assertEquals(3, solver.solve(items, 3).getValue())
        assertEquals(4, solver.solve(items, 4).getValue())
        assertEquals(5, solver.solve(items, 5).getValue())
        assertEquals(6, solver.solve(items, 6).getValue())
        assertEquals(7, solver.solve(items, 7).getValue())
        assertEquals(8, solver.solve(items, 8).getValue())
        assertEquals(9, solver.solve(items, 9).getValue())
        assertEquals(10, solver.solve(items, 10).getValue())
    }

    // item 2つのとき（軽い方がコスパいい）
    @Test
    fun twoItemsLight() {
        val items: List<Item> = listOf(item11, item21)
        assertEquals(1, solver.solve(items, 1).getValue())
        assertEquals(2, solver.solve(items, 2).getValue())
        assertEquals(3, solver.solve(items, 3).getValue())
        assertEquals(4, solver.solve(items, 4).getValue())
        assertEquals(5, solver.solve(items, 5).getValue())
        assertEquals(6, solver.solve(items, 6).getValue())
        assertEquals(7, solver.solve(items, 7).getValue())
        assertEquals(8, solver.solve(items, 8).getValue())
        assertEquals(9, solver.solve(items, 9).getValue())
        assertEquals(10, solver.solve(items, 10).getValue())
    }

    // item 2つのとき（重い方がコスパいい）
    @Test
    fun twoItemsHeavy() {
        val items: List<Item> = listOf(item11, item23)
        assertEquals(1, solver.solve(items, 1).getValue())
        assertEquals(3, solver.solve(items, 2).getValue())
        assertEquals(4, solver.solve(items, 3).getValue())
        assertEquals(6, solver.solve(items, 4).getValue())
        assertEquals(7, solver.solve(items, 5).getValue())
        assertEquals(9, solver.solve(items, 6).getValue())
        assertEquals(10, solver.solve(items, 7).getValue())
        assertEquals(12, solver.solve(items, 8).getValue())
        assertEquals(13, solver.solve(items, 9).getValue())
        assertEquals(15, solver.solve(items, 10).getValue())
    }

    // item 3つのとき（同重量で複数のものが存在するとき）
    @Test
    fun threeItemsHeavy() {
        val items: List<Item> = listOf(item11, item21, item23)
        assertEquals(1, solver.solve(items, 1).getValue())
        assertEquals(3, solver.solve(items, 2).getValue())
        assertEquals(4, solver.solve(items, 3).getValue())
        assertEquals(6, solver.solve(items, 4).getValue())
        assertEquals(7, solver.solve(items, 5).getValue())
        assertEquals(9, solver.solve(items, 6).getValue())
        assertEquals(10, solver.solve(items, 7).getValue())
        assertEquals(12, solver.solve(items, 8).getValue())
        assertEquals(13, solver.solve(items, 9).getValue())
        assertEquals(15, solver.solve(items, 10).getValue())
    }
    // もっと色々
}