package com.nexahub.domicoop.ui.more

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
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
import androidx.compose.foundation.layout.displayCutoutPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.SupportAgent
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.nexahub.domicoop.core.Format
import com.nexahub.domicoop.data.remote.ApiNotification
import com.nexahub.domicoop.data.remote.NotificationPreferencesDto
import com.nexahub.domicoop.data.remote.UpdateProfileRequest
import com.nexahub.domicoop.data.repository.AppContainer
import com.nexahub.domicoop.theme.DomiTheme
import com.nexahub.domicoop.theme.ThemePreference
import com.nexahub.domicoop.ui.common.BackButton
import com.nexahub.domicoop.ui.common.DomiButton
import com.nexahub.domicoop.ui.common.DomiButtonSize
import com.nexahub.domicoop.ui.common.DomiButtonVariant
import com.nexahub.domicoop.ui.common.DomiTextField
import com.nexahub.domicoop.ui.common.DropdownOption
import com.nexahub.domicoop.ui.common.DropdownSelect
import com.nexahub.domicoop.ui.common.EmptyState
import com.nexahub.domicoop.ui.common.ErrorState
import com.nexahub.domicoop.ui.common.InfoModal
import com.nexahub.domicoop.ui.common.ListItem
import com.nexahub.domicoop.ui.common.LoadingState
import com.nexahub.domicoop.ui.common.Money
import com.nexahub.domicoop.ui.common.MoneySize
import com.nexahub.domicoop.ui.common.MoneyText
import com.nexahub.domicoop.ui.common.ScreenHeader
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

// =========================================================================
// 1. NOTIFICATIONS SCREEN
// =========================================================================

data class NotifUiState(
    val loading: Boolean = true,
    val items: List<ApiNotification> = emptyList(),
    val unread: Int = 0,
    val error: String? = null,
)

class NotificationsViewModel(private val app: AppContainer) : ViewModel() {
    private val _state = MutableStateFlow(NotifUiState())
    val state: StateFlow<NotifUiState> = _state

    init { refresh() }

    fun refresh() {
        _state.value = _state.value.copy(loading = true, error = null)
        viewModelScope.launch {
            try {
                val p = app.notifications.page(1, 20)
                _state.value = NotifUiState(loading = false, items = p.notifications, unread = p.meta.unreadCount)
            } catch (e: Exception) {
                _state.value = NotifUiState(loading = false, error = e.message)
            }
        }
    }

    fun markRead(id: String) {
        viewModelScope.launch {
            try {
                app.notifications.markRead(id)
                _state.value = _state.value.copy(
                    items = _state.value.items.map { if (it.id == id) it.copy(read = true) else it },
                    unread = maxOf(0, _state.value.unread - 1),
                )
            } catch (_: Exception) {}
        }
    }

    fun markAllRead() {
        viewModelScope.launch {
            try {
                app.notifications.markAllRead()
                _state.value = _state.value.copy(
                    items = _state.value.items.map { it.copy(read = true) },
                    unread = 0,
                )
            } catch (_: Exception) {}
        }
    }

    fun clearAll() {
        viewModelScope.launch {
            try {
                app.notifications.clearAll()
                _state.value = _state.value.copy(items = emptyList(), unread = 0)
            } catch (_: Exception) {}
        }
    }
}

