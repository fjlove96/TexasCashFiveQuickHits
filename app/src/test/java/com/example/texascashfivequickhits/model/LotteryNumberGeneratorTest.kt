package com.example.texascashfivequickhits.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class LotteryNumberGeneratorTest {

    @Test
    fun testGenerateQuickPick_returnsFiveNumbers() {
        val quickPick = LotteryNumberGenerator.generateQuickPick()
        assertEquals(5, quickPick.size)
    }

    @Test
    fun testGenerateQuickPick_numbersInRange() {
        val quickPick = LotteryNumberGenerator.generateQuickPick()
        for (number in quickPick) {
            assertTrue("Number $number should be between 1 and 35", number in 1..35)
        }
    }

    @Test
    fun testGenerateQuickPick_uniqueNumbers() {
        val quickPick = LotteryNumberGenerator.generateQuickPick()
        assertEquals(5, quickPick.distinct().size)
    }

    @Test
    fun testGenerateQuickPick_sortedAscending() {
        val quickPick = LotteryNumberGenerator.generateQuickPick()
        val sorted = quickPick.sorted()
        assertEquals(sorted, quickPick)
    }
}
