#!/usr/bin/env bash
cd "$(dirname "$0")"
# Required for macOS Camera Permissions via AVFoundation in some environments
export OPENCV_AVFOUNDATION_SKIP_AUTH=1
# Must include sqlite-jdbc on classpath or login/DB will fail with "No suitable driver"
CP="out:lib/sqlite-jdbc-3.51.2.0.jar:lib/opencv-480.jar"

# OpenCV Java needs BOTH lib/opencv.jar AND the JNI native library (libopencv_java*.dylib on macOS).
# Set OPENCV_NATIVE_DIR yourself, or we try common Homebrew locations:
if [ -z "${OPENCV_NATIVE_DIR:-}" ]; then
  for d in lib /opt/homebrew/opt/opencv/lib /usr/local/opt/opencv/lib; do
    if [ -d "$d" ] && ls "$d"/libopencv_java*.dylib >/dev/null 2>&1; then
      # Make it an absolute path to be safe
      OPENCV_NATIVE_DIR="$(cd "$d" && pwd)"
      echo "Using OpenCV natives: $OPENCV_NATIVE_DIR"
      break
    fi
  done
fi

if [ -n "${OPENCV_NATIVE_DIR:-}" ]; then
  # DYLD_LIBRARY_PATH helps the OS find dependencies of the native library
  export DYLD_LIBRARY_PATH="$OPENCV_NATIVE_DIR:${DYLD_LIBRARY_PATH:-}"
  exec java -Djava.library.path="$OPENCV_NATIVE_DIR" -cp "$CP" com.smartexam.Main
else
  echo "Warning: OPENCV_NATIVE_DIR not set and no Homebrew OpenCV lib found."
  echo "Install: brew install opencv   then ensure lib/opencv.jar matches that version, or set:"
  echo "  export OPENCV_NATIVE_DIR=/path/to/folder/with/libopencv_java.dylib"
  exec java -cp "$CP" com.smartexam.Main
fi
