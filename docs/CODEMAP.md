# Карта проекта Authenticator

## 1. Структура модулей и пакетов

- Корневой проект: `authenticator`.
- Единственный Android-модуль приложения: `:app`.
- Namespace и `applicationId`: `com.example.itplaneta`.
- Основной исходный код: `app/src/main/java/com/example/itplaneta`.
- Gradle-файлы используют Groovy DSL: `settings.gradle`, `build.gradle`, `app/build.gradle`.
- Java/Kotlin target: Java 21 toolchain и `jvmTarget = 21`.

Точки входа:

- `App` - `Application` с `@HiltAndroidApp`, в debug инициализирует Timber.
- `MainActivity` - `@AndroidEntryPoint`, запускает Compose, читает тему через `SettingsManager`.
- `AuthenticatorApp` - создает `NavHostController` и подключает `AuthenticatorNavHost`.

Основные пакеты:

- `core` - общие низкоуровневые части: OTP, QR camera/ZXing, biometric, утилиты и `Result`.
- `data` - Room entity/DAO/database, репозитории, backup, DataStore settings, QR analyzer factory.
- `di` - Hilt-модули в `Modules.kt`.
- `domain` - интерфейсы репозиториев/сервисов, DTO, validation.
- `ui` - Compose-экраны, навигация, ViewModel, UI state/event, общие компоненты, тема.

## 2. Основные экраны

- `PinScreen` - стартовый экран PIN, используется для `UNLOCK`, `ENABLE`, `DISABLE`, поддерживает запуск biometric prompt.
- `MainScreen` - список аккаунтов, текущие OTP-коды, TOTP-таймеры, copy/edit/delete и FAB-действия.
- `AccountScreen` - создание и редактирование аккаунта, форма на базе `AccountInputDto`, validation errors, диалог несохраненных изменений.
- `ScannerScreen` - CameraX-экран для сканирования `otpauth://` QR-кодов, permission flow и сообщения об ошибках.
- `SettingsScreen` - тема, backup/export, restore/import, переход к PIN flow и `HowItWorksScreen`.
- `HowItWorksScreen` - информационный экран с описанием работы приложения.

## 3. Навигация

Навигация централизована в `ui/navigation/AuthenticatorNavHost.kt`.

- `startDestination`: `PinDestination.route`.
- `PinDestination`: `pin_screen?mode={mode}`, аргумент `mode`, default `PinScenario.UNLOCK.name`.
- `MainDestination`: `main_screen`.
- `AccountDestination`: `account_screen?accountId={accountId}`, default `accountId = -1` для создания нового аккаунта.
- `QrScannerDestination`: `qr_scanner_screen`.
- `SettingsDestination`: `settings_screen`.
- `HowItWorksDestination`: `how_it_works_screen`.

Основной поток:

1. Приложение стартует на `PinScreen`.
2. После успешного unlock выполняется переход на `MainScreen` с `popUpTo(PinDestination.route) { inclusive = true }`.
3. Из `MainScreen` доступны настройки, QR scanner и форма аккаунта.
4. Из `SettingsScreen` доступны `HowItWorksScreen` и `PinScreen` в режиме `ENABLE` или `DISABLE`.

## 4. ViewModel и UI state

Общий паттерн:

- Большинство экранных ViewModel наследуются от `BaseViewModel<State, Event>`.
- `BaseViewModel` публикует read-only `StateFlow` для длительного состояния и `SharedFlow` для одноразовых событий.
- Обновление состояния идет через `updateState`, события - через `emitEvent` или `postEvent`.
- `HowItWorksViewModel` минимальный: `@HiltViewModel` без собственного state-класса.

Основные ViewModel:

- `MainViewModel` читает `IAccountRepository.getAccounts()`, запускает `OtpCodeManager`, подписывается на коды и таймеры, управляет FAB, удалением и snackbar-событиями.
- `AccountViewModel` загружает аккаунт по `accountId`, редактирует `AccountInputDto`, валидирует через `AccountValidator`, сохраняет создание/изменение.
- `SettingsViewModel` следит за темой и PIN enabled state, вызывает backup/restore через `IAccountBackupManager`.
- `PinViewModel` ведет сценарии `UNLOCK`, `ENABLE`, `DISABLE`, хранит stage подтверждения PIN и biometric flags.
- `QrScannerViewModel` владеет `QrCodeAnalyzer`, парсит QR через `UriOtpParser`, сохраняет аккаунт через `IAccountRepository`.

Ключевые state-классы:

- `MainUiState`: `accounts`, `codes`, `timerProgresses`, `timerValues`, `isFabExpanded`, `deleteDialogAccount`, `screenState`.
- `AccountUiState`: `currentAccount`, `originalAccount`, `errors`, `showUnsavedChangesDialog`, `screenState`.
- `SettingsUiState`: `selectedTheme`, `isPinEnabled`, `lastBackupMessage`, `screenState`.
- `PinUiState`: `scenario`, `stage`, `firstValue`, `value`, `isError`, `canUseBiometric`, `isBiometricEnabled`, `isPinEnabled`.
- `QrScannerUiState`: `hasReadCode`, `hasCameraPermission`, `shouldShowRationale`, `isCameraReady`, `screenState`.

