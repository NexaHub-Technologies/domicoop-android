package com.nexahub.domicoop.ui.tabs

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Help
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.RequestQuote
import androidx.compose.material.icons.filled.Rule
import androidx.compose.material.icons.filled.Savings
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.nexahub.domicoop.core.Allocation
import com.nexahub.domicoop.core.Currency
import com.nexahub.domicoop.core.Format
import com.nexahub.domicoop.data.remote.ApiContribution
import com.nexahub.domicoop.data.remote.ApiLoan
import com.nexahub.domicoop.data.remote.DashboardAnnouncement
import com.nexahub.domicoop.data.remote.DashboardSummary
import com.nexahub.domicoop.data.remote.Profile
import com.nexahub.domicoop.data.repository.AppContainer
import com.nexahub.domicoop.theme.BrandGradientEnd
import com.nexahub.domicoop.theme.BrandGradientStart
import com.nexahub.domicoop.theme.DomiTheme
import com.nexahub.domicoop.theme.Inter
import com.nexahub.domicoop.theme.PlusJakartaSans
import com.nexahub.domicoop.ui.common.Badge
import com.nexahub.domicoop.ui.common.BadgeStatus
import com.nexahub.domicoop.ui.common.ConfirmationModal
import com.nexahub.domicoop.ui.common.DomiButton
import com.nexahub.domicoop.ui.common.DomiButtonSize
import com.nexahub.domicoop.ui.common.DomiButtonVariant
import com.nexahub.domicoop.ui.common.DomiCard
import com.nexahub.domicoop.ui.common.DomiCardElevation
import com.nexahub.domicoop.ui.common.EmptyState
import com.nexahub.domicoop.ui.common.ErrorState
import com.nexahub.domicoop.ui.common.ListItem
import com.nexahub.domicoop.ui.common.LoadingState
import com.nexahub.domicoop.ui.common.Money
import com.nexahub.domicoop.ui.common.MoneySize
import com.nexahub.domicoop.ui.common.MoneyText
import com.nexahub.domicoop.ui.common.MoneyTone
import com.nexahub.domicoop.ui.common.ScreenHeader
import androidx.compose.ui.text.TextStyle
import java.util.Calendar

data class MainTabItem(
    val route: String,
    val label: String,
    val icon: ImageVector,
)

val MainTabsList = listOf(
    MainTabItem("home", "HOME", Icons.Filled.Home),
    MainTabItem("loans", "LOANS", Icons.Filled.Payments),
    MainTabItem("contributions", "CONTRIBUTIONS", Icons.Filled.AccountBalanceWallet),
    MainTabItem("announcements", "ALERTS", Icons.Filled.Campaign),
    MainTabItem("profile", "PROFILE", Icons.Filled.Person),
)

// ---------- Main Tab Bar Component (matching components/tabs/TabBar/index.tsx) ----------
@Composable
fun DomiTabBar(
    selected: Int,
    onTabSelected: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .shadow(
                elevation = 8.dp,
                shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
                clip = false,
            ),
        shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 0.dp,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            MainTabsList.forEachIndexed { index, tab ->
                val isFocused = selected == index
                val activeBg = if (isFocused) {
                    DomiTheme.colors.primary.copy(alpha = 0.15f)
                } else {
                    Color.Transparent
                }
                val tint = if (isFocused) {
                    DomiTheme.colors.primaryBright
                } else {
                    MaterialTheme.colorScheme.onSurfaceVariant
                }

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(16.dp))
                        .background(activeBg)
                        .clickable { onTabSelected(index) }
                        .padding(vertical = 8.dp, horizontal = 6.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                    ) {
                        Icon(
                            imageVector = tab.icon,
                            contentDescription = tab.label,
                            tint = tint,
                            modifier = Modifier.size(24.dp),
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(
                            text = tab.label,
                            fontFamily = com.nexahub.domicoop.theme.Inter,
                            fontSize = 7.sp,
                            letterSpacing = 0.5.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = tint,
                            maxLines = 1,
                        )
                    }
                }
            }
        }
    }
}

// ---------- Main Container Screen ----------
@Composable
fun MainTabsScreen(
    app: AppContainer,
    onContributionClick: (String) -> Unit,
    onLoanClick: (String) -> Unit,
    onAddContribution: () -> Unit,
    onApplyLoan: () -> Unit,
    onAnnouncementClick: (String, String, String) -> Unit,
    onNotificationsClick: () -> Unit = {},
    onEditProfile: () -> Unit,
    onSecurity: () -> Unit,
    onNotifPrefs: () -> Unit,
    onThemePref: () -> Unit,
    onSupport: () -> Unit,
    onGuidelines: () -> Unit,
    onPrivacy: () -> Unit,
    onLoggedOut: () -> Unit,
) {
    var selected by rememberSaveable { mutableIntStateOf(0) }
    var showExitConfirm by remember { mutableStateOf(false) }

    // Intercept back button: go back to tab 0 or prompt exit
    BackHandler {
        if (selected != 0) {
            selected = 0
        } else {
            showExitConfirm = true
        }
    }

    Scaffold(
        bottomBar = {
            DomiTabBar(
                selected = selected,
                onTabSelected = { selected = it },
            )
        },
        containerColor = MaterialTheme.colorScheme.background,
    ) { padding ->
        Box(
            Modifier
                .fillMaxSize()
                .padding(padding),
        ) {
            when (selected) {
                0 -> DashboardTab(
                    app = app,
                    onDeposit = onAddContribution,
                    onApply = onApplyLoan,
                    onTxnClick = onContributionClick,
                    onNotificationsClick = onNotificationsClick,
                    onViewLoans = { selected = 1 },
                    onViewContributions = { selected = 2 },
                )
                1 -> LoansTab(
                    app = app,
                    onLoanClick = onLoanClick,
                    onApplyLoan = onApplyLoan,
                )
                2 -> ContributionsTab(
                    app = app,
                    onContributionClick = onContributionClick,
                    onAddContribution = onAddContribution,
                )
                3 -> AnnouncementsTab(
                    app = app,
                    onAnnouncementClick = onAnnouncementClick,
                )
                4 -> ProfileTab(
                    app = app,
                    onEditProfile = onEditProfile,
                    onSecurity = onSecurity,
                    onNotifPrefs = onNotifPrefs,
                    onThemePref = onThemePref,
                    onSupport = onSupport,
                    onGuidelines = onGuidelines,
                    onPrivacy = onPrivacy,
                    onLoggedOut = onLoggedOut,
                )
            }
        }
    }

    ConfirmationModal(
        visible = showExitConfirm,
        title = "Log out?",
        message = "Going back will log you out of DOMICOOP and return you to the login screen.",
        confirmText = "Log out",
        cancelText = "Stay",
        onConfirm = {
            showExitConfirm = false
            onLoggedOut()
        },
        onCancel = { showExitConfirm = false },
        isDestructive = true,
    )
}

