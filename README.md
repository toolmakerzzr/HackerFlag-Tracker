# 🛡️ Hackerflag Tracker & Cyber Defense Platform
### *Made by Akash Tiwari*

An enterprise-grade Android defense application built with **Jetpack Compose**, **BiometricPrompt API**, **Material Design 3**, and **GitHub Actions CI/CD** for automatic APK generation.

---

## 🚀 GitHub Se APK Kaise Generate Karein (Direct Step-by-Step Guide)

Aap is pure project ko GitHub par daal kar **1-click me ready APK** generate kar sakte hain. Is repo me **GitHub Actions CI/CD Workflow** (`.github/workflows/build.yml`) pehle se configured hai!

### Step 1: Code Ko GitHub Par Push Karein

Apne computer ya terminal me project directory me ye commands chalayein:

```bash
# 1. Git repository initialize karein
git init

# 2. Sabhi files add karein
git add .

# 3. Commit karein
git commit -m "Initial Release - Hackerflag Defense by Akash Tiwari"

# 4. Main branch set karein
git branch -M main

# 5. Apne GitHub repository ka URL connect karein
git remote add origin https://github.com/<AAPKA_GITHUB_USERNAME>/<AAPKA_REPO_NAME>.git

# 6. Push karein
git push -u origin main
```

---

### Step 2: GitHub Actions Se Direct APK Download Karein

1. Apne GitHub repository page par jayein (browser me).
2. Upar menu me **"Actions"** tab par click karein.
3. Wahan **"Build Android APK"** workflow dikhega.
   - Ye push karte hi automatically shuru ho jata hai.
   - Ya aap **"Run workflow"** button par click karke manually bhi APK build trigger kar sakte hain!
4. 2-3 minute me build complete ho jayega (Green Checkmark ✅).
5. Us build run par click karein aur page ke neeche **Artifacts** section se **`HackerFlag-Defense-AkashTiwari-APK`** download kar lijiye!
6. ZIP file ko extract karke `app-debug.apk` apne Android phone me install karein!

---

### Step 3: Local System (Offline) Me APK Kaise Banayein

Agar aap apne computer me Android Studio ya Terminal se direct APK banana chahte hain:

- **Linux / macOS:**
  ```bash
  chmod +x gradlew
  ./gradlew assembleDebug
  ```

- **Windows:**
  ```cmd
  gradlew.bat assembleDebug
  ```

- **APK Output Location:**
  ```
  app/build/outputs/apk/debug/app-debug.apk
  ```

---

## 🌟 Key Application Features

1. **BiometricPrompt API Entry Gate & Owner Security:**
   - App ke main entry point par hardware biometric prompt (Fingerprint / Face Unlock).
   - Emulator ya fallback ke liye default emergency owner PIN (`9999`).
   - "LOCK" button se kisi bhi screen se turant console lock karne ki suvidha.

2. **3-Second Emergency Police SOS Button:**
   - Upper right corner aur dashboard me hamesha visible.
   - Accidental click rokne ke liye 3-second hold circular animation.
   - 112 / 100 aur designated emergency contacts ko alert broadcast.

3. **Intelligent Cyber Defense AI (ChatGPT-grade Assistant):**
   - Natural Hindi & English me instant answers.
   - Gemini 3.5 Flash & neural heuristic offline fallback.
   - Ultra-smooth, high-fps loading indicator aur status telemetry.

4. **CEIR & IMEI Defense Portal:**
   - Bharat Sarkar ke Sanchar Saathi CEIR portal par stolen device blacklist karne ka pura process.
   - Dual-IMEI tracking radar aur operator alert simulation.

5. **Financial & UPI Lockdown Shield:**
   - Universal USSD code `*99*4*1#` se sabhi UPI accounts freeze.
   - PhonePe, Google Pay, Paytm, SBI, HDFC helplines.

6. **Legal FIR & Affidavit Generator:**
   - CrPC / BNS Section ke antargat Zero FIR legal complaint generator.

---

## 👨‍💻 Creator & Engineering

- **Lead Engineer & Architect:** **Akash Tiwari**
- **Architecture:** MVVM, Clean Jetpack Compose, Material Design 3
- **Language:** Kotlin 2.2.10
- **Build System:** Gradle 9.3.1 / AGP 9.1.1 with GitHub Actions CI/CD