## 5. TOTP/HOTP логика

Ключевые классы:

- `core/otp/models/OtpGenerator`
  - генерирует HOTP по RFC 4226 через HMAC (`Mac`, `SecretKeySpec`);
  - генерирует TOTP как HOTP от time-based counter;
  - декодирует Base32 secret через `Base32`;
  - очищает key bytes в вызывающем `OtpCodeManager` после генерации.
- `core/otp/OtpCodeManager`
  - принимает `Flow<List<Account>>`;
  - обновляет коды раз в секунду (`UPDATE_INTERVAL_MS = 1000L`);
  - публикует `codes`, `timerProgresses`, `timerValues`;
  - для HOTP использует `counter`, для TOTP считает остаток периода и progress.
- `core/otp/parser/UriOtpParser`
  - парсит только `otpauth://`;
  - поддерживает `totp` и `hotp`;
  - валидирует `secret`, `digits`, `period`, `counter`, `algorithm`;
  - создает `Account`.
- `core/otp/parser/Base32`
  - удаляет пробелы, приводит к uppercase, принимает символы `A-Z2-7`.
- `domain/validation/AccountValidator`
  - центральная валидация формы аккаунта.

Актуальные ограничения из `AccountConstraints`:

- `MIN_DIGITS = 6`, `MAX_DIGITS = 8`.
- `MIN_SECRET_LENGTH = 16`.
- `MIN_PERIOD = 1`, `MAX_PERIOD = 3600`.
- `DEFAULT_PERIOD = 30`, `DEFAULT_DIGITS = 6`.
- Counter должен быть `Long >= 0`.

## 6. Room/DataStore

Room:

- Entity: `data/sources/Account.kt`, таблица `accounts`.
- Поля аккаунта: `id`, `issuer`, `label`, `tokenType`, `algorithm`, `secret`, `digits`, `counter`, `period`.
- DAO: `data/sources/database/AccountDao.kt`.
- Основные DAO-операции: add/update/delete, `getAllAccountsFlow()`, `getAllAccounts()`, поиск по `id`, `secret`, `label + issuer`, `existsWithSecret`, `updateHotpCounter`, flow-фильтры.
- Database: `AccountDatabase`, version `3`, `exportSchema = true`.
- Schema history: `app/schemas/com.example.itplaneta.data.sources.database.AccountDatabase/3.json`.
- В `AccountDatabase` сейчас нет явных migration-объектов.

DataStore:

- Реализован в `data/SettingsManager.kt` через `preferencesDataStore("settings")`.
- Хранит тему (`THEME_KEY`), флаг PIN (`PIN_ENABLED_KEY`), PIN hash/salt (`PIN_HASH_KEY`, `PIN_SALT_KEY`) и флаг биометрии (`BIOMETRIC_ENABLED_KEY`).
- PIN сохраняется не в открытом виде: `PinHashUtils` генерирует salt и считает SHA-256 hash, сравнение выполняется через `MessageDigest.isEqual`.

## 7. Backup/import/export

Основные компоненты:

- `BackupRepository`
  - сериализует `List<Account>` в `List<AccountBackupDto>` через kotlinx serialization;
  - десериализует backup DTO обратно в `Account`;
  - шифрует и расшифровывает payload через Tink `Aead`;
  - использует associated data `backup_data`.
- `AccountBackupManager`
  - сохраняет backup в выбранный `Uri` через `ContentResolver.openFileDescriptor`;
  - читает backup из `Uri` через `ContentResolver.openInputStream`;
  - при restore сначала пробует старый plaintext JSON, затем новый encrypted backup;
  - импортирует аккаунты через `IAccountRepository.addAccount`.
- `AccountBackupDto`
  - wire DTO для backup-формата;
  - содержит `secret`, поэтому plaintext JSON считается legacy-рискованным форматом.

Особенности:

- Restore поддерживает два формата, поэтому менять backup wire format нужно только с совместимостью.
- Импорт явно не блокирует дубликаты на уровне manager: итог зависит от `addAccount`, данных и Room.
- Backup-логи не должны содержать secrets, decrypted payload или raw backup contents.

## 8. PIN flow

Основные классы:

- `PinScreen`, `NumericKeyboard`.
- `PinViewModel`, `PinUiState`, `PinUiEvent`.
- `SettingsManager`.
- `PinHashUtils`.
- `core.biometric.BiometricManager` и `BiometricRepository`.

Сценарии:

- `UNLOCK`
  - стартовый сценарий;
  - проверяет, включен ли PIN;
  - валидирует ввод через `settingsManager.isPinValid`;
  - при успехе отправляет `PinUiEvent.OpenApp`.
- `ENABLE`
  - шаг `INPUT`: ввод нового PIN;
  - шаг `CONFIRM`: повторный ввод;
  - при совпадении вызывает `savePin` и `setPinEnabled(true)`.
