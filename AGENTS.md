# AGENTS.md

## Project overview

This repository contains an Android authenticator app for creating and managing one-time authentication codes.

The app is written in Kotlin and uses a single Android application module:

* Root project: `authenticator`
* Main module: `:app`
* Current application package / namespace: `com.example.itplaneta`
* Main source path: `app/src/main/java/com/example/itplaneta`

The project is organized around these main layers:

* `core` — shared utilities, common types, constants, helpers
* `data` — Room database, data sources, repositories, persistence
* `di` — Hilt dependency injection modules
* `domain` — business logic, models, use cases, validation
* `ui` — Jetpack Compose screens, navigation, ViewModels, UI state

## Main tech stack

Use the existing stack unless the task explicitly asks for a migration:

* Kotlin
* Android Gradle Plugin
* Java 21 toolchain
* Jetpack Compose
* Material 3
* Navigation Compose
* Hilt
* Room
* DataStore Preferences
* CameraX
* ZXing
* AndroidX Security Crypto
* Tink
* Kotlinx Serialization / Gson
* Timber
* JUnit / AndroidX Test / Espresso / Compose UI tests

Do not replace the architecture or libraries with another stack without a direct request.

## Build and test commands

Use the Gradle wrapper from the repository.

On Linux/macOS:

```bash
./gradlew :app:assembleDebug
./gradlew :app:testDebugUnitTest
./gradlew :app:lintDebug
```

On Windows:

```bat
gradlew.bat :app:assembleDebug
gradlew.bat :app:testDebugUnitTest
gradlew.bat :app:lintDebug
```

For release checks:

```bash
./gradlew :app:assembleRelease
```

For instrumented tests, use a running emulator or connected Android device:

```bash
./gradlew :app:connectedDebugAndroidTest
```

Before finishing a code change, run at least:

```bash
./gradlew :app:assembleDebug
```

If the change touches business logic, validation, TOTP/HOTP generation, parsing, storage, database, or security, also run unit tests.

## General coding rules

* Keep changes small, focused, and directly related to the task.
* Prefer fixing the existing architecture over introducing new patterns.
* Do not rename the package, application ID, module, or project structure unless explicitly requested.
* Do not rewrite large parts of the app when a local fix is enough.
* Preserve existing public behavior unless the task asks to change it.
* Use Kotlin idioms: immutable data classes, sealed classes/interfaces for state and errors, extension functions only when they improve readability.
* Avoid global mutable state.
* Avoid blocking the main thread.
* Use coroutines and `Flow`/`StateFlow` for asynchronous state.
* Do not add network access, analytics, tracking, ads, or remote services unless explicitly requested.
* This app should work offline.

## Architecture rules

Follow the existing layered structure:

### UI layer

The UI layer belongs in `ui`.

Use Jetpack Compose and Material 3.

Rules:

* Prefer stateless composables where possible.
* Hoist state to the caller or ViewModel.
* ViewModels should expose immutable UI state, usually through `StateFlow`.
* One-time events should use `Channel`, `SharedFlow`, or an existing project pattern.
* Do not put business logic directly inside composables.
* Do not access Room, DataStore, crypto, or repositories directly from composables.
* Keep navigation code centralized.
* Use string resources for user-facing text.
* Keep screens responsive for different device sizes.
* Keep PIN/numpad UI touch targets large enough for comfortable mobile use.
* Preserve Material 3 styling and theming.

### Domain layer

The domain layer belongs in `domain`.

Rules:

* Keep business logic independent from Android framework classes.
* Put validation, TOTP/HOTP calculation rules, account constraints, and use cases here.
* Prefer explicit result/error types instead of throwing exceptions for expected validation failures.
* Do not leak database entities into UI when domain models already exist.

### Data layer

The data layer belongs in `data`.

Rules:

* Use repositories as the boundary between domain/UI and persistence.
* Room entities and DAOs stay in the data layer.
* Map Room entities to domain models before exposing them outside the data layer.
* When changing Room schema, provide a migration or clearly explain why destructive migration is acceptable.
* Keep Room schema files in sync.
* Do not delete existing schema history unless explicitly requested.

### Dependency injection

Dependency injection belongs in `di`.

Rules:

* Use Hilt for repositories, database, crypto helpers, use cases, and other services.
* Prefer constructor injection.
* Use `@ApplicationContext` only where Android context is truly needed.
* Do not pass Activity context into long-lived objects.

## Security rules

This app handles sensitive authenticator secrets. Treat security as a core requirement.

Never log or expose:

* OTP secrets
* `otpauth://` URIs
* backup passwords
* backup encryption keys
* PIN values
* generated OTP codes in release builds
* decrypted account data
* raw QR contents if they contain secrets

Security requirements:

