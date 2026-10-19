package com.nexahub.domicoop.data.repository

import com.nexahub.domicoop.core.ApiException
import com.nexahub.domicoop.data.local.JsonCache
import com.nexahub.domicoop.data.local.SessionManager
import com.nexahub.domicoop.data.remote.ApiService
import com.nexahub.domicoop.data.remote.DomiJson
import com.nexahub.domicoop.data.remote.LoginRequest
import com.nexahub.domicoop.data.remote.MembershipApplicationRequest
import com.nexahub.domicoop.data.remote.PrecheckRequest
import com.nexahub.domicoop.data.remote.provideApi
import com.nexahub.domicoop.data.remote.provideOkHttp
import com.nexahub.domicoop.data.remote.safeCall
import android.content.Context
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.decodeFromJsonElement

class AppContainer(context: Context) {
    private val appContext = context.applicationContext
    val session: SessionManager = SessionManager(appContext)
    val cache: JsonCache = JsonCache(appContext)
    val api: ApiService by lazy { provideApi(provideOkHttp(session)) }

    val auth: AuthRepository by lazy { AuthRepository(api, session, cache) }
    val registration: RegistrationRepository by lazy { RegistrationRepository(api) }
    val contributions: ContributionsRepository by lazy { ContributionsRepository(api, cache) }
    val loans: LoansRepository by lazy { LoansRepository(api, cache) }
    val dashboard: DashboardRepository by lazy { DashboardRepository(api, cache) }
    val members: MembersRepository by lazy { MembersRepository(api, cache) }
    val notifications: NotificationsRepository by lazy { NotificationsRepository(api, cache) }
}

class AuthRepository(
    private val api: ApiService,
    private val session: SessionManager,
    private val cache: JsonCache,
) {
    suspend fun login(email: String, password: String) {
        val res = safeCall { api.login(LoginRequest(email.trim(), password)) }
        session.saveTokens(res.accessToken, res.refreshToken)
        session.saveEmail(email.trim())
        session.touch()
    }

    suspend fun logout() {
        try {
            val token = session.getToken()
            if (token != null) safeCall { api.logout() }
        } catch (_: Exception) {
        } finally {
            try {
                // Best-effort device unregister is done by caller when token known.
            } catch (_: Exception) {
            }
            session.clear()
            cache.clearAll()
        }
    }
}

class RegistrationRepository(private val api: ApiService) {
    suspend fun window() = safeCall { api.registrationWindow() }

    suspend fun precheck(email: String, monthly: Double? = null) {
        safeCall { api.registrationPrecheck(PrecheckRequest(email, monthly)) }
    }

    suspend fun apply(req: MembershipApplicationRequest) =
        safeCall { api.registrationApply(req) }
}

class ContributionsRepository(
    private val api: ApiService,
    private val cache: JsonCache,
) {
    suspend fun myContributions(year: Int? = null) =
        try {
            val res = safeCall { api.myContributions(year = year) }
            cache.put(JsonCache.CONTRIBUTIONS, DomiJson.encodeToString(res))
            res
        } catch (e: ApiException) {
            if (e.status == 403) throw e
            val cached = cache.get(JsonCache.CONTRIBUTIONS)
            if (cached != null) DomiJson.decodeFromString<com.nexahub.domicoop.data.remote.ApiContributionsResponse>(cached)
            else throw e
        }

    suspend fun byId(id: String) = safeCall { api.contributionById(id) }

    suspend fun verify(reference: String, month: String, year: Int) =
        safeCall { api.verifyContribution(com.nexahub.domicoop.data.remote.VerifyContributionRequest(reference, month, year)) }
}

