package com.nexahub.domicoop.data.remote

import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * Retrofit mirror of the lib/api modules. All paths are relative to
 * https://api.domicoop.org/v1 (BuildConfig.API_BASE_URL).
 */
interface ApiService {
    // Auth
    @POST("auth/login")
    suspend fun login(@Body req: LoginRequest): LoginResponse

    @POST("auth/logout")
    suspend fun logout(): Map<String, Boolean>

    @POST("auth/refresh")
    suspend fun refresh(@Body body: Map<String, String>): RefreshResponse

    // Registration (unauthenticated)
    @GET("registration/window")
    suspend fun registrationWindow(): RegistrationWindowResponse

    @POST("registration/precheck")
    suspend fun registrationPrecheck(@Body req: PrecheckRequest): Map<String, kotlinx.serialization.json.JsonElement>

    @POST("registration/apply")
    suspend fun registrationApply(@Body req: MembershipApplicationRequest): MembershipApplicationResponse

    // Contributions
    @GET("contributions/me")
    suspend fun myContributions(
        @Query("year") year: Int? = null,
        @Query("month") month: String? = null,
        @Query("page") page: Int? = null,
        @Query("limit") limit: Int? = null,
    ): ApiContributionsResponse

    @GET("contributions/{id}")
    suspend fun contributionById(@Path("id") id: String): ApiContribution

    @POST("contributions/verify")
    suspend fun verifyContribution(@Body req: VerifyContributionRequest): VerifyContributionResponse

    // Loans — server returns bare array for /loans/me but be lenient.
    @GET("loans/me")
    suspend fun myLoansRaw(): kotlinx.serialization.json.JsonElement

    @GET("loans/{id}")
    suspend fun loanById(@Path("id") id: String): ApiLoan

    @POST("loans/apply")
    suspend fun applyForLoanRaw(@Body req: LoanApplyRequest): kotlinx.serialization.json.JsonElement

    @POST("loans/{id}/repayment")
    suspend fun repayLoan(@Path("id") id: String, @Body req: RepaymentRequest): RepaymentResponse

    // Dashboard
    @GET("dashboard/summary")
    suspend fun dashboardSummary(): DashboardSummary

    // Members
    @GET("members/me")
    suspend fun myProfile(): Profile

    @PATCH("members/me")
    suspend fun updateProfile(@Body req: UpdateProfileRequest): Profile

    // Notifications
    @GET("notifications/me")
    suspend fun notifications(
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 20,
    ): NotificationListResponse

    @PATCH("notifications/{id}/read")
    suspend fun markRead(@Path("id") id: String): UnreadCountResponse

    @POST("notifications/me/read-all")
    suspend fun markAllRead(): UnreadCountResponse

    @DELETE("notifications/me")
    suspend fun clearAll(): Map<String, Boolean>

    @POST("notifications/devices")
    suspend fun registerDevice(@Body req: DeviceRegisterRequest): Map<String, Boolean>

    @POST("notifications/devices/unregister")
    suspend fun unregisterDevice(@Body req: DeviceUnregisterRequest): Map<String, Boolean>

    @GET("notifications/preferences")
    suspend fun getPreferences(): NotificationPreferencesDto

    @PATCH("notifications/preferences")
    suspend fun updatePreferences(@Body req: NotificationPreferencesDto): NotificationPreferencesDto
}
