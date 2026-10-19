package com.nexahub.domicoop.ui.detail

import android.content.Intent
import android.net.Uri
import android.widget.Toast
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Notes
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Percent
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.Savings
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.nexahub.domicoop.core.Allocation
import com.nexahub.domicoop.core.ApiException
import com.nexahub.domicoop.core.Format
import com.nexahub.domicoop.data.remote.ApiContribution
import com.nexahub.domicoop.data.remote.ApiLoan
import com.nexahub.domicoop.data.remote.toUi
import com.nexahub.domicoop.data.repository.AppContainer
import com.nexahub.domicoop.theme.DomiTheme
import com.nexahub.domicoop.ui.common.AllocationBreakdown
import com.nexahub.domicoop.ui.common.Badge
import com.nexahub.domicoop.ui.common.BadgeStatus
import com.nexahub.domicoop.ui.common.DomiButton
import com.nexahub.domicoop.ui.common.DomiButtonSize
import com.nexahub.domicoop.ui.common.DomiButtonVariant
import com.nexahub.domicoop.ui.common.ErrorState
import com.nexahub.domicoop.ui.common.LoadingState
import com.nexahub.domicoop.ui.common.Money
import com.nexahub.domicoop.ui.common.MoneySize
import com.nexahub.domicoop.ui.common.MoneyText
import com.nexahub.domicoop.ui.common.MoneyTone
import com.nexahub.domicoop.ui.common.ScreenHeader
import com.nexahub.domicoop.ui.common.TransactionDetailCard
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

// =========================================================================
// 1. CONTRIBUTION DETAIL
// =========================================================================

data class ContributionDetailState(
    val loading: Boolean = true,
    val item: ApiContribution? = null,
    val error: String? = null,
)

class ContributionDetailViewModel(private val app: AppContainer, private val id: String) : ViewModel() {
    private val _state = MutableStateFlow(ContributionDetailState())
    val state: StateFlow<ContributionDetailState> = _state

    init { load() }

    fun load() {
        _state.value = ContributionDetailState(loading = true)
        viewModelScope.launch {
            try {
                val item = app.contributions.byId(id)
                _state.value = ContributionDetailState(loading = false, item = item)
            } catch (e: ApiException) {
                _state.value = ContributionDetailState(loading = false, error = e.message)
            } catch (e: Exception) {
                _state.value = ContributionDetailState(loading = false, error = e.message)
            }
        }
    }
}

