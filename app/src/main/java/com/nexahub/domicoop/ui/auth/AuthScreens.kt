package com.nexahub.domicoop.ui.auth

import androidx.compose.foundation.Image
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material.icons.filled.EventBusy
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.nexahub.domicoop.R
import com.nexahub.domicoop.core.Currency
import com.nexahub.domicoop.data.repository.AppContainer
import com.nexahub.domicoop.theme.Dimens
import com.nexahub.domicoop.theme.Inter
import com.nexahub.domicoop.theme.PlusJakartaSans
import com.nexahub.domicoop.theme.isDarkMode
import com.nexahub.domicoop.theme.primaryBright
import com.nexahub.domicoop.ui.common.BackButton
import com.nexahub.domicoop.ui.common.ButtonSize
import com.nexahub.domicoop.ui.common.ButtonVariant
import com.nexahub.domicoop.ui.common.DomiButton
import com.nexahub.domicoop.ui.common.DomiTextField
import com.nexahub.domicoop.ui.common.DropdownOption
import com.nexahub.domicoop.ui.common.DropdownSelect
import com.nexahub.domicoop.ui.common.FormCard
import com.nexahub.domicoop.ui.common.HeroSection
import com.nexahub.domicoop.ui.common.InfoModal
import com.nexahub.domicoop.ui.common.Money
import com.nexahub.domicoop.ui.common.MoneySize
import com.nexahub.domicoop.ui.common.SecurityBadge

val NIGERIAN_BANKS = listOf(
    DropdownOption("044", "Access Bank (044)"),
    DropdownOption("023", "Citibank Nigeria (023)"),
    DropdownOption("050", "Ecobank Nigeria (050)"),
    DropdownOption("070", "Fidelity Bank (070)"),
    DropdownOption("011", "First Bank of Nigeria (011)"),
    DropdownOption("214", "First City Monument Bank (214)"),
    DropdownOption("058", "Guaranty Trust Bank (058)"),
    DropdownOption("030", "Heritage Bank (030)"),
    DropdownOption("301", "Jaiz Bank (301)"),
    DropdownOption("082", "Keystone Bank (082)"),
    DropdownOption("076", "Polaris Bank (076)"),
    DropdownOption("039", "Stanbic IBTC Bank (039)"),
    DropdownOption("232", "Sterling Bank (232)"),
    DropdownOption("032", "Union Bank of Nigeria (032)"),
    DropdownOption("033", "United Bank for Africa (033)"),
    DropdownOption("215", "Unity Bank (215)"),
    DropdownOption("035", "Wema Bank (035)"),
    DropdownOption("057", "Zenith Bank (057)"),
    DropdownOption("559", "Coronation Merchant Bank (559)"),
    DropdownOption("502", "Providus Bank (502)"),
    DropdownOption("526", "Parallex Bank (526)"),
    DropdownOption("503", "SunTrust Bank (503)"),
)

val SEX_OPTIONS = listOf(
    DropdownOption("Male", "Male"),
    DropdownOption("Female", "Female"),
)

val MARITAL_STATUS_OPTIONS = listOf(
    DropdownOption("Single", "Single"),
    DropdownOption("Married", "Married"),
    DropdownOption("Divorced", "Divorced"),
    DropdownOption("Widowed", "Widowed"),
)

// ============================================================================
// 1. Splash Screen (mirrors app/(auth)/splash.tsx)
// ============================================================================

@Composable
fun SplashScreen(onGetStarted: () -> Unit) {
    val isDark = isDarkMode()
    val primaryColor = MaterialTheme.colorScheme.primary

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
    ) {
        // Decorative circles
        Box(
            modifier = Modifier
                .offset(x = 180.dp, y = (-80).dp)
                .size(360.dp)
                .clip(CircleShape)
                .background(primaryColor.copy(alpha = 0.05f))
        )
        Box(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .offset(x = (-100).dp, y = 100.dp)
                .size(360.dp)
                .clip(CircleShape)
                .background(primaryColor.copy(alpha = 0.08f))
        )

        // Main Center Content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Image(
                painter = painterResource(
                    id = if (isDark) R.drawable.domicoop_logo else R.drawable.domicoop_logo_black
                ),
                contentDescription = "DOMICOOP Logo",
                modifier = Modifier.size(140.dp),
                contentScale = ContentScale.Fit,
            )
            Spacer(Modifier.height(24.dp))
            Text(
                text = "DOMICOOP",
                fontFamily = PlusJakartaSans,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 36.sp,
                letterSpacing = (-0.5).sp,
                color = MaterialTheme.colorScheme.onSurface,
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = "Born to Dominate",
                fontFamily = Inter,
                fontWeight = FontWeight.Medium,
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.secondary,
                textAlign = TextAlign.Center,
            )
        }

        // Footer Actions
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(horizontal = 24.dp, vertical = 36.dp),
        ) {
            DomiButton(
                text = "Let's Get Started",
                onClick = onGetStarted,
                icon = Icons.AutoMirrored.Filled.ArrowForward,
                size = ButtonSize.LG,
            )
        }
    }
}