// =========================================================================
// TAB 1: DASHBOARD
// =========================================================================

private fun getGreeting(): String {
    val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
    return when {
        hour < 12 -> "Good morning,"
        hour < 17 -> "Good afternoon,"
        else -> "Good evening,"
    }
}

private fun getInitials(name: String): String {
    val parts = name.trim().split(" ").filter { it.isNotBlank() }
    return when {
        parts.isEmpty() -> "U"
        parts.size == 1 -> parts[0].take(2).uppercase()
        else -> "${parts[0].first()}${parts[parts.size - 1].first()}".uppercase()
    }
}

@Composable
fun DashboardTab(
    app: AppContainer,
    onDeposit: () -> Unit,
    onApply: () -> Unit,
    onTxnClick: (String) -> Unit,
    onNotificationsClick: () -> Unit,
    onViewLoans: () -> Unit,
    onViewContributions: () -> Unit,
    vm: DashboardViewModel = viewModel { DashboardViewModel(app) },
) {
    val s by vm.state.collectAsState()

    if (s.loading) {
        LoadingState("Loading overview…")
        return
    }
    if (s.error != null && s.data == null) {
        ErrorState(s.error!!, vm::refresh)
        return
    }
    val d = s.data ?: return
    val fullName = d.member.fullName.ifBlank { "Member" }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(24.dp),
    ) {
        // Fixed Dashboard Header
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 16.dp),
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    // Left avatar + text
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f),
                    ) {
                        Box(
                            modifier = Modifier
                                .size(52.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.surfaceContainer)
                                .border(2.dp, DomiTheme.colors.primaryBright, CircleShape),
                            contentAlignment = Alignment.Center,
                        ) {
                            Text(
                                text = getInitials(fullName),
                                fontFamily = PlusJakartaSans,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = DomiTheme.colors.primaryBright,
                            )
                        }
                        Spacer(Modifier.width(12.dp))
                        Column {
                            Text(
                                text = getGreeting(),
                                fontFamily = Inter,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                letterSpacing = 0.5.sp,
                            )
                            Text(
                                text = fullName,
                                fontFamily = PlusJakartaSans,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = MaterialTheme.colorScheme.onSurface,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                            )
                        }
                    }

                    // Notification bell button
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.surfaceContainer)
                            .clickable { onNotificationsClick() },
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Notifications,
                            contentDescription = "Notifications",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(24.dp),
                        )
                        // If unread announcements or notifications exist
                        if (d.announcements.isNotEmpty()) {
                            Box(
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .offset(x = (-2).dp, y = 2.dp)
                                    .size(18.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.error)
                                    .border(2.dp, MaterialTheme.colorScheme.surface, CircleShape),
                                contentAlignment = Alignment.Center,
                            ) {
                                Text(
                                    text = if (d.announcements.size > 9) "9+" else "${d.announcements.size}",
                                    fontFamily = Inter,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onError,
                                )
                            }
                        }
                    }
                }
            }
        }

        // Offline Banner
        if (s.offline) {
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.surfaceContainer)
                        .padding(vertical = 8.dp, horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                ) {
                    Icon(
                        imageVector = Icons.Filled.CloudOff,
                        contentDescription = "Offline",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(16.dp),
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        "Showing cached data — offline",
                        fontFamily = Inter,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        }

        // Financial Snapshot / ContributionSummary
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
            ) {
                Text(
                    text = "Your Financial Snapshot",
                    fontFamily = Inter,
                    fontSize = 10.sp,
                    letterSpacing = 1.5.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(start = 4.dp, bottom = 12.dp),
                )

                // Card container with 20dp radius and 10dp glow shadow
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = MaterialTheme.colorScheme.surface,
                    shadowElevation = 10.dp,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Column {
                        // Brand Gradient Top Hero
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(
                                    Brush.linearGradient(
                                        listOf(
                                            BrandGradientStart,
                                            BrandGradientEnd,
                                        ),
                                    ),
                                )
                                .padding(24.dp),
                        ) {
                            Column {
                                Text(
                                    text = "Your Position with DOMICOOP",
                                    fontFamily = Inter,
                                    fontSize = 11.sp,
                                    color = Color.White.copy(alpha = 0.70f),
                                    letterSpacing = 0.5.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    modifier = Modifier.padding(bottom = 16.dp),
                                )

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.Top,
                                ) {
                                    // Left: Contributions
                                    Column(modifier = Modifier.weight(1f)) {
                                        Box(
                                            modifier = Modifier
                                                .size(28.dp)
                                                .clip(CircleShape)
                                                .background(Color.White.copy(alpha = 0.12f)),
                                            contentAlignment = Alignment.Center,
                                        ) {
                                            Icon(
                                                imageVector = Icons.Filled.Savings,
                                                contentDescription = null,
                                                tint = Color.White,
                                                modifier = Modifier.size(16.dp),
                                            )
                                        }
                                        Spacer(Modifier.height(8.dp))
                                        Text(
                                            text = "CONTRIBUTIONS",
                                            fontFamily = Inter,
                                            fontSize = 9.sp,
                                            letterSpacing = 0.5.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White.copy(alpha = 0.90f),
                                        )
                                        Spacer(Modifier.height(4.dp))
                                        Money(
                                            amount = d.totalSavings,
                                            size = MoneySize.MD,
                                            tone = MoneyTone.ON_PRIMARY,
                                        )
                                        Spacer(Modifier.height(4.dp))
                                        Text(
                                            text = if (d.paidThisMonth) "Paid this month ✓" else "Not paid this month",
                                            fontFamily = Inter,
                                            fontSize = 11.sp,
                                            color = Color.White.copy(alpha = 0.70f),
                                        )
                                    }

                                    // Divider
                                    Box(
                                        modifier = Modifier
                                            .width(1.dp)
                                            .height(80.dp)
                                            .background(Color.White.copy(alpha = 0.15f))
                                            .padding(horizontal = 8.dp),
                                    )

                                    // Right: Loan Balance
                                    Column(
                                        modifier = Modifier
                                            .weight(1f)
                                            .padding(start = 12.dp)
                                            .clickable { onViewLoans() },
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(28.dp)
                                                .clip(CircleShape)
                                                .background(Color.White.copy(alpha = 0.12f)),
                                            contentAlignment = Alignment.Center,
                                        ) {
                                            Icon(
                                                imageVector = Icons.Filled.AccountBalanceWallet,
                                                contentDescription = null,
                                                tint = Color.White,
                                                modifier = Modifier.size(16.dp),
                                            )
                                        }
                                        Spacer(Modifier.height(8.dp))
                                        Text(
                                            text = "LOAN BALANCE",
                                            fontFamily = Inter,
                                            fontSize = 9.sp,
                                            letterSpacing = 0.5.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White.copy(alpha = 0.90f),
                                        )
                                        Spacer(Modifier.height(4.dp))
                                        val loanBalance = d.activeLoan?.balance ?: 0.0
                                        if (loanBalance > 0) {
                                            Money(
                                                amount = loanBalance,
                                                size = MoneySize.MD,
                                                tone = MoneyTone.ON_PRIMARY,
                                            )
                                        } else {
                                            Text(
                                                text = "All clear",
                                                fontFamily = PlusJakartaSans,
                                                fontSize = 16.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color.White,
                                            )
                                        }
                                        Spacer(Modifier.height(4.dp))
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(2.dp),
                                        ) {
                                            Text(
                                                text = if (loanBalance > 0) "View loans" else "Apply for one",
                                                fontFamily = Inter,
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Medium,
                                                color = Color.White,
                                            )
                                            Icon(
                                                imageVector = Icons.Filled.ChevronRight,
                                                contentDescription = null,
                                                tint = Color.White,
                                                modifier = Modifier.size(14.dp),
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        // Allocation breakdown for this year
                        val alloc = remember(d.totalSavings) {
                            Allocation.calculate(if (d.totalSavings > 0) d.totalSavings else 0.0)
                        }
                        val totalAttributed = alloc.shares + alloc.social + alloc.savings + alloc.deposit
                        val yearSavings = if (d.totalSavings > 0) d.totalSavings else 1.0

                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(MaterialTheme.colorScheme.surface)
                                .padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                        ) {
                            Text(
                                text = "This Year’s Allocation",
                                fontFamily = Inter,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                letterSpacing = 0.5.sp,
                            )

                            // Segmented bar
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(6.dp)
                                    .clip(RoundedCornerShape(3.dp))
                                    .background(MaterialTheme.colorScheme.surfaceContainerHigh),
                            ) {
                                if (alloc.shares > 0) {
                                    Box(
                                        Modifier
                                            .weight(alloc.shares.toFloat())
                                            .fillMaxSize()
                                            .background(DomiTheme.colors.primaryBright),
                                    )
                                }
                                if (alloc.social > 0) {
                                    Box(
                                        Modifier
                                            .weight(alloc.social.toFloat())
                                            .fillMaxSize()
                                            .background(DomiTheme.colors.tertiary),
                                    )
                                }
                                if (alloc.savings > 0) {
                                    Box(
                                        Modifier
                                            .weight(alloc.savings.toFloat())
                                            .fillMaxSize()
                                            .background(DomiTheme.colors.success),
                                    )
                                }
                                if (alloc.deposit > 0) {
                                    Box(
                                        Modifier
                                            .weight(alloc.deposit.toFloat())
                                            .fillMaxSize()
                                            .background(DomiTheme.colors.warning),
                                    )
                                }
                            }

                            // Items
                            AllocationRow(
                                dotColor = DomiTheme.colors.primaryBright,
                                label = "Shares",
                                desc = "Fixed monthly",
                                amount = alloc.shares,
                                percent = (alloc.shares / yearSavings) * 100,
                            )
                            AllocationRow(
                                dotColor = DomiTheme.colors.tertiary,
                                label = "Social",
                                desc = "Fixed monthly",
                                amount = alloc.social,
                                percent = (alloc.social / yearSavings) * 100,
                            )
                            AllocationRow(
                                dotColor = DomiTheme.colors.success,
                                label = "Savings",
                                desc = "Flexible (capped)",
                                amount = alloc.savings,
                                percent = (alloc.savings / yearSavings) * 100,
                            )
                            if (alloc.deposit > 0) {
                                AllocationRow(
                                    dotColor = DomiTheme.colors.warning,
                                    label = "Deposit",
                                    desc = "Overflow only",
                                    amount = alloc.deposit,
                                    percent = (alloc.deposit / yearSavings) * 100,
                                )
                            }

                            HorizontalDivider(
                                color = MaterialTheme.colorScheme.outlineVariant,
                                thickness = 1.dp,
                                modifier = Modifier.padding(top = 4.dp),
                            )

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Text(
                                    "Total Attributed",
                                    fontFamily = Inter,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface,
                                )
                                Money(
                                    amount = totalAttributed,
                                    size = MoneySize.SM,
                                    style = TextStyle(color = DomiTheme.colors.primaryBright),
                                )
                            }
                        }
                    }
                }
            }
        }

        // Quick Actions (Deposit & Apply)
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
            ) {
                Text(
                    text = "Quick Actions",
                    fontFamily = Inter,
                    fontSize = 10.sp,
                    letterSpacing = 1.5.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(start = 4.dp, bottom = 12.dp),
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    // Deposit
                    QuickActionButton(
                        icon = Icons.Filled.AddCircle,
                        label = "Deposit",
                        tint = DomiTheme.colors.success,
                        onClick = onDeposit,
                    )
                    // Apply
                    QuickActionButton(
                        icon = Icons.Filled.RequestQuote,
                        label = "Apply",
                        tint = DomiTheme.colors.tertiary,
                        onClick = onApply,
                    )
                }
            }
        }

        // Recent Transactions
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 4.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = "Recent Transactions",
                        fontFamily = Inter,
                        fontSize = 10.sp,
                        letterSpacing = 1.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    if (d.recentTransactions.isNotEmpty()) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.clickable { onViewContributions() },
                        ) {
                            Text(
                                "VIEW ARCHIVE",
                                fontFamily = Inter,
                                fontSize = 11.sp,
                                letterSpacing = 0.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = DomiTheme.colors.primaryBright,
                            )
                            Spacer(Modifier.width(4.dp))
                            Icon(
                                imageVector = Icons.Filled.ArrowForwardIos,
                                contentDescription = null,
                                tint = DomiTheme.colors.primaryBright,
                                modifier = Modifier.size(10.dp),
                            )
                        }
                    }
                }

                Spacer(Modifier.height(8.dp))

                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = MaterialTheme.colorScheme.surface,
                    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    if (d.recentTransactions.isEmpty()) {
                        EmptyState(
                            icon = Icons.Filled.Payments,
                            title = "No transactions yet",
                            message = "Your contributions and loan payments will appear here.",
                        )
                    } else {
                        Column {
                            // Category Header
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(MaterialTheme.colorScheme.surfaceContainerLow)
                                    .padding(horizontal = 16.dp, vertical = 12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(6.dp)
                                            .clip(CircleShape)
                                            .background(DomiTheme.colors.success),
                                    )
                                    Spacer(Modifier.width(8.dp))
                                    Text(
                                        "Savings Activity",
                                        fontFamily = Inter,
                                        fontSize = 10.sp,
                                        letterSpacing = 0.5.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    )
                                }
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.clickable { onViewContributions() },
                                ) {
                                    Text(
                                        "SEE ALL",
                                        fontFamily = Inter,
                                        fontSize = 10.sp,
                                        letterSpacing = 0.5.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = DomiTheme.colors.primaryBright,
                                    )
                                    Icon(
                                        imageVector = Icons.Filled.ChevronRight,
                                        contentDescription = null,
                                        tint = DomiTheme.colors.primaryBright,
                                        modifier = Modifier.size(14.dp),
                                    )
                                }
                            }

                            d.recentTransactions.take(5).forEachIndexed { index, txn ->
                                Box(modifier = Modifier.padding(horizontal = 16.dp)) {
                                    ListItem(
                                        title = Format.humanize(txn.title?.ifBlank { txn.type }, "Contribution"),
                                        subtitle = Format.formatDate(txn.date).takeIf { it != "—" },
                                        leadingIcon = Icons.Filled.Savings,
                                        leadingColor = DomiTheme.colors.success,
                                        chevron = false,
                                        onClick = { onTxnClick(txn.id) },
                                        trailing = {
                                            Column(horizontalAlignment = Alignment.End) {
                                                Money(
                                                    amount = txn.amount,
                                                    size = MoneySize.SM,
                                                    tone = MoneyTone.SUCCESS,
                                                    signed = true,
                                                )
                                                Text(
                                                    text = txn.status.uppercase(),
                                                    fontFamily = Inter,
                                                    fontSize = 9.sp,
                                                    letterSpacing = 0.5.sp,
                                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                                )
                                            }
                                        },
                                    )
                                }
                                if (index < d.recentTransactions.take(5).size - 1) {
                                    HorizontalDivider(
                                        color = MaterialTheme.colorScheme.outlineVariant,
                                        thickness = 1.dp,
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        item { Spacer(Modifier.height(32.dp)) }
    }
}

@Composable
private fun AllocationRow(
    dotColor: Color,
    label: String,
    desc: String,
    amount: Double,
    percent: Double,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f),
        ) {
            Box(
                modifier = Modifier
                    .size(10.dp)
                    .clip(CircleShape)
                    .background(dotColor),
            )
            Spacer(Modifier.width(12.dp))
            Column {
                Text(
                    text = label,
                    fontFamily = Inter,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface,
                )
                Text(
                    text = desc,
                    fontFamily = Inter,
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
        Column(horizontalAlignment = Alignment.End) {
            Money(amount = amount, size = MoneySize.SM)
            Text(
                text = "${Format.round1(percent)}%",
                fontFamily = Inter,
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun QuickActionButton(
    icon: ImageVector,
    label: String,
    tint: Color,
    onClick: () -> Unit,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .clickable { onClick() }
            .padding(4.dp),
    ) {
        Box(
            modifier = Modifier
                .size(56.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(tint.copy(alpha = 0.08f)),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = tint,
                modifier = Modifier.size(28.dp),
            )
        }
        Spacer(Modifier.height(8.dp))
        Text(
            text = label.uppercase(),
            fontFamily = Inter,
            fontSize = 10.sp,
            letterSpacing = 0.5.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

// =========================================================================
// TAB 2: CONTRIBUTIONS / SAVINGS
// =========================================================================

@Composable
fun ContributionsTab(
    app: AppContainer,
    onContributionClick: (String) -> Unit,
    onAddContribution: () -> Unit,
    vm: ContributionsViewModel = viewModel { ContributionsViewModel(app) },
) {
    val s by vm.state.collectAsState()
    var showAll by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            ScreenHeader(title = "Contributions", large = true, applyTopInset = false)
        },
        containerColor = MaterialTheme.colorScheme.background,
    ) { padding ->
        if (s.loading) {
            LoadingState("Loading contributions…")
            return@Scaffold
        }
        if (s.error != null && s.items.isEmpty()) {
            ErrorState(s.error!!, vm::refresh)
            return@Scaffold
        }

        val displayed = if (showAll) s.items else s.items.take(5)

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            // Offline banner
            if (s.offline) {
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(MaterialTheme.colorScheme.surfaceContainer)
                            .padding(vertical = 8.dp, horizontal = 16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center,
                    ) {
                        Icon(
                            imageVector = Icons.Filled.CloudOff,
                            contentDescription = "Offline",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(16.dp),
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(
                            "Showing cached data — offline",
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            ),
                        )
                    }
                }
            }

            // Portfolio Card
            item {
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(elevation = 10.dp, shape = RoundedCornerShape(20.dp)),
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                Brush.linearGradient(
                                    listOf(
                                        BrandGradientStart,
                                        BrandGradientEnd,
                                    ),
                                ),
                            )
                            .padding(24.dp),
                    ) {
                        // Watermark pattern
                        Box(
                            modifier = Modifier
                                .size(200.dp)
                                .offset(x = 60.dp, y = (-60).dp)
                                .clip(CircleShape)
                                .background(Color.White.copy(alpha = 0.05f))
                                .align(Alignment.TopEnd),
                        )

                        val paidThisMonth = s.items.any {
                            it.month.startsWith("${Calendar.getInstance().get(Calendar.YEAR)}")
                        }

                        Column {
                            Text(
                                "Total Contributions",
                                fontFamily = Inter,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color.White.copy(alpha = 0.90f),
                            )
                            Spacer(Modifier.height(8.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween,
                            ) {
                                Money(
                                    amount = s.totalBalance,
                                    size = MoneySize.XL,
                                    tone = MoneyTone.ON_PRIMARY,
                                )
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(Color.White.copy(alpha = 0.20f))
                                        .padding(horizontal = 8.dp, vertical = 2.dp),
                                ) {
                                    Text(
                                        text = if (paidThisMonth) "PAID THIS MONTH" else "NOT PAID",
                                        fontFamily = Inter,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White,
                                    )
                                }
                            }

                            Spacer(Modifier.height(20.dp))
                            HorizontalDivider(
                                color = Color.White.copy(alpha = 0.15f),
                                thickness = 1.dp,
                            )
                            Spacer(Modifier.height(16.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                            ) {
                                Column {
                                    Text(
                                        "CURRENT MONTH",
                                        fontFamily = Inter,
                                        fontSize = 9.sp,
                                        letterSpacing = 0.5.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White.copy(alpha = 0.70f),
                                    )
                                    Spacer(Modifier.height(4.dp))
                                    Text(
                                        Format.currentMonthLong(),
                                        fontFamily = Inter,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = Color.White,
                                    )
                                }
                                Column(horizontalAlignment = Alignment.End) {
                                    Text(
                                        "STATUS",
                                        fontFamily = Inter,
                                        fontSize = 9.sp,
                                        letterSpacing = 0.5.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White.copy(alpha = 0.70f),
                                    )
                                    Spacer(Modifier.height(4.dp))
                                    Text(
                                        if (paidThisMonth) "Contribution Made" else "No Contribution Yet",
                                        fontFamily = Inter,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = Color.White,
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Add Contribution Button
            item {
                DomiButton(
                    text = "Add Contribution",
                    onClick = onAddContribution,
                    variant = DomiButtonVariant.Primary,
                    size = DomiButtonSize.Large,
                    icon = Icons.Filled.AddCircle,
                    modifier = Modifier.fillMaxWidth(),
                )
            }

            // Transaction History
            if (s.items.isNotEmpty()) {
                item {
                    Text(
                        text = "Transaction History",
                        fontFamily = Inter,
                        fontSize = 10.sp,
                        letterSpacing = 1.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(start = 4.dp, top = 8.dp),
                    )
                }

                item {
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = MaterialTheme.colorScheme.surface,
                        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Column {
                            displayed.forEachIndexed { index, c ->
                                val statusTone = when (c.status.lowercase()) {
                                    "verified" -> MoneyTone.SUCCESS
                                    "pending" -> MoneyTone.DEFAULT
                                    "rejected" -> MoneyTone.ERROR
                                    else -> MoneyTone.MUTED
                                }
                                val statusColor = when (c.status.lowercase()) {
                                    "verified" -> DomiTheme.colors.success
                                    "pending" -> DomiTheme.colors.warning
                                    "rejected" -> MaterialTheme.colorScheme.error
                                    else -> MaterialTheme.colorScheme.onSurfaceVariant
                                }

                                Box(modifier = Modifier.padding(horizontal = 16.dp)) {
                                    ListItem(
                                        title = Format.monthLabel(c.month),
                                        subtitle = "${c.month.take(4)} Contribution",
                                        leadingIcon = Icons.Filled.Savings,
                                        leadingColor = statusColor,
                                        chevron = false,
                                        onClick = { onContributionClick(c.id) },
                                        trailing = {
                                            Column(horizontalAlignment = Alignment.End) {
                                                Money(
                                                    amount = c.amount,
                                                    size = MoneySize.SM,
                                                    tone = statusTone,
                                                )
                                                Text(
                                                    text = c.status.uppercase(),
                                                    fontFamily = Inter,
                                                    fontSize = 9.sp,
                                                    letterSpacing = 0.5.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = statusColor,
                                                )
                                            }
                                        },
                                    )
                                }
                                if (index < displayed.size - 1) {
                                    HorizontalDivider(
                                        color = MaterialTheme.colorScheme.outlineVariant,
                                        thickness = 1.dp,
                                    )
                                }
                            }
                        }
                    }
                }

                if (s.items.size > 5) {
                    item {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { showAll = !showAll }
                                .padding(vertical = 12.dp),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Text(
                                text = if (showAll) "Show Less" else "View All Transactions (${s.items.size})",
                                fontFamily = Inter,
                                fontSize = 12.sp,
                                color = DomiTheme.colors.primaryBright,
                                fontWeight = FontWeight.Bold,
                            )
                            Spacer(Modifier.width(4.dp))
                            Icon(
                                imageVector = if (showAll) Icons.Filled.ExpandLess else Icons.Filled.ExpandMore,
                                contentDescription = null,
                                tint = DomiTheme.colors.primaryBright,
                                modifier = Modifier.size(20.dp),
                            )
                        }
                    }
                }
            } else {
                item {
                    EmptyState(
                        icon = Icons.Filled.Savings,
                        title = "No Contributions Yet",
                        message = "Your contribution balance is ₦${Format.currency(s.totalBalance)}. Start making contributions to build your savings with DOMICOOP.",
                        actionLabel = "Add First Contribution",
                        onAction = onAddContribution,
                    )
                }
            }

            item { Spacer(Modifier.height(32.dp)) }
        }
    }
}

// =========================================================================
// TAB 3: LOANS
// =========================================================================

@Composable
fun LoansTab(
    app: AppContainer,
    onLoanClick: (String) -> Unit,
    onApplyLoan: () -> Unit,
    vm: LoansViewModel = viewModel { LoansViewModel(app) },
) {
    val s by vm.state.collectAsState()

    Scaffold(
        topBar = {
            ScreenHeader(title = "Loans", large = true, applyTopInset = false)
        },
        containerColor = MaterialTheme.colorScheme.background,
    ) { padding ->
        if (s.loading) {
            LoadingState("Loading loans…")
            return@Scaffold
        }
        if (s.error != null && s.items.isEmpty()) {
            ErrorState(s.error!!, vm::refresh)
            return@Scaffold
        }

        val activeLoans = s.items.filter { it.status?.lowercase() != "completed" }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            // Offline banner
            if (s.offline) {
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(MaterialTheme.colorScheme.surfaceContainer)
                            .padding(vertical = 8.dp, horizontal = 16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center,
                    ) {
                        Icon(
                            imageVector = Icons.Filled.CloudOff,
                            contentDescription = "Offline",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(16.dp),
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(
                            "Showing cached data — offline",
                            fontFamily = Inter,
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
            }

            // Loan Portfolio Card
            item {
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(elevation = 10.dp, shape = RoundedCornerShape(20.dp)),
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                Brush.linearGradient(
                                    listOf(
                                        BrandGradientStart,
                                        BrandGradientEnd,
                                    ),
                                ),
                            )
                            .padding(24.dp),
                    ) {
                        Box(
                            modifier = Modifier
                                .size(200.dp)
                                .offset(x = 60.dp, y = (-60).dp)
                                .clip(CircleShape)
                                .background(Color.White.copy(alpha = 0.05f))
                                .align(Alignment.TopEnd),
                        )

                        Column {
                            Text(
                                "Total Debt",
                                fontFamily = Inter,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color.White.copy(alpha = 0.90f),
                            )
                            Spacer(Modifier.height(8.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween,
                            ) {
                                Money(
                                    amount = s.totalDebt,
                                    size = MoneySize.XL,
                                    tone = MoneyTone.ON_PRIMARY,
                                )
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(Color.White.copy(alpha = 0.20f))
                                        .padding(horizontal = 8.dp, vertical = 2.dp),
                                ) {
                                    Text(
                                        text = if (activeLoans.isNotEmpty()) {
                                            "${activeLoans.size} ACTIVE ${if (activeLoans.size == 1) "LOAN" else "LOANS"}"
                                        } else {
                                            "NO ACTIVE LOANS"
                                        },
                                        fontFamily = Inter,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White,
                                    )
                                }
                            }

                            Spacer(Modifier.height(20.dp))
                            HorizontalDivider(
                                color = Color.White.copy(alpha = 0.15f),
                                thickness = 1.dp,
                            )
                            Spacer(Modifier.height(16.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                            ) {
                                Column {
                                    Text(
                                        "NEXT PAYMENT",
                                        fontFamily = Inter,
                                        fontSize = 9.sp,
                                        letterSpacing = 0.5.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White.copy(alpha = 0.70f),
                                    )
                                    Spacer(Modifier.height(4.dp))
                                    Text(
                                        activeLoans.firstOrNull()?.dueDate ?: "—",
                                        fontFamily = Inter,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = Color.White,
                                    )
                                }
                                Column(horizontalAlignment = Alignment.End) {
                                    Text(
                                        "AMOUNT DUE",
                                        fontFamily = Inter,
                                        fontSize = 9.sp,
                                        letterSpacing = 0.5.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White.copy(alpha = 0.70f),
                                    )
                                    Spacer(Modifier.height(4.dp))
                                    val nextAmount = activeLoans.firstOrNull()?.monthlyRepayment ?: 0.0
                                    if (nextAmount > 0) {
                                        Money(amount = nextAmount, size = MoneySize.MD, tone = MoneyTone.ON_PRIMARY)
                                    } else {
                                        Text(
                                            "All clear",
                                            fontFamily = Inter,
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = Color.White,
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Apply for a Loan Button
            item {
                DomiButton(
                    text = "Apply for a Loan",
                    onClick = onApplyLoan,
                    variant = DomiButtonVariant.Primary,
                    size = DomiButtonSize.Large,
                    icon = Icons.Filled.AddCircle,
                    modifier = Modifier.fillMaxWidth(),
                )
            }

            // Active Loans list
            if (activeLoans.isNotEmpty()) {
                item {
                    Text(
                        text = "Active Loans",
                        fontFamily = Inter,
                        fontSize = 10.sp,
                        letterSpacing = 1.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(start = 4.dp, top = 8.dp),
                    )
                }

                items(activeLoans) { loan ->
                    LoanCardItem(loan = loan, onClick = { onLoanClick(loan.id) })
                }

                // Insights Card
                item {
                    InsightsCard()
                }
            } else {
                item {
                    EmptyState(
                        icon = Icons.Filled.AccountBalance,
                        title = "No loans yet",
                        message = "Apply for a loan and it will show up here.",
                        actionLabel = "Apply for a Loan",
                        onAction = onApplyLoan,
                    )
                }
            }

            item { Spacer(Modifier.height(32.dp)) }
        }
    }
}

@Composable
private fun LoanCardItem(
    loan: ApiLoan,
    onClick: () -> Unit,
) {
    val totalAmount = loan.amountApproved ?: loan.amountRequested
    val balance = loan.balance ?: totalAmount
    val progress = if (totalAmount > 0) {
        (((totalAmount - balance) / totalAmount) * 100).toInt().coerceIn(0, 100)
    } else 0

    val (badgeStatus, badgeLabel) = when (loan.status?.lowercase()) {
        "active", "approved", "repaying", "disbursed" -> Pair(BadgeStatus.SUCCESS, "ON TRACK")
        "pending" -> Pair(BadgeStatus.WARNING, "PENDING")
        "overdue", "late", "defaulted" -> Pair(BadgeStatus.ERROR, "OVERDUE")
        "completed", "closed", "paid" -> Pair(BadgeStatus.NEUTRAL, "COMPLETED")
        else -> Pair(BadgeStatus.NEUTRAL, (loan.status ?: "UNKNOWN").uppercase())
    }

    Surface(
        shape = RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.surface,
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .clickable { onClick() },
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top,
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f),
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(DomiTheme.colors.primary.copy(alpha = 0.08f)),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Payments,
                            contentDescription = null,
                            tint = DomiTheme.colors.primary,
                            modifier = Modifier.size(20.dp),
                        )
                    }
                    Spacer(Modifier.width(12.dp))
                    Column {
                        Text(
                            text = loan.purpose?.ifBlank { "Personal Loan" } ?: "Personal Loan",
                            fontFamily = Inter,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurface,
                        )
                        Text(
                            text = "ID: ${loan.id.take(8).uppercase()}",
                            fontFamily = Inter,
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
                Badge(status = badgeStatus, label = badgeLabel, dot = false)
            }

            Spacer(Modifier.height(16.dp))

            // Amounts Row
            Row(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        "TOTAL LOAN",
                        fontFamily = Inter,
                        fontSize = 10.sp,
                        letterSpacing = 0.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    Spacer(Modifier.height(4.dp))
                    Money(amount = totalAmount, size = MoneySize.MD)
                }
                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.End,
                ) {
                    Text(
                        "REMAINING",
                        fontFamily = Inter,
                        fontSize = 10.sp,
                        letterSpacing = 0.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    Spacer(Modifier.height(4.dp))
                    Money(amount = balance, size = MoneySize.MD, style = TextStyle(color = DomiTheme.colors.primaryBright))
                }
            }

            Spacer(Modifier.height(16.dp))

            // Progress bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    "Repayment progress",
                    fontFamily = Inter,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Text(
                    "$progress%",
                    fontFamily = Inter,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                )
            }
            Spacer(Modifier.height(6.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp))
                    .background(MaterialTheme.colorScheme.surfaceContainerHigh),
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(fraction = (progress / 100f).coerceIn(0f, 1f))
                        .fillMaxSize()
                        .clip(RoundedCornerShape(3.dp))
                        .background(DomiTheme.colors.primaryBright),
                )
            }

            Spacer(Modifier.height(16.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant, thickness = 1.dp)
            Spacer(Modifier.height(12.dp))

            // Footer
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f),
                ) {
                    Icon(
                        imageVector = Icons.Filled.CalendarToday,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(14.dp),
                    )
                    Spacer(Modifier.width(6.dp))
                    Text(
                        text = "Next: ₦${Format.currency(loan.monthlyRepayment ?: 0.0)} on ${Format.formatDate(loan.dueDate)}",
                        fontFamily = Inter,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.surfaceContainer)
                        .padding(horizontal = 14.dp, vertical = 6.dp),
                ) {
                    Text(
                        "Manage",
                        fontFamily = Inter,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = DomiTheme.colors.primaryBright,
                    )
                }
            }
        }
    }
}

@Composable
private fun InsightsCard() {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surfaceContainerLow,
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.20f)),
        modifier = Modifier.fillMaxWidth(),
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.Top,
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surface)
                    .shadow(2.dp, CircleShape),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = Icons.Filled.Lightbulb,
                    contentDescription = null,
                    tint = DomiTheme.colors.primary,
                    modifier = Modifier.size(24.dp),
                )
            }
            Spacer(Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Did you know?",
                    fontFamily = PlusJakartaSans,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = "You can borrow up to 3 times your total savings balance once you have been an active contributing member for at least 6 months.",
                    fontFamily = Inter,
                    fontSize = 12.sp,
                    lineHeight = 18.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

// =========================================================================
// TAB 4: ANNOUNCEMENTS / ALERTS
// =========================================================================

@Composable
fun AnnouncementsTab(
    app: AppContainer,
    onAnnouncementClick: (String, String, String) -> Unit,
    vm: DashboardViewModel = viewModel { DashboardViewModel(app) },
) {
    val s by vm.state.collectAsState()

    Scaffold(
        topBar = {
            ScreenHeader(title = "Announcements", large = true, applyTopInset = false)
        },
        containerColor = MaterialTheme.colorScheme.background,
    ) { padding ->
        if (s.loading) {
            LoadingState("Loading announcements…")
            return@Scaffold
        }
        val list = s.data?.announcements ?: emptyList()

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            if (list.isEmpty()) {
                item {
                    EmptyState(
                        icon = Icons.Filled.Campaign,
                        title = "No announcements",
                        message = "Cooperative announcements and community broadcasts will appear here.",
                    )
                }
            } else {
                item {
                    Text(
                        text = "Latest Updates",
                        fontFamily = Inter,
                        fontSize = 10.sp,
                        letterSpacing = 1.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(start = 4.dp),
                    )
                }

                item {
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = MaterialTheme.colorScheme.surface,
                        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Column {
                            list.forEachIndexed { index, a ->
                                Box(modifier = Modifier.padding(horizontal = 16.dp)) {
                                    ListItem(
                                        title = a.title,
                                        subtitle = a.body,
                                        leadingIcon = Icons.Filled.Campaign,
                                        leadingColor = DomiTheme.colors.primaryBright,
                                        chevron = false,
                                        onClick = { onAnnouncementClick(a.title, a.body, a.createdAt) },
                                        trailing = {
                                            Text(
                                                text = Format.relativeTime(a.createdAt),
                                                fontFamily = Inter,
                                                fontSize = 9.sp,
                                                letterSpacing = 0.5.sp,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                            )
                                        },
                                    )
                                }
                                if (index < list.size - 1) {
                                    HorizontalDivider(
                                        color = MaterialTheme.colorScheme.outlineVariant,
                                        thickness = 1.dp,
                                    )
                                }
                            }
                        }
                    }
                }
            }

            item { Spacer(Modifier.height(32.dp)) }
        }
    }
}

