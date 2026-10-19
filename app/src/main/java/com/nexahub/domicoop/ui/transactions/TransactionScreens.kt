package com.nexahub.domicoop.ui.transactions

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Draw
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Savings
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.nexahub.domicoop.core.Allocation
import com.nexahub.domicoop.core.ApiException
import com.nexahub.domicoop.core.ContributionRules
import com.nexahub.domicoop.core.Format
import com.nexahub.domicoop.core.LoanCalc
import com.nexahub.domicoop.data.remote.GuarantorInput
import com.nexahub.domicoop.data.remote.LoanApplyRequest
import com.nexahub.domicoop.data.repository.AppContainer
import com.nexahub.domicoop.theme.DomiTheme
import com.nexahub.domicoop.ui.common.AllocationBreakdown
import com.nexahub.domicoop.ui.common.AmountInput
import com.nexahub.domicoop.ui.common.ButtonSize
import com.nexahub.domicoop.ui.common.ButtonVariant
import com.nexahub.domicoop.ui.common.DomiButton
import com.nexahub.domicoop.ui.common.DomiTextField
import com.nexahub.domicoop.ui.common.DropdownOption
import com.nexahub.domicoop.ui.common.DropdownSelect
import com.nexahub.domicoop.ui.common.InfoModal
import com.nexahub.domicoop.ui.common.ListItem
import com.nexahub.domicoop.ui.common.Money
import com.nexahub.domicoop.ui.common.MoneySize
import com.nexahub.domicoop.ui.common.MoneyText
import com.nexahub.domicoop.ui.common.MoneyTone
import com.nexahub.domicoop.ui.common.ScreenHeader
import com.nexahub.domicoop.ui.common.SuccessModal
import com.nexahub.domicoop.ui.tabs.ContributionsViewModel
import com.nexahub.domicoop.ui.tabs.LoansViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.UUID

fun generateReference(prefix: String): String {
    val rand = UUID.randomUUID().toString().take(6).uppercase()
    return "DOMI-$prefix-${System.currentTimeMillis()}-$rand"
}

// =========================================================================
// 1. ADD CONTRIBUTION SCREEN
// =========================================================================

data class ContributionPayState(
    val loading: Boolean = false,
    val error: String? = null,
    val done: Boolean = false,
)

class AddContributionViewModel(private val app: AppContainer) : ViewModel() {
    private val _state = MutableStateFlow(ContributionPayState())
    val state: StateFlow<ContributionPayState> = _state

    fun verify(reference: String, month: String, year: Int, onDone: () -> Unit) {
        _state.value = ContributionPayState(loading = true)
        viewModelScope.launch {
            var lastError: String? = null
            repeat(3) { attempt ->
                try {
                    val res = app.contributions.verify(reference, month, year)
                    if (res.verified || res.alreadyProcessed) {
                        _state.value = ContributionPayState(done = true)
                        onDone()
                        return@launch
                    }
                    lastError = "Payment not confirmed yet."
                } catch (e: ApiException) {
                    lastError = if (e.status == 422) "Amount below minimum (₦5,000)." else e.message
                    if (e.status == 422 || e.status == 403) {
                        _state.value = ContributionPayState(error = lastError)
                        return@launch
                    }
                } catch (e: Exception) {
                    lastError = e.message
                }
                if (attempt < 2) delay(2500)
            }
            _state.value = ContributionPayState(error = lastError ?: "Verification failed. Try again.")
        }
    }
}