class LoansRepository(
    private val api: ApiService,
    private val cache: JsonCache,
) {
    suspend fun myLoans(): List<com.nexahub.domicoop.data.remote.ApiLoan> {
        return try {
            val raw = safeCall { api.myLoansRaw() }
            val res: List<com.nexahub.domicoop.data.remote.ApiLoan> = try {
                DomiJson.decodeFromJsonElement<List<com.nexahub.domicoop.data.remote.ApiLoan>>(raw)
            } catch (_: Exception) {
                // {loans: [...]} wrapper fallback (RN transformLoansResponse).
                DomiJson.decodeFromJsonElement<com.nexahub.domicoop.data.remote.ApiLoansWrapper>(raw).loans
            }
            cache.put(JsonCache.LOANS, DomiJson.encodeToString(res))
            res
        } catch (e: ApiException) {
            if (e.status == 403) throw e
            val cached = cache.get(JsonCache.LOANS)
            if (cached != null) DomiJson.decodeFromString<List<com.nexahub.domicoop.data.remote.ApiLoan>>(cached)
            else throw e
        }
    }

    suspend fun byId(id: String) = safeCall { api.loanById(id) }

    suspend fun apply(req: com.nexahub.domicoop.data.remote.LoanApplyRequest): com.nexahub.domicoop.data.remote.ApiLoan? {
        val raw = safeCall { api.applyForLoanRaw(req) }
        return try {
            DomiJson.decodeFromJsonElement<com.nexahub.domicoop.data.remote.LoanApplyWrapper>(raw).loan
        } catch (_: Exception) {
            try {
                DomiJson.decodeFromJsonElement<com.nexahub.domicoop.data.remote.ApiLoan>(raw)
            } catch (_: Exception) {
                null
            }
        }
    }

    suspend fun repay(loanId: String, reference: String) =
        safeCall { api.repayLoan(loanId, com.nexahub.domicoop.data.remote.RepaymentRequest(reference)) }
}

class DashboardRepository(
    private val api: ApiService,
    private val cache: JsonCache,
) {
    suspend fun summary(): com.nexahub.domicoop.data.remote.DashboardSummary {
        return try {
            val res = safeCall { api.dashboardSummary() }
            cache.put(JsonCache.DASHBOARD, DomiJson.encodeToString(res))
            res
        } catch (e: ApiException) {
            if (e.status == 403) throw e
            val cached = cache.get(JsonCache.DASHBOARD)
            if (cached != null) DomiJson.decodeFromString<com.nexahub.domicoop.data.remote.DashboardSummary>(cached) else throw e
        }
    }
}

class MembersRepository(
    private val api: ApiService,
    private val cache: JsonCache,
) {
    suspend fun profile(): com.nexahub.domicoop.data.remote.Profile {
        return try {
            val res = safeCall { api.myProfile() }
            cache.put(JsonCache.PROFILE, DomiJson.encodeToString(res))
            res
        } catch (e: ApiException) {
            if (e.status == 403) throw e
            val cached = cache.get(JsonCache.PROFILE)
            if (cached != null) DomiJson.decodeFromString<com.nexahub.domicoop.data.remote.Profile>(cached) else throw e
        }
    }

    suspend fun update(req: com.nexahub.domicoop.data.remote.UpdateProfileRequest) =
        safeCall { api.updateProfile(req) }
}

class NotificationsRepository(
    private val api: ApiService,
    private val cache: JsonCache,
) {
    suspend fun page(page: Int = 1, limit: Int = 20) =
        try {
            val res = safeCall { api.notifications(page, limit) }
            if (page == 1) cache.put(JsonCache.NOTIFICATIONS_P1, DomiJson.encodeToString(res))
            res
        } catch (e: ApiException) {
            // 404 → backend not deployed yet: empty page (RN EMPTY_PAGE behaviour).
            if (e.status == 404) {
                com.nexahub.domicoop.data.remote.NotificationListResponse()
            } else if (page == 1) {
                val cached = cache.get(JsonCache.NOTIFICATIONS_P1)
                if (cached != null) DomiJson.decodeFromString<com.nexahub.domicoop.data.remote.NotificationListResponse>(cached) else throw e
            } else throw e
        }

    suspend fun markRead(id: String) = try { safeCall { api.markRead(id) } } catch (_: Exception) { null }
    suspend fun markAllRead() = try { safeCall { api.markAllRead() } } catch (_: Exception) { null }
    suspend fun clearAll() = try { safeCall { api.clearAll() } } catch (_: Exception) { null }

    suspend fun preferences(): com.nexahub.domicoop.data.remote.NotificationPreferencesDto? {
        return try {
            safeCall { api.getPreferences() }
        } catch (e: ApiException) {
            if (e.status == 404) null else throw e
        }
    }

    suspend fun savePreferences(prefs: com.nexahub.domicoop.data.remote.NotificationPreferencesDto) {
        try {
            safeCall { api.updatePreferences(prefs) }
        } catch (e: ApiException) {
            if (e.status != 404) throw e
            // Silent success on 404 (RN behaviour).
        }
    }
}