@Composable
fun NotificationsScreen(
    app: AppContainer,
    onBack: () -> Unit,
    vm: NotificationsViewModel = viewModel { NotificationsViewModel(app) },
) {
    val s by vm.state.collectAsState()
    var filterType by remember { mutableStateOf("all") }

    val filterChips = listOf(
        "all" to "All",
        "loan" to "Loans",
        "contribution" to "Contributions",
        "announcement" to "Announcements",
        "security" to "Security",
    )

    val filteredItems = remember(s.items, filterType) {
        if (filterType == "all") s.items else s.items.filter { it.type.lowercase() == filterType }
    }

    Scaffold(
        topBar = {
            ScreenHeader(title = "Notifications", onBack = onBack)
        },
        containerColor = MaterialTheme.colorScheme.background,
    ) { padding ->
        if (s.loading) {
            LoadingState("Loading alerts…")
            return@Scaffold
        }
        if (s.error != null && s.items.isEmpty()) {
            ErrorState(s.error!!, vm::refresh)
            return@Scaffold
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            // Summary Cards
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    NotifStatCard(
                        count = s.unread,
                        label = "Unread Alerts",
                        icon = Icons.Filled.NotificationsActive,
                        tint = DomiTheme.colors.primaryBright,
                        modifier = Modifier.weight(1f),
                    )
                    NotifStatCard(
                        count = s.items.size,
                        label = "Total Alerts",
                        icon = Icons.Filled.History,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.weight(1f),
                    )
                }
            }

            // Filter Chips
            item {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    items(filterChips) { (key, label) ->
                        val isSelected = filterType == key
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(100.dp))
                                .background(
                                    if (isSelected) MaterialTheme.colorScheme.primary
                                    else MaterialTheme.colorScheme.surfaceContainer,
                                )
                                .clickable { filterType = key }
                                .padding(horizontal = 14.dp, vertical = 6.dp),
                        ) {
                            Text(
                                text = label,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                                ),
                            )
                        }
                    }
                }
            }

            // Section Header
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        "Latest Updates",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontSize = 11.sp,
                            letterSpacing = 1.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        ),
                    )
                    if (s.unread > 0) {
                        Text(
                            "MARK ALL AS READ",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = DomiTheme.colors.primaryBright,
                            ),
                            modifier = Modifier.clickable { vm.markAllRead() },
                        )
                    }
                }
            }

            // List
            if (filteredItems.isEmpty()) {
                item {
                    EmptyState(
                        icon = Icons.Filled.Notifications,
                        title = "No notifications",
                        message = "You're all caught up! Check back later for updates.",
                    )
                }
            } else {
                item {
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = MaterialTheme.colorScheme.surface,
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(20.dp)),
                    ) {
                        Column {
                            filteredItems.forEachIndexed { index, notif ->
                                val (icon, tint) = when (notif.type.lowercase()) {
                                    "loan" -> Icons.Filled.AccountBalanceWallet to DomiTheme.colors.info
                                    "contribution" -> Icons.Filled.CalendarMonth to DomiTheme.colors.warning
                                    "security" -> Icons.Filled.Security to MaterialTheme.colorScheme.error
                                    else -> Icons.Filled.Campaign to DomiTheme.colors.primaryBright
                                }

                                ListItem(
                                    title = notif.title,
                                    subtitle = notif.body,
                                    leadingIcon = icon,
                                    leadingColor = tint,
                                    chevron = false,
                                    onClick = { vm.markRead(notif.id) },
                                    trailing = {
                                        Column(horizontalAlignment = Alignment.End) {
                                            if (!notif.read) {
                                                Box(
                                                    modifier = Modifier
                                                        .size(8.dp)
                                                        .clip(CircleShape)
                                                        .background(DomiTheme.colors.primaryBright),
                                                )
                                                Spacer(Modifier.height(4.dp))
                                            }
                                            Text(
                                                text = Format.relativeTime(notif.createdAt),
                                                style = MaterialTheme.typography.labelSmall.copy(
                                                    fontSize = 9.sp,
                                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                                ),
                                            )
                                        }
                                    },
                                )
                                if (index < filteredItems.size - 1) {
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

@Composable
private fun NotifStatCard(
    count: Int,
    label: String,
    icon: ImageVector,
    tint: Color,
    modifier: Modifier = Modifier,
) {
    Surface(
        shape = RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.surface,
        modifier = modifier,
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(tint.copy(alpha = 0.10f)),
                contentAlignment = Alignment.Center,
            ) {
                Icon(imageVector = icon, contentDescription = null, tint = tint, modifier = Modifier.size(24.dp))
            }
            Spacer(Modifier.width(12.dp))
            Column {
                Text("$count", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold))
                Text(label, style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant))
            }
        }
    }
}

// =========================================================================
// 2. ANNOUNCEMENT DETAIL SCREEN
// =========================================================================

@Composable
fun AnnouncementDetailScreen(
    title: String,
    body: String,
    timestamp: String,
    onBack: () -> Unit,
) {
    Scaffold(
        topBar = {
            ScreenHeader(title = "Announcement", onBack = onBack)
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
            item {
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = MaterialTheme.colorScheme.surface,
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(20.dp)),
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .background(MaterialTheme.colorScheme.primaryContainer),
                            contentAlignment = Alignment.Center,
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Campaign,
                                contentDescription = null,
                                tint = DomiTheme.colors.primaryBright,
                                modifier = Modifier.size(28.dp),
                            )
                        }

                        Spacer(Modifier.height(16.dp))

                        Text(
                            text = title.ifBlank { "Announcement" },
                            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                        )

                        Spacer(Modifier.height(8.dp))

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Schedule,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(14.dp),
                            )
                            Text(
                                text = "${Format.relativeTime(timestamp)} — ${Format.formatDate(timestamp)}",
                                style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant),
                            )
                        }

                        Spacer(Modifier.height(16.dp))
                        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant, thickness = 1.dp)
                        Spacer(Modifier.height(16.dp))

                        Text(
                            text = body.ifBlank { "No content available." },
                            style = MaterialTheme.typography.bodyLarge.copy(
                                lineHeight = 24.sp,
                                color = MaterialTheme.colorScheme.onSurface,
                            ),
                        )
                    }
                }
            }

            item { Spacer(Modifier.height(32.dp)) }
        }
    }
}

// =========================================================================
// 3. EDIT PROFILE SCREEN
// =========================================================================

val NIGERIAN_BANKS_OPTIONS = listOf(
    DropdownOption("044", "Access Bank"),
    DropdownOption("023", "Citibank Nigeria"),
    DropdownOption("050", "Ecobank Nigeria"),
    DropdownOption("070", "Fidelity Bank"),
    DropdownOption("011", "First Bank of Nigeria"),
    DropdownOption("214", "First City Monument Bank"),
    DropdownOption("058", "Guaranty Trust Bank"),
    DropdownOption("030", "Heritage Bank"),
    DropdownOption("301", "Jaiz Bank"),
    DropdownOption("082", "Keystone Bank"),
    DropdownOption("076", "Polaris Bank"),
    DropdownOption("039", "Stanbic IBTC Bank"),
    DropdownOption("232", "Sterling Bank"),
    DropdownOption("032", "Union Bank of Nigeria"),
    DropdownOption("033", "United Bank for Africa"),
    DropdownOption("215", "Unity Bank"),
    DropdownOption("035", "Wema Bank"),
    DropdownOption("057", "Zenith Bank"),
    DropdownOption("502", "Providus Bank"),
)

