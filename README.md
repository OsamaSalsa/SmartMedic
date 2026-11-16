<div align="center">

# 🩺 SmartMedic — طبيبي الذكي

**An Intelligent Personal Health, Lab Tracking, Medication Alarm & AI Medical Advisor App for Android**

[![English](https://img.shields.io/badge/Language-English-blue.svg)](README.md)
[![Arabic](https://img.shields.io/badge/اللغة-العربية-green.svg)](README.ar.md)
[![Russian](https://img.shields.io/badge/Язык-Русский-red.svg)](README.ru.md)

*Built with 100% Kotlin, Jetpack Compose, Material Design 3, Room Database, Firebase Auth (Google Sign-In), Firebase App Check, and Firebase AI Logic (`gemini-3.8-flash`).*

</div>

---

## 🌐 Languages / اللغات / Языки

- 🇺🇸 **English**: [README.md](README.md) *(Current)*
- 🇸🇦 **العربية**: [README.ar.md](README.ar.md)
- 🇷🇺 **Русский**: [README.ru.md](README.ru.md)

---

## 📖 Overview

**SmartMedic (طبيبي الذكي)** is a modern, comprehensive Android health management application built exclusively in **Kotlin** and **Jetpack Compose (Material 3)**. It helps patients organize laboratory results, track daily vital signs (Blood Sugar, Blood Pressure, Weight & BMI), manage medication schedules with reliable **Android 12–16+ Exact Alarms**, scan paper/PDF lab reports and prescriptions using multimodal AI, and consult an **AI Medical Advisor** powered by **Firebase AI Logic**—without bundling any developer API keys inside the app.

---

## ✨ Key Features

### 1. 🔐 Google Sign-In & Keyless Firebase AI Security
- **Google Account Authentication**: Users authenticate with their personal Google account via Jetpack **Credential Manager** (`GetSignInWithGoogleOption`) and silent auto-sign-in (`GetGoogleIdOption`), backed by **Firebase Authentication**.
- **Auth-Gated Experience**: Main screens and AI features remain gated behind authentication, with quick account switching and clean sign-out in **Profile & Settings**.
- **Firebase App Check**: Automatically configures `DebugAppCheckProviderFactory` during debug builds and `RecaptchaAppCheckProviderFactory` in release builds before any UI or AI call executes.
- **No Embedded `GEMINI_API_KEY`**: All Gemini (`gemini-3.8-flash`) calls route securely through **Firebase AI Logic** (`Firebase.ai`), eliminating client-side API key exposure.

### 2. 🤖 AI Medical Advisor & Streaming Chat (`AiAdvisorScreen`)
- **Comprehensive Clinical Report Generation**: Produces structured JSON medical reports (`Schema.obj`) analyzing:
  - Completed laboratory test values and abnormal flags (`High` / `Low` / `Normal`)
  - Recent blood pressure & blood sugar trends
  - Body Mass Index (BMI) and patient chronic profile
  - Active medications and **drug–food / drug–nutrient interactions**
- **Real-Time Streaming Medical Chat**: Uses `generateContentStream()` for low-latency Q&A on lab interpretation, nutrition, lifestyle, and medication safety.
- **Transparent Error Propagation**: Displays clear timeout (60s) and connection/attestation error cards with retry controls.

### 3. 📄 Multimodal AI Document & Prescription Scanners
- **AI Lab Report Scanner (`UploadLabReportDialog`)**:
  - Supports **multi-page PDF reports** and **camera/gallery photos** (under 20 MB payload handling).
  - Extracts test names, numeric values, units, reference ranges, lab name, date, and status flags automatically.
- **AI Prescription Scanner (`UploadPrescriptionDialog`)**:
  - Reads printed or handwritten prescriptions from camera, gallery, or PDF.
  - Extracts medications, dosages, meal instructions, frequencies, reminder times, active ingredients, uses, side effects, and food interactions—ready to save with one tap and schedule alarms automatically.

### 4. 🏠 Unified Home Dashboard (`HomeScreen`)
- **Dynamic Single-Metric Vitals Selector**: Switch cleanly between **Blood Sugar**, **Blood Pressure**, and **Weight & BMI** cards to inspect the latest reading without screen clutter.
- **Quick Vitals Logging**: Fast modal dialogs for Blood Sugar (Fasting / Post-meal / Random), Blood Pressure (Systolic / Diastolic / Pulse / Period), and Weight/Height with automatic BMI calculation.
- **Critical Test Alerts & Quick Shortcuts**: Highlights abnormal lab values and provides direct shortcuts to AI Consultation, Lab Report Scan, and Prescription Scan.

### 5. 💊 Daily Tracking & Android 12–16+ Medication Alarms (`DailyTrackingScreen`)
- **Reliable Exact Alarm Engine (`MedicationReminderScheduler` & `MedicationAlarmReceiver`)**:
  - Uses `AlarmManager.setExactAndAllowWhileIdle` / `setAlarmClock` for accurate medication reminders even in Doze mode.
  - Full **Android 12+ (`SCHEDULE_EXACT_ALARM` / `USE_EXACT_ALARM`)** and **Android 13+ (`POST_NOTIFICATIONS`)** permission handling with clear status cards.
  - Supports **Full-Screen Urgent Alerts**, custom sound & vibration, `BootCompletedReceiver` automatic rescheduling after device reboot, and battery optimization guidance.
- **Expandable Medication Cards**: Inspect active ingredients, uses, common side effects, food interactions, mark doses as **Taken Today**, edit schedules, or test instant alarm notifications.

### 6. 🧪 Lab Tests & 📊 Results / PDF Export (`LabTestsScreen` & `ResultsVitalsScreen`)
- Search, filter (`All` / `Abnormal` / `Normal`), add custom test types, and track historical results for any biomarker.
- View vitals history & trends across Blood Sugar, Blood Pressure, and Weight.
- Export clean **PDF Health Reports** (`PdfReportExporter`) for doctor visits.

### 7. ⚙️ Adaptive UI, Themes, Localization & Backup (`ProfileSettingsScreen`)
- **Consistent Light & Dark Mode**: Carefully tuned Material 3 dark and light surface/text contrast across all screens, dialogs, and pharmacology cards (`android:forceDarkAllowed="false"`).
- **Multi-Language + RTL/LTR Support**: Live switching between **System Auto**, **Arabic (`AR` - RTL)**, **English (`EN` - LTR)**, and **Russian (`RU` - LTR)**.
- **Backup & Restore (`GoogleDriveBackupManager`)**: Export full JSON backups of labs, vitals, medications, and consultations to Google Drive or local storage, and restore with **Merge** or **Replace** modes.

---

## 🛠️ Tech Stack & Architecture

| Layer | Technologies |
| :--- | :--- |
| **Language** | Kotlin `2.2.10` |
| **UI Framework** | Jetpack Compose (Compose BOM `2024.09.00`), Material Design 3 |
| **Architecture** | Single-Activity (`MainActivity`), MVVM (`MedicalViewModel`, `StateFlow`, `collectAsStateWithLifecycle`) |
| **Local Persistence** | Room Database (`2.7.0`) with KSP (`MedicalDatabase`, `MedicalDao`, 8 entities) |
| **Authentication** | Firebase Auth + Jetpack Credential Manager (`1.5.0`) + Google Identity (`googleid`) |
| **AI Engine** | Firebase AI Logic (`firebase-ai`, model: `gemini-3.8-flash`) + Firebase BoM `34.17.0` |
| **Security** | Firebase App Check (`firebase-appcheck-debug`, `firebase-appcheck-recaptcha`) |
| **Alarms & System** | `AlarmManager`, `BroadcastReceiver` (`BOOT_COMPLETED`, exact alarms), `NotificationManager`, `FileProvider` |
| **Images & Files** | Coil Compose (`2.7.0`), Android `PdfRenderer` & `PdfDocument` |
| **Min / Target SDK** | Min SDK `24` (Android 7.0) · Compile / Target SDK `36` |

---

## 📂 Project Structure

```text
app/src/main/java/com/example/
├── MainActivity.kt                        # App Check init, Google Auth Gate & navigation host
├── data/
│   ├── local/
│   │   ├── MedicalDatabase.kt             # Room database configuration & migrations
│   │   ├── MedicalDao.kt                  # DAO queries & Flow streams
│   │   └── DefaultData.kt                 # Initial clinical templates
│   ├── model/
│   │   ├── Entities.kt                    # LabTest, BloodPressure, BloodSugar, Weight, Medication, etc.
│   │   └── PredefinedLabTests.kt          # Reference medical test catalog
│   ├── remote/
│   │   └── GeminiService.kt               # Firebase AI Logic (gemini-3.8-flash) prompt, schema & multimodal calls
│   └── repository/
│       └── MedicalRepository.kt           # Unified data repository
├── receiver/
│   ├── MedicationAlarmReceiver.kt         # High-priority alarm notifications & full-screen alerts
│   └── BootCompletedReceiver.kt           # Restores scheduled alarms after reboot/update
├── ui/
│   ├── components/                        # Reusable cards, dialogs, Drive sync, PDF/prescription scanners
│   ├── locale/                            # AppLanguage & AppStrings (AR, EN, RU)
│   ├── screens/                           # AuthScreen, HomeScreen, DailyTrackingScreen, LabTestsScreen,
│   │                                      # ResultsVitalsScreen, AiAdvisorScreen, HealthProfileScreen, ProfileSettingsScreen
│   ├── theme/                             # Material 3 Light/Dark ColorSchemes, Typography, Shapes
│   └── viewmodel/
│       └── MedicalViewModel.kt            # Centralized StateFlow state, alarms, AI streaming & backup logic
└── util/
    ├── AppCheckInit.kt                    # configureAppCheck(context, intent) debug & reCAPTCHA setup
    ├── AuthHelper.kt                      # Credential Manager silent/interactive Google Sign-In & Sign-Out
    ├── GoogleDriveBackupManager.kt        # JSON backup & restore
    ├── LabReportFileHelper.kt             # PDF page rendering & image compression (<20MB)
    ├── MedicationReminderScheduler.kt     # Android 12+ exact alarm scheduling
    └── PdfReportExporter.kt               # Printable PDF medical summary generator
```

---

## 📋 Permissions Declared

| Permission | Purpose |
| :--- | :--- |
| `INTERNET`, `ACCESS_NETWORK_STATE` | Firebase Auth, App Check attestation & Firebase AI Logic requests |
| `POST_NOTIFICATIONS` | Displaying medication reminder notifications on Android 13+ |
| `SCHEDULE_EXACT_ALARM`, `USE_EXACT_ALARM` | Firing medication alarms on time on Android 12+ |
| `VIBRATE`, `WAKE_LOCK`, `USE_FULL_SCREEN_INTENT` | Ensuring urgent medication reminders alert the user reliably |
| `RECEIVE_BOOT_COMPLETED` | Automatically rescheduling medication alarms after device reboot |
| `REQUEST_IGNORE_BATTERY_OPTIMIZATIONS` | Optional prompt to prevent background alarm throttling |

---

## 📦 Building & Exporting the APK

### Option 1: Build Debug APK via Gradle CLI
Run from the project root directory (use system `gradle`, not `./gradlew` in cloud container environments):

```bash
gradle assembleDebug
```

The generated APK is saved at:
```text
app/build/outputs/apk/debug/app-debug.apk
```

### Option 2: Export Signed Release APK / AAB in AI Studio
1. Open the **Settings / Export menu** in Google AI Studio.
2. Select **Generate APK / AAB** (or export ZIP / push to GitHub).
3. For manual release signing via CLI, set `KEYSTORE_PATH`, `STORE_PASSWORD`, and `KEY_PASSWORD` environment variables and run:
   ```bash
   gradle assembleRelease
   ```

---

## ⚠️ Medical Disclaimer

SmartMedic provides educational insights and AI-assisted organization of personal health records. AI-generated summaries, medication interactions, and chat responses are **not a substitute for professional medical diagnosis, advice, or treatment**. Always consult a qualified physician or clinical pharmacist before starting, changing, or stopping any medication.