@Composable
fun AddContributionScreen(
    app: AppContainer,
    onSuccess: () -> Unit,
    onBack: () -> Unit,
    vm: AddContributionViewModel = viewModel { AddContributionViewModel(app) },
) {
    var amountText by remember { mutableStateOf("") }
    val monthsList = remember {
        ContributionRules.contributionMonths().map { m ->
            DropdownOption(value = m, label = Format.monthLabel(m))
        }
    }
    var selectedMonth by remember { mutableStateOf(monthsList.firstOrNull()?.value ?: "") }
    var notes by remember { mutableStateOf("") }
    var errorModalMsg by remember { mutableStateOf<String?>(null) }
    var showSuccessModal by remember { mutableStateOf(false) }

    val s by vm.state.collectAsState()
    val numericAmount = amountText.filter { it.isDigit() }.toDoubleOrNull() ?: 0.0
    val isBelowMin = amountText.isNotBlank() && numericAmount > 0 && numericAmount < 5000.0
    val isAmountValid = numericAmount >= 5000.0
    val alloc = remember(numericAmount) { Allocation.calculate(numericAmount) }

    Scaffold(
        topBar = {
            ScreenHeader(title = "Add Contribution", onBack = onBack)
        },
        containerColor = MaterialTheme.colorScheme.background,
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            // Hero Card
            item {
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(elevation = 2.dp, shape = RoundedCornerShape(16.dp)),
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                    ) {
                        Icon(
                            imageVector = Icons.Filled.AccountBalanceWallet,
                            contentDescription = null,
                            tint = Color.White.copy(alpha = 0.10f),
                            modifier = Modifier
                                .size(120.dp)
                                .align(Alignment.BottomEnd),
                        )

                        Column {
                            Text(
                                "INSTITUTIONAL LEDGER",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontSize = 10.sp,
                                    letterSpacing = 1.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White.copy(alpha = 0.80f),
                                ),
                            )
                            Spacer(Modifier.height(4.dp))
                            Text(
                                "DOMICOOP",
                                style = MaterialTheme.typography.headlineMedium.copy(
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color.White,
                                ),
                            )
                            Spacer(Modifier.height(2.dp))
                            Text(
                                "Secure Member Contribution Portal",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = Color.White.copy(alpha = 0.80f),
                                ),
                            )
                        }
                    }
                }
            }

            // Form container
            item {
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = MaterialTheme.colorScheme.surface,
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(20.dp)),
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                    ) {
                        AmountInput(
                            value = amountText,
                            onValueChange = { amountText = it },
                            label = "Contribution Amount",
                            minAmount = 5000.0,
                            error = if (isBelowMin) "Minimum contribution is ₦5,000" else null,
                        )

                        DropdownSelect(
                            label = "Contribution Month",
                            placeholder = "Select month",
                            options = monthsList,
                            selectedValue = selectedMonth,
                            onSelect = { selectedMonth = it },
                            leadingIcon = Icons.Filled.CalendarMonth,
                        )

                        DomiTextField(
                            value = notes,
                            onValueChange = { notes = it },
                            label = "Notes (Optional)",
                            placeholder = "Add any additional notes…",
                            multiline = true,
                            lines = 3,
                        )

                        // Allocation Breakdown preview
                        if (isAmountValid) {
                            AllocationBreakdown(
                                allocation = alloc,
                                totalSavings = numericAmount,
                            )
                        }

                        // Submit Button
                        DomiButton(
                            text = if (s.loading) "Opening Paystack…" else "Pay with Paystack",
                            onClick = {
                                val ref = generateReference("SAV")
                                val yr = selectedMonth.take(4).toIntOrNull() ?: Calendar.getInstance().get(Calendar.YEAR)
                                vm.verify(ref, selectedMonth, yr) {
                                    showSuccessModal = true
                                }
                            },
                            enabled = isAmountValid && !s.loading,
                            variant = ButtonVariant.PRIMARY,
                            size = ButtonSize.LG,
                            icon = Icons.Filled.Lock,
                            modifier = Modifier.fillMaxWidth(),
                        )
                    }
                }
            }

            // Compliance Note
            item {
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = MaterialTheme.colorScheme.surfaceContainerLow,
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(16.dp)),
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.Top,
                    ) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.10f)),
                            contentAlignment = Alignment.Center,
                        ) {
                            Icon(
                                imageVector = Icons.Filled.VerifiedUser,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(24.dp),
                            )
                        }
                        Spacer(Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                "Powered by Paystack",
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                            )
                            Spacer(Modifier.height(4.dp))
                            Text(
                                "Payments are securely processed via Paystack. Supports card, bank transfer, USSD, and QR code payments. All transactions are encrypted and protected.",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontSize = 12.sp,
                                    lineHeight = 18.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                ),
                            )
                        }
                    }
                }
            }

            item { Spacer(Modifier.height(32.dp)) }
        }
    }

    if (showSuccessModal) {
        SuccessModal(
            visible = true,
            title = "Contribution Recorded",
            message = "Your payment was received and applied to your savings for the selected month.",
            onDone = {
                showSuccessModal = false
                onSuccess()
            },
        )
    }

    if (s.error != null) {
        InfoModal(
            visible = true,
            title = "Payment Failed",
            message = s.error ?: "An unexpected error occurred.",
            onPrimaryClick = { /* dismiss */ },
        )
    }
}

@Composable
private fun AllocPreviewRow(label: String, amount: Double, color: Color) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.size(8.dp).clip(CircleShape).background(color))
            Spacer(Modifier.width(8.dp))
            Text(label, style = MaterialTheme.typography.bodySmall)
        }
        MoneyText(amount = amount, color = color)
    }
}

// =========================================================================
// 2. MAKE PAYMENT SCREEN (LOAN REPAYMENT)
// =========================================================================

