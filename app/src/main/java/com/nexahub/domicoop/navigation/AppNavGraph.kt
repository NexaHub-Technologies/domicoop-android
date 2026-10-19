package com.nexahub.domicoop.navigation

import android.net.Uri
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.nexahub.domicoop.data.repository.AppContainer
import com.nexahub.domicoop.theme.ThemePreference
import com.nexahub.domicoop.ui.auth.ForgotPasswordScreen
import com.nexahub.domicoop.ui.auth.RegistrationClosedScreen
import com.nexahub.domicoop.ui.auth.ResetPasswordScreen
import com.nexahub.domicoop.ui.auth.SignInScreen
import com.nexahub.domicoop.ui.auth.SignUpScreen
import com.nexahub.domicoop.ui.auth.SplashScreen
import com.nexahub.domicoop.ui.auth.WelcomeScreen
import com.nexahub.domicoop.ui.detail.ContributionDetailScreen
import com.nexahub.domicoop.ui.detail.LoanDetailScreen
import com.nexahub.domicoop.ui.more.AnnouncementDetailScreen
import com.nexahub.domicoop.ui.more.ChangePasswordScreen
import com.nexahub.domicoop.ui.more.EditProfileScreen
import com.nexahub.domicoop.ui.more.GuidelinesScreen
import com.nexahub.domicoop.ui.more.NotificationPrefsScreen
import com.nexahub.domicoop.ui.more.NotificationsScreen
import com.nexahub.domicoop.ui.more.PrivacyPolicyScreen
import com.nexahub.domicoop.ui.more.SecurityScreen
import com.nexahub.domicoop.ui.more.SuccessScreen
import com.nexahub.domicoop.ui.more.SupportScreen
import com.nexahub.domicoop.ui.more.ThemePreferenceScreen
import com.nexahub.domicoop.ui.tabs.MainTabsScreen
import com.nexahub.domicoop.ui.transactions.AddContributionScreen
import com.nexahub.domicoop.ui.transactions.ApplyForLoanScreen
import com.nexahub.domicoop.ui.transactions.MakePaymentScreen
import kotlinx.coroutines.launch