// ============================================================================
// 2. Welcome Screen (mirrors app/(auth)/welcome.tsx)
// ============================================================================

@Composable
fun WelcomeScreen(
    app: AppContainer,
    onCreateAccount: () -> Unit,
    onSignIn: () -> Unit,
    vm: RegistrationWindowViewModel = viewModel { RegistrationWindowViewModel(app) },
) {
    val state by vm.state.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
        ) {
            // Hero section with welcome graphic
            HeroSection(
                title = "Let's Get Started!",
                subtitle = "Born to Dominate",
                showLogo = true,
                height = 420.dp,
                imageRes = R.drawable.hero_welcome,
            )

            // Content
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .padding(top = 12.dp, bottom = 32.dp)
                    .navigationBarsPadding(),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                DomiButton(
                    text = if (state.loading) "Checking registration…" else "Create Account",
                    onClick = onCreateAccount,
                    enabled = !state.loading,
                    loading = state.loading,
                    icon = Icons.AutoMirrored.Filled.ArrowForward,
                    size = ButtonSize.LG,
                )

                if (!state.loading) {
                    Spacer(Modifier.height(12.dp))
                    Text(
                        text = if (state.isOpen) {
                            "Registration is open until ${state.closesAt?.take(10) ?: "the announced date"}."
                        } else {
                            "Registration for new members is currently closed."
                        },
                        fontFamily = Inter,
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center,
                    )
                }

                Spacer(Modifier.height(20.dp))
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = "Already a member? ",
                        fontFamily = Inter,
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    Text(
                        text = "Sign in",
                        fontFamily = Inter,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = primaryBright(),
                        modifier = Modifier.clickable(onClick = onSignIn),
                    )
                }

                Spacer(Modifier.height(24.dp))
                Text(
                    text = "By continuing, you agree to our Terms of Service and Privacy Policy.",
                    fontFamily = Inter,
                    fontSize = 11.sp,
                    lineHeight = 16.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 16.dp),
                )
            }
        }

        // Security Badge top-right
        SecurityBadge(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .statusBarsPadding()
                .padding(top = 16.dp, end = 20.dp),
        )
    }
}

// ============================================================================
// 3. Sign In Screen (mirrors app/(auth)/sign-in.tsx)
// ============================================================================

