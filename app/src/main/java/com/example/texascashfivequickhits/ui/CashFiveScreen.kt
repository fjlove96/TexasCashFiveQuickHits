package com.example.texascashfivequickhits.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.window.Dialog
import com.example.texascashfivequickhits.model.PickStrategy
import com.example.texascashfivequickhits.model.TimePeriod
import com.example.texascashfivequickhits.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Locale
import kotlin.math.roundToInt

private val monthNames = listOf("", "January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December")

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CashFiveScreen(
    viewModel: CashFiveViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    BackHandler(
        enabled = uiState.showFavoritesDialog || uiState.showAddDrawDialog || uiState.showDailyDrawingsPage || uiState.showGeneratorPage || uiState.selectedTab != 0
    ) {
        when {
            uiState.showFavoritesDialog -> viewModel.closeFavoritesDialog()
            uiState.showAddDrawDialog -> viewModel.closeAddDrawDialog()
            uiState.showDailyDrawingsPage -> viewModel.closeDailyDrawingsPage()
            uiState.showGeneratorPage -> viewModel.closeGeneratorPage()
            uiState.selectedTab != 0 -> viewModel.selectTab(0)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Image(
                                painter = painterResource(id = com.example.texascashfivequickhits.R.drawable.app_logo_icon),
                                contentDescription = "App Logo",
                                modifier = Modifier
                                    .size(32.dp)
                                    .aspectRatio(1f)
                                    .clip(CircleShape),
                                contentScale = ContentScale.Crop
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("TX CASH 5 SMART PICKS", fontSize = 17.sp, fontWeight = FontWeight.Bold, color = CyberText, letterSpacing = 0.5.sp)
                        }
                        Text("Historical number analysis · Updated Nov 9, 2026", fontSize = 11.sp, color = CyberTextSecondary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = CyberSurface)
            )
        },
        bottomBar = {
            NavigationBar(containerColor = CyberSurface) {
                NavigationBarItem(
                    selected = uiState.selectedTab == 0 && !uiState.showDailyDrawingsPage,
                    onClick = { viewModel.selectTab(0) },
                    icon = { Icon(Icons.Default.Place, contentDescription = "Home") },
                    label = { Text("Home", color = if (uiState.selectedTab == 0 && !uiState.showDailyDrawingsPage) CyberCyan else CyberTextSecondary) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = CyberCyan,
                        unselectedIconColor = CyberTextSecondary,
                        indicatorColor = CyberBackground
                    )
                )
                NavigationBarItem(
                    selected = uiState.selectedTab == 1,
                    onClick = { viewModel.selectTab(1) },
                    icon = { Icon(Icons.Default.Search, contentDescription = "Analysis") },
                    label = { Text("Analysis", color = if (uiState.selectedTab == 1) CyberCyan else CyberTextSecondary) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = CyberCyan,
                        unselectedIconColor = CyberTextSecondary,
                        indicatorColor = CyberBackground
                    )
                )
                NavigationBarItem(
                    selected = uiState.selectedTab == 2,
                    onClick = { viewModel.selectTab(2) },
                    icon = { Icon(Icons.AutoMirrored.Filled.List, contentDescription = "History") },
                    label = { Text("History", color = if (uiState.selectedTab == 2) CyberCyan else CyberTextSecondary) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = CyberCyan,
                        unselectedIconColor = CyberTextSecondary,
                        indicatorColor = CyberBackground
                    )
                )
                NavigationBarItem(
                    selected = uiState.selectedTab == 3,
                    onClick = { viewModel.selectTab(3) },
                    icon = { Icon(Icons.Default.Info, contentDescription = "About") },
                    label = { Text("About", color = if (uiState.selectedTab == 3) CyberCyan else CyberTextSecondary) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = CyberCyan,
                        unselectedIconColor = CyberTextSecondary,
                        indicatorColor = CyberBackground
                    )
                )
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(CyberBackground)
        ) {
            if (uiState.showDailyDrawingsPage) {
                DailyDrawingsPage(uiState, viewModel)
            } else {
                when (uiState.selectedTab) {
                    0 -> HomeTab(uiState, viewModel)
                    1 -> AnalysisTab(uiState, viewModel)
                    2 -> HistoryTab(uiState, viewModel)
                    3 -> AboutTab(uiState, viewModel)
                }
            }

            if (uiState.showGeneratorPage) {
                GeneratorPageModal(uiState, viewModel)
            }

            if (uiState.showAddDrawDialog) {
                AddDrawDialog(uiState, viewModel)
            }

            if (uiState.showFavoritesDialog) {
                FavoritesDialog(uiState, viewModel)
            }

            if (uiState.showTermsDialog) {
                TermsOfUseDialog(uiState, viewModel)
            }
        }
    }
}

@Composable
fun TermsOfUseDialog(uiState: CashFiveUiState, viewModel: CashFiveViewModel) {
    Dialog(onDismissRequest = { /* Non-dismissible without accepting */ }) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .heightIn(max = 550.dp),
            colors = CardDefaults.cardColors(containerColor = CyberSurface),
            border = BorderStroke(1.dp, CyberGold)
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Welcome to Texas Cash Five Quick Hits", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = CyberGold, textAlign = TextAlign.Center)
                Spacer(modifier = Modifier.height(6.dp))
                Text("Please review and agree to the Terms of Use before proceeding.", fontSize = 12.sp, color = CyberTextSecondary, textAlign = TextAlign.Center)
                Spacer(modifier = Modifier.height(16.dp))

                Text("1. For Entertainment & Informational Use Only", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = CyberCyan)
                Spacer(modifier = Modifier.height(2.dp))
                Text("Texas Cash Five Quick Hits is created solely for statistical exploration, educational, and entertainment purposes.", fontSize = 12.sp, color = CyberTextSecondary, lineHeight = 16.sp)

                Spacer(modifier = Modifier.height(10.dp))
                Text("2. No Guarantee of Winnings or Odds Alteration", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = CyberCyan)
                Spacer(modifier = Modifier.height(2.dp))
                Text("Historical draw frequency describes past drawings only; it does not predict future outcomes or alter lottery odds.", fontSize = 12.sp, color = CyberTextSecondary, lineHeight = 16.sp)

                Spacer(modifier = Modifier.height(10.dp))
                Text("3. Limitation of Liability & Waiver", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = CyberCyan)
                Spacer(modifier = Modifier.height(2.dp))
                Text("You are solely responsible for any lottery purchases you make. The developers assume no liability for any financial losses.", fontSize = 12.sp, color = CyberTextSecondary, lineHeight = 16.sp)

                Spacer(modifier = Modifier.height(10.dp))
                Text("4. Unofficial & Non-Affiliated", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = CyberCyan)
                Spacer(modifier = Modifier.height(2.dp))
                Text("Texas Cash Five Quick Hits is an independent app and is not affiliated with the Texas Lottery Commission or any official lottery entity.", fontSize = 12.sp, color = CyberTextSecondary, lineHeight = 16.sp)

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = { viewModel.acceptTerms() },
                    modifier = Modifier.fillMaxWidth().height(48.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = CyberGold)
                ) {
                    Text("Accept & Continue", fontWeight = FontWeight.Bold, color = Color.Black, fontSize = 14.sp)
                }
            }
        }
    }
}