@Composable
fun AppNavGraph(
    app: AppContainer,
    themePref: ThemePreference,
    onThemeChange: (ThemePreference) -> Unit,
) {
    val nav = rememberNavController()
    val scope = rememberCoroutineScope()

    NavHost(navController = nav, startDestination = Routes.BOOT) {
        composable(Routes.BOOT) {
            // Session gate — mirrors app/index.tsx
            LaunchedEffect(Unit) {
                val valid = try { app.session.isValid() } catch (_: Exception) { false }
                if (valid) {
                    val acked = try { app.session.isGuidelinesAcknowledged() } catch (_: Exception) { true }
                    if (!acked) nav.navigate(Routes.GUIDELINES) { popUpTo(Routes.BOOT) { inclusive = true } }
                    else nav.navigate(Routes.TABS) { popUpTo(Routes.BOOT) { inclusive = true } }
                } else {
                    try { app.session.clear() } catch (_: Exception) { }
                    nav.navigate(Routes.SPLASH) { popUpTo(Routes.BOOT) { inclusive = true } }
                }
            }
            Box(Modifier.fillMaxSize())
        }

        composable(Routes.SPLASH) {
            SplashScreen(onGetStarted = { nav.navigate("${Routes.GUIDELINES}?onboarding=true") })
        }

        composable(
            "${Routes.GUIDELINES}?onboarding={onboarding}",
            arguments = listOf(navArgument("onboarding") { defaultValue = "false" }),
        ) { backStack ->
            val onboarding = backStack.arguments?.getString("onboarding") == "true"
            GuidelinesScreen(
                onboarding = onboarding,
                onAcknowledge = {
                    scope.launch {
                        try {
                            app.session.markGuidelinesSeen()
                            app.session.acknowledgeGuidelines()
                        } catch (_: Exception) { }
                        if (onboarding) nav.navigate(Routes.WELCOME) { popUpTo(Routes.SPLASH) { inclusive = true } }
                        else nav.popBackStack()
                    }
                },
                onBack = { nav.popBackStack() },
            )
        }

        composable(Routes.WELCOME) {
            WelcomeScreen(
                app = app,
                onCreateAccount = {
                    scope.launch {
                        try {
                            val w = app.registration.window()
                            if (w.isOpen) nav.navigate("${Routes.SIGN_UP}?totalDue=${w.window?.totalDue ?: 0.0}")
                            else nav.navigate("${Routes.REG_CLOSED}?reason=${w.reason ?: "closed"}")
                        } catch (_: Exception) {
                            nav.navigate("${Routes.SIGN_UP}?totalDue=0.0")
                        }
                    }
                },
                onSignIn = { nav.navigate(Routes.SIGN_IN) },
            )
        }

        composable(Routes.SIGN_IN) {
            SignInScreen(
                app = app,
                onSignedIn = { nav.navigate(Routes.TABS) { popUpTo(Routes.SPLASH) { inclusive = true } } },
                onForgot = { nav.navigate(Routes.FORGOT) },
                onSignUp = { nav.navigate("${Routes.SIGN_UP}?totalDue=0.0") },
            )
        }

        composable(
            "${Routes.SIGN_UP}?totalDue={totalDue}",
            arguments = listOf(navArgument("totalDue") { defaultValue = "0.0" }),
        ) { backStack ->
            val totalDue = backStack.arguments?.getString("totalDue")?.toDoubleOrNull() ?: 0.0
            SignUpScreen(
                app = app,
                totalDue = totalDue,
                onDone = { nav.navigate(Routes.TABS) { popUpTo(Routes.SPLASH) { inclusive = true } } },
                onSignIn = { nav.navigate(Routes.SIGN_IN) },
            )
        }

        composable(Routes.FORGOT) {
            ForgotPasswordScreen(onBack = { nav.popBackStack() })
        }
        composable(Routes.RESET) {
            ResetPasswordScreen(onDone = { nav.navigate(Routes.SIGN_IN) { popUpTo(Routes.SIGN_IN) { inclusive = true } } })
        }
        composable(
            "${Routes.REG_CLOSED}?reason={reason}",
            arguments = listOf(navArgument("reason") { defaultValue = "" }),
        ) { backStack ->
            RegistrationClosedScreen(
                reason = backStack.arguments?.getString("reason"),
                onRetry = { nav.popBackStack() },
                onSignIn = { nav.navigate(Routes.SIGN_IN) },
            )
        }

        composable(Routes.TABS) {
            MainTabsScreen(
                app = app,
                onContributionClick = { nav.navigate(Routes.contributionDetail(it)) },
                onLoanClick = { nav.navigate(Routes.loanDetail(it)) },
                onAddContribution = { nav.navigate(Routes.ADD_CONTRIBUTION) },
                onApplyLoan = { nav.navigate(Routes.APPLY_LOAN) },
                onAnnouncementClick = { t, b, ts ->
                    nav.navigate("announcement_detail?title=${Uri.encode(t)}&body=${Uri.encode(b)}&ts=${Uri.encode(ts)}")
                },
                onNotificationsClick = { nav.navigate(Routes.NOTIFICATIONS) },
                onEditProfile = { nav.navigate(Routes.EDIT_PROFILE) },
                onSecurity = { nav.navigate(Routes.SECURITY) },
                onNotifPrefs = { nav.navigate(Routes.NOTIF_PREFS) },
                onThemePref = { nav.navigate(Routes.THEME_PREF) },
                onSupport = { nav.navigate(Routes.SUPPORT) },
                onGuidelines = { nav.navigate(Routes.GUIDELINES) },
                onPrivacy = { nav.navigate(Routes.PRIVACY) },
                onLoggedOut = { nav.navigate(Routes.SPLASH) { popUpTo(Routes.TABS) { inclusive = true } } },
            )
        }

        composable(
            Routes.CONTRIBUTION_DETAIL,
            arguments = listOf(navArgument("id") { type = NavType.StringType }),
        ) { backStack ->
            ContributionDetailScreen(
                app = app,
                id = backStack.arguments?.getString("id") ?: "",
                onBack = { nav.popBackStack() },
            )
        }

        composable(
            Routes.LOAN_DETAIL,
            arguments = listOf(navArgument("id") { type = NavType.StringType }),
        ) { backStack ->
            val id = backStack.arguments?.getString("id") ?: ""
            LoanDetailScreen(
                app = app,
                id = id,
                onPay = { nav.navigate("${Routes.MAKE_PAYMENT}?loanId=$id") },
                onBack = { nav.popBackStack() },
            )
        }

        composable(Routes.ADD_CONTRIBUTION) {
            AddContributionScreen(
                app = app,
                onSuccess = {
                    nav.navigate("success?title=${Uri.encode("Contribution received")}&message=${Uri.encode("Your payment is being verified.")}&amount=0.0") {
                        popUpTo(Routes.TABS)
                    }
                },
                onBack = { nav.popBackStack() },
            )
        }

        composable(
            "${Routes.MAKE_PAYMENT}?loanId={loanId}",
            arguments = listOf(navArgument("loanId") { defaultValue = "" }),
        ) { backStack ->
            MakePaymentScreen(
                app = app,
                preselectedLoanId = backStack.arguments?.getString("loanId")?.ifBlank { null },
                onSuccess = {
                    nav.navigate("success?title=${Uri.encode("Payment received")}&message=${Uri.encode("Your repayment is being verified.")}&amount=0.0") {
                        popUpTo(Routes.TABS)
                    }
                },
                onBack = { nav.popBackStack() },
            )
        }

        composable(Routes.APPLY_LOAN) {
            ApplyForLoanScreen(
                app = app,
                onSuccess = {
                    nav.navigate("success?title=${Uri.encode("Application submitted")}&message=${Uri.encode("Your loan is under review.")}&amount=0.0") {
                        popUpTo(Routes.TABS)
                    }
                },
                onBack = { nav.popBackStack() },
            )
        }

        composable(Routes.NOTIFICATIONS) {
            NotificationsScreen(app = app, onBack = { nav.popBackStack() })
        }

        composable(
            "announcement_detail?title={title}&body={body}&ts={ts}",
            arguments = listOf(
                navArgument("title") { defaultValue = "" },
                navArgument("body") { defaultValue = "" },
                navArgument("ts") { defaultValue = "" },
            ),
        ) { backStack ->
            AnnouncementDetailScreen(
                title = backStack.arguments?.getString("title") ?: "",
                body = backStack.arguments?.getString("body") ?: "",
                timestamp = backStack.arguments?.getString("ts") ?: "",
                onBack = { nav.popBackStack() },
            )
        }

        composable(Routes.EDIT_PROFILE) {
            EditProfileScreen(app = app, onDone = { nav.popBackStack() }, onBack = { nav.popBackStack() })
        }
        composable(Routes.SECURITY) {
            SecurityScreen(onChangePassword = { nav.navigate(Routes.CHANGE_PASSWORD) }, onBack = { nav.popBackStack() })
        }
        composable(Routes.CHANGE_PASSWORD) {
            com.nexahub.domicoop.ui.more.ChangePasswordScreen(onBack = { nav.popBackStack() })
        }
        composable(Routes.THEME_PREF) {
            ThemePreferenceScreen(current = themePref, onPick = onThemeChange, onBack = { nav.popBackStack() })
        }
        composable(Routes.NOTIF_PREFS) {
            NotificationPrefsScreen(app = app, onBack = { nav.popBackStack() })
        }
        composable(Routes.PRIVACY) {
            PrivacyPolicyScreen(onBack = { nav.popBackStack() })
        }
        composable(Routes.SUPPORT) {
            SupportScreen(onBack = { nav.popBackStack() })
        }
        composable(
            "success?title={title}&message={message}&amount={amount}",
            arguments = listOf(
                navArgument("title") { defaultValue = "Done" },
                navArgument("message") { defaultValue = "" },
                navArgument("amount") { defaultValue = "0.0" },
            ),
        ) { backStack ->
            SuccessScreen(
                title = backStack.arguments?.getString("title") ?: "Done",
                message = backStack.arguments?.getString("message") ?: "",
                amount = backStack.arguments?.getString("amount")?.toDoubleOrNull(),
                onHome = { nav.navigate(Routes.TABS) { popUpTo(Routes.TABS) { inclusive = true } } },
            )
        }
    }
}