@Composable
fun SignInScreen(
    app: AppContainer,
    onSignedIn: () -> Unit,
    onForgot: () -> Unit,
    onSignUp: () -> Unit,
    vm: SignInViewModel = viewModel { SignInViewModel(app) },
) {
    val state by vm.state.collectAsState()
    var showUnverifiedModal by remember { mutableStateOf(false) }

    if (state.loggedIn) {
        onSignedIn()
        vm.consumeLogin()
    }

    if (state.unverified && !showUnverifiedModal) {
        showUnverifiedModal = true
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
        ) {
            // Hero section
            HeroSection(
                title = "Welcome Back! 👋",
                subtitle = "Access your institutional ledger and vault",
                showLogo = false,
                height = 260.dp,
                imageRes = R.drawable.hero_auth,
            )

            // Form card
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .offset(y = (-28).dp)
                    .padding(horizontal = 20.dp),
            ) {
                FormCard(overlap = false) {
                    DomiTextField(
                        value = state.email,
                        onValueChange = vm::setEmail,
                        label = "Email Address",
                        placeholder = "name@institution.com",
                        keyboardType = KeyboardType.Email,
                    )

                    DomiTextField(
                        value = state.password,
                        onValueChange = vm::setPassword,
                        label = "Password",
                        placeholder = "••••••••",
                        isPassword = true,
                        error = state.error,
                    )

                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.CenterEnd,
                    ) {
                        Text(
                            text = "FORGOT PASSWORD?",
                            fontFamily = Inter,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp,
                            letterSpacing = 0.5.sp,
                            color = primaryBright(),
                            modifier = Modifier.clickable(onClick = onForgot),
                        )
                    }

                    DomiButton(
                        text = "Sign In",
                        onClick = vm::signIn,
                        loading = state.loading,
                        icon = Icons.AutoMirrored.Filled.ArrowForward,
                        size = ButtonSize.LG,
                    )
                }
            }

            // Footer
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                HorizontalDivider(
                    color = MaterialTheme.colorScheme.outlineVariant,
                    thickness = 1.dp,
                    modifier = Modifier.padding(bottom = 20.dp),
                )

                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = "Don't have an account? ",
                        fontFamily = Inter,
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    Text(
                        text = "Create an account",
                        fontFamily = Inter,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = primaryBright(),
                        modifier = Modifier.clickable(onClick = onSignUp),
                    )
                }
                Spacer(Modifier.height(32.dp))
            }
        }

        // Top bar buttons
        BackButton(
            onPress = onSignUp,
            modifier = Modifier
                .align(Alignment.TopStart)
                .statusBarsPadding()
                .padding(top = 16.dp, start = 20.dp),
        )

        SecurityBadge(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .statusBarsPadding()
                .padding(top = 16.dp, end = 20.dp),
        )

        if (showUnverifiedModal) {
            InfoModal(
                visible = showUnverifiedModal,
                title = "Email Not Verified",
                message = "Please verify your email address before signing in. Check your inbox (and spam folder) for the verification link.",
                primaryButtonText = "OK",
                onPrimaryClick = { showUnverifiedModal = false },
            )
        }
    }
}

// ============================================================================
// 4. Sign Up Screen (6 steps - mirrors app/(auth)/sign-up.tsx)
// ============================================================================

