# 🤖 Root Servant

[![Kotlin](https://img.shields.io/badge/Kotlin-1.9+-purple.svg)](https://kotlinlang.org)
[![Jetpack Compose](https://img.shields.io/badge/Jetpack_Compose-Latest-green.svg)](https://developer.android.com/jetpack/compose)
[![Gemini API](https://img.shields.io/badge/Gemini_API-2.5_Flash-blue.svg)](https://ai.google.dev/)

**Root Servant** is an experimental Android application that bridges the gap between **AI intelligence** and **Android root-level system management**. Powered by the **Gemini 2.5 Flash** model, it allows users to control their rooted devices using natural language.

> ⚠️ **IMPORTANT DEVELOPMENT NOTE:** This project is a proof of concept. Refactoring of command execution architecture, security layers, and continuous updates are planned for upcoming releases (v2).

---

## 🚨 STRICT DISCLAIMER

**THIS APPLICATION REQUIRES ROOT ACCESS AND ACTS AS AN AI-DRIVEN SYSTEM COMMANDER.**

By using this application, you acknowledge that **YOU** are solely responsible for any data loss, system corruption, bootloops, or permanently bricked devices. The developer assumes **ZERO** liability. Use strictly at your own risk.

---

## ✨ Features

*   🧠 **Natural Language to Root Commands:** Type what you want to do (e.g., *"Make system partition writeable"* or *"Change build.prop permissions to 644"*), and Gemini will generate the appropriate Linux command.
*   🔒 **Safe API Management:** No hardcoded API keys. Users initialize the app safely using their own free Gemini API Key.
*   💻 **Terminal UI:** Modern, cyberpunk-themed dark interface with a dynamic console output showing live success/error execution streams.
*   ⚡ **Modern Stack:** Built entirely using **Kotlin**, **Jetpack Compose** for the UI, and **Coroutines / Flow** for reactive state management.

---

## 🚀 How It Works (`BakingViewModel.kt` Logic)

1. The user inputs a prompt in plain English/Turkish.
2. The prompt is wrapped inside a strict **System Prompt** instructing Gemini to *only* output structured commands: `COMMAND: <linux_command>`.
3. The app parses the AI output, runs safety checks, and executes it via a standard Superuser (`su -c`) runtime shell.

Currently supported core actions: `chmod`, `cp`, `rm`, `mount`, `mkdir`.

---

## 🛠️ How to Build & Run

1. Clone this repository:
```bash
   git clone [https://github.com/YOUR_GITHUB_USERNAME/root-servant.git](https://github.com/YOUR_GITHUB_USERNAME/root-servant.git)
Open the project in Android Studio.

Make sure you have a rooted Android device or emulator with Magisk / KernelSU installed.

Get a free Gemini API Key from Google AI Studio.

Build, install, and grant root access when prompted!

🗺️ Roadmap & Upcoming Updates (TODO)
[ ] Implement robust regex-based secondary validation before command execution.

[ ] Fix command execution chaining (Refactoring isCommandSafe execution flow).

[ ] Add a history log for previously executed AI commands.

[ ] Dynamic partition remount safety features.

📄 License
This project is open-source. Feel free to fork and experiment at your own peril!
