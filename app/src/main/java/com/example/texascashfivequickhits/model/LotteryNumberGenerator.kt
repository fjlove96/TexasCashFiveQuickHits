package com.example.texascashfivequickhits.model

import kotlin.random.Random

object LotteryNumberGenerator {
    /**
     * Generates a Texas Cash Five Quick Pick: 5 unique numbers from 1 to 35, sorted in ascending order.
     */
    fun generateQuickPick(random: Random = Random.Default): List<Int> {
        val numbers = (1..35).shuffled(random).take(5).sorted()
        return numbers
    }
}
