# HTFS Android wrapper project

This project provides an Android library wrapper for the **HTFS (Hierarchical Tagging File System)** Python package, enabling native tagging operations inside Android applications.

## Project Structure

*   **`HTFS/`** – A Git submodule referencing the main Python tagging library repository.
*   **`android-lib/`** – The Android Library project containing:
    *   `settings.gradle` & `build.gradle` configurations for Android and **Chaquopy** compilation.
    *   `htfs-android` Library Module targeting compileSdk 34 and minSdk 21.
    *   [HTFS.java](file:///linuxdev/github/HTFS-android/android-lib/htfs-android/src/main/java/org/htfs/android/HTFS.java) – Java wrapper interface that interacts with the python library via the Chaquopy API.
*   **`INTEGRATION_DOCUMENTATION.md`** – Integration guide detailing how to incorporate this library into target apps, gradle configuration parameters, and code usage snippets.

---

## Build Requirements

1.  **Java Environment:** JDK 21 (or newer) is recommended to run the configured Gradle 8.7 build.
2.  **Android SDK:** Set `ANDROID_HOME` pointing to your Android SDK location.
3.  **Host Python Environment:** Chaquopy compiles requirements and proxies on the build machine. It expects a host Python 3.10 executable to construct build dependencies (avoids `cgi` deprecation issues in python 3.13).
    *   The build is currently configured to use: `/home/raghub/.conda/envs/chaquopy_build/bin/python`. Update `buildPython` in `android-lib/htfs-android/build.gradle` if compiling on a different environment.

---

## How to Build the AAR

To build the debug AAR library package, execute the following from the `android-lib` directory:

```bash
cd android-lib
./gradlew assembleDebug
```

The compiled Android archive file will be produced at:
`android-lib/htfs-android/build/outputs/aar/htfs-android-debug.aar`

For full details on importing the library and code examples, see [INTEGRATION_DOCUMENTATION.md](file:///linuxdev/github/HTFS-android/INTEGRATION_DOCUMENTATION.md).