* Store secrets encrypted.
* Use AndroidX Security Crypto, Android Keystore, or Tink according to the existing project implementation.
* Do not store OTP secrets, backup contents, or PIN data in plain text.
* Do not add debug screens that reveal secrets.
* Do not include real secrets in tests, screenshots, sample data, README, logs, or comments.
* Mask secrets in UI and logs.
* Keep backup/import/export encrypted.
* Do not weaken release ProGuard/R8/security settings unless fixing a documented build issue.
* Do not disable encryption to make tests easier.

## Authenticator validation rules

For TOTP/HOTP validation, prefer compatibility with real-world `otpauth://` QR codes while staying close to RFC 4226 / RFC 6238 behavior.

Use these default account constraints unless the source code already contains newer values:

```kotlin id="y37j4z"
object AccountConstraints {
    const val MIN_DIGITS = 6
    const val MAX_DIGITS = 8

    // Base32 characters. 16 chars is common in real authenticator QR codes.
    // For stricter RFC-oriented validation, also validate decoded byte length.
    const val MIN_SECRET_LENGTH = 16

    const val MIN_PERIOD = 1
    const val MAX_PERIOD = 3600

    const val DEFAULT_PERIOD = 30
    const val DEFAULT_DIGITS = 6
}
```

Validation rules:

* Accept `digits` from `6` to `8`.
* Default `digits` to `6` when missing.
* Do not allow `digits = 12` for normal HOTP/TOTP accounts.
* Accept `period` from `1` to `3600` seconds.
* Default TOTP `period` to `30` seconds when missing.
* Reject `period <= 0`.
* Use `Long` for time calculations.
* Do not calculate milliseconds using unsafe `Int` multiplication.
* Prefer `period.toLong() * 1000L`.
* Validate Base32 secrets before saving.
* Normalize Base32 secrets carefully:

  * trim whitespace;
  * remove spaces if the current parser supports grouped secrets;
  * uppercase consistently;
  * handle optional padding deliberately.
* Keep `MIN_SECRET_LENGTH = 16` for practical compatibility with existing QR codes.
* For stronger validation, prefer checking decoded secret byte length:

  * compatibility minimum: 10 bytes / 80 bits;
  * recommended minimum: 16 bytes / 128 bits;
  * strong recommendation: 20 bytes / 160 bits.
* Do not silently accept malformed secrets.
* Do not log raw secrets, normalized secrets, decoded secret bytes, OTP codes, counters, or raw `otpauth://` URIs.

When changing `AccountConstraints`, also update:

* account validation logic;
* Add/Edit account UI validation;
* QR parser validation;
* import/backup validation if it reuses constraints;
* unit tests for valid and invalid digits, period, and secret values.

Recommended tests:

```kotlin id="lk1mya"
fun digitsBelowMinimumReturnsValidationError()
fun digitsAboveMaximumReturnsValidationError()
fun sixDigitAccountIsAccepted()
fun eightDigitAccountIsAccepted()
fun twelveDigitAccountIsRejected()
fun missingDigitsUsesDefaultSix()
fun periodBelowMinimumReturnsValidationError()
fun periodAboveMaximumReturnsValidationError()
fun missingPeriodUsesDefaultThirty()
fun malformedBase32SecretReturnsValidationError()
fun validSixteenCharBase32SecretIsAccepted()
```

## Authenticator logic rules

For TOTP/HOTP and account handling:

* Follow standard HOTP/TOTP behavior.
* Preserve support for common `otpauth://` URI formats.
* Validate issuer, label, secret, period, digits, algorithm, and counter.
* Keep account validation centralized.
* Use existing constraints from `AccountConstraints`.
* Do not hardcode separate digit/period limits in UI, parser, import, or repository code.
* Avoid silently accepting malformed secrets.
* Normalize Base32 secrets carefully.
* Do not break existing imported accounts.
* Be careful with time calculations and clock-based code refresh.
* Do not perform heavy calculations in composables.
* Do not support non-standard digit lengths unless there is a clear product requirement and tests.
* For normal authenticator compatibility, use 6–8 digits.
* For TOTP, use 30 seconds as the default period.
* For HOTP, validate the counter and avoid negative values.
* Keep TOTP/HOTP generation deterministic and covered by tests.


## QR scanner rules

The QR scanner uses CameraX and ZXing.

When editing scanner code:

* Keep camera permission handling clear.
* Do not block the analyzer thread.
* Close image proxies correctly.
* Avoid scanning the same QR code repeatedly if the UI has already handled it.
* Validate parsed `otpauth://` data before saving.
* Show useful error messages for invalid QR codes.
* Do not log raw QR contents.

## PIN and biometric rules

When editing PIN or lock-related code:

* Do not store PIN in plain text.
* Do not log PIN input.
* Keep error states explicit.
* Avoid leaking whether a specific secret/account exists.
* Keep UI responsive and accessible.
* Preserve flows for enabling, confirming, unlocking, and disabling PIN.
* Use biometric APIs only through the existing project abstraction or AndroidX Biometric.