@Composable
fun SignUpScreen(
    app: AppContainer,
    totalDue: Double,
    onDone: () -> Unit,
    onSignIn: () -> Unit,
    vm: SignUpViewModel = viewModel { SignUpViewModel(app) },
) {
    val s by vm.state.collectAsState()
    var payRef by remember { mutableStateOf("") }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding(),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp),
        ) {
            Spacer(Modifier.height(16.dp))

            // Step Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "Create account",
                    fontFamily = PlusJakartaSans,
                    fontWeight = FontWeight.Bold,
                    fontSize = 24.sp,
                    color = MaterialTheme.colorScheme.onSurface,
                )
                Text(
                    text = "Step ${s.step} of 6",
                    fontFamily = Inter,
                    fontWeight = FontWeight.Medium,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }

            Spacer(Modifier.height(8.dp))
            LinearProgressIndicator(
                progress = { s.step / 6f },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp)),
                color = MaterialTheme.colorScheme.primary,
                trackColor = MaterialTheme.colorScheme.surfaceContainerHigh,
            )

            Spacer(Modifier.height(20.dp))

            // Step Content
            when (s.step) {
                1 -> {
                    // Step 1: Account
                    Text(
                        text = "Account Credentials",
                        fontFamily = PlusJakartaSans,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                    Spacer(Modifier.height(12.dp))
                    DomiTextField(
                        value = s.email,
                        onValueChange = { v -> vm.update { it.copy(email = v) } },
                        label = "Email Address",
                        placeholder = "name@institution.com",
                        keyboardType = KeyboardType.Email,
                    )
                    Spacer(Modifier.height(12.dp))
                    DomiTextField(
                        value = s.password,
                        onValueChange = { v -> vm.update { it.copy(password = v) } },
                        label = "Password",
                        placeholder = "••••••••",
                        isPassword = true,
                    )
                    Spacer(Modifier.height(12.dp))
                    DomiTextField(
                        value = s.confirmPassword,
                        onValueChange = { v -> vm.update { it.copy(confirmPassword = v) } },
                        label = "Confirm Password",
                        placeholder = "••••••••",
                        isPassword = true,
                    )
                }
                2 -> {
                    // Step 2: Personal
                    Text(
                        text = "Personal Details",
                        fontFamily = PlusJakartaSans,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                    Spacer(Modifier.height(12.dp))
                    DomiTextField(
                        value = s.fullName,
                        onValueChange = { v -> vm.update { it.copy(fullName = v) } },
                        label = "Full Name",
                        placeholder = "First and last name",
                    )
                    Spacer(Modifier.height(12.dp))
                    DropdownSelect(
                        label = "Sex",
                        value = s.sex,
                        options = SEX_OPTIONS,
                        onSelect = { v -> vm.update { it.copy(sex = v) } },
                        placeholder = "Select sex",
                    )
                    Spacer(Modifier.height(12.dp))
                    DomiTextField(
                        value = s.dob,
                        onValueChange = { v -> vm.update { it.copy(dob = v) } },
                        label = "Date of Birth",
                        placeholder = "YYYY-MM-DD",
                    )
                    Spacer(Modifier.height(12.dp))
                    DomiTextField(
                        value = s.phone,
                        onValueChange = { v -> vm.update { it.copy(phone = v) } },
                        label = "Phone Number",
                        placeholder = "08012345678",
                        keyboardType = KeyboardType.Phone,
                    )
                    Spacer(Modifier.height(12.dp))
                    DomiTextField(
                        value = s.whatsappNumber,
                        onValueChange = { v -> vm.update { it.copy(whatsappNumber = v) } },
                        label = "WhatsApp Number",
                        placeholder = "08012345678",
                        keyboardType = KeyboardType.Phone,
                    )
                    Spacer(Modifier.height(12.dp))
                    DropdownSelect(
                        label = "Marital Status",
                        value = s.maritalStatus,
                        options = MARITAL_STATUS_OPTIONS,
                        onSelect = { v -> vm.update { it.copy(maritalStatus = v) } },
                        placeholder = "Select marital status",
                    )
                }
                3 -> {
                    // Step 3: Identity & Next of Kin
                    Text(
                        text = "Identity & Next of Kin",
                        fontFamily = PlusJakartaSans,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                    Spacer(Modifier.height(12.dp))
                    DomiTextField(
                        value = s.idCardNumber,
                        onValueChange = { v -> vm.update { it.copy(idCardNumber = v) } },
                        label = "ID Card Number",
                        placeholder = "National ID / Voter's Card / Passport",
                    )
                    Spacer(Modifier.height(12.dp))
                    DomiTextField(
                        value = s.address,
                        onValueChange = { v -> vm.update { it.copy(address = v) } },
                        label = "Residential Address",
                        placeholder = "Your home address",
                    )
                    Spacer(Modifier.height(12.dp))
                    DomiTextField(
                        value = s.placeOfWork,
                        onValueChange = { v -> vm.update { it.copy(placeOfWork = v) } },
                        label = "Place of Work",
                        placeholder = "Company or employer name",
                    )
                    Spacer(Modifier.height(12.dp))
                    DomiTextField(
                        value = s.typeOfBusiness,
                        onValueChange = { v -> vm.update { it.copy(typeOfBusiness = v) } },
                        label = "Type of Business / Occupation",
                        placeholder = "e.g. Civil Servant, Merchant, Tech",
                    )
                    Spacer(Modifier.height(12.dp))
                    DomiTextField(
                        value = s.nextOfKin,
                        onValueChange = { v -> vm.update { it.copy(nextOfKin = v) } },
                        label = "Next of Kin (Full Name & Phone)",
                        placeholder = "Name and contact number",
                    )
                }
                4 -> {
                    // Step 4: Banking & Financials
                    Text(
                        text = "Banking & Financials",
                        fontFamily = PlusJakartaSans,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                    Spacer(Modifier.height(12.dp))
                    DropdownSelect(
                        label = "Bank Name",
                        value = s.bankCode,
                        options = NIGERIAN_BANKS,
                        onSelect = { code ->
                            val bank = NIGERIAN_BANKS.find { it.value == code }
                            vm.update { it.copy(bankCode = code, bankName = bank?.label ?: code) }
                        },
                        placeholder = "Select your bank",
                    )
                    Spacer(Modifier.height(12.dp))
                    DomiTextField(
                        value = s.bankAccount,
                        onValueChange = { v -> vm.update { it.copy(bankAccount = v) } },
                        label = "Account Number",
                        placeholder = "10-digit NUBAN",
                        keyboardType = KeyboardType.Number,
                    )
                    Spacer(Modifier.height(12.dp))
                    DomiTextField(
                        value = s.monthlySubscription,
                        onValueChange = { v -> vm.update { it.copy(monthlySubscription = v) } },
                        label = "Monthly Subscription (₦)",
                        placeholder = "5000",
                        keyboardType = KeyboardType.Number,
                        helper = "Minimum ₦5,000 / Maximum ₦50,000",
                    )
                    Spacer(Modifier.height(12.dp))
                    DomiTextField(
                        value = s.referredBy,
                        onValueChange = { v -> vm.update { it.copy(referredBy = v) } },
                        label = "Referred By (Optional)",
                        placeholder = "Name or ID of referrer",
                    )
                }
                5 -> {
                    // Step 5: Profile & Signature
                    Text(
                        text = "Profile & Signature",
                        fontFamily = PlusJakartaSans,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                    Spacer(Modifier.height(12.dp))
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        color = MaterialTheme.colorScheme.surfaceContainerLow,
                        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                    ) {
                        Column(
                            modifier = Modifier.padding(20.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(72.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.surfaceContainer),
                                contentAlignment = Alignment.Center,
                            ) {
                                Text(
                                    text = if (s.fullName.isNotEmpty()) s.fullName.take(2).uppercase() else "DC",
                                    fontFamily = PlusJakartaSans,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 24.sp,
                                    color = primaryBright(),
                                )
                            }
                            Spacer(Modifier.height(12.dp))
                            Text(
                                text = "Profile Avatar",
                                fontFamily = Inter,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.onSurface,
                            )
                            Text(
                                text = "Avatar can be updated anytime under settings",
                                fontFamily = Inter,
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                    }

                    Spacer(Modifier.height(16.dp))
                    DomiTextField(
                        value = s.signatureName,
                        onValueChange = { v -> vm.update { it.copy(signatureName = v) } },
                        label = "Digital Signature (Full Legal Name)",
                        placeholder = s.fullName.ifEmpty { "Enter your full name" },
                        helper = "By entering your name you sign and attest to your application",
                    )
                }
                6 -> {
                    // Step 6: Review & Payment
                    Text(
                        text = "Review & Payment",
                        fontFamily = PlusJakartaSans,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                    Spacer(Modifier.height(12.dp))

                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        color = MaterialTheme.colorScheme.surfaceContainerLow,
                        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                        ) {
                            Text(
                                text = "APPLICANT SUMMARY",
                                fontFamily = Inter,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                letterSpacing = 1.sp,
                            )
                            Text("Name: ${s.fullName}", fontFamily = Inter, fontSize = 14.sp)
                            Text("Email: ${s.email}", fontFamily = Inter, fontSize = 14.sp)
                            Text("Phone: ${s.phone}", fontFamily = Inter, fontSize = 14.sp)
                            Text("Bank: ${s.bankName} (${s.bankAccount})", fontFamily = Inter, fontSize = 14.sp)
                            Text("Monthly Subscription: ₦${s.monthlySubscription}", fontFamily = Inter, fontSize = 14.sp)
                        }
                    }

                    Spacer(Modifier.height(16.dp))

                    // Fees card
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        color = MaterialTheme.colorScheme.surface,
                        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                        shadowElevation = 2.dp,
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                            ) {
                                Text("Registration Fee", fontFamily = Inter, fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Money(amount = 20000.0, size = MoneySize.SM)
                            }
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                            ) {
                                Text("Social Fee", fontFamily = Inter, fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Money(amount = 1000.0, size = MoneySize.SM)
                            }
                            HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Text("Total Amount Due", fontFamily = PlusJakartaSans, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                                Money(amount = if (totalDue > 0) totalDue else 21000.0, size = MoneySize.LG, tone = com.nexahub.domicoop.ui.common.MoneyTone.DEFAULT)
                            }
                        }
                    }

                    Spacer(Modifier.height(16.dp))

                    // Terms agreement
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Checkbox(
                            checked = s.termsAccepted,
                            onCheckedChange = { v -> vm.update { it.copy(termsAccepted = v) } },
                            colors = CheckboxDefaults.colors(checkedColor = MaterialTheme.colorScheme.primary),
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(
                            text = "I agree to Dominion Cooperative guidelines, bye-laws and regulations.",
                            fontFamily = Inter,
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.weight(1f),
                        )
                    }

                    Spacer(Modifier.height(12.dp))
                    DomiTextField(
                        value = payRef,
                        onValueChange = { payRef = it },
                        label = "Paystack Payment Reference",
                        placeholder = "e.g. domi_reg_xxx or reference from receipt",
                        helper = "Enter the reference after completing payment via Paystack",
                    )
                }
            }

            if (s.error != null) {
                Spacer(Modifier.height(12.dp))
                Text(
                    text = s.error!!,
                    fontFamily = Inter,
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.error,
                )
            }

            Spacer(Modifier.height(24.dp))

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                if (s.step > 1) {
                    DomiButton(
                        text = "Back",
                        onClick = vm::prev,
                        tonal = true,
                        modifier = Modifier.weight(1f),
                    )
                }

                if (s.step < 6) {
                    DomiButton(
                        text = "Continue",
                        onClick = {
                            if (s.step == 1 && (s.email.isBlank() || s.password.isBlank())) {
                                vm.update { it.copy(error = "Please provide an email and password.") }
                            } else if (s.step == 1 && s.password != s.confirmPassword) {
                                vm.update { it.copy(error = "Passwords do not match.") }
                            } else if (s.step == 2 && (s.fullName.isBlank() || s.phone.isBlank())) {
                                vm.update { it.copy(error = "Please enter your full name and phone number.") }
                            } else {
                                vm.next()
                            }
                        },
                        icon = Icons.AutoMirrored.Filled.ArrowForward,
                        modifier = Modifier.weight(1f),
                    )
                } else {
                    DomiButton(
                        text = "Submit Application",
                        onClick = {
                            if (!s.termsAccepted) {
                                vm.update { it.copy(error = "Please accept the cooperative guidelines to proceed.") }
                            } else if (payRef.isBlank()) {
                                vm.update { it.copy(error = "Please provide your Paystack payment reference.") }
                            } else {
                                vm.submit(payRef.trim(), onDone)
                            }
                        },
                        loading = s.loading,
                        icon = Icons.Default.Check,
                        modifier = Modifier.weight(1f),
                    )
                }
            }

            Spacer(Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text("Already a member? ", fontFamily = Inter, fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text(
                    text = "Sign in",
                    fontFamily = Inter,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = primaryBright(),
                    modifier = Modifier.clickable(onClick = onSignIn),
                )
            }
            Spacer(Modifier.height(32.dp))
        }
    }
}

