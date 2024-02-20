package crypt.rsa

import kotlin.math.sqrt

class Rsa {

    val pk: Long
    val modular: Long
    private val sk: Long

    init {
        val primes = createPrimeList()
        val p = primes.random()
        val q = primes.filter { it != p }.random()
        pk = primes.filter { it != p && it != q }.random()
        modular = p * q
        val phi = lcm(p - 1, q - 1)
        sk = extendedEuclidean(pk, -phi).first
        println("p=$p, q=$q, e=$pk, n=$modular, phi=$phi, d=$sk")
    }

    companion object {
        private const val MAX_PRIME_VALUE = 10000L
        private fun createPrimeList(): List<Long> {
            // val net = (2..(sqrt(MAX_PRIME_VALUE.toDouble())).toLong()).toMutableList()
//            val net = mutableListOf<Long>()
//            for (i in 2L..(sqrt(MAX_PRIME_VALUE.toDouble()).toLong())) {
//                if(net.any { i % it == 0L }){
//                    continue
//                }
//                net.add(i)
//            }
//            val result = (2..MAX_PRIME_VALUE).toMutableList()
//            net.forEach { sieve ->
//                result.removeIf { it != sieve && it % sieve == 0L }
//            }
            val result = mutableListOf(2L)
            for (i in 3L..MAX_PRIME_VALUE) {
                for(k in result){
                    if(i % k == 0L){
                        break
                    }
                    if(k * k > i){
                        result.add(i)
                        break
                    }
                }
            }
            return result
        }

        fun lcm(n1: Long, n2: Long): Long {
            return n1 * n2 / gcd(n1, n2)
        }

        fun gcd(n1: Long, n2: Long): Long =
            if (n1 % n2 == 0L) {
                n2
            } else {
                gcd(n2, n1 % n2)
            }

        /**
         * mx + ny = 1
         * @param m s.t. mとnは互いに素
         * @param n s.t. mとnは互いに素
         * @return (x,y)
         */
        fun extendedEuclidean(m: Long, n: Long): Pair<Long, Long> =
            euclideanDivisors(m, n).map {
                Matrix22(
                    0, 1,
                    1, -it
                )
            }.reduce { acc, matrix22 ->
                acc * matrix22
            }.let {
                Pair(it.a11, it.a12)
            }

        /**
         * ユークリッド互除法で割った数のリストを返す
         */
        private fun euclideanDivisors(m: Long, n: Long): List<Long> {
            var n1 = m
            var n2 = n
            val result = mutableListOf<Long>()
            while (n2 != 0L) {
                result.add(n1 / n2)
                val temp = n1
                n1 = n2
                n2 = temp % n2
            }
            return result.reversed()
        }
    }

    fun encode(raw: Long) = powerWithMod(raw, pk, modular)

    fun decode(cipher: Long) = powerWithMod(cipher, sk, modular)

    private fun powerWithMod(x: Long, power: Long, mod: Long): Long {
        var ans = 1L
        (1L..power).forEach { _ ->
            ans = (ans * x) % mod
        }
        return ans
    }
}