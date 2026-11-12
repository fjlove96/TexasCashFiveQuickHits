package com.example.texascashfivequickhits.model

import android.content.Context
import java.io.BufferedReader
import java.io.InputStreamReader
import java.util.Calendar
import java.util.Locale

data class HistoricalDraw(
    val month: Int,
    val day: Int,
    val year: Int,
    val numbers: List<Int> // Exact Draw Order
) {
    val dateString: String = String.format(Locale.getDefault(), "%02d/%02d/%04d", month, day, year)
    val timeInMillis: Long = run {
        val cal = Calendar.getInstance()
        cal.set(year, month - 1, day, 0, 0, 0)
        cal.timeInMillis
    }
}

enum class TimePeriod {
    ALL_TIME,
    QUARTERLY,
    MONTHLY,
    FOUR_WEEKS
}

enum class LottoTier {
    TIER_1_HIGHEST,
    TIER_2_MODERATE,
    TIER_3_LOWEST
}

data class Stat(
    val number: Int,
    val hits: Int,
    val eligible: Int
) {
    val rate: Double = if (eligible > 0) hits.toDouble() / eligible.toDouble() else 0.0
    val percent: Int = Math.round((rate * 100).toFloat())
}

object HistoricalDrawRepository {
    private var cachedDraws: List<HistoricalDraw>? = null

