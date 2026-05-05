# RecompOS

RecompOS is a local-first native Android 12-week bodybuilding recomp tracker. It is written in Kotlin with Jetpack Compose, Material 3, Room, DataStore, WorkManager, coroutines, and Flow.

## What Is Included

- Onboarding with start date, units, baseline bodyweight/waist, reminders, and medical disclaimer.
- Dashboard with current week/day/phase, deload banners, today workout, checklist, coach alert, and quick logs.
- Full preloaded 12-week workout plan with cues, cautions, alternatives, deload behavior, and shoulder safety rules.
- Active workout logger with weight, reps, RIR, pain score, warm-up/drop-set flags, notes, rest timer, skip, and finish.
- Persistent logs for bodyweight, waist, nutrition, sleep, digestion/reflux, steps, cardio, supplements, habits, and photos by URI.
- Analytics using Compose Canvas charts and a weekly coach-review engine.
- Knowledge base covering recomp, RIR, progression, deloads, shoulder safety, nutrition, reflux, triglycerides, and supplements.
- Local JSON/CSV export surface and JSON schema validation.
- Configurable theme, units, and reminder scheduling.

## Build Requirements

- Android Studio with Android SDK 36.
- JDK 17.
- Gradle 9.3.1 or Android Studio's bundled Gradle support.

This workspace shell did not have Java, Gradle, or Android SDK on `PATH`, so command-line build verification could not be completed here. Android Studio should download build-time dependencies from Google/Maven Central.

## Build A Debug APK

1. Open this folder in Android Studio.
2. Let Gradle sync complete.
3. Select `app` as the run configuration.
4. Build with **Build > Build Bundle(s) / APK(s) > Build APK(s)**.

Command-line equivalent after JDK/SDK and Gradle are installed:

```powershell
gradle assembleDebug test
```

Debug APK output:

```text
app/build/outputs/apk/debug/app-debug.apk
```

## Privacy

RecompOS has no mandatory login, no backend, no ads, no paywall, and no runtime network permission. Training, body, nutrition, recovery, and photo URI data are stored locally.

## Medical Disclaimer

This app is not medical advice. For high triglycerides, reflux, supplements, and vitamin D dosing, consult a licensed clinician.
