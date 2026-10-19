package com.nexahub.domicoop.ui.tabs

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nexahub.domicoop.core.ApiException
import com.nexahub.domicoop.data.remote.ApiContribution
import com.nexahub.domicoop.data.remote.ApiLoan
import com.nexahub.domicoop.data.remote.DashboardSummary
import com.nexahub.domicoop.data.remote.Profile
import com.nexahub.domicoop.data.repository.AppContainer
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class DashboardUiState(
    val loading: Boolean = true,
    val data: DashboardSummary? = null,
    val offline: Boolean = false,
    val error: String? = null,
)

class DashboardViewModel(private val app: AppContainer) : ViewModel() {
    private val _state = MutableStateFlow(DashboardUiState())
    val state: StateFlow<DashboardUiState> = _state

    init { refresh() }

    fun refresh() {
        _state.value = _state.value.copy(loading = true, error = null)
        viewModelScope.launch {
            try {
                val res = app.dashboard.summary()
                _state.value = DashboardUiState(loading = false, data = res)
            } catch (e: ApiException) {
                _state.value = DashboardUiState(
                    loading = false,
                    error = e.message,
                    offline = e.status == 0,
                )
            } catch (e: Exception) {
                _state.value = DashboardUiState(loading = false, error = e.message)
            }
        }
    }
}

data class ContributionsUiState(
    val loading: Boolean = true,
    val items: List<ApiContribution> = emptyList(),
    val totalBalance: Double = 0.0,
    val yearBalance: Double = 0.0,
    val error: String? = null,
    val offline: Boolean = false,
)

class ContributionsViewModel(private val app: AppContainer) : ViewModel() {
    private val _state = MutableStateFlow(ContributionsUiState())
    val state: StateFlow<ContributionsUiState> = _state

    init { refresh() }

    fun refresh(year: Int? = null) {
        _state.value = _state.value.copy(loading = true, error = null)
        viewModelScope.launch {
            try {
                val res = app.contributions.myContributions(year)
                _state.value = ContributionsUiState(
                    loading = false,
                    items = res.contributions,
                    totalBalance = res.totalBalance,
                    yearBalance = res.yearBalance,
                )
            } catch (e: ApiException) {
                _state.value = _state.value.copy(loading = false, error = e.message, offline = e.status == 0)
            } catch (e: Exception) {
                _state.value = _state.value.copy(loading = false, error = e.message)
            }
        }
    }
}

data class LoansUiState(
    val loading: Boolean = true,
    val items: List<ApiLoan> = emptyList(),
    val error: String? = null,
    val offline: Boolean = false,
) {
    val totalDebt: Double get() = items.sumOf { (it.balance ?: it.amountApproved ?: it.amountRequested) }
}

class LoansViewModel(private val app: AppContainer) : ViewModel() {
    private val _state = MutableStateFlow(LoansUiState())
    val state: StateFlow<LoansUiState> = _state

    init { refresh() }

    fun refresh() {
        _state.value = _state.value.copy(loading = true, error = null)
        viewModelScope.launch {
            try {
                val res = app.loans.myLoans()
                _state.value = LoansUiState(loading = false, items = res)
            } catch (e: ApiException) {
                _state.value = _state.value.copy(loading = false, error = e.message, offline = e.status == 0)
            } catch (e: Exception) {
                _state.value = _state.value.copy(loading = false, error = e.message)
            }
        }
    }
}

data class ProfileUiState(
    val loading: Boolean = true,
    val profile: Profile? = null,
    val error: String? = null,
)

class ProfileViewModel(private val app: AppContainer) : ViewModel() {
    private val _state = MutableStateFlow(ProfileUiState())
    val state: StateFlow<ProfileUiState> = _state

    init { refresh() }

    fun refresh() {
        _state.value = _state.value.copy(loading = true, error = null)
        viewModelScope.launch {
            try {
                _state.value = ProfileUiState(loading = false, profile = app.members.profile())
            } catch (e: ApiException) {
                _state.value = ProfileUiState(loading = false, error = e.message)
            } catch (e: Exception) {
                _state.value = ProfileUiState(loading = false, error = e.message)
            }
        }
    }

    fun logout(onDone: () -> Unit) {
        viewModelScope.launch {
            app.auth.logout()
            onDone()
        }
    }
}