// ============================================================================
// 5. Forgot Password Screen (mirrors app/(auth)/forgot-password.tsx)
// ============================================================================

@Composable
fun ForgotPasswordScreen(onBack: () -> Unit) {
    var email by remember { mutableStateOf("") }
    var sent by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
        ) {
            // Hero section
            HeroSection(
                title = "Forgot Password?",
                subtitle = "Enter your email and we'll send you a reset link",
                showLogo = false,
                height = 260.dp,
                imageRes = R.drawable.hero_password,
            )

            // Centered lock icon
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .offset(y = (-20).dp),
                contentAlignment = Alignment.Center,
            ) {
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = MaterialTheme.colorScheme.surface,
                    shadowElevation = 4.dp,
                ) {
                    Box(modifier = Modifier.padding(14.dp)) {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = null,
                            tint = primaryBright(),
                            modifier = Modifier.size(36.dp),
                        )
                    }
                }
            }

            // Form card
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
            ) {
                FormCard(overlap = false) {
                    if (sent) {
                        Text(
                            text = "Reset instructions sent! Check your inbox.",
                            fontFamily = Inter,
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.primary,
                            textAlign = TextAlign.Center,
                        )
                    } else {
                        DomiTextField(
                            value = email,
                            onValueChange = { email = it },
                            label = "Email Address",
                            placeholder = "name@institution.com",
                            keyboardType = KeyboardType.Email,
                        )

                        DomiButton(
                            text = "Send Reset Link",
                            onClick = { sent = true },
                            icon = Icons.AutoMirrored.Filled.ArrowForward,
                            size = ButtonSize.LG,
                        )
                    }

                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center,
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Remember your password? ",
                                fontFamily = Inter,
                                fontSize = 13.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                            Text(
                                text = "Sign In",
                                fontFamily = Inter,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = primaryBright(),
                                modifier = Modifier.clickable(onClick = onBack),
                            )
                        }
                    }
                }
            }

            // Footer
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                HorizontalDivider(
                    color = MaterialTheme.colorScheme.outlineVariant,
                    thickness = 1.dp,
                    modifier = Modifier.padding(bottom = 16.dp),
                )
                Text(
                    text = "Join Archive",
                    fontFamily = PlusJakartaSans,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = primaryBright(),
                    modifier = Modifier.clickable(onClick = onBack),
                )
            }
        }

        BackButton(
            onPress = onBack,
            modifier = Modifier
                .align(Alignment.TopStart)
                .statusBarsPadding()
                .padding(top = 16.dp, start = 20.dp),
        )

        SecurityBadge(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .statusBarsPadding()
                .padding(top = 16.dp, end = 20.dp),
        )
    }
}