@Composable
fun HomeTab(uiState: CashFiveUiState, viewModel: CashFiveViewModel) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Column {
                Text("Statistical Patterns", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = CyberText)
                Text("Select time period baseline to analyze trend history.", fontSize = 13.sp, color = CyberTextSecondary)
            }
        }

        item {
            val periods = listOf(
                "All-Time" to TimePeriod.ALL_TIME,
                "90 Days" to TimePeriod.QUARTERLY,
                "4 Weeks" to TimePeriod.FOUR_WEEKS
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(CyberSurface, RoundedCornerShape(12.dp))
                    .border(1.dp, CyberCyan, RoundedCornerShape(12.dp))
                    .padding(4.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                periods.forEach { (label, period) ->
                    val isSelected = uiState.selectedTimePeriod == period
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isSelected) CyberBackground else Color.Transparent)
                            .clickable { viewModel.setTimePeriod(period) }
                            .padding(vertical = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            label,
                            color = if (isSelected) CyberCyan else CyberTextSecondary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }
                }
            }
        }

        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, CyberCyan, RoundedCornerShape(16.dp)),
                colors = CardDefaults.cardColors(containerColor = CyberSurface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    val periodTitle = when (uiState.selectedTimePeriod) {
                        TimePeriod.ALL_TIME -> "All-Time History"
                        TimePeriod.QUARTERLY -> "Past 90 Days"
                        TimePeriod.FOUR_WEEKS -> "Past 28 Days"
                        TimePeriod.MONTHLY -> "Past 30 Days"
                    }
                    Text("Frequency Rankings ($periodTitle)", fontWeight = FontWeight.Bold, color = CyberText, fontSize = 16.sp)
                    Text("Ranked by appearance frequency in this period", fontSize = 11.sp, color = CyberTextSecondary)

                    Spacer(modifier = Modifier.height(14.dp))

                    TierPillBoxSection(
                        title = uiState.tier1Header,
                        tierColor = CyberGold,
                        items = uiState.tier1WithDates.take(5)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    TierPillBoxSection(
                        title = uiState.tier2Header,
                        tierColor = CyberCyan,
                        items = uiState.tier2WithDates.take(5)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    TierPillBoxSection(
                        title = uiState.tier3Header,
                        tierColor = CyberTextSecondary,
                        items = uiState.tier3WithDates.take(5)
                    )

                    Spacer(modifier = Modifier.height(10.dp))
                    Text("Hit rates describe past draws; they don't predict the next one.", fontSize = 10.sp, color = CyberTextSecondary)
                }
            }
        }

        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, CyberGold, RoundedCornerShape(16.dp))
                    .clickable { viewModel.openGeneratorPage() },
                colors = CardDefaults.cardColors(containerColor = CyberSurface),
                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
            ) {
                Row(
                    modifier = Modifier.padding(18.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Image(
                            painter = painterResource(id = com.example.texascashfivequickhits.R.drawable.smart_pick_target),
                            contentDescription = "Smart Pick Generator",
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape),
                            contentScale = ContentScale.Crop
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text("Smart Pick Generator", fontWeight = FontWeight.Bold, color = CyberGold, fontSize = 16.sp)
                            Text("Pick numbers using 4-factor smart algorithm", fontSize = 12.sp, color = CyberTextSecondary)
                        }
                    }
                }
            }
        }

        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, CyberGold, RoundedCornerShape(16.dp))
                    .clickable { viewModel.openFavoritesDialog() },
                colors = CardDefaults.cardColors(containerColor = CyberSurface)
            ) {
                Row(
                    modifier = Modifier.padding(18.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier.size(40.dp).aspectRatio(1f).clip(CircleShape).background(CyberBackground),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Star, contentDescription = null, tint = CyberGold)
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text("My Saved Favorites (${uiState.savedTickets.size})", fontWeight = FontWeight.Bold, color = CyberGold, fontSize = 16.sp)
                            Text("${uiState.savedTickets.size} custom sets saved locally", fontSize = 12.sp, color = CyberTextSecondary)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun GridNumberBall(
    number: Int,
    isLocked: Boolean,
    isTopTier: Boolean,
    isNextTier: Boolean,
    onClick: () -> Unit
) {
    val bgColor = when {
        isTopTier -> CyberGold
        isNextTier -> Color(0xFF0284C7)
        else -> Color(0xFF1E293B)
    }
    val textColor = when {
        isTopTier || isNextTier -> Color.Black
        else -> Color.White
    }
    val borderColor = when {
        isLocked -> Color.White
        isTopTier -> CyberGold
        isNextTier -> CyberCyan
        else -> CyberCyan.copy(alpha = 0.25f)
    }

    Box(
        modifier = Modifier
            .size(38.dp)
            .aspectRatio(1f)
            .clip(CircleShape)
            .background(bgColor)
            .border(if (isLocked) 3.5.dp else 1.5.dp, borderColor, CircleShape)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = number.toString(),
            fontSize = 15.sp,
            fontWeight = FontWeight.ExtraBold,
            color = textColor
        )
    }
}

@Composable
fun MonthDropdownSelector(selectedMonth: Int, onMonthSelected: (Int) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    val monthName = if (selectedMonth in 1..12) monthNames[selectedMonth] else "November"

    Box {
        Text(
            text = "$monthName ▾",
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = CyberCyan,
            modifier = Modifier.clickable { expanded = true }
        )

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.background(CyberSurface)
        ) {
            (1..12).forEach { month ->
                DropdownMenuItem(
                    text = {
                        Text(
                            text = monthNames[month],
                            color = if (month == selectedMonth) CyberCyan else CyberText,
                            fontWeight = if (month == selectedMonth) FontWeight.Bold else FontWeight.Normal
                        )
                    },
                    onClick = {
                        onMonthSelected(month)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
fun YearDropdownSelector(selectedYear: Int, onYearSelected: (Int) -> Unit) {
    var expanded by remember { mutableStateOf(false) }

    Box {
        Text(
            text = "$selectedYear ▾",
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = CyberCyan,
            modifier = Modifier.clickable { expanded = true }
        )

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.background(CyberSurface)
        ) {
            (2026 downTo 1995).forEach { year ->
                DropdownMenuItem(
                    text = {
                        Text(
                            text = year.toString(),
                            color = if (year == selectedYear) CyberCyan else CyberText,
                            fontWeight = if (year == selectedYear) FontWeight.Bold else FontWeight.Normal
                        )
                    },
                    onClick = {
                        onYearSelected(year)
                        expanded = false
                    }
                )
            }
        }
    }
}

// Saved Favorites Dialog Matching Texas Lotto Inspection Screen 1:1
@Composable
fun FavoritesDialog(uiState: CashFiveUiState, viewModel: CashFiveViewModel) {
    val activeWinningDraw = if (uiState.recentWinningDraws.isNotEmpty()) {
        uiState.recentWinningDraws[uiState.selectedDrawIndex.coerceIn(0, uiState.recentWinningDraws.size - 1)]
    } else null

    val winningNumbersSet = remember(activeWinningDraw) { activeWinningDraw?.numbers?.toSet() ?: emptySet() }
    val topTierSet = remember(uiState.tier1WithDates) { uiState.tier1WithDates.map { it.first }.toSet() }
    val nextTierSet = remember(uiState.tier2WithDates) { uiState.tier2WithDates.map { it.first }.toSet() }

    Dialog(onDismissRequest = { viewModel.closeFavoritesDialog() }) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, CyberCyan, RoundedCornerShape(20.dp)),
            colors = CardDefaults.cardColors(containerColor = CyberSurface)
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                item {
                    Text("Saved Favorites Inspection", fontWeight = FontWeight.Bold, color = CyberText, fontSize = 16.sp)
                }

                // 1. Draw Navigator Carousel (Matching Texas Lotto)
                if (uiState.recentWinningDraws.isNotEmpty()) {
                    item {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(1.dp, CyberCyan, RoundedCornerShape(12.dp)),
                            colors = CardDefaults.cardColors(containerColor = CyberBackground)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(10.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    "‹",
                                    fontSize = 22.sp,
                                    color = CyberCyan,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.clickable { viewModel.navigateWinningDraw(-1) }
                                )
                                Text(
                                    "Draw ${uiState.selectedDrawIndex + 1} of ${uiState.recentWinningDraws.size} · ${activeWinningDraw?.dateString}",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = CyberCyan
                                )
                                Text(
                                    "›",
                                    fontSize = 22.sp,
                                    color = CyberCyan,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.clickable { viewModel.navigateWinningDraw(1) }
                                )
                            }
                        }
                    }

                    // 2. Active Winning Draw Box
                    item {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(1.dp, CyberCyan.copy(alpha = 0.6f), RoundedCornerShape(12.dp)),
                            colors = CardDefaults.cardColors(containerColor = CyberBackground)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text("🏆 Winning Draw (${activeWinningDraw?.dateString})", fontWeight = FontWeight.Bold, color = CyberCyan, fontSize = 13.sp)
                                Spacer(modifier = Modifier.height(8.dp))
                                activeWinningDraw?.let { draw ->
                                    PillBoxWinningNumbers(numbers = draw.numbers)
                                }
                            }
                        }
                    }
                }

                // 3. Saved Favorites List
                if (uiState.savedTickets.isEmpty()) {
                    item {
                        Box(modifier = Modifier.fillMaxWidth().padding(24.dp), contentAlignment = Alignment.Center) {
                            Text("No saved favorites yet. Generate a Smart Pick and tap 'Save Pick to Favorites'!", color = CyberTextSecondary, textAlign = TextAlign.Center, fontSize = 13.sp)
                        }
                    }
                } else {
                    items(uiState.savedTickets, key = { it.id }) { ticket ->
                        val isChecked = uiState.selectedFavoriteIds.contains(ticket.id)
                        val matchesCount = ticket.numbers.count { winningNumbersSet.contains(it) }

                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(1.dp, if (matchesCount >= 3) CyberGold else CyberCyan.copy(alpha = 0.5f), RoundedCornerShape(12.dp)),
                            colors = CardDefaults.cardColors(containerColor = CyberBackground)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Checkbox(
                                        checked = isChecked,
                                        onCheckedChange = { viewModel.toggleFavoriteSelection(ticket.id) },
                                        colors = CheckboxDefaults.colors(checkedColor = CyberCyan, uncheckedColor = CyberTextSecondary)
                                    )

                                    Row(
                                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        for (num in ticket.numbers) {
                                            val isHit = winningNumbersSet.contains(num)
                                            val isTop = topTierSet.contains(num)
                                            val isNext = nextTierSet.contains(num)
                                            val ballBg = when {
                                                isHit -> CyberGold
                                                isTop -> CyberGold.copy(alpha = 0.7f)
                                                isNext -> CyberCyan.copy(alpha = 0.7f)
                                                else -> Color(0xFF1E293B)
                                            }
                                            Box(
                                                modifier = Modifier
                                                    .size(36.dp)
                                                    .aspectRatio(1f)
                                                    .clip(CircleShape)
                                                    .background(ballBg)
                                                    .border(if (isHit) 2.5.dp else 1.dp, if (isHit) Color.White else CyberCyan.copy(alpha = 0.3f), CircleShape),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Text(
                                                    text = String.format(Locale.US, "%02d", num),
                                                    fontSize = 12.sp,
                                                    fontWeight = FontWeight.ExtraBold,
                                                    color = if (isHit || isTop || isNext) Color.Black else Color.White
                                                )
                                            }
                                        }
                                    }
                                }

                                if (matchesCount > 0) {
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text("🎯 ", fontSize = 12.sp)
                                        Text(
                                            text = "$matchesCount/5 Match with Winning Draw!",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 11.sp,
                                            color = if (matchesCount >= 3) CyberGold else Color(0xFFF59E0B)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // 4. Batch Delete Selected Action Button
                if (uiState.savedTickets.isNotEmpty()) {
                    item {
                        Button(
                            onClick = { viewModel.deleteSelectedFavorites() },
                            enabled = uiState.selectedFavoriteIds.isNotEmpty(),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(44.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF4444)),
                            shape = RoundedCornerShape(22.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("🗑 ", fontSize = 14.sp)
                                Text("Delete Selected (${uiState.selectedFavoriteIds.size})", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            }
                        }
                    }
                }

                item {
                    TextButton(onClick = { viewModel.closeFavoritesDialog() }) {
                        Text("CLOSE", color = CyberCyan, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

// Clean History Tab (Without Search Bar)
@Composable
fun HistoryTab(uiState: CashFiveUiState, viewModel: CashFiveViewModel) {
    val monthName = if (uiState.selectedMonth in 1..12) monthNames[uiState.selectedMonth] else "November"

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Column {
                Text("${uiState.selectedYear} Draw History", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = CyberText)
                Text("Drawings for ${uiState.selectedYear}. Saved manual results stay on this device.", fontSize = 13.sp, color = CyberTextSecondary)
            }
        }

        // Search Month & Dropdown Year Carousels
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .border(1.dp, CyberCyan, RoundedCornerShape(12.dp)),
                    colors = CardDefaults.cardColors(containerColor = CyberSurface)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("‹", fontSize = 20.sp, color = CyberCyan, fontWeight = FontWeight.Bold, modifier = Modifier.clickable { viewModel.setSelectedMonth(uiState.selectedMonth - 1) })
                        MonthDropdownSelector(selectedMonth = uiState.selectedMonth, onMonthSelected = { viewModel.setSelectedMonth(it) })
                        Text("›", fontSize = 20.sp, color = CyberCyan, fontWeight = FontWeight.Bold, modifier = Modifier.clickable { viewModel.setSelectedMonth(uiState.selectedMonth + 1) })
                    }
                }

                Card(
                    modifier = Modifier
                        .weight(1f)
                        .border(1.dp, CyberCyan, RoundedCornerShape(12.dp)),
                    colors = CardDefaults.cardColors(containerColor = CyberSurface)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("‹", fontSize = 20.sp, color = CyberCyan, fontWeight = FontWeight.Bold, modifier = Modifier.clickable { viewModel.setSelectedYear(uiState.selectedYear - 1) })
                        YearDropdownSelector(selectedYear = uiState.selectedYear, onYearSelected = { viewModel.setSelectedYear(it) })
                        Text("›", fontSize = 20.sp, color = CyberCyan, fontWeight = FontWeight.Bold, modifier = Modifier.clickable { viewModel.setSelectedYear(uiState.selectedYear + 1) })
                    }
                }
            }
        }

        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Button(
                    onClick = { viewModel.openAddDrawDialog() },
                    modifier = Modifier.fillMaxWidth().height(48.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = CyberCyan),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text("＋ Add Draw Result for ${uiState.selectedYear}", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                }

                Button(
                    onClick = { viewModel.triggerCloudflareSync() },
                    modifier = Modifier.fillMaxWidth().height(48.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = CyberSurface),
                    border = ButtonDefaults.outlinedButtonBorder.copy(brush = Brush.linearGradient(listOf(CyberCyan, CyberCyan))),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("☁️", fontSize = 16.sp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Sync Latest Cloud Results (Cloudflare Pro)", color = CyberCyan, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }
                }

                Text(uiState.cloudSyncStatus, fontSize = 10.sp, color = CyberTextSecondary, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
            }
        }

        item {
            Text("${uiState.selectedYear} Drawings (${uiState.monthDrawsByDay.size})", fontWeight = FontWeight.Bold, color = CyberGold, fontSize = 16.sp)
        }

        if (uiState.monthDrawsByDay.isEmpty()) {
            item {
                Box(
                    modifier = Modifier.fillMaxWidth().padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No drawings recorded for $monthName ${uiState.selectedYear}.", color = CyberTextSecondary)
                }
            }
        } else {
            items(uiState.monthDrawsByDay, key = { "${it.year}-${it.month}-${it.day}" }) { draw ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = CyberSurface)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(draw.dateString, fontWeight = FontWeight.Bold, color = CyberCyan, fontSize = 15.sp)
                                Text("Historical result", fontSize = 11.sp, color = CyberTextSecondary)
                            }
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        PillBoxWinningNumbers(numbers = draw.numbers)
                    }
                }
            }
        }
    }
}