## Backup/import/export rules

Backups are security-sensitive.

When editing backup code:

* Keep exported backup data encrypted.
* Do not export raw secrets.
* Validate backup format before import.
* Handle corrupted backups gracefully.
* Do not overwrite existing accounts without clear user intent.
* Avoid duplicate imports where possible.
* Keep compatibility with existing backup files unless a migration is explicitly requested.
* Add tests for encryption/decryption, invalid password, corrupted data, and duplicate import behavior.

## Compose and UX rules

When changing UI:

* Use Material 3 components.
* Keep screens simple and mobile-first.
* Prefer clear empty states and error messages.
* Keep destructive actions confirmed.
* Use Snackbar or existing event pattern for short feedback.
* Do not show raw technical exceptions to users.
* Keep navigation predictable:

  * Main screen
  * Add account
  * Edit account
  * QR scanner
  * Settings
  * How it works
  * PIN screens
* Avoid deeply nested composables in one file; split when readability suffers.
* Do not introduce XML layouts for new UI unless the task specifically requires it.

## Gradle and dependency rules

The project currently uses Groovy Gradle files, not Kotlin DSL.

Rules:

* Do not convert Gradle files to Kotlin DSL unless explicitly requested.
* Keep SDK versions centralized in the root Gradle configuration.
* Do not hardcode SDK versions in module files when root ext values are already used.
* Avoid broad dependency upgrades in unrelated tasks.
* When upgrading Compose, Kotlin, AGP, Hilt, Room, CameraX, or Material 3, check compatibility together.
* Do not add duplicate dependencies with conflicting versions.
* Keep the Java toolchain and Kotlin JVM target aligned.
* Preserve release minification/shrinking unless a task specifically requires changing it.

## Testing expectations

Add or update tests when changing:

* TOTP/HOTP generation
* QR parsing
* Account validation
* Secret normalization
* Room migrations
* Repository behavior
* Backup encryption/import/export
* PIN validation
* Settings persistence

Prefer local JVM tests for domain logic.

Use instrumented tests only when Android framework behavior, Room integration, Compose UI, CameraX, or biometric behavior requires it.

Test names should describe behavior, for example:

```kotlin
fun invalidBase32SecretReturnsValidationError()
fun totpCodeChangesAfterPeriod()
fun importingDuplicateAccountDoesNotCreateSecondCopy()
```

## Error handling rules

* Use typed errors for expected failures.
* Do not catch broad exceptions without handling or logging safely.
* Do not swallow errors silently.
* Show user-friendly messages in UI.
* Keep technical details in debug logs only, and never include secrets.

## Logging rules

Use Timber according to existing project style.

* Debug logs are allowed for lifecycle/build/debug information.
* Release logs must not expose sensitive data.
* Never log secrets, PINs, backup keys, QR raw contents, or generated codes.
* Prefer masked values when logs are necessary.

## Resource and localization rules

* Put user-facing strings in resources.
* Do not hardcode visible text inside composables unless it is temporary debug-only text.
* Keep existing translations valid.
* Do not remove resources that are still referenced.
* Even if lint allows missing translations, prefer adding translations when practical.

## Database rules

When changing Room:

* Update entities, DAOs, database version, migrations, and schema files together.
* Keep migration tests when possible.
* Do not use destructive migration for user data unless explicitly requested.
* Do not store decrypted secrets in additional database columns.
* Keep indexes and queries reasonable for account list performance.

## Review checklist for agents

Before completing a task, verify:

* The project builds.
* The changed code follows existing package/layer structure.
* No secrets, PINs, QR contents, or OTP codes are logged.
* UI changes use Compose and Material 3.
* Business logic is not placed directly in composables.
* Data access goes through repositories/use cases.
* Room schema/migrations are updated if database structure changed.
* Tests are added or updated for changed logic.
* Dependency changes are minimal and compatible.
* No unrelated formatting or large rewrites were made.

## What not to do

Do not:

* Rename `com.example.itplaneta` or `applicationId` without explicit instruction.
* Replace Compose with XML layouts.
* Replace Hilt with another DI framework.
* Replace Room with another database.
* Add server sync, cloud storage, analytics, ads, or tracking without explicit instruction.
* Store secrets in plain text.
* Disable encryption for convenience.
* Log sensitive data.
* Remove PIN/backup/security behavior while refactoring.
* Apply massive dependency upgrades as part of a small feature.
* Convert Gradle Groovy files to Kotlin DSL unless asked.
* Delete Room schema history unless asked.
* Make broad architectural rewrites without a clear reason.

## Preferred response style for coding agents

When proposing or making changes:

* Explain what changed briefly.
* Mention which files were touched.
* Mention build/test commands that were run.
* If a command was not run, say so clearly.
* If behavior changed, describe the user-visible effect.
* If security-sensitive code changed, explain how secrets remain protected.
