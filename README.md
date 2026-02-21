# Blac.ai – Lightweight AI Assistant for Android

**Blac.ai** is a free, open‑source AI assistant that runs on devices with as little as 1.5 GB RAM. It combines the power of Google's Gemini API with on‑device OCR and offline voice recognition – all in a sleek, dark‑themed Material 3 interface.

## Features

- 💬 **Chat & Coding** – Powered by Gemini 1.5 Flash, with specialised coding prompts.
- 📁 **File Upload** – Upload images, PDFs, and documents (OCR via ML Kit).
- 🖼️ **Multi‑Image OCR** – Extract text from several images at once and merge intelligently.
- 🎤 **Offline Voice Input** – Uses Vosk; model downloaded on first use.
- 📝 **Code Highlighting** – Syntax highlighting for Kotlin, Python, JavaScript, etc.
- ⚙️ **Toggleable Modes** – Think mode (reasoning), Search mode (real‑time web), Code mode.
- 🔐 **Privacy‑First** – Optional user‑provided API key; no data leaves device unless you choose.
- 🌙 **Material 3 Dark Theme** – Clean, professional, easy on the eyes.
- 📦 **Lightweight** – APK < 15 MB, runs on devices with 1.5 GB RAM.

## Screenshots

*(Add your own screenshots here)*

## Getting Started

### Prerequisites

- Android 7.0 (API 24) or higher.
- A Gemini API key from [Google AI Studio](https://aistudio.google.com/) (optional – a built‑in demo key is included but rate‑limited).

### Installation

1. Download the latest APK from the [Releases](https://github.com/yourusername/Blac.ai/releases) page.
2. Install on your device (you may need to enable “Install from unknown sources”).
3. Open the app and start chatting!

### Building from Source

1. Clone the repository:
   ```bash
   git clone https://github.com/yourusername/Blac.ai.git
```

1. Open the project in Android Studio (or build via command line).
2. Add your Gemini API key to local.properties:
   ```
   GEMINI_API_KEY=your_key_here
   ```
3. Build the APK:
   ```bash
   ./gradlew assembleDebug
   ```
   The APK will be at app/build/outputs/apk/debug/.

GitHub Actions

This repository includes a workflow that automatically builds the APK on every push to the main branch. Go to the Actions tab, select the latest workflow, and download the artifact.

Contributing

Contributions are welcome! Please open an issue or submit a pull request.

License

This project is licensed under the MIT License – see the LICENSE file for details.

Acknowledgements

· Google Gemini API
· ML Kit Text Recognition
· Vosk Speech Recognition
· Prism4j for syntax highlighting
· Jetpack Compose for the UI

```