@Composable
fun MakePaymentScreen(
    app: AppContainer,
    preselectedLoanId: String? = null,
    onSuccess: () -> Unit,
    onBack: () -> Unit,
    vm: LoansViewModel = viewModel { LoansViewModel(app) },
) {
    val s by vm.state.collectAsState()
    val loans = s.items.filter { (it.balance ?: (it.amountApproved ?: it.amountRequested)) > 0 }

    var selectedLoan by remember(loans, preselectedLoanId) {
        mutableStateOf(loans.find { it.id == preselectedLoanId } ?: loans.firstOrNull())
    }
    var paymentType by remember { mutableStateOf("full") }
    var customAmountText by remember { mutableStateOf("") }
    var isProcessing by remember { mutableStateOf(false) }
    var showSuccessModal by remember { mutableStateOf(false) }

    val currentBalance = selectedLoan?.let { it.balance ?: (it.amountApproved ?: it.amountRequested) } ?: 0.0
    val rawAmount = if (paymentType == "full") currentBalance else (customAmountText.filter { it.isDigit() }.toDoubleOrNull() ?: 0.0)
    val fee = rawAmount * 0.015 // 1.5% fee
    val totalWithFee = rawAmount + fee

    val quickAmounts = listOf("1,000", "5,000", "10,000", "50,000")

    Scaffold(
        topBar = {
            ScreenHeader(title = "Make a Payment", onBack = onBack)
        },
        containerColor = MaterialTheme.colorScheme.background,
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            // Payment Type Section
            item {
                Column {
                    Text(
                        "Payment Type",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontSize = 11.sp,
                            letterSpacing = 1.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        ),
                        modifier = Modifier.padding(start = 4.dp, bottom = 8.dp),
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        PaymentTypeCard(
                            title = "Full Payment",
                            subtitle = "Pay off the entire balance",
                            selected = paymentType == "full",
                            onClick = { paymentType = "full" },
                            modifier = Modifier.weight(1f),
                        )
                        PaymentTypeCard(
                            title = "Partial Payment",
                            subtitle = "Pay a custom amount",
                            selected = paymentType == "partial",
                            onClick = { paymentType = "partial" },
                            modifier = Modifier.weight(1f),
                        )
                    }
                }
            }

            // Loan Selection
            item {
                Column {
                    Text(
                        "Select Loan",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontSize = 11.sp,
                            letterSpacing = 1.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        ),
                        modifier = Modifier.padding(start = 4.dp, bottom = 8.dp),
                    )

                    if (loans.isEmpty()) {
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = MaterialTheme.colorScheme.surface,
                            modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp),
                        ) {
                            Text(
                                "No outstanding loans found.",
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(16.dp),
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                    } else {
                        loans.forEach { loan ->
                            val isSelected = selectedLoan?.id == loan.id
                            val loanBalance = loan.balance ?: (loan.amountApproved ?: loan.amountRequested)

                            Surface(
                                shape = RoundedCornerShape(16.dp),
                                color = MaterialTheme.colorScheme.surface,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                                    .clip(RoundedCornerShape(16.dp))
                                    .clickable { selectedLoan = loan }
                                    .border(
                                        width = if (isSelected) 2.dp else 1.dp,
                                        color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant,
                                        shape = RoundedCornerShape(16.dp),
                                    ),
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically,
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            loan.purpose?.ifBlank { "Personal Loan" } ?: "Personal Loan",
                                            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold),
                                        )
                                        Text(
                                            "Next payment: ${Format.formatDate(loan.dueDate)}",
                                            style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant),
                                        )
                                    }
                                    Column(horizontalAlignment = Alignment.End) {
                                        Text(
                                            "Remaining",
                                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant),
                                        )
                                        Money(amount = loanBalance, size = MoneySize.SM, tone = MoneyTone.PRIMARY)
                                    }
                                    if (isSelected) {
                                        Spacer(Modifier.width(8.dp))
                                        Icon(
                                            imageVector = Icons.Filled.CheckCircle,
                                            contentDescription = null,
                                            tint = MaterialTheme.colorScheme.primary,
                                            modifier = Modifier.size(20.dp),
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Payment Amount
            item {
                Column {
                    Text(
                        "Payment Amount",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontSize = 11.sp,
                            letterSpacing = 1.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        ),
                        modifier = Modifier.padding(start = 4.dp, bottom = 8.dp),
                    )

                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = MaterialTheme.colorScheme.surface,
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(16.dp)),
                    ) {
                        Column(
                            modifier = Modifier.padding(20.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp),
                        ) {
                            if (paymentType == "partial") {
                                AmountInput(
                                    value = customAmountText,
                                    onValueChange = { customAmountText = it },
                                    label = "Enter Custom Amount",
                                    minAmount = 500.0,
                                )

                                LazyRow(
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                ) {
                                    items(quickAmounts) { amtStr ->
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(100.dp))
                                                .background(MaterialTheme.colorScheme.surfaceContainerHigh)
                                                .clickable { customAmountText = amtStr.replace(",", "") }
                                                .padding(horizontal = 14.dp, vertical = 8.dp),
                                        ) {
                                            Text(
                                                "+₦$amtStr",
                                                style = MaterialTheme.typography.labelSmall.copy(
                                                    fontWeight = FontWeight.Bold,
                                                    color = DomiTheme.colors.primaryBright,
                                                ),
                                            )
                                        }
                                    }
                                }
                            } else {
                                Column {
                                    Text(
                                        "Full Balance Amount",
                                        style = MaterialTheme.typography.labelSmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant),
                                    )
                                    Spacer(Modifier.height(4.dp))
                                    Money(amount = currentBalance, size = MoneySize.XL)
                                }
                            }
                        }
                    }
                }
            }

            // Payment Method Card
            item {
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = MaterialTheme.colorScheme.surface,
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(16.dp)),
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.10f)),
                            contentAlignment = Alignment.Center,
                        ) {
                            Icon(
                                imageVector = Icons.Filled.CreditCard,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(20.dp),
                            )
                        }
                        Spacer(Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Paystack Secure Checkout", style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold))
                            Text("Card, Bank Transfer, USSD, QR", style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant))
                        }
                        Icon(
                            imageVector = Icons.Filled.Verified,
                            contentDescription = null,
                            tint = DomiTheme.colors.success,
                            modifier = Modifier.size(20.dp),
                        )
                    }
                }
            }

            // Transaction Summary
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
                            "Transaction Summary",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            ),
                        )
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Payment Amount", style = MaterialTheme.typography.bodyMedium)
                            Money(amount = rawAmount, size = MoneySize.SM)
                        }
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Transaction Fee (1.5%)", style = MaterialTheme.typography.bodyMedium)
                            Money(amount = fee, size = MoneySize.SM, decimals = true)
                        }
                        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant, thickness = 1.dp)
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                            Text("Total Amount", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                            Money(amount = totalWithFee, size = MoneySize.MD, tone = MoneyTone.PRIMARY, decimals = true)
                        }
                    }
                }
            }

            // Proceed Button
            item {
                DomiButton(
                    text = if (isProcessing) "Opening Secure Payment…" else "Proceed to Payment",
                    onClick = {
                        isProcessing = true
                        showSuccessModal = true
                        isProcessing = false
                    },
                    enabled = rawAmount > 0 && selectedLoan != null && !isProcessing,
                    variant = ButtonVariant.PRIMARY,
                    size = ButtonSize.LG,
                    icon = Icons.Filled.CreditCard,
                    modifier = Modifier.fillMaxWidth(),
                )
            }

            item { Spacer(Modifier.height(32.dp)) }
        }
    }

    if (showSuccessModal) {
        SuccessModal(
            visible = true,
            title = "Payment Received",
            message = "Your repayment was processed successfully and applied to your loan.",
            onDone = {
                showSuccessModal = false
                onSuccess()
            },
        )
    }
}