// ============================================================================
// 6. Reset Password Screen (mirrors app/(auth)/reset-password.tsx)
// ============================================================================

@Composable
fun ResetPasswordScreen(onDone: () -> Unit) {
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    val passwordsMatch = newPassword.isNotEmpty() && newPassword == confirmPassword

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
        ) {
            HeroSection(
                title = "Reset Password",
                subtitle = "Create a new secure password for your account",
                showLogo = false,
                height = 260.dp,
                imageRes = R.drawable.hero_password,
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .offset(y = (-28).dp)
                    .padding(horizontal = 20.dp),
            ) {
                FormCard(overlap = false) {
                    DomiTextField(
                        value = newPassword,
                        onValueChange = { newPassword = it },
                        label = "New Password",
                        placeholder = "Enter new password",
                        isPassword = true,
                    )

                    Text(
                        text = "Use 8+ characters with a mix of letters, numbers & symbols.",
                        fontFamily = Inter,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(start = 4.dp),
                    )

                    DomiTextField(
                        value = confirmPassword,
                        onValueChange = { confirmPassword = it },
                        label = "Confirm Password",
                        placeholder = "Re-enter new password",
                        isPassword = true,
                        error = if (confirmPassword.isNotEmpty() && !passwordsMatch) "Passwords do not match" else null,
                    )

                    DomiButton(
                        text = "Update Password",
                        onClick = onDone,
                        enabled = passwordsMatch && newPassword.length >= 8,
                        icon = Icons.AutoMirrored.Filled.ArrowForward,
                        size = ButtonSize.LG,
                    )

                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center,
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Remembered your password? ",
                                fontFamily = Inter,
                                fontSize = 13.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                            Text(
                                text = "Sign In",
                                fontFamily = Inter,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = primaryBright(),
                                modifier = Modifier.clickable(onClick = onDone),
                            )
                        }
                    }
                }
            }
        }

        BackButton(
            onPress = onDone,
            modifier = Modifier
                .align(Alignment.TopStart)
                .statusBarsPadding()
                .padding(top = 16.dp, start = 20.dp),
        )

        SecurityBadge(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .statusBarsPadding()
                .padding(top = 16.dp, end = 20.dp),
        )
    }
}