// Add Manual Draw Dialog
@Composable
fun AddDrawDialog(uiState: CashFiveUiState, viewModel: CashFiveViewModel) {
    var dayText by remember { mutableStateOf("10") }
    var n1 by remember { mutableStateOf("2") }
    var n2 by remember { mutableStateOf("8") }
    var n3 by remember { mutableStateOf("15") }
    var n4 by remember { mutableStateOf("23") }
    var n5 by remember { mutableStateOf("29") }

    Dialog(onDismissRequest = { viewModel.closeAddDrawDialog() }) {
        Card(
            modifier = Modifier.fillMaxWidth().border(1.dp, CyberCyan, RoundedCornerShape(20.dp)),
            colors = CardDefaults.cardColors(containerColor = CyberSurface)
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("＋ Add Manual Drawing Result", fontWeight = FontWeight.Bold, color = CyberGold, fontSize = 17.sp)
                Text("Enter winning numbers for ${monthNames[uiState.selectedMonth]} ${uiState.selectedYear}", fontSize = 12.sp, color = CyberTextSecondary)

                OutlinedTextField(
                    value = dayText,
                    onValueChange = { dayText = it },
                    label = { Text("Day of Month (1-31)", color = CyberCyan) },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = CyberCyan, unfocusedBorderColor = CyberTextSecondary)
                )

                Text("Enter 5 Winning Numbers (1-35):", fontSize = 12.sp, color = CyberText, fontWeight = FontWeight.Bold)

                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    OutlinedTextField(value = n1, onValueChange = { n1 = it }, modifier = Modifier.weight(1f), singleLine = true)
                    OutlinedTextField(value = n2, onValueChange = { n2 = it }, modifier = Modifier.weight(1f), singleLine = true)
                    OutlinedTextField(value = n3, onValueChange = { n3 = it }, modifier = Modifier.weight(1f), singleLine = true)
                    OutlinedTextField(value = n4, onValueChange = { n4 = it }, modifier = Modifier.weight(1f), singleLine = true)
                    OutlinedTextField(value = n5, onValueChange = { n5 = it }, modifier = Modifier.weight(1f), singleLine = true)
                }

                Button(
                    onClick = {
                        val d = dayText.toIntOrNull() ?: 1
                        val nums = listOf(n1, n2, n3, n4, n5).mapNotNull { it.toIntOrNull() }
                        viewModel.addManualDraw(uiState.selectedMonth, d, uiState.selectedYear, nums)
                    },
                    modifier = Modifier.fillMaxWidth().height(48.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = CyberGold),
                    shape = RoundedCornerShape(24.dp)
                ) {
                    Text("Save Drawing Result", color = Color.Black, fontWeight = FontWeight.Bold)
                }

                TextButton(onClick = { viewModel.closeAddDrawDialog() }) {
                    Text("CANCEL", color = CyberCyan, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun TierPillBoxSection(title: String, tierColor: Color, items: List<Pair<Int, String>>) {
    Column(modifier = Modifier.padding(vertical = 4.dp)) {
        Text(title, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = tierColor)
        Spacer(modifier = Modifier.height(8.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(CyberBackground)
                .border(1.dp, CyberCyan.copy(alpha = 0.5f), RoundedCornerShape(20.dp))
                .padding(horizontal = 12.dp, vertical = 10.dp),
            contentAlignment = Alignment.Center
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (items.isEmpty()) {
                    Text("No draws in this period", fontSize = 11.sp, color = CyberTextSecondary)
                } else {
                    items.forEach { (num, hitLabel) ->
                        val isNotHit = hitLabel == "Not Hit" || hitLabel == "0 hits"
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            SmallHitBall(number = num.toString(), isGreyedOut = isNotHit)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                hitLabel,
                                fontSize = 9.sp,
                                color = if (isNotHit) CyberTextSecondary.copy(alpha = 0.6f) else CyberGreen,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PillBoxWinningNumbers(numbers: List<Int>, highlightNumber: Int = -1) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(CyberBackground)
            .border(1.dp, CyberCyan.copy(alpha = 0.6f), RoundedCornerShape(20.dp))
            .padding(horizontal = 8.dp, vertical = 6.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            modifier = Modifier.horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            for (num in numbers) {
                val isHighlighted = num == highlightNumber
                SmallHitBallHighlighted(number = num.toString(), isHighlighted = isHighlighted)
            }
        }
    }
}

@Composable
fun SmallHitBallHighlighted(number: String, isHighlighted: Boolean = false) {
    Box(
        modifier = Modifier
            .size(30.dp)
            .aspectRatio(1f)
            .clip(CircleShape)
            .background(if (isHighlighted) CyberGold else CyberGreen),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = number,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )
    }
}

// Dedicated Drawings by Day Page
@Composable
fun DailyDrawingsPage(uiState: CashFiveUiState, viewModel: CashFiveViewModel) {
    val monthName = if (uiState.selectedMonth in 1..12) monthNames[uiState.selectedMonth] else "November"

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "← Back",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = CyberCyan,
                    modifier = Modifier
                        .clickable { viewModel.closeDailyDrawingsPage() }
                        .padding(end = 12.dp)
                )
                Column {
                    Text("$monthName ${uiState.selectedYear} Drawings", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = CyberText)
                    Text("${uiState.monthDrawsByDay.size} winning draws recorded", fontSize = 11.sp, color = CyberTextSecondary)
                }
            }
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .border(1.dp, CyberCyan, RoundedCornerShape(12.dp)),
                    colors = CardDefaults.cardColors(containerColor = CyberSurface)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("‹", fontSize = 20.sp, color = CyberCyan, fontWeight = FontWeight.Bold, modifier = Modifier.clickable { viewModel.setSelectedMonth(uiState.selectedMonth - 1) })
                        MonthDropdownSelector(selectedMonth = uiState.selectedMonth, onMonthSelected = { viewModel.setSelectedMonth(it) })
                        Text("›", fontSize = 20.sp, color = CyberCyan, fontWeight = FontWeight.Bold, modifier = Modifier.clickable { viewModel.setSelectedMonth(uiState.selectedMonth + 1) })
                    }
                }

                Card(
                    modifier = Modifier
                        .weight(1f)
                        .border(1.dp, CyberCyan, RoundedCornerShape(12.dp)),
                    colors = CardDefaults.cardColors(containerColor = CyberSurface)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("‹", fontSize = 20.sp, color = CyberCyan, fontWeight = FontWeight.Bold, modifier = Modifier.clickable { viewModel.setSelectedYear(uiState.selectedYear - 1) })
                        YearDropdownSelector(selectedYear = uiState.selectedYear, onYearSelected = { viewModel.setSelectedYear(it) })
                        Text("›", fontSize = 20.sp, color = CyberCyan, fontWeight = FontWeight.Bold, modifier = Modifier.clickable { viewModel.setSelectedYear(uiState.selectedYear + 1) })
                    }
                }
            }
        }

        if (uiState.monthDrawsByDay.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No drawings recorded for $monthName ${uiState.selectedYear}.", color = CyberTextSecondary)
                }
            }
        } else {
            items(uiState.monthDrawsByDay, key = { "${it.year}-${it.month}-${it.day}" }) { draw ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = CyberSurface)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(draw.dateString, fontWeight = FontWeight.Bold, color = CyberCyan, fontSize = 15.sp)
                            Text("Winning Result", fontSize = 11.sp, color = CyberTextSecondary)
                        }
                        Spacer(modifier = Modifier.height(10.dp))

                        PillBoxWinningNumbers(numbers = draw.numbers)
                    }
                }
            }
        }
    }
}

