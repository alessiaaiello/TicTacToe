# TicTacToe (Android)

Simple native Android TicTacToe app (Kotlin).

## About This Project

This is a **vibe-coded experiment** — a fully functional Android app built through conversational AI-assisted development. The entire project was scaffolded, debugged, built, and deployed to an emulator through natural language instructions, demonstrating rapid prototyping and iterative problem-solving with AI pair programming.

**Features:**
- 3×3 grid-based gameplay
- Real-time turn indication (X vs O)
- Win/draw detection
- Reset button for new games
- ViewBinding for type-safe UI references
- Material Design with AppCompat theme
- Ready-to-run debug APK

## Import and run

- Open Android Studio.
- Choose "Open" and select this repository's root folder.
- Let Android Studio sync Gradle.
- Run on an emulator or a connected device.

## Build with Gradle (command line)

If you prefer the command line, from the project root you can build an APK with Gradle (Android Studio will generate the Gradle wrapper if needed):

```bash
./gradlew assembleDebug
```

The debug APK will be in `app/build/outputs/apk/debug/` after a successful build.

## macOS: install Java & build

1. Install Homebrew if you don't have it:

```bash
/bin/bash -c "$(curl -fsSL https://raw.githubusercontent.com/Homebrew/install/HEAD/install.sh)"
```

2. Install Java (OpenJDK):

```bash
brew install openjdk
# If needed, add Java to your PATH (follow brew output instructions)
```

3. (Optional) Install Gradle to generate the wrapper locally:

```bash
brew install gradle
cd /path/to/project
gradle wrapper
```

4. Build the debug APK (uses `gradlew` if present):

```bash
# If wrapper exists
./gradlew assembleDebug
# Or, with system gradle
gradle assembleDebug
```

If you prefer not to install CLI tools, open the project in Android Studio and run it there — Android Studio will manage the Java/Gradle toolchain and generate any missing wrapper files.

## APK Built!

A debug APK has been built successfully:

```
app/build/outputs/apk/debug/app-debug.apk (5.0 MB)
```

**To install on a device or emulator:**

```bash
adb install app/build/outputs/apk/debug/app-debug.apk
```

Or, drag the APK to an Android emulator window in Android Studio.

Notes:
- This project targets `compileSdk 33` and `minSdk 21`.
- If Android Studio requests Gradle or plugin upgrades, accept the recommended changes.
