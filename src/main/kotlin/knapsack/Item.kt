package knapsack

data class Item(val v: Int, val weight: Int) {
    fun getEvaluatedValue(): Double {
        if (v == 0 || weight == 0) {
            return 0.0
        }
        return v.toDouble() / weight.toDouble()
    }
}