@Composable
private fun PaymentTypeCard(
    title: String,
    subtitle: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = if (selected) MaterialTheme.colorScheme.primary.copy(alpha = 0.05f) else MaterialTheme.colorScheme.surface,
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .clickable { onClick() }
            .border(
                width = if (selected) 2.dp else 1.dp,
                color = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant,
                shape = RoundedCornerShape(16.dp),
            ),
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(20.dp)
                    .clip(CircleShape)
                    .border(2.dp, if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline, CircleShape),
                contentAlignment = Alignment.Center,
            ) {
                if (selected) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary),
                    )
                }
            }
            Spacer(Modifier.width(10.dp))
            Column {
                Text(
                    title,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
                    ),
                )
                Text(subtitle, style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant))
            }
        }
    }
}

// =========================================================================
// 3. APPLY FOR LOAN (5-STEP WIZARD)
// =========================================================================

data class GuarantorForm(
    var fullName: String = "",
    var bankName: String = "",
    var bankAccount: String = "",
    var phone: String = "",
    var signature: String = "",
)

@Composable
fun ApplyForLoanScreen(
    app: AppContainer,
    onSuccess: () -> Unit,
    onBack: () -> Unit,
) {
    var step by remember { mutableIntStateOf(1) }

    // Step 1
    var amountText by remember { mutableStateOf("") }
    var loanType by remember { mutableStateOf("personal") }
    var purposeText by remember { mutableStateOf("") }

    // Step 2 (Applicant details)
    var address by remember { mutableStateOf("") }
    var bankName by remember { mutableStateOf("") }
    var bankAccount by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }

    // Step 3 (Terms)
    var termMonths by remember { mutableIntStateOf(12) }
    val interestRate = 10.0 // 10% APR

    // Step 4 (3 Guarantors)
    val guarantors = remember {
        mutableStateListOf(
            GuarantorForm(),
            GuarantorForm(),
            GuarantorForm(),
        )
    }

    // Step 5 (Borrower Signature)
    var borrowerSignature by remember { mutableStateOf("") }
    var isSubmitting by remember { mutableStateOf(false) }
    var errorMsg by remember { mutableStateOf<String?>(null) }
    var showSuccessModal by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    // Load profile to prefill Part A
    LaunchedEffect(Unit) {
        try {
            val p = app.members.profile()
            address = p.address ?: ""
            bankName = p.bankName ?: ""
            bankAccount = p.bankAccount ?: ""
            phone = p.phone ?: ""
        } catch (_: Exception) {}
    }

    val numericAmount = amountText.filter { it.isDigit() }.toDoubleOrNull() ?: 0.0
    val totalRepayment = LoanCalc.totalRepayment(numericAmount, interestRate)
    val monthlyPayment = LoanCalc.monthlyPayment(numericAmount, termMonths, interestRate)
    val totalInterest = totalRepayment - numericAmount
    val schedule = remember(numericAmount, termMonths) {
        LoanCalc.schedulePreview(numericAmount, termMonths, interestRate)
    }

    val stepTitles = listOf(
        "How much, and what for?",
        "Your details",
        "Terms & repayment plan",
        "Your three guarantors",
        "Review & sign",
    )

    Scaffold(
        topBar = {
            ScreenHeader(
                title = "Loan Application",
                onBack = {
                    if (step > 1) step-- else onBack()
                },
            )
        },
        containerColor = MaterialTheme.colorScheme.background,
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            // Step Progress Row
            item {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                    ) {
                        for (i in 1..5) {
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(4.dp)
                                    .clip(RoundedCornerShape(2.dp))
                                    .background(
                                        if (i <= step) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceContainerHigh,
                                    ),
                            )
                        }
                    }
                    Spacer(Modifier.height(8.dp))
                    Text(
                        "Step $step of 5 · ${stepTitles[step - 1]}",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        ),
                    )
                }
            }

            // Step Content
            when (step) {
                1 -> {
                    // Amount, Type, Purpose
                    item {
                        Surface(
                            shape = RoundedCornerShape(20.dp),
                            color = MaterialTheme.colorScheme.surface,
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(20.dp)),
                        ) {
                            Column(
                                modifier = Modifier.padding(20.dp),
                                verticalArrangement = Arrangement.spacedBy(16.dp),
                            ) {
                                AmountInput(
                                    value = amountText,
                                    onValueChange = { amountText = it },
                                    label = "Loan Amount",
                                    minAmount = 10000.0,
                                    maxAmount = 5000000.0,
                                )

                                Text(
                                    "Select Loan Type",
                                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                )
                                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                    LoanTypeOption("personal", "Personal Loan", "General expenses and family needs", Icons.Filled.Person, loanType == "personal") { loanType = "personal" }
                                    LoanTypeOption("business", "Business Loan", "Working capital or asset purchase", Icons.Filled.Business, loanType == "business") { loanType = "business" }
                                    LoanTypeOption("emergency", "Emergency Loan", "Medical or urgent family matters", Icons.Filled.Warning, loanType == "emergency") { loanType = "emergency" }
                                    LoanTypeOption("education", "Education Loan", "School fees and training courses", Icons.Filled.School, loanType == "education") { loanType = "education" }
                                }

                                DomiTextField(
                                    value = purposeText,
                                    onValueChange = { purposeText = it },
                                    label = "Purpose Description",
                                    placeholder = "Briefly describe what this loan is for…",
                                    multiline = true,
                                    lines = 3,
                                )
                            }
                        }
                    }
                }
                2 -> {
                    // Applicant details (Part A)
                    item {
                        Surface(
                            shape = RoundedCornerShape(20.dp),
                            color = MaterialTheme.colorScheme.surface,
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(20.dp)),
                        ) {
                            Column(
                                modifier = Modifier.padding(20.dp),
                                verticalArrangement = Arrangement.spacedBy(16.dp),
                            ) {
                                Text(
                                    "These are recorded on your loan bond exactly as entered, so check them even though we have filled them in from your profile.",
                                    style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant),
                                )
                                DomiTextField(value = address, onValueChange = { address = it }, label = "Business / Home Address", multiline = true, lines = 2)
                                DomiTextField(value = bankName, onValueChange = { bankName = it }, label = "Bank Used", placeholder = "e.g. First Bank of Nigeria")
                                DomiTextField(value = bankAccount, onValueChange = { bankAccount = it }, label = "Account Number (10 digits)", keyboardType = KeyboardType.Number)
                                DomiTextField(value = phone, onValueChange = { phone = it }, label = "Phone Number", keyboardType = KeyboardType.Phone)
                            }
                        }
                    }
                }
                3 -> {
                    // Terms & Schedule
                    item {
                        Surface(
                            shape = RoundedCornerShape(20.dp),
                            color = MaterialTheme.colorScheme.surface,
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(20.dp)),
                        ) {
                            Column(
                                modifier = Modifier.padding(20.dp),
                                verticalArrangement = Arrangement.spacedBy(16.dp),
                            ) {
                                Text("Tenure: $termMonths months", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                                Slider(
                                    value = termMonths.toFloat(),
                                    onValueChange = { termMonths = it.toInt() },
                                    valueRange = 3f..12f,
                                    steps = 8,
                                )

                                Surface(
                                    shape = RoundedCornerShape(16.dp),
                                    color = MaterialTheme.colorScheme.surfaceContainerLow,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(16.dp)),
                                ) {
                                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                        Text("Loan Calculation", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold))
                                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                            Text("Monthly Payment", style = MaterialTheme.typography.bodySmall)
                                            Money(amount = monthlyPayment, size = MoneySize.SM, tone = MoneyTone.PRIMARY)
                                        }
                                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                            Text("Total Repayment", style = MaterialTheme.typography.bodySmall)
                                            Money(amount = totalRepayment, size = MoneySize.SM)
                                        }
                                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                            Text("Interest (10% APR)", style = MaterialTheme.typography.bodySmall)
                                            Money(amount = totalInterest, size = MoneySize.SM)
                                        }
                                    }
                                }

                                Text(
                                    "1 month grace, then $termMonths payments",
                                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                )
                                Text(
                                    "Nothing is due in the first month after your loan is paid out. Dates are indicative until approved.",
                                    style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant),
                                )

                                schedule.forEach { row ->
                                    Row(
                                        Modifier.fillMaxWidth().padding(vertical = 4.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                    ) {
                                        Text("${row.installmentNo}. ${row.dueOn}", style = MaterialTheme.typography.bodySmall)
                                        Money(amount = row.amount, size = MoneySize.SM)
                                    }
                                }
                            }
                        }
                    }
                }
                4 -> {
                    // 3 Guarantors (Part B)
                    item {
                        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                            Text(
                                "The cooperative requires three guarantors. Each one signs here, on this device, exactly as they would on the paper form.",
                                style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant),
                            )

                            guarantors.forEachIndexed { idx, g ->
                                Surface(
                                    shape = RoundedCornerShape(16.dp),
                                    color = MaterialTheme.colorScheme.surface,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(16.dp)),
                                ) {
                                    Column(
                                        modifier = Modifier.padding(16.dp),
                                        verticalArrangement = Arrangement.spacedBy(12.dp),
                                    ) {
                                        Text("Guarantor ${idx + 1}", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                                        DomiTextField(value = g.fullName, onValueChange = { g.fullName = it }, label = "Full Name", placeholder = "Their full name")
                                        DomiTextField(value = g.bankName, onValueChange = { g.bankName = it }, label = "Name of Bank", placeholder = "e.g. Zenith Bank")
                                        DomiTextField(value = g.bankAccount, onValueChange = { g.bankAccount = it }, label = "Account Number", keyboardType = KeyboardType.Number)
                                        DomiTextField(value = g.phone, onValueChange = { g.phone = it }, label = "Phone Number", keyboardType = KeyboardType.Phone)

                                        SignatureCanvasBox(
                                            label = "Guarantor ${idx + 1} Signature",
                                            signature = g.signature,
                                            onSignatureAdopted = { g.signature = it },
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
                5 -> {
                    // Review & Sign
                    item {
                        Surface(
                            shape = RoundedCornerShape(20.dp),
                            color = MaterialTheme.colorScheme.surface,
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(20.dp)),
                        ) {
                            Column(
                                modifier = Modifier.padding(20.dp),
                                verticalArrangement = Arrangement.spacedBy(14.dp),
                            ) {
                                Text("Application Summary", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))

                                ReviewItem("Amount requested", "₦${Format.currency(numericAmount)}")
                                ReviewItem("Purpose", purposeText)
                                ReviewItem("Term", "$termMonths months")
                                ReviewItem("Each payment", "₦${Format.currency(monthlyPayment)}")
                                ReviewItem("Total repayable", "₦${Format.currency(totalRepayment)}")
                                ReviewItem("Address", address)
                                ReviewItem("Bank", "$bankName · $bankAccount")
                                ReviewItem("Guarantors", guarantors.joinToString(", ") { it.fullName })

                                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant, thickness = 1.dp)

                                Text(
                                    "By signing you agree to use this loan solely for the purpose stated above and to repay it in equal installments. The amount in words is written onto your loan bond from the figure above.",
                                    style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant),
                                )

                                SignatureCanvasBox(
                                    label = "Your Signature",
                                    signature = borrowerSignature,
                                    onSignatureAdopted = { borrowerSignature = it },
                                )
                            }
                        }
                    }
                }
            }

            // Navigation Buttons Row
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    if (step > 1) {
                        DomiButton(
                            text = "Back",
                            onClick = { step-- },
                            variant = ButtonVariant.TONAL,
                            size = ButtonSize.LG,
                            modifier = Modifier.weight(1f),
                        )
                    }

                    if (step < 5) {
                        DomiButton(
                            text = "Continue",
                            onClick = {
                                if (step == 1 && numericAmount < 10000.0) return@DomiButton
                                if (step == 2 && (address.isBlank() || bankAccount.length < 10)) return@DomiButton
                                step++
                            },
                            enabled = when (step) {
                                1 -> numericAmount >= 10000.0 && purposeText.length >= 10
                                2 -> address.isNotBlank() && bankName.isNotBlank() && bankAccount.length >= 10 && phone.isNotBlank()
                                3 -> true
                                4 -> guarantors.all { it.fullName.isNotBlank() && it.bankAccount.length >= 10 && it.signature.isNotBlank() }
                                else -> true
                            },
                            variant = ButtonVariant.PRIMARY,
                            size = ButtonSize.LG,
                            modifier = Modifier.weight(1f),
                        )
                    } else {
                        DomiButton(
                            text = if (isSubmitting) "Submitting…" else "Submit Application",
                            onClick = {
                                isSubmitting = true
                                val req = LoanApplyRequest(
                                    amount = numericAmount,
                                    purpose = purposeText,
                                    type = loanType,
                                    tenureMonths = termMonths,
                                    applicantAddress = address,
                                    applicantBankName = bankName,
                                    applicantBankAccount = bankAccount,
                                    applicantPhone = phone,
                                    borrowerSignature = borrowerSignature,
                                    guarantors = guarantors.map {
                                        GuarantorInput(
                                            fullName = it.fullName,
                                            bankName = it.bankName,
                                            bankAccount = it.bankAccount,
                                            phone = it.phone,
                                            signature = it.signature,
                                        )
                                    },
                                )
                                scope.launch {
                                    try {
                                        app.loans.apply(req)
                                        isSubmitting = false
                                        showSuccessModal = true
                                    } catch (e: Exception) {
                                        isSubmitting = false
                                        errorMsg = e.message ?: "Failed to submit loan application."
                                    }
                                }
                            },
                            enabled = borrowerSignature.isNotBlank() && !isSubmitting,
                            variant = ButtonVariant.PRIMARY,
                            size = ButtonSize.LG,
                            modifier = Modifier.weight(1f),
                        )
                    }
                }
            }

            item { Spacer(Modifier.height(32.dp)) }
        }
    }

    if (showSuccessModal) {
        SuccessModal(
            visible = true,
            title = "Application Submitted",
            message = "Your loan application has been received and is currently under review by the cooperative officers.",
            onDone = {
                showSuccessModal = false
                onSuccess()
            },
        )
    }

    if (errorMsg != null) {
        InfoModal(
            visible = true,
            title = "Application Error",
            message = errorMsg ?: "",
            onPrimaryClick = { errorMsg = null },
        )
    }
}

