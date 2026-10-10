# Line 16

Free, offline US federal tax calculator for Android.

Line 16 does the arithmetic of a US federal tax return. You type in a dozen numbers off your W-2 and 1099s, and it tells you exactly what goes on each line of your 1040.

Not a filing service. Not a document manager. Not tax advice. A calculator that knows the tax code and shows its work.

## What it does

- Computes federal income tax and self-employment tax for tax year 2026
- Handles W-2 income, Schedule C (self-employment), business mileage, and simplified home office
- Shows a line-by-line 1040 breakdown in plain English
- Exports a one-page PDF summary you can print, email, or hand to a CPA
- All data stays on device. No internet permission. No accounts. No tracking.

## What it does not do (yet)

- E-file (requires IRS EFIN registration — separate project)
- State taxes
- Itemized deductions (Schedule A)
- Most credits (Child Tax Credit, EITC, education credits)

## Status

v0.1 — working. Builds, runs, computes correct federal tax and self-employment tax, exports a PDF summary. 41 unit tests passing.

## Privacy

The app declares no `INTERNET` permission. Nothing you type leaves the device. There is no account, no telemetry, no ads. The Room database lives in the app's private storage and is wiped when you uninstall.

See [PRIVACY.md](PRIVACY.md).

## Building

Requirements: JDK 17+, Android SDK with platform 35 and build-tools 35.0.0.

    ./gradlew assembleDebug

APK lands at `app/build/outputs/apk/debug/app-debug.apk`.

Run the tests:

    ./gradlew :app:testDebugUnitTest

## Disclaimer

Line 16 is software, not a CPA. It does not file your return and does not give tax advice. You are responsible for what you file.

The 2026 tax figures are based on the best available published guidance. Replace the constants in `domain/year/TaxYear2026.kt` with the official IRS numbers when Rev. Proc. 2025-32 (or its successor) is published.

## License

GPL-3.0-or-later. See [LICENSE](LICENSE).