@Composable
fun EditProfileScreen(
    app: AppContainer,
    onDone: () -> Unit,
    onBack: () -> Unit,
) {
    var fullName by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var bankCode by remember { mutableStateOf("") }
    var bankName by remember { mutableStateOf("") }
    var bankAccount by remember { mutableStateOf("") }
    var nextOfKin by remember { mutableStateOf("") }

    var isLoading by remember { mutableStateOf(true) }
    var isSaving by remember { mutableStateOf(false) }
    var showSuccessModal by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        try {
            val p = app.members.profile()
            fullName = p.fullName
            phone = p.phone ?: ""
            address = p.address ?: ""
            bankCode = p.bankCode ?: ""
            bankName = p.bankName ?: ""
            bankAccount = p.bankAccount ?: ""
            nextOfKin = p.nextOfKin ?: ""
        } catch (_: Exception) {}
        isLoading = false
    }

    Scaffold(
        topBar = {
            ScreenHeader(title = "Edit Profile", onBack = onBack)
        },
        containerColor = MaterialTheme.colorScheme.background,
    ) { padding ->
        if (isLoading) {
            LoadingState("Loading profile details…")
            return@Scaffold
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            // Personal Information
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
                        Text("Personal Information", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                        DomiTextField(value = fullName, onValueChange = { fullName = it }, label = "Full Name")
                        DomiTextField(value = phone, onValueChange = { phone = it }, label = "Phone Number", keyboardType = KeyboardType.Phone)
                        DomiTextField(value = address, onValueChange = { address = it }, label = "Residential Address", multiline = true, lines = 2)
                        DomiTextField(value = nextOfKin, onValueChange = { nextOfKin = it }, label = "Next of Kin")
                    }
                }
            }

            // Banking Information
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
                        Text("Banking Information", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))

                        DropdownSelect(
                            label = "Bank Name",
                            placeholder = "Select your bank",
                            options = NIGERIAN_BANKS_OPTIONS,
                            selectedValue = bankCode,
                            onSelect = { code ->
                                bankCode = code
                                bankName = NIGERIAN_BANKS_OPTIONS.find { it.value == code }?.label ?: bankName
                            },
                        )

                        DomiTextField(
                            value = bankAccount,
                            onValueChange = { bankAccount = it },
                            label = "Account Number (10 digits)",
                            keyboardType = KeyboardType.Number,
                        )
                    }
                }
            }

            // Save Button
            item {
                DomiButton(
                    text = if (isSaving) "Saving Changes…" else "Save Changes",
                    onClick = {
                        isSaving = true
                        scope.launch {
                            try {
                                app.members.update(
                                    UpdateProfileRequest(
                                        fullName = fullName,
                                        phone = phone,
                                        address = address,
                                        bankCode = bankCode,
                                        bankName = bankName,
                                        bankAccount = bankAccount,
                                        nextOfKin = nextOfKin,
                                    ),
                                )
                                isSaving = false
                                showSuccessModal = true
                            } catch (_: Exception) {
                                isSaving = false
                            }
                        }
                    },
                    enabled = !isSaving,
                    variant = DomiButtonVariant.Primary,
                    size = DomiButtonSize.Large,
                    icon = Icons.Filled.Check,
                    modifier = Modifier.fillMaxWidth(),
                )
            }

            item { Spacer(Modifier.height(32.dp)) }
        }
    }

    if (showSuccessModal) {
        InfoModal(
            visible = true,
            title = "Profile Updated",
            message = "Your account details have been successfully saved.",
            onDismiss = {
                showSuccessModal = false
                onDone()
            },
        )
    }
}

// =========================================================================
// 4. SECURITY SCREEN
// =========================================================================