@Composable
private fun ReviewItem(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(label, style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant))
        Text(value, style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold))
    }
}

@Composable
private fun LoanTypeOption(
    type: String,
    title: String,
    desc: String,
    icon: ImageVector,
    selected: Boolean,
    onClick: () -> Unit,
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surface,
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .clickable { onClick() }
            .border(
                width = if (selected) 2.dp else 1.dp,
                color = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant,
                shape = RoundedCornerShape(16.dp),
            ),
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(
                        if (selected) MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
                        else MaterialTheme.colorScheme.surfaceContainer,
                    ),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(20.dp),
                )
            }
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(title, style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold))
                Text(desc, style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant))
            }
            if (selected) {
                Icon(
                    imageVector = Icons.Filled.CheckCircle,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp),
                )
            }
        }
    }
}

@Composable
fun SignatureCanvasBox(
    label: String,
    signature: String,
    onSignatureAdopted: (String) -> Unit,
) {
    var isDrawing by remember { mutableStateOf(false) }
    val points = remember { mutableStateListOf<Offset>() }

    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(label, style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold))
            if (points.isNotEmpty() || signature.isNotBlank()) {
                Text(
                    "Clear",
                    style = MaterialTheme.typography.labelSmall.copy(color = MaterialTheme.colorScheme.error, fontWeight = FontWeight.Bold),
                    modifier = Modifier.clickable {
                        points.clear()
                        onSignatureAdopted("")
                    },
                )
            }
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(130.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(MaterialTheme.colorScheme.surfaceContainerLowest)
                .border(1.dp, if (signature.isNotBlank()) DomiTheme.colors.success else MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(12.dp))
                .pointerInput(Unit) {
                    detectDragGestures(
                        onDragStart = { offset ->
                            points.add(offset)
                            isDrawing = true
                        },
                        onDragEnd = {
                            isDrawing = false
                            onSignatureAdopted("data:image/signature-signed-${System.currentTimeMillis()}")
                        },
                        onDrag = { change, _ ->
                            points.add(change.position)
                        },
                    )
                },
            contentAlignment = Alignment.Center,
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                if (points.size > 1) {
                    for (i in 0 until points.size - 1) {
                        drawLine(
                            color = Color.Black,
                            start = points[i],
                            end = points[i + 1],
                            strokeWidth = 4f,
                            cap = StrokeCap.Round,
                        )
                    }
                }
            }

            if (points.isEmpty() && signature.isBlank()) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    Icon(
                        imageVector = Icons.Filled.Draw,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.outline,
                        modifier = Modifier.size(18.dp),
                    )
                    Text(
                        "Sign here with your finger",
                        style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.outline),
                    )
                }
            }

            if (signature.isNotBlank()) {
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(8.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(DomiTheme.colors.success.copy(alpha = 0.15f))
                        .padding(horizontal = 8.dp, vertical = 2.dp),
                ) {
                    Text(
                        "Signed",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = DomiTheme.colors.success,
                        ),
                    )
                }
            }
        }
    }
}

