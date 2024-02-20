package knapsack

interface KnapsackSolver {

    /**
     * @param items ナップザックに入れる候補
     * @param targetWeight ナップザックに入る容量
     */
    fun solve(items: List<Item>, targetWeight: Int): Knapsack
}