@Composable
fun SecurityScreen(
    onChangePassword: () -> Unit,
    onBack: () -> Unit,
) {
    Scaffold(
        topBar = {
            ScreenHeader(title = "Security", onBack = onBack)
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
            // Title
            item {
                Column {
                    Text("Security Settings", style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold))
                    Spacer(Modifier.height(4.dp))
                    Text(
                        "Manage your account security and authentication methods.",
                        style = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onSurfaceVariant),
                    )
                }
            }

            // Status Card
            item {
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = MaterialTheme.colorScheme.primary,
                    shadowElevation = 2.dp,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Box(modifier = Modifier.fillMaxWidth().padding(20.dp)) {
                        Icon(
                            imageVector = Icons.Filled.Shield,
                            contentDescription = null,
                            tint = Color.White.copy(alpha = 0.10f),
                            modifier = Modifier.size(120.dp).align(Alignment.BottomEnd),
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Column {
                                Text("SECURITY STATUS", style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp, letterSpacing = 1.sp, color = Color.White.copy(alpha = 0.80f), fontWeight = FontWeight.Bold))
                                Spacer(Modifier.height(4.dp))
                                Text("Highly Protected", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold, color = Color.White))
                                Spacer(Modifier.height(2.dp))
                                Text("Last checked: Today at 09:42 AM", style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp, color = Color.White.copy(alpha = 0.80f)))
                            }
                            Box(
                                modifier = Modifier.size(56.dp).clip(CircleShape).background(Color.White.copy(alpha = 0.20f)),
                                contentAlignment = Alignment.Center,
                            ) {
                                Icon(Icons.Filled.VerifiedUser, contentDescription = null, tint = Color.White, modifier = Modifier.size(32.dp))
                            }
                        }
                    }
                }
            }

            // Options List
            item {
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = MaterialTheme.colorScheme.surface,
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(20.dp)),
                ) {
                    ListItem(
                        title = "Change Password",
                        subtitle = "Last updated 3 months ago",
                        leadingIcon = Icons.Filled.Lock,
                        chevron = true,
                        onClick = onChangePassword,
                    )
                }
            }

            // Security Tip
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
                        Icon(
                            imageVector = Icons.Filled.Lightbulb,
                            contentDescription = null,
                            tint = DomiTheme.colors.tertiary,
                            modifier = Modifier.size(24.dp),
                        )
                        Spacer(Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Pro Security Tip", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold))
                            Spacer(Modifier.height(4.dp))
                            Text(
                                "Regularly updating your password significantly reduces the risk of unauthorized access.",
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
}

// =========================================================================
// 5. CHANGE PASSWORD SCREEN
// =========================================================================

@Composable
fun ChangePasswordScreen(
    onBack: () -> Unit,
) {
    var currentPassword by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var isSaving by remember { mutableStateOf(false) }
    var errorMsg by remember { mutableStateOf<String?>(null) }
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            ScreenHeader(title = "Change Password", onBack = onBack)
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
            item {
                Text(
                    "Create a strong password to keep your account secure. Your new password must be at least 6 characters long.",
                    style = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onSurfaceVariant),
                )
            }

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
                        DomiTextField(
                            value = currentPassword,
                            onValueChange = { currentPassword = it },
                            label = "Current Password",
                            isPassword = true,
                        )
                        DomiTextField(
                            value = newPassword,
                            onValueChange = { newPassword = it },
                            label = "New Password",
                            isPassword = true,
                        )
                        DomiTextField(
                            value = confirmPassword,
                            onValueChange = { confirmPassword = it },
                            label = "Confirm New Password",
                            isPassword = true,
                        )
                    }
                }
            }

            item {
                DomiButton(
                    text = if (isSaving) "Updating Password…" else "Update Password",
                    onClick = {
                        if (currentPassword.isBlank()) {
                            errorMsg = "Current password is required"
                            return@DomiButton
                        }
                        if (newPassword.length < 6) {
                            errorMsg = "New password must be at least 6 characters"
                            return@DomiButton
                        }
                        if (newPassword != confirmPassword) {
                            errorMsg = "Passwords do not match"
                            return@DomiButton
                        }
                        isSaving = true
                        scope.launch {
                            kotlinx.coroutines.delay(1000)
                            isSaving = false
                            Toast.makeText(context, "Password updated successfully", Toast.LENGTH_SHORT).show()
                            onBack()
                        }
                    },
                    enabled = !isSaving,
                    variant = DomiButtonVariant.Primary,
                    size = DomiButtonSize.Large,
                    icon = Icons.Filled.CheckCircle,
                    modifier = Modifier.fillMaxWidth(),
                )
            }

            item { Spacer(Modifier.height(32.dp)) }
        }
    }

    if (errorMsg != null) {
        InfoModal(
            visible = true,
            title = "Invalid Password",
            message = errorMsg ?: "",
            onDismiss = { errorMsg = null },
        )
    }
}

// =========================================================================
// 6. THEME PREFERENCE SCREEN
// =========================================================================

