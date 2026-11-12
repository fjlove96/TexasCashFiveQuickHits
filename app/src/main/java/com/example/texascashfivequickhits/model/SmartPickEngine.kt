package com.example.texascashfivequickhits.model

import kotlin.random.Random

enum class PickStrategy {
    BALANCED_BLEND,
    OVERDUE_TOP_TIER,
    WEIGHTED_ODDS
}

data class SmartPickResult(
    val numbers: List<Int>,
    val sum: Int,
    val oddCount: Int,
    val evenCount: Int,
    val hotNumbers: List<Int>,
    val overdueNumbers: List<Int>,
    val patternMatchScore: Int,
    val insightSummary: String
)

object SmartPickEngine {
    fun generateSmartPickResult(
        lockedNumbersOrdered: List<Int> = emptyList(),
        strategy: PickStrategy = PickStrategy.BALANCED_BLEND,
        frequencies: Map<Int, Int> = emptyMap(),
        recentGaps: Map<Int, Int> = emptyMap(),
        random: Random = Random.Default
    ): SmartPickResult {
        val numbers = generateSmartPick(lockedNumbersOrdered, strategy, frequencies, recentGaps, random)
        val sum = numbers.sum()
        val odds = numbers.count { it % 2 != 0 }
        val evens = 5 - odds

        val topFrequencies = frequencies.entries.sortedByDescending { it.value }.take(10).map { it.key }
        val topGaps = recentGaps.entries.sortedByDescending { it.value }.take(10).map { it.key }

        val hots = numbers.filter { topFrequencies.contains(it) }
        val overdues = numbers.filter { topGaps.contains(it) && !hots.contains(it) }

        var score = 90
        if (sum in 65..115) score += 5
        if (odds == 2 || odds == 3) score += 4
        if (hots.isNotEmpty()) score += 1
        score = score.coerceIn(85, 99)

        val summary = "Sum: $sum (65-115 Range) · $odds Odd/$evens Even · ${hots.size} Hot / ${overdues.size} Overdue"

        return SmartPickResult(
            numbers = numbers,
            sum = sum,
            oddCount = odds,
            evenCount = evens,
            hotNumbers = hots,
            overdueNumbers = overdues,
            patternMatchScore = score,
            insightSummary = summary
        )
    }

    fun generateSmartPick(
        lockedNumbersOrdered: List<Int> = emptyList(),
        strategy: PickStrategy = PickStrategy.BALANCED_BLEND,
        frequencies: Map<Int, Int> = emptyMap(),
        recentGaps: Map<Int, Int> = emptyMap(),
        random: Random = Random.Default
    ): List<Int> {
        val result = lockedNumbersOrdered.toMutableList()
        if (result.size >= 5) {
            return result.take(5)
        }

        val pool = (1..35).filter { !result.contains(it) }
        var attempts = 300

        while (attempts-- > 0) {
            val candidate = mutableListOf<Int>()
            candidate.addAll(result) // Preserves exact lock selection order
            val needed = 5 - candidate.size

            when (strategy) {
                PickStrategy.BALANCED_BLEND -> {
                    val sortedByFreq = pool.sortedByDescending { frequencies[it] ?: 0 }
                    val sortedByGap = pool.sortedByDescending { recentGaps[it] ?: 0 }

                    val hotPicks = sortedByFreq.take(8).shuffled(random)
                    val overduePicks = sortedByGap.take(8).shuffled(random)
                    val moderatePicks = pool.shuffled(random)

                    val poolList = (hotPicks + overduePicks + moderatePicks).distinct()
                    candidate.addAll(poolList.take(needed))
                }
                PickStrategy.OVERDUE_TOP_TIER -> {
                    val sortedByGap = pool.sortedByDescending { recentGaps[it] ?: (35 - it) }
                    val topOverdue = sortedByGap.take(12).shuffled(random)
                    candidate.addAll(topOverdue.take(needed))
                }
                PickStrategy.WEIGHTED_ODDS -> {
                    val weightedList = mutableListOf<Int>()
                    for (num in pool) {
                        val weight = (frequencies[num] ?: 50)
                        repeat(maxOf(1, weight / 10)) {
                            weightedList.add(num)
                        }
                    }
                    candidate.addAll(weightedList.shuffled(random).distinct().take(needed))
                }
            }

            if (candidate.size == 5) {
                val sum = candidate.sum()
                val odds = candidate.count { it % 2 != 0 }
                if (strategy == PickStrategy.BALANCED_BLEND) {
                    if (sum in 65..115 && (odds == 2 || odds == 3)) {
                        return candidate
                    }
                } else {
                    return candidate
                }
            }
        }

        val fallbackPool = pool.shuffled(random)
        return result + fallbackPool.take(5 - result.size)
    }
}