// Draggable Generator Page Floating Overlay (Allows Peeking at Home Page)
@Composable
fun GeneratorPageModal(uiState: CashFiveUiState, viewModel: CashFiveViewModel) {
    var isJumbling by remember { mutableStateOf(false) }
    var jumbleNumbers by remember { mutableStateOf<List<Int>?>(null) }
    val coroutineScope = rememberCoroutineScope()

    var offsetX by remember { mutableFloatStateOf(0f) }
    var offsetY by remember { mutableFloatStateOf(0f) }

    val topTierSet = remember(uiState.tier1WithDates) { uiState.tier1WithDates.map { it.first }.toSet() }
    val nextTierSet = remember(uiState.tier2WithDates) { uiState.tier2WithDates.map { it.first }.toSet() }

    fun computeJumbleList(lockedList: List<Int>): List<Int> {
        val fixed = lockedList.toMutableList()
        val openPool = (1..35).filter { !fixed.contains(it) }.shuffled()
        val needed = 5 - fixed.size
        return fixed + openPool.take(needed)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.35f)) // Translucent scrim so Home Page is visible underneath!
            .clickable(indication = null, interactionSource = remember { MutableInteractionSource() }) {
                // Prevent tap pass-through
            },
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .fillMaxHeight(0.85f)
                .offset { IntOffset(offsetX.roundToInt(), offsetY.roundToInt()) }
                .border(2.dp, CyberCyan, RoundedCornerShape(20.dp)),
            colors = CardDefaults.cardColors(containerColor = CyberSurface.copy(alpha = 0.96f)),
            elevation = CardDefaults.cardElevation(defaultElevation = 12.dp)
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // 1. Draggable Header Title Bar
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .pointerInput(Unit) {
                                detectDragGestures { change, dragAmount ->
                                    change.consume()
                                    offsetX += dragAmount.x
                                    offsetY += dragAmount.y
                                }
                            }
                            .background(CyberBackground, RoundedCornerShape(12.dp))
                            .padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("⋮⋮", fontSize = 18.sp, color = CyberCyan, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("⚙️ Drag Generator Window", fontWeight = FontWeight.Bold, color = CyberText, fontSize = 14.sp)
                        }
                        IconButton(
                            onClick = { viewModel.closeGeneratorPage() },
                            modifier = Modifier.size(28.dp)
                        ) {
                            Text("✕", color = CyberCyan, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        }
                    }
                }

                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, CyberCyan, RoundedCornerShape(12.dp)),
                        colors = CardDefaults.cardColors(containerColor = CyberBackground)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text("Select Strategy", fontWeight = FontWeight.Bold, color = CyberGold, fontSize = 13.sp)
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                PickStrategy.entries.forEach { strategy ->
                                    val isSelected = uiState.selectedStrategy == strategy
                                    OutlinedButton(
                                        onClick = { viewModel.setStrategy(strategy) },
                                        modifier = Modifier.weight(1f),
                                        colors = ButtonDefaults.outlinedButtonColors(
                                            containerColor = if (isSelected) CyberCyan else Color.Transparent,
                                            contentColor = if (isSelected) Color.Black else CyberText
                                        ),
                                        contentPadding = PaddingValues(2.dp)
                                    ) {
                                        Text(
                                            text = when(strategy) {
                                                PickStrategy.BALANCED_BLEND -> "Balanced"
                                                PickStrategy.OVERDUE_TOP_TIER -> "Overdue"
                                                PickStrategy.WEIGHTED_ODDS -> "Weighted"
                                            },
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, CyberCyan.copy(alpha = 0.5f), RoundedCornerShape(12.dp)),
                        colors = CardDefaults.cardColors(containerColor = CyberBackground)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Lock Favorite Numbers (1–35)", fontWeight = FontWeight.Bold, color = CyberGold, fontSize = 13.sp)
                                Text("Locked: ${uiState.lockedNumbersList.size}/5", color = CyberCyan, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(modifier = Modifier.size(10.dp).clip(CircleShape).background(CyberGold))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Top Tier", fontSize = 10.sp, color = CyberGold, fontWeight = FontWeight.Bold)
                                }
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(modifier = Modifier.size(10.dp).clip(CircleShape).background(Color(0xFF0284C7)))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Next Tier", fontSize = 10.sp, color = CyberCyan, fontWeight = FontWeight.Bold)
                                }
                            }
                            Spacer(modifier = Modifier.height(10.dp))

                            val rows = (1..35).chunked(5)
                            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                for (row in rows) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceEvenly
                                    ) {
                                        for (num in row) {
                                            val isLocked = uiState.lockedNumbers.contains(num)
                                            val isTopTier = topTierSet.contains(num)
                                            val isNextTier = nextTierSet.contains(num)

                                            GridNumberBall(
                                                number = num,
                                                isLocked = isLocked,
                                                isTopTier = isTopTier,
                                                isNextTier = isNextTier,
                                                onClick = { viewModel.toggleNumberLock(num) }
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                item {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("GENERATED SMART PICK", fontWeight = FontWeight.Bold, color = CyberGold, fontSize = 12.sp)
                        Spacer(modifier = Modifier.height(8.dp))

                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            val lockedList = uiState.lockedNumbersList
                            val fullGeneratedList = if (uiState.hasGeneratedPick) uiState.currentNumbers else emptyList()

                            for (i in 0 until 5) {
                                if (isJumbling && jumbleNumbers != null && i < jumbleNumbers!!.size) {
                                    val num = jumbleNumbers!![i]
                                    val isLocked = i < lockedList.size
                                    GeneratedBall(
                                        number = num.toString(),
                                        isLocked = isLocked,
                                        isJumbling = true
                                    )
                                } else if (i < lockedList.size) {
                                    val num = lockedList[i]
                                    val isTop = topTierSet.contains(num)
                                    val isNext = nextTierSet.contains(num)
                                    GeneratedBall(
                                        number = num.toString(),
                                        isLocked = true,
                                        isTopTier = isTop,
                                        isNextTier = isNext
                                    )
                                } else if (uiState.hasGeneratedPick && i < fullGeneratedList.size) {
                                    val num = fullGeneratedList[i]
                                    val isTop = topTierSet.contains(num)
                                    val isNext = nextTierSet.contains(num)
                                    GeneratedBall(
                                        number = num.toString(),
                                        isLocked = false,
                                        isTopTier = isTop,
                                        isNextTier = isNext
                                    )
                                } else {
                                    QuestionBall()
                                }
                            }
                        }

                        if (uiState.hasGeneratedPick) {
                            uiState.latestPickResult?.let { res ->
                                Spacer(modifier = Modifier.height(12.dp))
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = CardDefaults.cardColors(containerColor = CyberBackground)
                                ) {
                                    Column(modifier = Modifier.padding(10.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                                        Text(res.insightSummary, fontSize = 10.sp, color = CyberTextSecondary, textAlign = TextAlign.Center)
                                    }
                                }
                            }
                        }
                    }
                }

                item {
                    Button(
                        onClick = {
                            if (!isJumbling) {
                                isJumbling = true
                                coroutineScope.launch {
                                    repeat(18) {
                                        jumbleNumbers = computeJumbleList(uiState.lockedNumbersList)
                                        delay(65)
                                    }
                                    viewModel.generateSmartPick(isTriggeredByUser = true)
                                    jumbleNumbers = null
                                    isJumbling = false
                                }
                            }
                        },
                        enabled = !isJumbling,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = CyberGold),
                        shape = RoundedCornerShape(24.dp)
                    ) {
                        Text(
                            if (isJumbling) "✨ . . . Generating . . ." else "✨ Generate Numbers Now",
                            color = Color.Black,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                    }
                }

                item {
                    Button(
                        onClick = { viewModel.saveCurrentTicket() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = CyberCyan),
                        shape = RoundedCornerShape(24.dp)
                    ) {
                        Text("⭐ Save Pick to Favorites", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    }
                }

                item {
                    Button(
                        onClick = { viewModel.resetGeneratorScreen() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = CyberSurface),
                        border = ButtonDefaults.outlinedButtonBorder.copy(brush = Brush.linearGradient(listOf(CyberCyan, CyberCyan))),
                        shape = RoundedCornerShape(24.dp)
                    ) {
                        Text("🔄 New Pick", color = CyberCyan, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    }
                }

                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(onClick = { viewModel.closeGeneratorPage() }) {
                            Text("CLOSE", color = CyberCyan, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun QuestionBall() {
    Box(
        modifier = Modifier
            .size(40.dp)
            .aspectRatio(1f)
            .clip(CircleShape)
            .background(CyberBackground)
            .border(1.dp, CyberCyan.copy(alpha = 0.5f), CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Text("?", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = CyberTextSecondary)
    }
}

@Composable
fun GeneratedBall(
    number: String,
    isLocked: Boolean = false,
    isTopTier: Boolean = false,
    isNextTier: Boolean = false,
    isJumbling: Boolean = false
) {
    val bgColor = when {
        isJumbling -> CyberCyan
        isTopTier -> CyberGold
        isNextTier -> Color(0xFF0284C7)
        else -> Color(0xFF334155)
    }
    val textColor = when {
        isJumbling || isTopTier || isNextTier -> Color.Black
        else -> Color.White
    }
    val borderColor = when {
        isLocked -> Color.White
        isTopTier -> CyberGold
        isNextTier -> CyberCyan
        else -> CyberCyan.copy(alpha = 0.5f)
    }

    Box(
        modifier = Modifier
            .size(44.dp)
            .aspectRatio(1f)
            .clip(CircleShape)
            .background(bgColor)
            .border(if (isLocked) 3.dp else 1.5.dp, borderColor, CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = number,
            fontSize = 16.sp,
            fontWeight = FontWeight.ExtraBold,
            color = textColor
        )
    }
}

@Composable
fun AnalysisTab(uiState: CashFiveUiState, viewModel: CashFiveViewModel) {
    var selectedNumber by remember { mutableStateOf(1) }
    val (hits, gap, recentDraws) = remember(selectedNumber, uiState.historicalDrawsCount) {
        viewModel.getNumberStats(selectedNumber)
    }
    val totalDraws = uiState.historicalDrawsCount.coerceAtLeast(1)
    val hitRatePercent = ((hits.toFloat() / totalDraws.toFloat()) * 100).toInt()

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text("Number Hit Analysis (1–35)", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = CyberGold)
            Text("Select any number to inspect its complete historical performance.", fontSize = 12.sp, color = CyberTextSecondary)
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = CyberSurface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Select Number: #$selectedNumber", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = CyberText)
                    Spacer(modifier = Modifier.height(10.dp))

                    for (row in 0 until 5) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            for (col in 1..7) {
                                val num = row * 7 + col
                                val isSelected = num == selectedNumber
                                Box(
                                    modifier = Modifier
                                        .size(38.dp)
                                        .aspectRatio(1f)
                                        .clip(CircleShape)
                                        .background(if (isSelected) CyberGold else CyberBackground)
                                        .border(1.dp, if (isSelected) CyberGold else CyberTextSecondary.copy(alpha = 0.3f), CircleShape)
                                        .clickable { selectedNumber = num },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = num.toString(),
                                        fontWeight = FontWeight.Bold,
                                        color = if (isSelected) Color.Black else CyberText,
                                        fontSize = 14.sp
                                    )
                                }
                            }
                        }
                        if (row < 4) Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = CyberSurface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Number #$selectedNumber Statistics", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = CyberGold)
                            Text("Total Dataset: $totalDraws draws", fontSize = 11.sp, color = CyberTextSecondary)
                        }
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(CyberCyan),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("#$selectedNumber", fontWeight = FontWeight.Bold, color = Color.Black, fontSize = 15.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        StatBox("Total Hits", "$hits times", CyberCyan)
                        StatBox("Hit Rate", "$hitRatePercent%", CyberGreen)
                        StatBox("Overdue Gap", "$gap draws ago", CyberGold)
                    }
                }
            }
        }

        item {
            Text("Recent Draws Containing #$selectedNumber", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = CyberText)
        }

        items(recentDraws, key = { "num-$selectedNumber-${it.year}-${it.month}-${it.day}" }) { draw ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = CyberSurface)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(draw.dateString, fontWeight = FontWeight.Bold, color = CyberCyan, fontSize = 14.sp)
                        Text("Historical Draw Result", fontSize = 11.sp, color = CyberTextSecondary)
                    }
                    PillBoxWinningNumbers(numbers = draw.numbers, highlightNumber = selectedNumber)
                }
            }
        }
    }
}

