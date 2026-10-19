package com.nexahub.domicoop.data.remote

import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.JsonDecoder
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.booleanOrNull
import kotlinx.serialization.json.doubleOrNull

// ---------- Auth (lib/types/sign-up.ts + auth.api.ts) ----------
@Serializable
data class LoginRequest(val email: String, val password: String)

@Serializable
data class ApiUser(
    val id: String = "",
    val email: String = "",
    val role: String = "member",
    @SerialName("email_verified") val emailVerified: Boolean = true,
)

@Serializable
data class LoginResponse(
    @SerialName("access_token") val accessToken: String,
    @SerialName("refresh_token") val refreshToken: String,
    @SerialName("expires_in") val expiresIn: Long = 0,
    val user: ApiUser? = null,
)

@Serializable
data class RefreshResponse(
    @SerialName("access_token") val accessToken: String,
    @SerialName("refresh_token") val refreshToken: String,
    @SerialName("expires_in") val expiresIn: Long = 0,
)

// ---------- Registration ----------
@Serializable
data class RegistrationWindowFees(
    val name: String = "",
    @SerialName("opens_at") val opensAt: String? = null,
    @SerialName("closes_at") val closesAt: String? = null,
    @SerialName("registration_fee") val registrationFee: Double = 0.0,
    @SerialName("social_fee") val socialFee: Double = 0.0,
    @SerialName("total_due") val totalDue: Double = 0.0,
    @SerialName("slots_remaining") val slotsRemaining: Int? = null,
)

@Serializable
data class RegistrationWindowResponse(
    @SerialName("is_open") val isOpen: Boolean = false,
    val reason: String? = null,
    val window: RegistrationWindowFees? = null,
)

@Serializable
data class PrecheckRequest(val email: String, @SerialName("monthly_subscription") val monthlySubscription: Double? = null)

@Serializable
data class MembershipApplicationRequest(
    val email: String,
    val password: String,
    @SerialName("full_name") val fullName: String,
    val sex: String? = null,
    @SerialName("date_of_birth") val dateOfBirth: String? = null,
    val phone: String,
    @SerialName("whatsapp_number") val whatsappNumber: String? = null,
    @SerialName("marital_status") val maritalStatus: String? = null,
    val address: String,
    @SerialName("id_card_number") val idCardNumber: String? = null,
    @SerialName("next_of_kin") val nextOfKin: String? = null,
    @SerialName("place_of_work") val placeOfWork: String? = null,
    @SerialName("type_of_business") val typeOfBusiness: String? = null,
    @SerialName("bank_name") val bankName: String,
    @SerialName("bank_account") val bankAccount: String,
    @SerialName("bank_code") val bankCode: String,
    @SerialName("referred_by") val referredBy: String? = null,
    @SerialName("monthly_subscription") val monthlySubscription: Double? = null,
    @SerialName("avatar_url") val avatarUrl: String? = null,
    val signature: String? = null,
    @SerialName("payment_reference") val paymentReference: String,
)

@Serializable
data class MembershipApplicationResponse(
    val message: String = "",
    @SerialName("user_id") val userId: String? = null,
    val email: String? = null,
)

// ---------- Contributions ----------
@Serializable
data class ApiContribution(
    val id: String,
    val amount: Double = 0.0,
    val month: String = "",
    val status: String = "pending",
    @SerialName("proof_url") val proofUrl: String? = null,
    val notes: String? = null,
    @SerialName("created_at") val createdAt: String? = null,
    @SerialName("updated_at") val updatedAt: String? = null,
    @SerialName("payment_status") val paymentStatus: String? = null,
)

@Serializable
data class ApiContributionsResponse(
    val contributions: List<ApiContribution> = emptyList(),
    @SerialName("total_balance") val totalBalance: Double = 0.0,
    @SerialName("year_balance") val yearBalance: Double = 0.0,
)

@Serializable
data class VerifyContributionRequest(val reference: String, val month: String, val year: Int)

@Serializable
data class VerifyContributionResponse(
    val verified: Boolean = false,
    @SerialName("already_processed") val alreadyProcessed: Boolean = false,
    val status: String? = null,
    val contribution: ApiContribution? = null,
)