// ============================================================================
// 7. Registration Closed Screen (mirrors app/(auth)/registration-closed.tsx)
// ============================================================================

@Composable
fun RegistrationClosedScreen(
    reason: String?,
    onRetry: () -> Unit,
    onSignIn: () -> Unit,
) {
    val (title, body) = when (reason) {
        "not_yet_open" -> "Registration opens soon" to "New members will be able to apply once the cooperative opens this intake."
        "period_ended" -> "Registration has closed" to "This intake has closed. The cooperative will announce the next one."
        "at_capacity" -> "This intake is full" to "Every place in the current intake has been taken. The cooperative will announce the next one."
        else -> "Registration is closed" to "The cooperative registers new members during set periods. There is no intake open at the moment."
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding(),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Box(
                modifier = Modifier
                    .size(88.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surfaceContainer),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = Icons.Default.EventBusy,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(40.dp),
                )
            }

            Spacer(Modifier.height(24.dp))
            Text(
                text = title,
                fontFamily = PlusJakartaSans,
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center,
            )
            Spacer(Modifier.height(10.dp))
            Text(
                text = body,
                fontFamily = Inter,
                fontSize = 15.sp,
                lineHeight = 22.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 16.dp),
            )

            Spacer(Modifier.height(32.dp))
            DomiButton(
                text = "Try again",
                onClick = onRetry,
                tonal = true,
                size = ButtonSize.LG,
            )

            Spacer(Modifier.height(16.dp))
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "Already a member? ",
                    fontFamily = Inter,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Text(
                    text = "Sign in",
                    fontFamily = Inter,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = primaryBright(),
                    modifier = Modifier.clickable(onClick = onSignIn),
                )
            }
        }

        BackButton(
            onPress = onRetry,
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(top = 16.dp, start = 20.dp),
        )
    }
}