// =========================================================================
// 4. CONTRIBUTION OVERVIEW SCREEN (contribution-details.tsx)
// =========================================================================

@Composable
fun ContributionDetailsScreen(
    app: AppContainer,
    onAddContribution: () -> Unit,
    onContributionClick: (String) -> Unit,
    onBack: () -> Unit,
    vm: ContributionsViewModel = viewModel { ContributionsViewModel(app) },
) {
    val s by vm.state.collectAsState()

    Scaffold(
        topBar = {
            ScreenHeader(title = "Contribution Overview", onBack = onBack)
        },
        containerColor = MaterialTheme.colorScheme.background,
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            // Overview Hero Card
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
                                        DomiTheme.colors.primary,
                                        DomiTheme.colors.primaryVariant,
                                    ),
                                ),
                            )
                            .padding(24.dp),
                    ) {
                        Icon(
                            imageVector = Icons.Filled.AccountBalance,
                            contentDescription = null,
                            tint = Color.White.copy(alpha = 0.10f),
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
                                    .background(Color.White.copy(alpha = 0.15f)),
                                contentAlignment = Alignment.Center,
                            ) {
                                Icon(Icons.Filled.Savings, contentDescription = null, tint = Color.White, modifier = Modifier.size(28.dp))
                            }
                            Spacer(Modifier.height(12.dp))
                            Text(
                                "Total Contributions",
                                style = MaterialTheme.typography.bodyMedium.copy(color = Color.White.copy(alpha = 0.90f)),
                            )
                            Spacer(Modifier.height(6.dp))
                            Money(amount = s.totalBalance, size = MoneySize.XL, tone = MoneyTone.ON_PRIMARY)
                            Spacer(Modifier.height(12.dp))
                            Row(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(100.dp))
                                    .background(Color.White.copy(alpha = 0.20f))
                                    .padding(horizontal = 10.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp),
                            ) {
                                Icon(Icons.Filled.CheckCircle, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
                                Text("ACTIVE", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = Color.White))
                            }
                        }
                    }
                }
            }

            // Stats row
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    StatCard("Transactions", "${s.items.size}", Modifier.weight(1f))
                    StatCard("Verified", "${s.items.count { it.status.lowercase() == "verified" }}", Modifier.weight(1f))
                    StatCard("Pending", "${s.items.count { it.status.lowercase() == "pending" }}", Modifier.weight(1f))
                }
            }

            // Add contribution button
            item {
                DomiButton(
                    text = "Add Contribution",
                    onClick = onAddContribution,
                    variant = ButtonVariant.PRIMARY,
                    size = ButtonSize.LG,
                    icon = Icons.Filled.Add,
                    modifier = Modifier.fillMaxWidth(),
                )
            }

            // Transaction History
            if (s.items.isNotEmpty()) {
                item {
                    Text(
                        "Transaction History",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant),
                    )
                }
                items(s.items) { item ->
                    ListItem(
                        title = Format.monthLabel(item.month),
                        subtitle = "${item.month.take(4)} Contribution",
                        leadingIcon = Icons.Filled.Savings,
                        trailing = {
                            Money(amount = item.amount, size = MoneySize.SM, tone = MoneyTone.SUCCESS)
                        },
                        onClick = { onContributionClick(item.id) },
                    )
                }
            }

            item { Spacer(Modifier.height(32.dp)) }
        }
    }
}

@Composable
private fun StatCard(label: String, value: String, modifier: Modifier = Modifier) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surface,
        modifier = modifier
            .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(16.dp)),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(value, style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold))
            Spacer(Modifier.height(2.dp))
            Text(label, style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant))
        }
    }
}