@Composable
fun ThemePreferenceScreen(
    current: ThemePreference,
    onPick: (ThemePreference) -> Unit,
    onBack: () -> Unit,
) {
    var selectedTheme by remember { mutableStateOf(current) }

    Scaffold(
        topBar = {
            ScreenHeader(title = "Theme Preference", onBack = onBack)
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
            item {
                Column {
                    Text("Personalize your experience", style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold))
                    Spacer(Modifier.height(4.dp))
                    Text(
                        "Choose a visual style that matches your workflow and environment.",
                        style = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onSurfaceVariant),
                    )
                }
            }

            // Light Option
            item {
                ThemeCard(
                    title = "Light Mode",
                    desc = "Clean and crisp for bright environments",
                    isSelected = selectedTheme == ThemePreference.LIGHT,
                    onClick = {
                        selectedTheme = ThemePreference.LIGHT
                        onPick(ThemePreference.LIGHT)
                    },
                    isDarkPreview = false,
                )
            }

            // Dark Option
            item {
                ThemeCard(
                    title = "Dark Mode",
                    desc = "Easier on the eyes in low-light settings",
                    isSelected = selectedTheme == ThemePreference.DARK,
                    onClick = {
                        selectedTheme = ThemePreference.DARK
                        onPick(ThemePreference.DARK)
                    },
                    isDarkPreview = true,
                )
            }

            // System Default Option
            item {
                ThemeCard(
                    title = "System Default",
                    desc = "Synchronize with your device's settings",
                    isSelected = selectedTheme == ThemePreference.SYSTEM,
                    onClick = {
                        selectedTheme = ThemePreference.SYSTEM
                        onPick(ThemePreference.SYSTEM)
                    },
                    isSplitPreview = true,
                )
            }

            // Did you know?
            item {
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.08f),
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .background(MaterialTheme.colorScheme.primary),
                            contentAlignment = Alignment.Center,
                        ) {
                            Icon(Icons.Filled.Palette, contentDescription = null, tint = Color.White, modifier = Modifier.size(28.dp))
                        }
                        Spacer(Modifier.width(16.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Did you know?", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold))
                            Spacer(Modifier.height(4.dp))
                            Text(
                                "Switching to Dark Mode can save up to 30% battery on OLED screens and reduces eye strain during night shifts.",
                                style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant),
                            )
                        }
                    }
                }
            }

            // Save Preferences Button
            item {
                DomiButton(
                    text = "Save Preferences",
                    onClick = onBack,
                    variant = DomiButtonVariant.Primary,
                    size = DomiButtonSize.Large,
                    icon = Icons.Filled.CheckCircle,
                    modifier = Modifier.fillMaxWidth(),
                )
            }

            item { Spacer(Modifier.height(32.dp)) }
        }
    }
}

@Composable
private fun ThemeCard(
    title: String,
    desc: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    isDarkPreview: Boolean = false,
    isSplitPreview: Boolean = false,
) {
    Surface(
        shape = RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.surface,
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .clickable { onClick() }
            .border(
                width = if (isSelected) 2.dp else 1.dp,
                color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant,
                shape = RoundedCornerShape(20.dp),
            ),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column {
                    Text(title, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold))
                    Text(desc, style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant))
                }
                RadioButton(selected = isSelected, onClick = onClick)
            }

            Spacer(Modifier.height(12.dp))

            // Mini visual preview
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(80.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(
                        when {
                            isSplitPreview -> MaterialTheme.colorScheme.surfaceContainer
                            isDarkPreview -> Color(0xFF141923)
                            else -> Color(0xFFF6F8FA)
                        },
                    )
                    .padding(12.dp),
            ) {
                Row(
                    modifier = Modifier.fillMaxSize(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .clip(CircleShape)
                            .background(if (isDarkPreview) Color(0xFF202938) else Color.White),
                    )
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(4.dp),
                    ) {
                        Box(
                            modifier = Modifier
                                .width(80.dp)
                                .height(6.dp)
                                .clip(RoundedCornerShape(3.dp))
                                .background(if (isDarkPreview) Color(0xFF384358) else Color(0xFFD0D7DE)),
                        )
                        Box(
                            modifier = Modifier
                                .width(50.dp)
                                .height(6.dp)
                                .clip(RoundedCornerShape(3.dp))
                                .background(if (isDarkPreview) Color(0xFF2B3648) else Color(0xFFE2E8F0)),
                        )
                    }
                }
            }
        }
    }
}

// =========================================================================
// 7. NOTIFICATION PREFERENCES SCREEN
// =========================================================================

@Composable
fun NotificationPrefsScreen(
    app: AppContainer,
    onBack: () -> Unit,
) {
    var pushEnabled by remember { mutableStateOf(true) }
    var emailEnabled by remember { mutableStateOf(true) }
    var loansEnabled by remember { mutableStateOf(true) }
    var contributionsEnabled by remember { mutableStateOf(true) }
    var dividendsEnabled by remember { mutableStateOf(true) }
    var meetingsEnabled by remember { mutableStateOf(true) }

    val context = LocalContext.current

    Scaffold(
        topBar = {
            ScreenHeader(title = "Notification Preferences", onBack = onBack)
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
            // Delivery Channels
            item {
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = MaterialTheme.colorScheme.surface,
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(16.dp)),
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier.size(36.dp).clip(RoundedCornerShape(10.dp)).background(MaterialTheme.colorScheme.primary.copy(alpha = 0.10f)),
                                contentAlignment = Alignment.Center,
                            ) {
                                Icon(Icons.Filled.Notifications, contentDescription = null, tint = DomiTheme.colors.primaryBright, modifier = Modifier.size(20.dp))
                            }
                            Spacer(Modifier.width(12.dp))
                            Column {
                                Text("Delivery Channels", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                                Text("Choose how you receive notifications", style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant))
                            }
                        }

                        Spacer(Modifier.height(16.dp))
                        ToggleRow("Push Notifications", "Alerts directly on this device", pushEnabled) { pushEnabled = it }
                        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant, thickness = 1.dp)
                        ToggleRow("Email Notifications", "Monthly ledger summaries and bonds", emailEnabled) { emailEnabled = it }
                    }
                }
            }

            // Notification Categories
            item {
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = MaterialTheme.colorScheme.surface,
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(16.dp)),
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier.size(36.dp).clip(RoundedCornerShape(10.dp)).background(MaterialTheme.colorScheme.primary.copy(alpha = 0.10f)),
                                contentAlignment = Alignment.Center,
                            ) {
                                Icon(Icons.Filled.Category, contentDescription = null, tint = DomiTheme.colors.primaryBright, modifier = Modifier.size(20.dp))
                            }
                            Spacer(Modifier.width(12.dp))
                            Column {
                                Text("Notification Categories", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                                Text("Choose which notifications you want to receive", style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant))
                            }
                        }

                        Spacer(Modifier.height(16.dp))
                        ToggleRow("Loans", "Approvals, disbursements and repayment reminders", loansEnabled) { loansEnabled = it }
                        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant, thickness = 1.dp)
                        ToggleRow("Contributions", "Due reminders and payment confirmations", contributionsEnabled) { contributionsEnabled = it }
                        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant, thickness = 1.dp)
                        ToggleRow("Dividends", "Payout announcements and allocation updates", dividendsEnabled) { dividendsEnabled = it }
                        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant, thickness = 1.dp)
                        ToggleRow("Meetings", "AGM and cooperative meeting notices", meetingsEnabled) { meetingsEnabled = it }
                    }
                }
            }

            // Save Preferences Button
            item {
                DomiButton(
                    text = "Save Preferences",
                    onClick = {
                        Toast.makeText(context, "Preferences saved successfully", Toast.LENGTH_SHORT).show()
                        onBack()
                    },
                    variant = DomiButtonVariant.Primary,
                    size = DomiButtonSize.Large,
                    icon = Icons.Filled.Check,
                    modifier = Modifier.fillMaxWidth(),
                )
            }

            item { Spacer(Modifier.height(32.dp)) }
        }
    }
}

