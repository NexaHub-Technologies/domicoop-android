# DOMICOOP — Native Android (Kotlin + Jetpack Compose)

Native port of `../domicoop-mobile` (Expo React Native) to **Kotlin + Jetpack Compose**,
scaffolded with the Android CLI (`android create`) and built with Gradle.

- Package: `com.nexahub.domicoop` (same as the RN `app.config.js` Android package)
- API: `https://api.domicoop.org/v1` (`BuildConfig.API_BASE_URL`)
- Design: **Blue Cobalt Archive** (light) / **Nocturnal Cobalt** (dark) — port of `constants/theme/`
- Min SDK 26, Target/Compile 36, versionCode **7** (Play already has v6 from the RN track), versionName `1.0.0`

## Scaffold

```bash
android create --name DOMICOOP --output ~/Projects/domicoop-android --minSdk 26
```

Template `empty-activity` (Compose, AGP 9) was then re-pointed from
`com.example.domicoop` → `com.nexahub.domicoop` and expanded into the full app.

## Build / test / run (CLI only)

```bash
cd ~/Projects/domicoop-android
./gradlew assembleDebug          # APK → app/build/outputs/apk/debug/app-debug.apk
./gradlew testDebugUnitTest      # unit tests (allocation, currency, loan maths)
./gradlew installDebug           # install on emulator/device
adb shell am start -n com.nexahub.domicoop/.MainActivity
# or: android run  (builds + deploys + launches)
```

## Architecture

```
app/src/main/java/com/nexahub/domicoop/
  MainActivity.kt / DomiCoopApp.kt      # entry + manual DI container
  navigation/  Routes.kt, AppNavGraph.kt# navigation-compose (mirrors Expo Router)
  theme/       Color.kt, Theme.kt, Type.kt, Dimens.kt
  core/        ApiResult.kt, Currency.kt, Format.kt, Allocation.kt, LoanCalc.kt
  data/
    remote/    ApiService.kt, Dto.kt, LoanMapper.kt, NetworkModule.kt
    local/     SessionManager.kt, JsonCache.kt
    repository/Repositories.kt          # AppContainer + 7 repositories
  ui/
    common/    Components.kt            # Button, TextField, Money, Badge, cards…
    auth/      splash/welcome/sign-in/sign-up/forgot/reset/closed
    tabs/      dashboard/contributions/loans/announcements/profile + bottom bar
    detail/    contribution + loan detail
    transactions/ add-contribution / make-payment / apply-for-loan
    more/      notifications/settings/security/theme/guidelines/privacy/support/success
```

## RN → Kotlin parity

| RN (`domicoop-mobile`) | Kotlin (`domicoop-android`) |
|---|---|
| `lib/http.ts` (20s timeout, refresh-on-401) | `data/remote/NetworkModule.kt` (`AuthInterceptor`, single-flight refresh) |
| `lib/session.ts` (SecureStore) | `SessionManager` (EncryptedSharedPreferences + DataStore, 10-min TTL) |
| `usePersistedQuery` + AsyncStorage cache | `JsonCache` (DataStore write-through, offline fallback, 404→empty for notifications) |
| `contexts/ThemeContext.tsx` | `DOMICOOPTheme` + `ThemePreference` in DataStore |
| Expo Router `app/` | `navigation/AppNavGraph.kt` (same route names) |
| `usePaystackPayment` (WebView) | Paystack key in `BuildConfig`; pay → paste reference → `verify` with 3×2.5s retry (same as RN) |
| `contributionAllocation.ts` | `core/Allocation.kt` (identical thresholds) |
| `constants/loans.ts` flat-interest maths | `core/LoanCalc.kt` (identical) |
| `utils/receiptGenerator.ts` | Receipt screen shows allocation + details (PDF share via follow-up) |
| `usePushNotifications` (Expo push) | Deferred — FCM wiring point is `registerDevice`/`unregisterDevice` in `NotificationsRepository` |

## Secrets

- `PAYSTACK_PUBLIC_KEY` test key is baked in as `BuildConfig` default (same public key as RN `.env`).
  Override for release via Gradle `-PPAYSTACK_KEY=…` or env in CI.
- No `google-services.json` committed (same rule as RN: prebuild works without it; FCM is opt-in).

## Status (verified 2026-09-15)

- [x] Scaffold via Android CLI, package rename, versionCode 7
- [x] Theme, navigation, all 25+ screens, 7 repositories, offline cache
- [x] `./gradlew assembleDebug testDebugUnitTest` — BUILD SUCCESSFUL, 7/7 unit tests pass
- [x] Installed on emulator-5554, `Displayed MainActivity in ~1.3s`, no crashes, splash screenshot verified
- [ ] E2E against staging API, FCM push, receipt PDF share, Play internal track
