package com.example.texascashfivequickhits.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.texascashfivequickhits.model.HistoricalDraw
import com.example.texascashfivequickhits.model.HistoricalDrawRepository
import com.example.texascashfivequickhits.model.LottoTier
import com.example.texascashfivequickhits.model.PickStrategy
import com.example.texascashfivequickhits.model.SmartPickEngine
import com.example.texascashfivequickhits.model.SmartPickResult
import com.example.texascashfivequickhits.model.TimePeriod
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

data class Ticket(
    val id: String = java.util.UUID.randomUUID().toString(),
    val numbers: List<Int>,
    val strategy: PickStrategy,
    val targetDay: String = "Next Draw",
    val dateFormatted: String = SimpleDateFormat("MMM dd, yyyy - h:mm a", Locale.getDefault()).format(Date())
)

data class TicketMatchInfo(
    val maxMatches: Int,
    val matchedDate: String,
    val winningNumbers: List<Int>
)

data class CashFiveUiState(
    val currentNumbers: List<Int> = emptyList(),
    val latestPickResult: SmartPickResult? = null,
    val generatedSets: List<List<Int>> = emptyList(),
    val lockedNumbersList: List<Int> = emptyList(), // Preserves exact lock selection order
    val lockedNumbers: Set<Int> = emptySet(),
    val hasGeneratedPick: Boolean = false, // Set to true ONLY when user taps "Generate Numbers Now"
    val selectedStrategy: PickStrategy = PickStrategy.BALANCED_BLEND,
    val selectedTimePeriod: TimePeriod = TimePeriod.ALL_TIME,
    val selectedMonth: Int = 11, // November
    val selectedYear: Int = 2026,
    val currentCalendarMonth: Int = 11,
    val currentCalendarYear: Int = 2026,
    val targetDrawDay: String = "Next Draw",
    val quantityToGenerate: Int = 1,
    val monthDrawsByDay: List<HistoricalDraw> = emptyList(),
    val recentWinningDraws: List<HistoricalDraw> = emptyList(),
    val selectedDrawIndex: Int = 0,
    val selectedFavoriteIds: Set<String> = emptySet(),
    val tier1Header: String = "TOP TIER · ALL-TIME DATASET",
    val tier2Header: String = "NEXT TIER · ALL-TIME DATASET",
    val tier3Header: String = "ALSO FREQUENT · ALL-TIME DATASET",
    val tier1WithDates: List<Pair<Int, String>> = emptyList(),
    val tier2WithDates: List<Pair<Int, String>> = emptyList(),
    val tier3WithDates: List<Pair<Int, String>> = emptyList(),
    val currentMonthHitsSoFar: List<Int> = emptyList(),
    val savedTickets: List<Ticket> = emptyList(),
    val historicalDrawsCount: Int = 0,
    val selectedTab: Int = 0, // 0: Home, 1: Generator, 2: History, 3: About
    val isDrawBreak: Boolean = false,
    val showGeneratorPage: Boolean = false,
    val showDailyDrawingsPage: Boolean = false,
    val showAddDrawDialog: Boolean = false,
    val showFavoritesDialog: Boolean = false,
    val cloudSyncStatus: String = "Ready to sync with Cloudflare Worker"
)

class CashFiveViewModel(application: Application) : AndroidViewModel(application) {
    private val _uiState = MutableStateFlow(CashFiveUiState())
    val uiState: StateFlow<CashFiveUiState> = _uiState.asStateFlow()

    private var allDraws: MutableList<HistoricalDraw> = mutableListOf()
    private var drawGapsMap: Map<Int, Int> = emptyMap()