@Composable
private fun ToggleRow(title: String, subtitle: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(title, style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Medium))
            Text(subtitle, style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant))
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = MaterialTheme.colorScheme.onPrimary,
                checkedTrackColor = MaterialTheme.colorScheme.primary,
            ),
        )
    }
}

// =========================================================================
// 8. GUIDELINES SCREEN
// =========================================================================

val COOP_GUIDELINES = listOf(
    1 to "Registration fee is ₦20,000, compulsory for new intake (non-refundable).",
    2 to "No payment through USSD/POS.",
    3 to "No third-party payment (another person's account).",
    4 to "Your payment should carry the name you used in your registration.",
    5 to "Compulsory payment of ₦1,000 together with your subscription (₦1,000 for socials/end-of-year souvenir).",
    6 to "We have a cooperative wrapper with a cost of ₦6,000, which will be paid together with the registration fee.",
    7 to "The least payment is ₦5,000.",
    8 to "Any payment above ₦50,000 will be posted to your deposit column.",
    9 to "No random payment like ₦12,000, ₦18,000, ₦22,000.",
    10 to "If you want to exit the cooperative, you must give 6 months' notice with a written letter.",
    11 to "If you want to increase your contribution, you should write to the cooperative informing them of your increase through the chairperson.",
    12 to "As a new member, if you fail to pay your dues for at least 3 months, you are automatically on your way to withdrawal at the end of the year.",
    13 to "Consistent payment for 6 months qualifies you for a loan of 3 times what you have, at an interest rate of 10%, repayable within one year.",
    14 to "Once you are on loan and repaying as agreed, you are entitled to sign for one person as a guarantor.",
    15 to "If you are not intending to collect a loan, you can sign for two persons.",
    16 to "If you guarantee someone and the person defaults, your money will be held until that person pays.",
    17 to "If you guarantee someone and the person is not repaying as agreed, at the end of the year your dividend will be placed on hold, and by the end of the first quarter of the following year the cooperative will draw from your assets. (Know who you are signing for very well.)",
    18 to "Guarantee only someone you are sure of.",
    19 to "Your guarantor must be in the cooperative.",
    20 to "To resolve issues with your account caused by wrong payment (POS or third-party account), you will pay a fee of ₦5,000.",
    21 to "There is a late payment fee of ₦500.",
    22 to "For any issues relating to loans, cloth, purchasing of items, etc., go to the office.",
    23 to "After filling your form, submit it back to the office.",
    24 to "Payment should be made on or before the 10th of every month.",
    25 to "Failure to attend meetings attracts a fine.",
    26 to "All meetings are held on the designated Sundays of the month.",
    27 to "Emergency meetings may be convened when necessary.",
    28 to "Members must maintain decorum during society deliberations.",
    29 to "No member shall malign or bring disrepute to the cooperative.",
    30 to "Disciplinary matters will be referred to the Executive Committee.",
    31 to "Dividends are declared and shared annually following audits.",
    32 to "Audited financial reports will be presented at every AGM.",
    33 to "Every active member has voting rights at general meetings.",
    34 to "Amendments to these bye-laws require a two-thirds majority.",
    35 to "God bless Dominion Cooperative Society and all its members.",
)

