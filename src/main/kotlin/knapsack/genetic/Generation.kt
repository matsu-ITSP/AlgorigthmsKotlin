package knapsack.genetic

sealed class Generation {
    object Crossover: Generation()
    object Keep: Generation()
    object Mutation: Generation()
    companion object {
        fun getRandomGeneration(): Generation =
            when((1..100).random()){
                in 1..90 -> Crossover
                in 91..99 -> Keep
                100 -> Mutation
                else -> throw Exception()
            }
        fun getRandomGenerationNoCross(): Generation =
            when((1..100).random()){
                in 1..80 -> Keep
                in 81..100 -> Mutation
                else -> throw Exception()
            }
    }
}