    companion object {
        private const val PREFS_NAME = "cash_five_favorites_prefs"
        private const val KEY_SAVED_TICKETS = "key_saved_tickets"

        private fun loadSavedTickets(context: android.content.Context): List<Ticket> {
            try {
                val prefs = context.getSharedPreferences(PREFS_NAME, android.content.Context.MODE_PRIVATE)
                val jsonString = prefs.getString(KEY_SAVED_TICKETS, null) ?: return emptyList()
                val jsonArray = org.json.JSONArray(jsonString)
                val list = mutableListOf<Ticket>()
                for (i in 0 until jsonArray.length()) {
                    val obj = jsonArray.getJSONObject(i)
                    val id = obj.optString("id", java.util.UUID.randomUUID().toString())
                    val numbersJson = obj.getJSONArray("numbers")
                    val numbers = mutableListOf<Int>()
                    for (j in 0 until numbersJson.length()) {
                        numbers.add(numbersJson.getInt(j))
                    }
                    val strategyName = obj.optString("strategy", PickStrategy.BALANCED_BLEND.name)
                    val strategy = try { PickStrategy.valueOf(strategyName) } catch (_: Exception) { PickStrategy.BALANCED_BLEND }
                    val targetDay = obj.optString("targetDay", "Next Draw")
                    val dateFormatted = obj.optString("dateFormatted", "")
                    list.add(Ticket(id = id, numbers = numbers, strategy = strategy, targetDay = targetDay, dateFormatted = dateFormatted))
                }
                return list
            } catch (_: Exception) {
                return emptyList()
            }
        }

        private fun persistSavedTickets(context: android.content.Context, tickets: List<Ticket>) {
            try {
                val prefs = context.getSharedPreferences(PREFS_NAME, android.content.Context.MODE_PRIVATE)
                val jsonArray = org.json.JSONArray()
                for (ticket in tickets) {
                    val obj = org.json.JSONObject()
                    obj.put("id", ticket.id)
                    val numbersArray = org.json.JSONArray()
                    for (num in ticket.numbers) {
                        numbersArray.put(num)
                    }
                    obj.put("numbers", numbersArray)
                    obj.put("strategy", ticket.strategy.name)
                    obj.put("targetDay", ticket.targetDay)
                    obj.put("dateFormatted", ticket.dateFormatted)
                    jsonArray.put(obj)
                }
                prefs.edit().putString(KEY_SAVED_TICKETS, jsonArray.toString()).apply()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    init {
        val cal = Calendar.getInstance()
        val cMonth = cal.get(Calendar.MONTH) + 1
        val cYear = cal.get(Calendar.YEAR)
        val loadedTickets = loadSavedTickets(application)
        _uiState.update { 
            it.copy(
                currentCalendarMonth = cMonth, 
                currentCalendarYear = cYear, 
                selectedMonth = 11, 
                selectedYear = 2026,
                savedTickets = loadedTickets
            ) 
        }
        loadData(application)
        checkDrawBreak()
    }

    private fun loadData(context: android.content.Context) {
        allDraws = HistoricalDrawRepository.loadDraws(context).toMutableList()
        drawGapsMap = HistoricalDrawRepository.getDrawGaps(allDraws)
        val recentDraws = allDraws.take(20)
        _uiState.update { it.copy(recentWinningDraws = recentDraws) }
        updateForPeriod(_uiState.value.selectedTimePeriod)
        updateDrawingsForMonthYear(_uiState.value.selectedMonth, _uiState.value.selectedYear)
    }

    fun setSelectedMonth(month: Int) {
        val clamped = if (month < 1) 12 else if (month > 12) 1 else month
        _uiState.update { it.copy(selectedMonth = clamped) }
        updateDrawingsForMonthYear(clamped, _uiState.value.selectedYear)
    }

    fun setSelectedYear(year: Int) {
        val clamped = year.coerceIn(1995, 2026)
        _uiState.update { it.copy(selectedYear = clamped) }
        updateDrawingsForMonthYear(_uiState.value.selectedMonth, clamped)
    }

    fun navigateWinningDraw(direction: Int) {
        _uiState.update { state ->
            val total = state.recentWinningDraws.size
            if (total == 0) return@update state
            val nextIdx = (state.selectedDrawIndex + direction).coerceIn(0, total - 1)
            state.copy(selectedDrawIndex = nextIdx)
        }
    }

    fun toggleFavoriteSelection(id: String) {
        _uiState.update { state ->
            val current = state.selectedFavoriteIds.toMutableSet()
            if (current.contains(id)) {
                current.remove(id)
            } else {
                current.add(id)
            }
            state.copy(selectedFavoriteIds = current)
        }
    }

    fun deleteSelectedFavorites() {
        _uiState.update { state ->
            val remaining = state.savedTickets.filter { !state.selectedFavoriteIds.contains(it.id) }
            persistSavedTickets(getApplication(), remaining)
            state.copy(savedTickets = remaining, selectedFavoriteIds = emptySet())
        }
    }

    fun setTimePeriod(period: TimePeriod) {
        _uiState.update { it.copy(selectedTimePeriod = period) }
        updateForPeriod(period)
    }

    fun jumpToCurrentPeriod() {
        val cMonth = 11
        val cYear = 2026
        _uiState.update { it.copy(selectedMonth = cMonth, selectedYear = cYear) }
        updateDrawingsForMonthYear(cMonth, cYear)
    }

    private fun updateForPeriod(period: TimePeriod) {
        val periodDraws = HistoricalDrawRepository.getDrawsForPeriod(allDraws, period)
        val freqs = HistoricalDrawRepository.getNumberFrequencies(periodDraws)
        val tiers = HistoricalDrawRepository.getLottoTiers(freqs)

        val t1Items = tiers[LottoTier.TIER_1_HIGHEST] ?: emptyList()
        val t2Items = tiers[LottoTier.TIER_2_MODERATE] ?: emptyList()
        val t3Items = tiers[LottoTier.TIER_3_LOWEST] ?: emptyList()

        val h1 = HistoricalDrawRepository.computePeriodHeaderInfo(LottoTier.TIER_1_HIGHEST, t1Items, periodDraws, period)
        val h2 = HistoricalDrawRepository.computePeriodHeaderInfo(LottoTier.TIER_2_MODERATE, t2Items, periodDraws, period)
        val h3 = HistoricalDrawRepository.computePeriodHeaderInfo(LottoTier.TIER_3_LOWEST, t3Items, periodDraws, period)

        fun mapWithHits(l: List<Pair<Int, Int>>): List<Pair<Int, String>> {
            return l.map { (num, count) ->
                val label = if (count > 0) "$count hits" else "Not Hit"
                num to label
            }
        }

        _uiState.update {
            it.copy(
                tier1Header = h1,
                tier2Header = h2,
                tier3Header = h3,
                tier1WithDates = mapWithHits(t1Items),
                tier2WithDates = mapWithHits(t2Items),
                tier3WithDates = mapWithHits(t3Items)
            )
        }
    }

    private fun updateDrawingsForMonthYear(month: Int, year: Int) {
        val yearMonthDraws = HistoricalDrawRepository.getDrawsForYearAndMonth(allDraws, year, month).sortedBy { it.timeInMillis }
        val hitsSoFarInYear = yearMonthDraws.flatMap { it.numbers }.distinct().sorted()

        _uiState.update {
            it.copy(
                historicalDrawsCount = allDraws.size,
                monthDrawsByDay = yearMonthDraws,
                currentMonthHitsSoFar = hitsSoFarInYear
            )
        }
    }

    fun checkTicketMatchInHistory(ticketNumbers: List<Int>): TicketMatchInfo? {
        if (ticketNumbers.isEmpty()) return null
        var maxHits = 0
        var bestDraw: HistoricalDraw? = null

        for (draw in allDraws) {
            val hits = ticketNumbers.count { draw.numbers.contains(it) }
            if (hits > maxHits) {
                maxHits = hits
                bestDraw = draw
            }
        }

        return if (maxHits >= 2 && bestDraw != null) {
            TicketMatchInfo(
                maxMatches = maxHits,
                matchedDate = bestDraw.dateString,
                winningNumbers = bestDraw.numbers
            )
        } else null
    }

    fun addManualDraw(month: Int, day: Int, year: Int, numbers: List<Int>) {
        if (numbers.size == 5 && numbers.all { it in 1..35 }) {
            val newDraw = HistoricalDraw(month, day, year, numbers.sorted())
            allDraws.add(0, newDraw)
            drawGapsMap = HistoricalDrawRepository.getDrawGaps(allDraws)
            updateForPeriod(_uiState.value.selectedTimePeriod)
            updateDrawingsForMonthYear(_uiState.value.selectedMonth, _uiState.value.selectedYear)
            _uiState.update { it.copy(showAddDrawDialog = false) }
        }
    }

    fun openAddDrawDialog() {
        _uiState.update { it.copy(showAddDrawDialog = true) }
    }

    fun closeAddDrawDialog() {
        _uiState.update { it.copy(showAddDrawDialog = false) }
    }

    fun openFavoritesDialog() {
        _uiState.update { it.copy(showFavoritesDialog = true) }
    }

    fun closeFavoritesDialog() {
        _uiState.update { it.copy(showFavoritesDialog = false) }
    }

    fun triggerCloudflareSync(workerUrl: String = "https://cash5.quickhitsapps.com/cashfive.csv") {
        _uiState.update { it.copy(cloudSyncStatus = "Syncing latest drawings with Cloudflare R2...") }
        HistoricalDrawRepository.syncWithCloudflareWorker(getApplication(), workerUrl) { success, message ->
            if (success) {
                loadData(getApplication())
            }
            _uiState.update { it.copy(cloudSyncStatus = message) }
        }
    }

    fun selectTab(tab: Int) {
        _uiState.update { state ->
            if (tab == 1) {
                state.copy(
                    selectedTab = tab,
                    showDailyDrawingsPage = false,
                    currentNumbers = emptyList(),
                    hasGeneratedPick = false,
                    latestPickResult = null,
                    lockedNumbersList = emptyList(),
                    lockedNumbers = emptySet()
                )
            } else {
                state.copy(selectedTab = tab, showDailyDrawingsPage = false)
            }
        }
    }

    fun resetGeneratorScreen() {
        _uiState.update {
            it.copy(
                currentNumbers = emptyList(),
                hasGeneratedPick = false,
                latestPickResult = null,
                lockedNumbersList = emptyList(),
                lockedNumbers = emptySet()
            )
        }
    }

    fun setStrategy(strategy: PickStrategy) {
        _uiState.update { it.copy(selectedStrategy = strategy) }
        if (_uiState.value.hasGeneratedPick) {
            generateSmartPick(isTriggeredByUser = true)
        }
    }

    fun toggleNumberLock(number: Int) {
        _uiState.update { state ->
            val lockedList = state.lockedNumbersList.toMutableList()
            if (lockedList.contains(number)) {
                lockedList.remove(number)
            } else {
                if (lockedList.size < 5) {
                    lockedList.add(number)
                }
            }
            state.copy(
                lockedNumbersList = lockedList,
                lockedNumbers = lockedList.toSet(),
                hasGeneratedPick = false
            )
        }
    }

    fun generateSmartPick(isTriggeredByUser: Boolean = false) {
        val state = _uiState.value
        val periodDraws = HistoricalDrawRepository.getDrawsForPeriod(allDraws, state.selectedTimePeriod)
        val freqs = HistoricalDrawRepository.getNumberFrequencies(periodDraws.ifEmpty { allDraws })
        val pickResult = SmartPickEngine.generateSmartPickResult(
            lockedNumbersOrdered = state.lockedNumbersList,
            strategy = state.selectedStrategy,
            frequencies = freqs,
            recentGaps = drawGapsMap
        )
        _uiState.update {
            it.copy(
                currentNumbers = pickResult.numbers,
                latestPickResult = pickResult,
                generatedSets = listOf(pickResult.numbers),
                hasGeneratedPick = isTriggeredByUser
            )
        }
    }

    fun openGeneratorPage() {
        _uiState.update {
            it.copy(
                showGeneratorPage = true,
                currentNumbers = emptyList(),
                hasGeneratedPick = false,
                latestPickResult = null,
                lockedNumbersList = emptyList(),
                lockedNumbers = emptySet()
            )
        }
    }

    fun closeGeneratorPage() {
        _uiState.update {
            it.copy(
                showGeneratorPage = false,
                selectedTab = 0,
                currentNumbers = emptyList(),
                hasGeneratedPick = false,
                latestPickResult = null,
                lockedNumbersList = emptyList(),
                lockedNumbers = emptySet()
            )
        }
    }

    fun openDailyDrawingsPage() {
        _uiState.update { it.copy(showDailyDrawingsPage = true) }
    }

    fun closeDailyDrawingsPage() {
        _uiState.update { it.copy(showDailyDrawingsPage = false) }
    }

    fun saveCurrentTicket() {
        val state = _uiState.value
        val newTickets = mutableListOf<Ticket>()
        for (set in state.generatedSets) {
            if (set.size == 5) {
                newTickets.add(
                    Ticket(
                        numbers = set,
                        strategy = state.selectedStrategy,
                        targetDay = state.targetDrawDay
                    )
                )
            }
        }
        val updatedTickets = newTickets + state.savedTickets
        persistSavedTickets(getApplication(), updatedTickets)
        _uiState.update {
            it.copy(
                savedTickets = updatedTickets,
                showGeneratorPage = false,
                selectedTab = 0,
                currentNumbers = emptyList(),
                hasGeneratedPick = false,
                latestPickResult = null,
                lockedNumbersList = emptyList(),
                lockedNumbers = emptySet()
            )
        }
    }

    fun deleteTicket(id: String) {
        _uiState.update { state ->
            val remaining = state.savedTickets.filter { t -> t.id != id }
            persistSavedTickets(getApplication(), remaining)
            state.copy(savedTickets = remaining)
        }
    }

    private fun checkDrawBreak() {
        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)
        val isBreak = (hour == 22 && minute in 2..15)
        _uiState.update { it.copy(isDrawBreak = isBreak) }
    }
}