- `DISABLE`
  - проверяет текущий PIN;
  - при успехе вызывает `setPinEnabled(false)` и возвращает в настройки.

Биометрия:

- Доступность устройства проверяется через abstraction в `core.biometric`.
- Включенность хранится в DataStore как `BIOMETRIC_ENABLED_KEY`.
- `PinViewModel` запускает biometric только когда доступно, включено и сценарий равен `UNLOCK`.

## 9. QR scanner

Основные элементы:

- `ScannerScreen` - Compose permission/UI flow.
- `CameraPreview` - CameraX `Preview`, `ImageAnalysis`, `PreviewView`, executor analyzer.
- `QrScannerViewModel` - связывает analyzer, parser и repository.
- `QrCodeAnalyzer` - читает Y-plane из `ImageProxy`, учитывает rotation 90/270, передает bytes в ZXing.
- `ZxingDecoder` - `MultiFormatReader` с `BarcodeFormat.QR_CODE`.
- `QrCodeAnalyzerFactoryImpl` - Hilt-friendly factory.
- `UriOtpParser` - финальная проверка `otpauth://` перед сохранением.

Защита от повторной обработки:

- `QrCodeAnalyzer.handled` останавливает повторную обработку после первого успешного decode.
- `QrScannerUiState.hasReadCode` дополнительно предотвращает повторное сохранение и инициирует возврат назад.
- `image.use {}` закрывает `ImageProxy`.
- Ошибки сканирования throttled через `scanErrorIntervalMs = 5000L`.

## 10. DI через Hilt

DI собрана в `di/Modules.kt`.

- `DatabaseModule`
  - `provideDatabase(@ApplicationContext context): AccountDatabase`;
  - `provideAccountDao(accountDatabase): AccountDao`.
- `RepositoryModule`
  - `AccountRepository -> IAccountRepository`;
  - `BackupRepository -> IBackupRepository`;
  - `AccountBackupManager -> IAccountBackupManager`.
- `CryptoModule`
  - регистрирует Tink;
  - создает singleton `Aead` через `AndroidKeysetManager`;
  - prefs: `tink_keyset_prefs`, key: `tink_keyset`;
  - master key URI: `android-keystore://backup_master_key`.
- `CameraModule`
  - `QrCodeAnalyzerFactoryImpl -> QrCodeAnalyzerFactory`.

Hilt entry points:

- `App` помечен `@HiltAndroidApp`.
- `MainActivity` помечен `@AndroidEntryPoint`.
- Экранные ViewModel помечены `@HiltViewModel`.

## 11. Команды сборки и тестов

Linux/macOS:

```bash
./gradlew :app:assembleDebug
./gradlew :app:testDebugUnitTest
./gradlew :app:lintDebug
./gradlew :app:assembleRelease
./gradlew :app:connectedDebugAndroidTest
```

Windows:

```bat
gradlew.bat :app:assembleDebug
gradlew.bat :app:testDebugUnitTest
gradlew.bat :app:lintDebug
gradlew.bat :app:assembleRelease
gradlew.bat :app:connectedDebugAndroidTest
```

Минимальная проверка перед завершением code change по AGENTS:

```bat
gradlew.bat :app:assembleDebug
```

Если изменение затрагивает business logic, validation, OTP/HOTP, parsing, storage, database или security, дополнительно запускать:

```bat
gradlew.bat :app:testDebugUnitTest
```

Для этой карты проекта сборка не обязательна, потому что меняется только документация.

## 12. Рискованные места, которые нельзя менять без необходимости

- OTP generation/parsing/validation:
  - `OtpGenerator`, `OtpCodeManager`, `Base32`, `UriOtpParser`, `AccountValidator`, `AccountConstraints`.
- Секреты и логи:
  - нельзя логировать или показывать OTP secrets, raw `otpauth://`, PIN, backup payload, decrypted account data, generated OTP codes.
- Room schema:
  - `Account`, `AccountDao`, `AccountDatabase`, version `3`, schema JSON;
  - изменения схемы требуют версии, migration и обновления schema history.
- Backup wire format и encryption:
  - `BackupRepository`, `AccountBackupManager`, `AccountBackupDto`, Tink `Aead`, associated data `backup_data`;
  - важно не сломать restore legacy plaintext JSON и encrypted backup.
- PIN/biometric storage:
  - `SettingsManager`, `PinHashUtils`, `PinViewModel`, `core.biometric`;
  - нельзя хранить PIN plaintext или раскрывать детали проверки.
- QR analyzer lifecycle:
  - `CameraPreview`, `QrCodeAnalyzer`, `ZxingDecoder`, `QrScannerViewModel`;
  - важно закрывать `ImageProxy` и не сохранять один QR несколько раз.
- Navigation стартового unlock flow:
  - `AuthenticatorNavHost`, `PinDestination`, переход `PinScreen -> MainDestination`.
- Gradle/dependencies:
  - не конвертировать Groovy Gradle в Kotlin DSL без прямого запроса;
  - не делать широкие upgrades Compose/Kotlin/AGP/Hilt/Room/CameraX без проверки совместимости.