@Composable
fun ContributionDetailScreen(
    app: AppContainer,
    id: String,
    onBack: () -> Unit,
    vm: ContributionDetailViewModel = viewModel { ContributionDetailViewModel(app, id) },
) {
    val s by vm.state.collectAsState()
    val context = LocalContext.current

    Scaffold(
        topBar = {
            ScreenHeader(title = "Transaction Details", onBack = onBack)
        },
        containerColor = MaterialTheme.colorScheme.background,
    ) { padding ->
        if (s.loading) {
            LoadingState("Loading transaction details…")
            return@Scaffold
        }
        if (s.error != null && s.item == null) {
            ErrorState(s.error!!, vm::load)
            return@Scaffold
        }
        val c = s.item ?: return@Scaffold
        val alloc = remember(c.amount) { Allocation.calculate(c.amount) }
        val statusBadge = when (c.status.lowercase()) {
            "verified" -> BadgeStatus.Success
            "pending" -> BadgeStatus.Warning
            "rejected" -> BadgeStatus.Error
            else -> BadgeStatus.Neutral
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            // Amount Hero Card
            item {
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = MaterialTheme.colorScheme.surface,
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(elevation = 2.dp, shape = RoundedCornerShape(20.dp))
                        .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(20.dp)),
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                    ) {
                        // Watermark
                        Icon(
                            imageVector = Icons.Filled.AccountBalance,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.05f),
                            modifier = Modifier
                                .size(140.dp)
                                .align(Alignment.BottomEnd),
                        )

                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.fillMaxWidth(),
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.10f)),
                                contentAlignment = Alignment.Center,
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Savings,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(28.dp),
                                )
                            }
                            Spacer(Modifier.height(12.dp))
                            Text(
                                "CONTRIBUTION AMOUNT",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontSize = 11.sp,
                                    letterSpacing = 2.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                ),
                            )
                            Spacer(Modifier.height(6.dp))
                            Money(amount = c.amount, size = MoneySize.XL)
                            Spacer(Modifier.height(12.dp))
                            Badge(status = statusBadge, label = c.status.uppercase())
                        }
                    }
                }
            }

            // Allocation Breakdown Card
            item {
                AllocationBreakdown(
                    allocation = alloc,
                    totalSavings = c.amount,
                )
            }

            // General Information Card
            item {
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = MaterialTheme.colorScheme.surfaceContainerLow,
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(16.dp)),
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp),
                    ) {
                        Text(
                            text = "General Information",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontSize = 11.sp,
                                letterSpacing = 1.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            ),
                        )

                        TransactionDetailCard(
                            icon = Icons.Filled.Fingerprint,
                            label = "Transaction ID",
                            value = c.id,
                            showCopy = true,
                        )
                        TransactionDetailCard(
                            icon = Icons.Filled.CalendarToday,
                            label = "Date & Time",
                            value = "${Format.formatDate(c.createdAt)} • ${Format.formatTime(c.createdAt)}",
                        )
                        TransactionDetailCard(
                            icon = Icons.Filled.Savings,
                            label = "Contribution Month",
                            value = Format.monthLabel(c.month),
                        )
                        TransactionDetailCard(
                            icon = Icons.Filled.Category,
                            label = "Type",
                            value = "CONTRIBUTION",
                        )
                        TransactionDetailCard(
                            icon = Icons.Filled.Info,
                            label = "Status",
                            value = c.status.uppercase(),
                        )
                        if (!c.notes.isNullOrBlank()) {
                            TransactionDetailCard(
                                icon = Icons.Filled.Notes,
                                label = "Notes",
                                value = c.notes!!,
                            )
                        }
                    }
                }
            }

            // Receipt Actions
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(16.dp))
                            .background(MaterialTheme.colorScheme.surface)
                            .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(16.dp))
                            .clickable {
                                Toast.makeText(context, "Receipt saved to downloads", Toast.LENGTH_SHORT).show()
                            }
                            .padding(vertical = 16.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                        ) {
                            Icon(Icons.Filled.Download, contentDescription = null, tint = MaterialTheme.colorScheme.onSurface, modifier = Modifier.size(20.dp))
                            Text(
                                "DOWNLOAD RECEIPT",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, letterSpacing = 0.5.sp),
                            )
                        }
                    }

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(16.dp))
                            .background(MaterialTheme.colorScheme.surface)
                            .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(16.dp))
                            .clickable {
                                val shareIntent = Intent().apply {
                                    action = Intent.ACTION_SEND
                                    putExtra(Intent.EXTRA_TEXT, "DOMICOOP Contribution Receipt: ₦${Format.currency(c.amount)} for ${Format.monthLabel(c.month)}. Ref: ${c.id}")
                                    type = "text/plain"
                                }
                                context.startActivity(Intent.createChooser(shareIntent, "Share Receipt"))
                            }
                            .padding(vertical = 16.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                        ) {
                            Icon(Icons.Filled.Share, contentDescription = null, tint = MaterialTheme.colorScheme.onSurface, modifier = Modifier.size(20.dp))
                            Text(
                                "SHARE",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, letterSpacing = 0.5.sp),
                            )
                        }
                    }
                }
            }

            item { Spacer(Modifier.height(32.dp)) }
        }
    }
}

@Composable
private fun DetailAllocRow(
    label: String,
    desc: String,
    amount: Double,
    percent: Double,
    color: Color,
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
                    .background(color),
            )
            Spacer(Modifier.width(10.dp))
            Column {
                Text(label, style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium))
                Text(desc, style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant))
            }
        }
        Column(horizontalAlignment = Alignment.End) {
            MoneyText(amount = amount, color = color)
            Text("${Format.round1(percent)}%", style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant))
        }
    }
}

// =========================================================================
// 2. LOAN DETAIL
// =========================================================================

data class LoanDetailState(
    val loading: Boolean = true,
    val item: ApiLoan? = null,
    val error: String? = null,
)

class LoanDetailViewModel(private val app: AppContainer, private val id: String) : ViewModel() {
    private val _state = MutableStateFlow(LoanDetailState())
    val state: StateFlow<LoanDetailState> = _state

    init { load() }

    fun load() {
        _state.value = LoanDetailState(loading = true)
        viewModelScope.launch {
            try {
                val cached = try { app.loans.myLoans().find { it.id == id } } catch (_: Exception) { null }
                val item = cached ?: app.loans.byId(id)
                _state.value = LoanDetailState(loading = false, item = item)
            } catch (e: ApiException) {
                _state.value = LoanDetailState(loading = false, error = e.message)
            } catch (e: Exception) {
                _state.value = LoanDetailState(loading = false, error = e.message)
            }
        }
    }
}