// =========================================================================
// TAB 5: PROFILE
// =========================================================================

@Composable
fun ProfileTab(
    app: AppContainer,
    onEditProfile: () -> Unit,
    onSecurity: () -> Unit,
    onNotifPrefs: () -> Unit,
    onThemePref: () -> Unit,
    onSupport: () -> Unit,
    onGuidelines: () -> Unit,
    onPrivacy: () -> Unit,
    onLoggedOut: () -> Unit,
    vm: ProfileViewModel = viewModel { ProfileViewModel(app) },
) {
    val s by vm.state.collectAsState()
    var showLogoutModal by remember { mutableStateOf(false) }
    var showDeleteModal by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            ScreenHeader(title = "Profile", large = true, applyTopInset = false)
        },
        containerColor = MaterialTheme.colorScheme.background,
    ) { padding ->
        if (s.loading) {
            LoadingState("Loading profile…")
            return@Scaffold
        }
        val profile = s.profile ?: Profile(fullName = "Member")
        val initials = getInitials(profile.fullName)

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp),
        ) {
            // Profile Header Row
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.surfaceContainer)
                            .border(2.dp, DomiTheme.colors.primaryBright, CircleShape),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = initials,
                            fontFamily = PlusJakartaSans,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = DomiTheme.colors.primaryBright,
                        )
                    }

                    Spacer(Modifier.width(16.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = profile.fullName,
                            fontFamily = PlusJakartaSans,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                        Spacer(Modifier.height(4.dp))
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                        ) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(100.dp))
                                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.08f))
                                    .padding(horizontal = 8.dp, vertical = 2.dp),
                            ) {
                                Text(
                                    text = "Member ID: ${profile.memberNo ?: "Pending"}",
                                    fontFamily = Inter,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = MaterialTheme.colorScheme.primary,
                                )
                            }
                            Text(
                                text = "Active",
                                fontFamily = Inter,
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                    }
                }
            }

            // Section 1: Account Settings
            item {
                Column {
                    Text(
                        text = "ACCOUNT SETTINGS",
                        fontFamily = Inter,
                        fontSize = 10.sp,
                        letterSpacing = 1.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(start = 4.dp, bottom = 8.dp),
                    )

                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = MaterialTheme.colorScheme.surface,
                        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Column {
                            ProfileMenuItem(
                                icon = Icons.Filled.Person,
                                title = "Edit Account Details",
                                subtitle = "Update name, email, and phone",
                                onClick = onEditProfile,
                            )
                            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant, thickness = 1.dp)
                            ProfileMenuItem(
                                icon = Icons.Filled.Security,
                                title = "Security Settings",
                                subtitle = "Password management",
                                onClick = onSecurity,
                            )
                            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant, thickness = 1.dp)
                            ProfileMenuItem(
                                icon = Icons.Filled.Notifications,
                                title = "Notification Preferences",
                                subtitle = "Email and push alerts",
                                onClick = onNotifPrefs,
                            )
                            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant, thickness = 1.dp)
                            ProfileMenuItem(
                                icon = Icons.Filled.Palette,
                                title = "Theme Preference",
                                subtitle = "Light, dark or system default",
                                onClick = onThemePref,
                            )
                        }
                    }
                }
            }

            // Section 2: More Options
            item {
                Column {
                    Text(
                        text = "MORE OPTIONS",
                        fontFamily = Inter,
                        fontSize = 10.sp,
                        letterSpacing = 1.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(start = 4.dp, bottom = 8.dp),
                    )

                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = MaterialTheme.colorScheme.surface,
                        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Column {
                            ProfileMenuItem(
                                icon = Icons.Filled.Help,
                                title = "Support & FAQ",
                                subtitle = null,
                                onClick = onSupport,
                            )
                            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant, thickness = 1.dp)
                            ProfileMenuItem(
                                icon = Icons.Filled.Rule,
                                title = "Cooperative Guidelines",
                                subtitle = "Rules and regulations",
                                onClick = onGuidelines,
                            )
                            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant, thickness = 1.dp)
                            ProfileMenuItem(
                                icon = Icons.Filled.Description,
                                title = "Privacy Policy",
                                subtitle = null,
                                onClick = onPrivacy,
                            )
                            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant, thickness = 1.dp)
                            ProfileMenuItem(
                                icon = Icons.Filled.Logout,
                                title = "Log Out",
                                subtitle = null,
                                destructive = true,
                                onClick = { showLogoutModal = true },
                            )
                            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant, thickness = 1.dp)
                            ProfileMenuItem(
                                icon = Icons.Filled.DeleteForever,
                                title = "Delete Account",
                                subtitle = null,
                                destructive = true,
                                onClick = { showDeleteModal = true },
                            )
                        }
                    }
                }
            }

            item { Spacer(Modifier.height(32.dp)) }
        }
    }

    // Logout Modal
    ConfirmationModal(
        visible = showLogoutModal,
        title = "Log Out",
        message = "Are you sure you want to log out? You'll need to sign in again to access your account.",
        confirmText = "Log Out",
        cancelText = "Cancel",
        onConfirm = {
            showLogoutModal = false
            vm.logout(onLoggedOut)
        },
        onCancel = { showLogoutModal = false },
        isDestructive = false,
    )

    // Delete Account Modal
    ConfirmationModal(
        visible = showDeleteModal,
        title = "Delete Account",
        message = "This action cannot be undone. All your data, including loans, savings, and transaction history will be permanently deleted.",
        confirmText = "Delete",
        cancelText = "Cancel",
        onConfirm = {
            showDeleteModal = false
            vm.logout(onLoggedOut)
        },
        onCancel = { showDeleteModal = false },
        isDestructive = true,
    )
}

@Composable
private fun ProfileMenuItem(
    icon: ImageVector,
    title: String,
    subtitle: String?,
    destructive: Boolean = false,
    onClick: () -> Unit,
) {
    val iconBg = if (destructive) {
        MaterialTheme.colorScheme.errorContainer
    } else {
        MaterialTheme.colorScheme.primary.copy(alpha = 0.08f)
    }
    val iconColor = if (destructive) {
        MaterialTheme.colorScheme.error
    } else {
        MaterialTheme.colorScheme.primary
    }
    val textColor = if (destructive) {
        MaterialTheme.colorScheme.error
    } else {
        MaterialTheme.colorScheme.onSurface
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(iconBg),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = iconColor,
                modifier = Modifier.size(24.dp),
            )
        }

        Spacer(Modifier.width(16.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                fontFamily = Inter,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = textColor,
            )
            if (!subtitle.isNullOrBlank()) {
                Text(
                    text = subtitle,
                    fontFamily = Inter,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }

        Icon(
            imageVector = Icons.Filled.ChevronRight,
            contentDescription = null,
            tint = if (destructive) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.outline,
            modifier = Modifier.size(24.dp),
        )
    }
}