@Composable
fun GuidelinesScreen(
    onboarding: Boolean = false,
    onAcknowledge: () -> Unit = {},
    onBack: () -> Unit = {},
) {
    var agreed by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            Box(
                Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .displayCutoutPadding()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
            ) {
                BackButton(onPress = onBack)
            }
        },
        containerColor = MaterialTheme.colorScheme.background,
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 24.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            item {
                Text(
                    "Cooperative Guidelines",
                    style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    "Good day our intending cooperator. You are welcome to Dominion Cooperative Society. Our office number is No. 75 Jack Novo Plaza, Water Resources, Effurun Sapele Road, Effurun.",
                    style = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onSurfaceVariant),
                )
                Spacer(Modifier.height(6.dp))
                Text(
                    "In this cooperative we have some rules and regulations that govern the body. Few are outlined for the purpose of this meeting.",
                    style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant),
                )
                Spacer(Modifier.height(12.dp))
            }

            items(COOP_GUIDELINES) { (num, text) ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.Top,
                ) {
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.10f)),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            "$num",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary,
                            ),
                        )
                    }
                    Spacer(Modifier.width(14.dp))
                    Text(
                        text = text,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            lineHeight = 22.sp,
                            color = MaterialTheme.colorScheme.onSurface,
                        ),
                        modifier = Modifier.weight(1f),
                    )
                }
            }

            if (onboarding) {
                item {
                    Spacer(Modifier.height(16.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { agreed = !agreed },
                    ) {
                        Checkbox(
                            checked = agreed,
                            onCheckedChange = { agreed = it },
                            colors = CheckboxDefaults.colors(checkedColor = MaterialTheme.colorScheme.primary),
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(
                            "I have read and agree to the cooperative guidelines",
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                        )
                    }
                    Spacer(Modifier.height(12.dp))
                    DomiButton(
                        text = "Continue",
                        onClick = onAcknowledge,
                        enabled = agreed,
                        variant = DomiButtonVariant.Primary,
                        size = DomiButtonSize.Large,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
            }

            item { Spacer(Modifier.height(32.dp)) }
        }
    }
}

// =========================================================================
// 9. PRIVACY POLICY SCREEN
// =========================================================================

val PRIVACY_SECTIONS_DATA = listOf(
    1 to ("What we collect" to "To run the society's ledger we collect your name, contact details, member number and the records of your contributions, loans and dividends. We collect only what the service needs — nothing more."),
    2 to ("How we use it" to "Your information is used to operate your membership of Dominion Co-operative: posting contributions, processing loan applications and disbursements, calculating dividends, and keeping you and the society's administrators informed."),
    3 to ("Payments" to "Payments are processed by Paystack, a PCI-DSS compliant provider. We do not store your card details. We record only that a payment was made so it can be posted to your passbook."),
    4 to ("How we protect it" to "Data is encrypted in transit, passwords are stored only as salted hashes, and administrator access is role-based."),
    5 to ("We don't sell your data" to "Your membership and financial records belong to you and to Dominion Co-operative. We do not sell them, and we share them only with the society's authorised administrators and the providers needed to run the service."),
    6 to ("Your choices" to "You can request a copy of your data or ask that your account be closed. Some records may be retained where the society's bye-laws or the law requires it."),
    7 to ("Contact" to "Questions about this policy? Reach us at domicorp14@gmail.com."),
)

@Composable
fun PrivacyPolicyScreen(
    onBack: () -> Unit,
) {
    Scaffold(
        topBar = {
            Box(
                Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .displayCutoutPadding()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
            ) {
                BackButton(onPress = onBack)
            }
        },
        containerColor = MaterialTheme.colorScheme.background,
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 24.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp),
        ) {
            item {
                Text(
                    "Privacy Policy",
                    style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    "Last updated 3 July 2026",
                    style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant),
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    "We collect only what running the ledger requires, and we never sell your data.",
                    style = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onSurface),
                )
                Spacer(Modifier.height(12.dp))
            }

            items(PRIVACY_SECTIONS_DATA) { (num, content) ->
                val (title, text) = content
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.Top,
                ) {
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.10f)),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            "$num",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary,
                            ),
                        )
                    }
                    Spacer(Modifier.width(14.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            title,
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(
                            text,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                lineHeight = 22.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            ),
                        )
                    }
                }
            }

            item { Spacer(Modifier.height(32.dp)) }
        }
    }
}

// =========================================================================
// 10. SUPPORT SCREEN
// =========================================================================

data class FaqItem(val id: String, val question: String, val answer: String)

val SUPPORT_FAQS = listOf(
    FaqItem("faq-1", "Membership eligibility & benefits", "DOMICOOP membership is open to all residents. Benefits include access to low-interest loans, community savings programs, and exclusive financial literacy workshops."),
    FaqItem("faq-2", "How do I apply for a loan?", "Navigate to the 'Loans' tab in the bottom bar, select 'Apply for a Loan', and follow the prompts. You'll need to provide details and 3 member guarantors."),
    FaqItem("faq-3", "Payment methods and schedules", "Contributions run from the 1st to the 10th of every month. Payments are handled securely via Paystack with cards, bank transfers, and USSD."),
    FaqItem("faq-4", "Loan repayment options", "You can make partial or full repayments at any time without penalties. Select 'Make a Payment' in the Loans section."),
    FaqItem("faq-5", "Savings contribution rules", "Members contribute a minimum of ₦5,000 monthly. Consistent savings build your capacity to qualify for loans."),
    FaqItem("faq-6", "Account security best practices", "Use strong passwords and never share your login credentials with anyone."),
    FaqItem("faq-7", "How to update personal information", "Go to Profile > Edit Account Details to update your name, email, phone number, and bank account information."),
)