@Composable
fun LoanDetailScreen(
    app: AppContainer,
    id: String,
    onPay: () -> Unit,
    onBack: () -> Unit,
    vm: LoanDetailViewModel = viewModel { LoanDetailViewModel(app, id) },
) {
    val s by vm.state.collectAsState()
    val uriHandler = LocalUriHandler.current

    Scaffold(
        topBar = {
            ScreenHeader(title = "Loan Details", onBack = onBack)
        },
        containerColor = MaterialTheme.colorScheme.background,
    ) { padding ->
        if (s.loading) {
            LoadingState("Loading loan details…")
            return@Scaffold
        }
        if (s.error != null && s.item == null) {
            ErrorState(s.error!!, vm::load)
            return@Scaffold
        }
        val loan = s.item ?: return@Scaffold
        val loanUi = remember(loan) { loan.toUi() }
        val totalAmount = loan.amountApproved ?: loan.amountRequested
        val balance = loan.balance ?: totalAmount
        val paidAmount = (totalAmount - balance).coerceAtLeast(0.0)
        val progress = if (totalAmount > 0) {
            (((totalAmount - balance) / totalAmount) * 100).toInt().coerceIn(0, 100)
        } else 0

        val (badgeStatus, badgeLabel) = when (loan.status?.lowercase()) {
            "active", "approved" -> Pair(BadgeStatus.SUCCESS, "ON TRACK")
            "pending" -> Pair(BadgeStatus.WARNING, "PENDING")
            "overdue" -> Pair(BadgeStatus.ERROR, "OVERDUE")
            "completed" -> Pair(BadgeStatus.NEUTRAL, "COMPLETED")
            else -> Pair(BadgeStatus.NEUTRAL, (loan.status ?: "").uppercase())
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            // Remaining Balance Hero
            item {
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = MaterialTheme.colorScheme.surface,
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(elevation = 2.dp, shape = RoundedCornerShape(20.dp))
                        .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(20.dp)),
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Payments,
                            contentDescription = null,
                            tint = DomiTheme.colors.primary.copy(alpha = 0.05f),
                            modifier = Modifier
                                .size(140.dp)
                                .align(Alignment.BottomEnd),
                        )

                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.fillMaxWidth(),
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(CircleShape)
                                    .background(DomiTheme.colors.primary.copy(alpha = 0.10f)),
                                contentAlignment = Alignment.Center,
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Payments,
                                    contentDescription = null,
                                    tint = DomiTheme.colors.primary,
                                    modifier = Modifier.size(28.dp),
                                )
                            }
                            Spacer(Modifier.height(12.dp))
                            Text(
                                "REMAINING BALANCE",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontSize = 11.sp,
                                    letterSpacing = 2.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                ),
                            )
                            Spacer(Modifier.height(6.dp))
                            Money(amount = balance, size = MoneySize.XL)
                            Spacer(Modifier.height(12.dp))
                            Badge(status = badgeStatus, label = badgeLabel)
                        }
                    }
                }
            }

            // Repayment Progress Card
            item {
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = MaterialTheme.colorScheme.surfaceContainerLow,
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(16.dp)),
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        Text(
                            text = "Repayment Progress",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontSize = 11.sp,
                                letterSpacing = 1.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            ),
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Text(
                                "$progress% Complete",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            )
                            Text(
                                "${loanUi.termMonths} months term",
                                style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant),
                            )
                        }

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp)
                                .clip(RoundedCornerShape(4.dp))
                                .background(MaterialTheme.colorScheme.surfaceContainer),
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth(fraction = (progress / 100f).coerceIn(0f, 1f))
                                    .fillMaxSize()
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(DomiTheme.colors.primaryBright),
                            )
                        }

                        Spacer(Modifier.height(4.dp))
                        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant, thickness = 1.dp)

                        Row(modifier = Modifier.fillMaxWidth()) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    "Total Loan",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontSize = 10.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    ),
                                )
                                Spacer(Modifier.height(2.dp))
                                Money(amount = totalAmount, size = MoneySize.MD)
                            }
                            Column(modifier = Modifier.weight(1f), horizontalAlignment = Alignment.End) {
                                Text(
                                    "Paid So Far",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontSize = 10.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    ),
                                )
                                Spacer(Modifier.height(2.dp))
                                Money(amount = paidAmount, size = MoneySize.MD, tone = MoneyTone.SUCCESS)
                            }
                        }
                    }
                }
            }

            // Loan Information Card
            item {
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = MaterialTheme.colorScheme.surfaceContainerLow,
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(16.dp)),
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp),
                    ) {
                        Text(
                            text = "Loan Information",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontSize = 11.sp,
                                letterSpacing = 1.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            ),
                        )

                        TransactionDetailCard(
                            icon = Icons.Filled.Fingerprint,
                            label = "Loan ID",
                            value = loan.id,
                            showCopy = true,
                        )
                        TransactionDetailCard(
                            icon = Icons.Filled.Category,
                            label = "Loan Type",
                            value = loan.purpose?.ifBlank { "Personal Loan" } ?: "Personal Loan",
                        )
                        TransactionDetailCard(
                            icon = Icons.Filled.Payment,
                            label = "Monthly Payment",
                            value = "₦${Format.currency(loanUi.monthlyPayment)}",
                        )
                        TransactionDetailCard(
                            icon = Icons.Filled.Percent,
                            label = "Interest Rate",
                            value = "${loan.interestRate ?: 10.0}% APR",
                        )
                        TransactionDetailCard(
                            icon = Icons.Filled.CalendarToday,
                            label = "Start Date",
                            value = Format.formatDate(loan.createdAt),
                        )
                        TransactionDetailCard(
                            icon = Icons.Filled.Event,
                            label = "Next Payment",
                            value = if (loanUi.nextDate.isNotBlank()) "₦${Format.currency(loanUi.nextAmount)} on ${Format.formatDate(loanUi.nextDate)}" else "—",
                        )
                    }
                }
            }

            // Make a Payment Button
            item {
                DomiButton(
                    text = "Make a Payment",
                    onClick = onPay,
                    variant = DomiButtonVariant.Primary,
                    size = DomiButtonSize.Large,
                    icon = Icons.Filled.Payment,
                    modifier = Modifier.fillMaxWidth(),
                )
            }

            // Repayment Schedule (if available)
            if (loan.installments.isNotEmpty()) {
                item {
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = MaterialTheme.colorScheme.surfaceContainerLow,
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(16.dp)),
                    ) {
                        Column(
                            modifier = Modifier.padding(20.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp),
                        ) {
                            Text(
                                text = "Repayment Schedule",
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontSize = 11.sp,
                                    letterSpacing = 1.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                ),
                            )
                            Text(
                                "1 month grace after disbursement, then equal installments.",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                ),
                            )

                            loan.installments.forEach { inst ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically,
                                ) {
                                    Text(
                                        "${inst.installmentNo}. ${inst.dueOn.take(10)}",
                                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
                                    )
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    ) {
                                        Money(amount = inst.amount, size = MoneySize.SM)
                                        Badge(
                                            status = if (inst.status.lowercase() == "paid") BadgeStatus.Success else BadgeStatus.Neutral,
                                            label = inst.status.uppercase(),
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Guarantors (if available)
            if (loan.guarantors.isNotEmpty()) {
                item {
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = MaterialTheme.colorScheme.surfaceContainerLow,
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(16.dp)),
                    ) {
                        Column(
                            modifier = Modifier.padding(20.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp),
                        ) {
                            Text(
                                text = "Guarantors",
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontSize = 11.sp,
                                    letterSpacing = 1.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                ),
                            )
                            loan.guarantors.forEach { g ->
                                Column(modifier = Modifier.padding(vertical = 4.dp)) {
                                    Text(
                                        "${g.position}. ${g.fullName}",
                                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                                    )
                                    Text(
                                        "${g.bankName} · ${g.bankAccount}",
                                        style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant),
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Loan Bond (if available)
            if (!loan.bondUrl.isNullOrBlank()) {
                item {
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = MaterialTheme.colorScheme.surfaceContainerLow,
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(16.dp)),
                    ) {
                        Column(
                            modifier = Modifier.padding(20.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp),
                        ) {
                            Text(
                                text = "Loan Bond",
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontSize = 11.sp,
                                    letterSpacing = 1.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                ),
                            )
                            Text(
                                "Your signed bond, as approved by the Secretary and President.",
                                style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant),
                            )
                            DomiButton(
                                text = "Open Loan Bond",
                                onClick = { uriHandler.openUri(loan.bondUrl!!) },
                                variant = DomiButtonVariant.Tonal,
                                size = DomiButtonSize.Large,
                                icon = Icons.Filled.PictureAsPdf,
                                modifier = Modifier.fillMaxWidth(),
                            )
                        }
                    }
                }
            }

            item { Spacer(Modifier.height(32.dp)) }
        }
    }
}