// ---------- Loans ----------
@Serializable
data class ApiLoanGuarantor(
    val position: Int = 0,
    @SerialName("full_name") val fullName: String = "",
    @SerialName("bank_name") val bankName: String = "",
    @SerialName("bank_account") val bankAccount: String = "",
    val phone: String = "",
    @SerialName("signed_at") val signedAt: String? = null,
)

@Serializable
data class ApiLoanInstallment(
    @SerialName("installment_no") val installmentNo: Int = 0,
    @SerialName("due_on") val dueOn: String = "",
    val amount: Double = 0.0,
    @SerialName("paid_amount") val paidAmount: Double = 0.0,
    val status: String = "pending",
)

@Serializable
data class ApiLoan(
    val id: String,
    @SerialName("member_id") val memberId: String? = null,
    @SerialName("amount_requested") val amountRequested: Double = 0.0,
    @SerialName("amount_approved") val amountApproved: Double? = null,
    val balance: Double? = null,
    @SerialName("monthly_repayment") val monthlyRepayment: Double? = null,
    @SerialName("interest_rate") val interestRate: Double? = null,
    @SerialName("tenure_months") val tenureMonths: Int? = null,
    val purpose: String = "",
    val type: String = "personal",
    val status: String? = "pending",
    @SerialName("due_date") val dueDate: String? = null,
    @SerialName("disbursed_at") val disbursedAt: String? = null,
    @SerialName("created_at") val createdAt: String? = null,
    @SerialName("updated_at") val updatedAt: String? = null,
    @SerialName("applicant_address") val applicantAddress: String? = null,
    @SerialName("applicant_bank_name") val applicantBankName: String? = null,
    @SerialName("applicant_bank_account") val applicantBankAccount: String? = null,
    @SerialName("applicant_phone") val applicantPhone: String? = null,
    @SerialName("grace_months") val graceMonths: Int = 1,
    @SerialName("bond_url") val bondUrl: String? = null,
    @SerialName("bond_cancelled_at") val bondCancelledAt: String? = null,
    @SerialName("loan_guarantors") val guarantors: List<ApiLoanGuarantor> = emptyList(),
    @SerialName("loan_installments") val installments: List<ApiLoanInstallment> = emptyList(),
)

@Serializable
data class ApiLoansWrapper(val loans: List<ApiLoan> = emptyList())

@Serializable
data class GuarantorInput(
    @SerialName("full_name") val fullName: String,
    @SerialName("bank_name") val bankName: String,
    @SerialName("bank_account") val bankAccount: String,
    val phone: String,
    val signature: String,
)

@Serializable
data class LoanApplyRequest(
    val amount: Double,
    val purpose: String,
    val type: String,
    @SerialName("tenure_months") val tenureMonths: Int,
    @SerialName("applicant_address") val applicantAddress: String,
    @SerialName("applicant_bank_name") val applicantBankName: String,
    @SerialName("applicant_bank_account") val applicantBankAccount: String,
    @SerialName("applicant_phone") val applicantPhone: String,
    @SerialName("borrower_signature") val borrowerSignature: String,
    val guarantors: List<GuarantorInput>,
)

@Serializable
data class LoanApplyWrapper(val loan: ApiLoan? = null)

@Serializable
data class RepaymentRequest(val reference: String)

@Serializable
data class RepaymentResponse(
    val success: Boolean = false,
    @SerialName("already_processed") val alreadyProcessed: Boolean = false,
    @SerialName("loan_id") val loanId: String? = null,
    @SerialName("amount_paid") val amountPaid: Double? = null,
    @SerialName("remaining_balance") val remainingBalance: Double? = null,
    val status: String? = null,
)

// ---------- Dashboard ----------
@Serializable
data class DashboardMember(
    @SerialName("full_name") val fullName: String = "",
    @SerialName("member_no") val memberNo: String? = null,
    val status: String = "active",
    @SerialName("avatar_url") val avatarUrl: String? = null,
)

@Serializable
data class DashboardActiveLoan(
    val id: String = "",
    val amount: Double = 0.0,
    val balance: Double = 0.0,
    val status: String = "",
    @SerialName("next_payment_date") val nextPaymentDate: String? = null,
    @SerialName("next_payment_amount") val nextPaymentAmount: Double? = null,
)

@Serializable
data class DashboardTxn(
    val id: String = "",
    val type: String = "",
    val category: String = "savings",
    val title: String? = null,
    val amount: Double = 0.0,
    val date: String = "",
    val status: String = "",
)

