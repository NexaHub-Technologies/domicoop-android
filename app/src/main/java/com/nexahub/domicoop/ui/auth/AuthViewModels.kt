package com.nexahub.domicoop.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nexahub.domicoop.core.ApiException
import com.nexahub.domicoop.data.repository.AppContainer
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class SignInUiState(
    val email: String = "",
    val password: String = "",
    val loading: Boolean = false,
    val error: String? = null,
    val unverified: Boolean = false,
    val loggedIn: Boolean = false,
)

class SignInViewModel(private val app: AppContainer) : ViewModel() {
    private val _state = MutableStateFlow(SignInUiState())
    val state: StateFlow<SignInUiState> = _state

    fun setEmail(v: String) { _state.value = _state.value.copy(email = v, error = null) }
    fun setPassword(v: String) { _state.value = _state.value.copy(password = v, error = null) }
    fun consumeLogin() { _state.value = _state.value.copy(loggedIn = false) }

    fun signIn() {
        val s = _state.value
        if (s.email.isBlank() || s.password.isBlank()) {
            _state.value = s.copy(error = "Enter your email and password.")
            return
        }
        _state.value = s.copy(loading = true, error = null)
        viewModelScope.launch {
            try {
                app.auth.login(s.email.trim(), s.password)
                _state.value = _state.value.copy(loading = false, loggedIn = true)
            } catch (e: ApiException) {
                val unverified = e.status == 403 &&
                    (e.body?.contains("verif", ignoreCase = true) == true ||
                        e.message.contains("verif", ignoreCase = true))
                _state.value = _state.value.copy(
                    loading = false,
                    error = e.message,
                    unverified = unverified,
                )
            } catch (e: Exception) {
                _state.value = _state.value.copy(loading = false, error = e.message ?: "Sign in failed")
            }
        }
    }
}

data class RegistrationWindowUiState(
    val loading: Boolean = true,
    val isOpen: Boolean = false,
    val reason: String? = null,
    val totalDue: Double = 0.0,
    val registrationFee: Double = 20000.0,
    val socialFee: Double = 1000.0,
    val closesAt: String? = null,
    val opensAt: String? = null,
    val name: String? = null,
    val error: String? = null,
)

class RegistrationWindowViewModel(private val app: AppContainer) : ViewModel() {
    private val _state = MutableStateFlow(RegistrationWindowUiState())
    val state: StateFlow<RegistrationWindowUiState> = _state

    init { refresh() }

    fun refresh() {
        _state.value = _state.value.copy(loading = true, error = null)
        viewModelScope.launch {
            try {
                val w = app.registration.window()
                _state.value = RegistrationWindowUiState(
                    loading = false,
                    isOpen = w.isOpen,
                    reason = w.reason,
                    totalDue = w.window?.totalDue ?: 21000.0,
                    registrationFee = w.window?.registrationFee ?: 20000.0,
                    socialFee = w.window?.socialFee ?: 1000.0,
                    closesAt = w.window?.closesAt,
                    opensAt = w.window?.opensAt,
                    name = w.window?.name,
                )
            } catch (e: ApiException) {
                _state.value = RegistrationWindowUiState(loading = false, error = e.message)
            } catch (e: Exception) {
                _state.value = RegistrationWindowUiState(loading = false, error = e.message)
            }
        }
    }
}

// Sign-up form state (6 steps mirror app/(auth)/sign-up.tsx)
data class SignUpForm(
    val email: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val fullName: String = "",
    val sex: String = "",
    val dob: String = "",
    val phone: String = "",
    val whatsappNumber: String = "",
    val maritalStatus: String = "",
    val idCardNumber: String = "",
    val address: String = "",
    val placeOfWork: String = "",
    val typeOfBusiness: String = "",
    val nextOfKin: String = "",
    val bankName: String = "",
    val bankAccount: String = "",
    val bankCode: String = "",
    val monthlySubscription: String = "5000",
    val referredBy: String = "",
    val signatureName: String = "",
    val termsAccepted: Boolean = false,
    val step: Int = 1,
    val loading: Boolean = false,
    val error: String? = null,
    val done: Boolean = false,
)

class SignUpViewModel(private val app: AppContainer) : ViewModel() {
    private val _state = MutableStateFlow(SignUpForm())
    val state: StateFlow<SignUpForm> = _state

    fun update(fn: (SignUpForm) -> SignUpForm) {
        _state.value = fn(_state.value).copy(error = null)
    }

    fun next() { _state.value = _state.value.copy(step = (_state.value.step + 1).coerceAtMost(6)) }
    fun prev() { _state.value = _state.value.copy(step = (_state.value.step - 1).coerceAtLeast(1)) }

    fun submit(paymentReference: String, onDone: () -> Unit) {
        val s = _state.value
        _state.value = s.copy(loading = true, error = null)
        viewModelScope.launch {
            try {
                val monthly = s.monthlySubscription.toDoubleOrNull()
                try { app.registration.precheck(s.email, monthly) } catch (_: Exception) { }
                app.registration.apply(
                    com.nexahub.domicoop.data.remote.MembershipApplicationRequest(
                        email = s.email.trim(),
                        password = s.password,
                        fullName = s.fullName.trim(),
                        sex = s.sex.ifBlank { null },
                        phone = s.phone.trim(),
                        address = s.address.trim(),
                        bankName = s.bankName.trim(),
                        bankAccount = s.bankAccount.trim(),
                        bankCode = s.bankCode.trim(),
                        nextOfKin = s.nextOfKin.trim().ifBlank { null },
                        monthlySubscription = monthly,
                        paymentReference = paymentReference,
                    )
                )
                // Auto-login like the RN flow.
                app.auth.login(s.email.trim(), s.password)
                _state.value = _state.value.copy(loading = false, done = true)
                onDone()
            } catch (e: ApiException) {
                _state.value = _state.value.copy(loading = false, error = e.message)
            } catch (e: Exception) {
                _state.value = _state.value.copy(loading = false, error = e.message ?: "Registration failed")
            }
        }
    }
}