@Composable
fun StatBox(label: String, value: String, color: Color) {
    Card(
        colors = CardDefaults.cardColors(containerColor = CyberBackground),
        modifier = Modifier.width(100.dp)
    ) {
        Column(
            modifier = Modifier.padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(label, fontSize = 10.sp, color = CyberTextSecondary, textAlign = TextAlign.Center)
            Spacer(modifier = Modifier.height(4.dp))
            Text(value, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = color, textAlign = TextAlign.Center)
        }
    }
}

@Composable
fun AboutTab(uiState: CashFiveUiState, viewModel: CashFiveViewModel) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text("About Texas Cash Five", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = CyberGold)
            Text("Version 1.0.0 · Quick Hits Apps", fontSize = 12.sp, color = CyberTextSecondary)
        }
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = CyberSurface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Smart Algorithm & Analysis", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = CyberCyan)
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        "Texas Cash Five Quick Hits uses a 4-factor historical analysis engine (frequency analysis, overdue gap tracking, tier balancing, and hot/cold number weighting) across 31 years of official Texas Lottery drawings (1995–2026).",
                        fontSize = 13.sp,
                        color = CyberText,
                        lineHeight = 18.sp
                    )
                }
            }
        }
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = CyberSurface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Cloud Sync & Database", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = CyberCyan)
                    Spacer(modifier = Modifier.height(6.dp))
                    Text("Database records: ${uiState.historicalDrawsCount} historical draws", fontSize = 13.sp, color = CyberText)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Status: ${uiState.cloudSyncStatus}", fontSize = 11.sp, color = CyberTextSecondary)
                    Spacer(modifier = Modifier.height(10.dp))
                    Button(
                        onClick = { viewModel.triggerCloudflareSync() },
                        colors = ButtonDefaults.buttonColors(containerColor = CyberBackground),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Sync Latest Cloud Results", color = CyberCyan, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = CyberSurface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Terms & Legal Disclaimer", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = CyberGold)
                    Spacer(modifier = Modifier.height(10.dp))

                    Text("1. For Entertainment & Informational Use Only", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = CyberCyan)
                    Spacer(modifier = Modifier.height(2.dp))
                    Text("Texas Cash Five Quick Hits is created solely for statistical exploration, educational, and entertainment purposes.", fontSize = 12.sp, color = CyberTextSecondary, lineHeight = 16.sp)

                    Spacer(modifier = Modifier.height(8.dp))
                    Text("2. No Guarantee of Winnings or Results", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = CyberCyan)
                    Spacer(modifier = Modifier.height(2.dp))
                    Text("Historical draw frequency describes past drawings only; it does not predict future outcomes or alter the random odds of any lottery drawing. The app does not guarantee any winning ticket, prize, or financial return.", fontSize = 12.sp, color = CyberTextSecondary, lineHeight = 16.sp)

                    Spacer(modifier = Modifier.height(8.dp))
                    Text("3. Limitation of Liability & Waiver", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = CyberCyan)
                    Spacer(modifier = Modifier.height(2.dp))
                    Text("You are solely responsible for any lottery purchases or financial decisions you make. The developers of Texas Cash Five Quick Hits assume no liability for any financial losses or claims arising from the use of this app or reliance on its data.", fontSize = 12.sp, color = CyberTextSecondary, lineHeight = 16.sp)

                    Spacer(modifier = Modifier.height(8.dp))
                    Text("4. Unofficial & Non-Affiliated", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = CyberCyan)
                    Spacer(modifier = Modifier.height(2.dp))
                    Text("Texas Cash Five Quick Hits is an independent application and is not affiliated with, endorsed by, or sponsored by the Texas Lottery Commission or any official lottery organization.", fontSize = 12.sp, color = CyberTextSecondary, lineHeight = 16.sp)

                    Spacer(modifier = Modifier.height(12.dp))
                    Text("Data Source Attribution", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = CyberGold)
                    Spacer(modifier = Modifier.height(2.dp))
                    Text("Winning number results and historical drawing records are sourced from public data provided by the Texas Lottery Commission (www.texaslottery.com), retrieved on October 2026.", fontSize = 12.sp, color = CyberTextSecondary, lineHeight = 16.sp)
                }
            }
        }
    }
}

@Composable
fun SmallHitBall(number: String, isGreyedOut: Boolean = false) {
    Box(
        modifier = Modifier
            .size(34.dp)
            .aspectRatio(1f)
            .clip(CircleShape)
            .background(if (isGreyedOut) Color(0xFF334155) else CyberGreen),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = number,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = if (isGreyedOut) Color(0xFF94A3B8) else Color.Black
        )
    }
}