@Serializable
data class DashboardAnnouncement(
    val id: String = "",
    val title: String = "",
    val body: String = "",
    @SerialName("created_at") val createdAt: String = "",
)

/**
 * The backend returns `paid_this_month` as a boolean flag. Older cached
 * payloads (and any in-flight responses) may carry a numeric 0/1 instead,
 * so accept both shapes instead of crashing the whole dashboard decode.
 */
object LenientBoolean : KSerializer<Boolean> {
    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("LenientBoolean", PrimitiveKind.BOOLEAN)

    override fun deserialize(decoder: Decoder): Boolean {
        if (decoder is JsonDecoder) {
            val element = decoder.decodeJsonElement()
            if (element is JsonPrimitive) {
                element.booleanOrNull?.let { return it }
                element.doubleOrNull?.let { return it != 0.0 }
                if (element.isString) {
                    when (element.content.lowercase()) {
                        "true", "1", "yes" -> return true
                        "false", "0", "no" -> return false
                    }
                }
            }
            return false
        }
        return try {
            decoder.decodeBoolean()
        } catch (_: Exception) {
            false
        }
    }

    override fun serialize(encoder: Encoder, value: Boolean) {
        encoder.encodeBoolean(value)
    }
}

@Serializable
data class DashboardSummary(
    val member: DashboardMember = DashboardMember(),
    @SerialName("total_savings") val totalSavings: Double = 0.0,
    @SerialName("paid_this_month")
    @Serializable(with = LenientBoolean::class)
    val paidThisMonth: Boolean = false,
    @SerialName("current_month") val currentMonth: String = "",
    @SerialName("active_loan") val activeLoan: DashboardActiveLoan? = null,
    @SerialName("recent_transactions") val recentTransactions: List<DashboardTxn> = emptyList(),
    val announcements: List<DashboardAnnouncement> = emptyList(),
)

// ---------- Members ----------
@Serializable
data class Profile(
    val id: String = "",
    @SerialName("full_name") val fullName: String = "",
    val phone: String? = null,
    val address: String? = null,
    @SerialName("bank_name") val bankName: String? = null,
    @SerialName("bank_account") val bankAccount: String? = null,
    @SerialName("bank_code") val bankCode: String? = null,
    @SerialName("next_of_kin") val nextOfKin: String? = null,
    val role: String = "member",
    val status: String = "active",
    @SerialName("member_no") val memberNo: String? = null,
    @SerialName("avatar_url") val avatarUrl: String? = null,
)

@Serializable
data class UpdateProfileRequest(
    @SerialName("full_name") val fullName: String? = null,
    val phone: String? = null,
    val address: String? = null,
    @SerialName("bank_name") val bankName: String? = null,
    @SerialName("bank_account") val bankAccount: String? = null,
    @SerialName("bank_code") val bankCode: String? = null,
    @SerialName("next_of_kin") val nextOfKin: String? = null,
    @SerialName("avatar_url") val avatarUrl: String? = null,
)

// ---------- Notifications ----------
@Serializable
data class ApiNotificationAction(val label: String? = null, val url: String? = null)

@Serializable
data class ApiNotification(
    val id: String,
    val type: String = "announcement",
    val title: String = "",
    val body: String = "",
    val read: Boolean = false,
    @SerialName("created_at") val createdAt: String = "",
    val action: ApiNotificationAction? = null,
)

@Serializable
data class NotificationMeta(
    val page: Int = 1,
    val limit: Int = 20,
    val total: Int = 0,
    @SerialName("total_pages") val totalPages: Int = 0,
    @SerialName("unread_count") val unreadCount: Int = 0,
)

@Serializable
data class NotificationListResponse(
    val notifications: List<ApiNotification> = emptyList(),
    val meta: NotificationMeta = NotificationMeta(),
)

@Serializable
data class UnreadCountResponse(@SerialName("unread_count") val unreadCount: Int = 0)

@Serializable
data class DeviceRegisterRequest(val token: String, val platform: String = "android", @SerialName("device_name") val deviceName: String? = null)

@Serializable
data class DeviceUnregisterRequest(val token: String)

@Serializable
data class NotificationPreferencesDto(
    @SerialName("push_enabled") val pushEnabled: Boolean = true,
    val categories: Map<String, Boolean> = emptyMap(),
)
