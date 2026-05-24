#!/usr/bin/env bash
# Sets up Android SDK + build tools in the Codespace.
# AI Edge Gallery compatible Gemma model info is printed at the end.
set -euo pipefail

ANDROID_SDK=/opt/android-sdk
CMDLINE_TOOLS_URL="https://dl.google.com/android/repository/commandlinetools-linux-11076708_latest.zip"

echo "==> Installing Android SDK command-line tools"
sudo mkdir -p "$ANDROID_SDK/cmdline-tools"
curl -fsSL "$CMDLINE_TOOLS_URL" -o /tmp/cmdline-tools.zip
sudo unzip -q /tmp/cmdline-tools.zip -d "$ANDROID_SDK/cmdline-tools"
sudo mv "$ANDROID_SDK/cmdline-tools/cmdline-tools" "$ANDROID_SDK/cmdline-tools/latest"
rm /tmp/cmdline-tools.zip

export PATH="$ANDROID_SDK/cmdline-tools/latest/bin:$ANDROID_SDK/platform-tools:$PATH"

echo "==> Accepting SDK licenses"
yes | sdkmanager --licenses > /dev/null 2>&1 || true

echo "==> Installing Android SDK platform + build tools (SDK 36)"
sdkmanager \
  "platforms;android-36" \
  "build-tools;36.0.0" \
  "platform-tools" \
  "extras;android;m2repository" \
  "extras;google;m2repository"

echo "==> Writing local.properties"
cat > /workspaces/TAVI/local.properties <<EOF
sdk.dir=$ANDROID_SDK
EOF

echo ""
echo "==> AI Edge Gallery model compatibility"
echo "    TAVI's LocalAIEngine uses MediaPipe Tasks GenAI (same as AI Edge Gallery)."
echo "    Compatible models (download from Kaggle — requires free account):"
echo ""
echo "    Gemma 2 2B IT q4_k_m  — recommended, ~1.5 GB"
echo "      https://www.kaggle.com/models/google/gemma-2/tfLite/gemma2-2b-it-gpu-int4"
echo ""
echo "    Gemma 3 1B IT          — smaller, ~600 MB"
echo "      https://www.kaggle.com/models/google/gemma-3/tfLite/gemma3-1b-it-int4"
echo ""
echo "    After downloading, set the model path in TAVI's Warden screen"
echo "    (AI model path field) — or set it at build time via:"
echo "    ./gradlew assembleDebug -PaiModelPath=/path/to/gemma.bin"
echo ""
echo "==> Building TAVI debug APK"
cd /workspaces/TAVI && ./gradlew assembleDebug --no-daemon

echo ""
echo "==> Done. APK: app/build/outputs/apk/debug/app-debug.apk"
echo "    To install on a connected device: adb install app/build/outputs/apk/debug/app-debug.apk"