    fun loadDraws(context: Context): List<HistoricalDraw> {
        cachedDraws?.let { return it }
        val draws = mutableListOf<HistoricalDraw>()
        try {
            val inputStream = context.assets.open("cashfive.csv")
            val reader = BufferedReader(InputStreamReader(inputStream))
            reader.forEachLine { line ->
                val parts = line.split(",")
                if (parts.size >= 9) {
                    val month = parts[1].trim().toIntOrNull() ?: 0
                    val day = parts[2].trim().toIntOrNull() ?: 0
                    val year = parts[3].trim().toIntOrNull() ?: 0
                    val n1 = parts[4].trim().toIntOrNull() ?: 0
                    val n2 = parts[5].trim().toIntOrNull() ?: 0
                    val n3 = parts[6].trim().toIntOrNull() ?: 0
                    val n4 = parts[7].trim().toIntOrNull() ?: 0
                    val n5 = parts[8].trim().toIntOrNull() ?: 0
                    if (month in 1..12 && day in 1..31 && year >= 1995) {
                        // Preserves exact DRAW ORDER as drawn in Texas Lottery history (No .sorted())
                        val nums = listOf(n1, n2, n3, n4, n5).filter { it > 0 }
                        if (nums.size == 5) {
                            draws.add(HistoricalDraw(month, day, year, nums))
                        }
                    }
                }
            }
            reader.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        cachedDraws = draws.sortedByDescending { it.timeInMillis }
        return cachedDraws!!
    }

    fun getDrawGaps(allDraws: List<HistoricalDraw>): Map<Int, Int> {
        val gaps = mutableMapOf<Int, Int>()
        val latestDraws = allDraws.sortedByDescending { it.timeInMillis }
        for (num in 1..35) {
            val index = latestDraws.indexOfFirst { it.numbers.contains(num) }
            gaps[num] = if (index >= 0) index else latestDraws.size
        }
        return gaps
    }

    fun calculateMonthStats(allDraws: List<HistoricalDraw>, month: Int): List<Stat> {
        val monthDraws = allDraws.filter { it.month == month }
        val yearBuckets = monthDraws.groupBy { it.year }
        val result = mutableListOf<Stat>()

        for (num in 1..35) {
            var eligible = 0
            var hits = 0
            for ((_, drawsInYear) in yearBuckets) {
                var canDraw = false
                var found = false
                for (draw in drawsInYear) {
                    if (num in 1..35) {
                        canDraw = true
                    }
                    if (draw.numbers.contains(num)) {
                        found = true
                    }
                }
                if (canDraw) {
                    eligible++
                    if (found) hits++
                }
            }
            if (eligible > 0) {
                result.add(Stat(num, hits, eligible))
            }
        }

        return result.sortedWith(
            compareByDescending<Stat> { it.rate }.thenBy { it.number }
        )
    }

    fun getDrawsForPeriod(draws: List<HistoricalDraw>, period: TimePeriod): List<HistoricalDraw> {
        if (period == TimePeriod.ALL_TIME || draws.isEmpty()) return draws
        val latestTime = draws.maxOfOrNull { it.timeInMillis } ?: System.currentTimeMillis()
        val cutoffDays = when (period) {
            TimePeriod.MONTHLY -> 30
            TimePeriod.QUARTERLY -> 90
            TimePeriod.FOUR_WEEKS -> 28
            TimePeriod.ALL_TIME -> Int.MAX_VALUE
        }
        val cutoffTime = latestTime - (cutoffDays.toLong() * 24 * 60 * 60 * 1000)
        return draws.filter { it.timeInMillis >= cutoffTime }
    }

    fun getDrawsForYearAndMonth(draws: List<HistoricalDraw>, year: Int, month: Int): List<HistoricalDraw> {
        return draws.filter { it.year == year && it.month == month }
    }

    fun getNumberFrequencies(draws: List<HistoricalDraw>): Map<Int, Int> {
        val frequencyMap = mutableMapOf<Int, Int>()
        for (i in 1..35) {
            frequencyMap[i] = 0
        }
        for (draw in draws) {
            for (num in draw.numbers) {
                if (num in 1..35) {
                    frequencyMap[num] = (frequencyMap[num] ?: 0) + 1
                }
            }
        }
        return frequencyMap.entries.sortedByDescending { it.value }.associate { it.key to it.value }
    }

    fun getLottoTiers(frequencies: Map<Int, Int>): Map<LottoTier, List<Pair<Int, Int>>> {
        val sorted = frequencies.entries.map { it.key to it.value }.sortedByDescending { it.second }
        val size = sorted.size
        val tier1Size = maxOf(1, size / 3)
        val tier2Size = maxOf(1, size / 3)
        return mapOf(
            LottoTier.TIER_1_HIGHEST to sorted.take(tier1Size),
            LottoTier.TIER_2_MODERATE to sorted.drop(tier1Size).take(tier2Size),
            LottoTier.TIER_3_LOWEST to sorted.drop(tier1Size + tier2Size)
        )
    }

    fun computePeriodHeaderInfo(
        tier: LottoTier,
        tierItems: List<Pair<Int, Int>>,
        periodDraws: List<HistoricalDraw>,
        period: TimePeriod
    ): String {
        if (tierItems.isEmpty() || periodDraws.isEmpty()) {
            val name = when (tier) {
                LottoTier.TIER_1_HIGHEST -> "TOP TIER"
                LottoTier.TIER_2_MODERATE -> "NEXT TIER"
                LottoTier.TIER_3_LOWEST -> "ALSO FREQUENT"
            }
            return "$name · 0 HITS · 0%"
        }

        val allFrequencies = getNumberFrequencies(periodDraws)
        val overallMaxHits = allFrequencies.values.maxOrNull() ?: 1

        val avgHitsInTier = tierItems.map { it.second }.average()
        val tierPct = Math.round((avgHitsInTier / overallMaxHits.toDouble()) * 100).toInt().coerceIn(10, 100)

        val periodLabel = when (period) {
            TimePeriod.ALL_TIME -> "${periodDraws.size} DRAWS"
            TimePeriod.QUARTERLY -> "90 DAYS (${periodDraws.size} DRAWS)"
            TimePeriod.FOUR_WEEKS -> "28 DAYS (${periodDraws.size} DRAWS)"
            TimePeriod.MONTHLY -> "30 DAYS (${periodDraws.size} DRAWS)"
        }

        val name = when (tier) {
            LottoTier.TIER_1_HIGHEST -> "TOP TIER"
            LottoTier.TIER_2_MODERATE -> "NEXT TIER"
            LottoTier.TIER_3_LOWEST -> "ALSO FREQUENT"
        }

        val avgHitsFormatted = String.format(Locale.getDefault(), "%.0f", avgHitsInTier)
        return "$name · AVG $avgHitsFormatted HITS IN $periodLabel · $tierPct%"
    }
}