@Composable
fun SupportScreen(
    onBack: () -> Unit,
) {
    val uriHandler = LocalUriHandler.current

    Scaffold(
        topBar = {
            ScreenHeader(title = "Help Center", onBack = onBack)
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
            // Hero Banner
            item {
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = MaterialTheme.colorScheme.primary,
                    shadowElevation = 2.dp,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Text("How can we help?", style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold, color = Color.White))
                        Spacer(Modifier.height(4.dp))
                        Text(
                            "Search our knowledge base or contact us directly.",
                            style = MaterialTheme.typography.bodyMedium.copy(color = Color.White.copy(alpha = 0.85f)),
                        )
                    }
                }
            }

            // Quick action: Contact Admin
            item {
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = MaterialTheme.colorScheme.surface,
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(16.dp))
                        .clip(RoundedCornerShape(16.dp))
                        .clickable {
                            uriHandler.openUri("mailto:support@domicoop.com?subject=Support%20Request")
                        },
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(RoundedCornerShape(14.dp))
                                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)),
                            contentAlignment = Alignment.Center,
                        ) {
                            Icon(Icons.Filled.AdminPanelSettings, contentDescription = null, tint = DomiTheme.colors.primaryBright, modifier = Modifier.size(24.dp))
                        }
                        Spacer(Modifier.width(16.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Contact Admin", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold))
                            Text("Formal requests & inquiries", style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant))
                        }
                        Icon(Icons.Filled.ChevronRight, contentDescription = null, tint = MaterialTheme.colorScheme.outline)
                    }
                }
            }

            // FAQ Accordion
            item {
                Text(
                    "Frequently Asked Questions",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    modifier = Modifier.padding(start = 4.dp),
                )
            }

            items(SUPPORT_FAQS) { faq ->
                var expanded by remember { mutableStateOf(false) }

                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = MaterialTheme.colorScheme.surface,
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(16.dp))
                        .clip(RoundedCornerShape(16.dp))
                        .clickable { expanded = !expanded }
                        .animateContentSize(),
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Text(
                                faq.question,
                                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold),
                                modifier = Modifier.weight(1f),
                            )
                            Icon(
                                imageVector = if (expanded) Icons.Filled.ExpandLess else Icons.Filled.ExpandMore,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }

                        if (expanded) {
                            Spacer(Modifier.height(8.dp))
                            Text(
                                faq.answer,
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    lineHeight = 20.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                ),
                            )
                        }
                    }
                }
            }

            // Still need help card
            item {
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = MaterialTheme.colorScheme.surfaceContainerLow,
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(20.dp)),
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primary),
                            contentAlignment = Alignment.Center,
                        ) {
                            Icon(Icons.Filled.SupportAgent, contentDescription = null, tint = Color.White, modifier = Modifier.size(28.dp))
                        }
                        Spacer(Modifier.height(12.dp))
                        Text("Still need help?", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                        Spacer(Modifier.height(4.dp))
                        Text(
                            "Our support team is available to assist you with any inquiries.",
                            style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant),
                            textAlign = TextAlign.Center,
                        )
                        Spacer(Modifier.height(16.dp))
                        DomiButton(
                            text = "Start Conversation",
                            onClick = {
                                uriHandler.openUri("mailto:support@domicoop.com?subject=Support%20Request")
                            },
                            variant = DomiButtonVariant.Primary,
                            size = DomiButtonSize.Medium,
                            modifier = Modifier.fillMaxWidth(),
                        )
                    }
                }
            }

            item { Spacer(Modifier.height(32.dp)) }
        }
    }
}

// =========================================================================
// 11. SUCCESS SCREEN
// =========================================================================

@Composable
fun SuccessScreen(
    title: String = "All done",
    message: String = "Your transaction was completed successfully.",
    amount: Double? = null,
    onHome: () -> Unit,
) {
    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Spacer(Modifier.weight(1f))

            // Icon circle
            Box(
                modifier = Modifier
                    .size(96.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.linearGradient(
                            listOf(
                                DomiTheme.colors.primary,
                                DomiTheme.colors.primaryVariant,
                            ),
                        ),
                    )
                    .shadow(elevation = 10.dp, shape = CircleShape),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = Icons.Filled.Check,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(44.dp),
                )
            }

            Spacer(Modifier.height(24.dp))

            Text(
                text = title,
                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                textAlign = TextAlign.Center,
            )

            if (amount != null && amount > 0) {
                Spacer(Modifier.height(12.dp))
                Money(amount = amount, size = MoneySize.XL)
            }

            Spacer(Modifier.height(12.dp))

            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onSurfaceVariant),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 24.dp),
            )

            Spacer(Modifier.weight(1f))

            DomiButton(
                text = "Back to Home",
                onClick = onHome,
                variant = DomiButtonVariant.Primary,
                size = DomiButtonSize.Large,
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}
