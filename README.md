# 🚀 Blac.ai – Lightweight AI Assistant for Android

<p align="center">
  <b>A fast, privacy-first AI assistant designed for low-RAM Android devices.</b><br>
  Powered by Google Gemini + On-Device AI
</p>

<p align="center">
  <img src="https://img.shields.io/badge/Platform-Android-green?style=for-the-badge" />
  <img src="https://img.shields.io/badge/Min%20RAM-1.5GB-blue?style=for-the-badge" />
  <img src="https://img.shields.io/badge/License-MIT-orange?style=for-the-badge" />
  <img src="https://img.shields.io/badge/Material%203-Dark%20Theme-black?style=for-the-badge" />
</p>

---

## 📱 Overview

**Blac.ai** is a free, open-source AI assistant built specifically for Android devices with as little as **1.5 GB RAM**.

It combines:

- ⚡ Google Gemini API (Gemini 1.5 Flash)
- 🧠 On-device OCR (ML Kit)
- 🎙️ Offline speech recognition (Vosk)
- 🎨 Material 3 dark UI

All packaged in a lightweight APK under **15 MB**.

---

## ✨ Features

### 💬 Smart Chat & Coding
- Powered by **Gemini 1.5 Flash**
- Specialised coding prompts
- Think Mode (reasoning support)
- Code Mode (optimized for programming)

### 📁 File & OCR Support
- Upload images, PDFs, and documents
- Multi-image OCR with intelligent text merging
- ML Kit text extraction

### 🎤 Offline Voice Input
- Fully offline voice recognition using **Vosk**
- Model auto-downloads on first use
- No internet required for speech input

### 📝 Code Highlighting
- Kotlin
- Python
- JavaScript
- And more (via Prism4j)

### ⚙️ Toggleable Modes
- 🧠 Think Mode
- 🔎 Search Mode (real-time web queries)
- 💻 Code Mode

### 🔐 Privacy First
- Optional user-provided API key
- No forced data collection
- No data leaves device without consent

### 🌙 Modern UI
- Material 3 design
- Dark theme optimized
- Clean & distraction-free interface

### 📦 Lightweight
- APK size: < 15 MB
- Runs smoothly on 1.5 GB RAM devices
- Optimized memory footprint

---

## 📸 Screenshots

> *(Add your screenshots here)*

Example:

```
![Chat Screen](screenshots/chat.png)
![OCR Mode](screenshots/ocr.png)
```

---

## 🚀 Getting Started

### 📋 Prerequisites

- Android 7.0 (API 24) or higher
- Gemini API key from:
  https://aistudio.google.com/

> ⚠ A built-in demo key is included but rate-limited.

---

## 📦 Installation

### Option 1 – Install APK

1. Download the latest APK from:
   https://github.com/yourusername/Blac.ai/releases
2. Enable **Install from Unknown Sources**
3. Install and open the app
4. Start chatting 🚀

---

## 🛠 Building From Source

### 1️⃣ Clone Repository

```bash
git clone https://github.com/yourusername/Blac.ai.git
cd Blac.ai
```

### 2️⃣ Open in Android Studio  
OR build via terminal.

### 3️⃣ Add API Key

Add to `local.properties`:

```
GEMINI_API_KEY=your_key_here
```

### 4️⃣ Build Debug APK

```bash
./gradlew assembleDebug
```

APK location:

```
app/build/outputs/apk/debug/
```

---

## 🤖 GitHub Actions (CI)

This repository includes an automated GitHub Actions workflow.

- Automatically builds APK on push to `main`
- Download build artifact from **Actions → Latest Workflow**

---

## 🤝 Contributing

Contributions are welcome!

1. Fork the repo
2. Create a feature branch
3. Submit a Pull Request

Or open an issue for suggestions/bugs.

---

## 🏗 Tech Stack

- Google Gemini API
- ML Kit Text Recognition
- Vosk Speech Recognition
- Prism4j (Syntax Highlighting)
- Jetpack Compose
- Material 3

---

## 🔐 Security & Privacy

Blac.ai follows a privacy-first philosophy:

- No tracking
- No analytics
- No forced cloud sync
- Fully offline speech recognition

---

## 📜 License

This project is licensed under the **MIT License**.  
See `LICENSE` for details.

---

## 🙏 Acknowledgements

- Google Gemini API  
- ML Kit Text Recognition  
- Vosk Speech Recognition  
- Prism4j  
- Jetpack Compose  

---

<p align="center">
  Made with ❤️ for lightweight Android devices
</p>