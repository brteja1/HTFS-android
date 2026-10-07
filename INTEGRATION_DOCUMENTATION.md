# HTFS Android Library

We have successfully built the Android Library wrapper for **HTFS (Hierarchical Tagging File System)**. The library leverages **Chaquopy** under the hood to execute the core Python tagging engine natively on Android.

## Artifact Location
The compiled Android library AAR file is located at:
*   [htfs-android-debug.aar](file:///linuxdev/github/HTFS-android/android-lib/htfs-android/build/outputs/aar/htfs-android-debug.aar)

## Project Layout
*   **Gradle Project Root:** `/linuxdev/github/HTFS-android/android-lib`
*   **Library Module:** `/linuxdev/github/HTFS-android/android-lib/htfs-android`
*   **Java Wrapper:** [HTFS.java](file:///linuxdev/github/HTFS-android/android-lib/htfs-android/src/main/java/org/htfs/android/HTFS.java)
*   **Python Engine Sources:** Linked directly via git submodule to the [HTFS/](file:///linuxdev/github/HTFS-android/HTFS) directory.

---

## How to Incorporate the Library in an Android App

### 1. Configure the Project Gradle Files

In the target application's top-level `build.gradle` (or `settings.gradle`), configure the **Chaquopy** plugin:

```gradle
// build.gradle (project level)
buildscript {
    repositories {
        google()
        mavenCentral()
    }
    dependencies {
        classpath 'com.android.tools.build:gradle:8.2.2'
        classpath 'com.chaquo.python:gradle:15.0.1'
    }
}
```

In the app module-level `build.gradle`, apply the plugins and specify the architecture filters (ABIs) and library dependencies:

```gradle
// app/build.gradle
plugins {
    id 'com.android.application'
    id 'com.chaquo.python'
}

android {
    defaultConfig {
        minSdk 21
        
        ndk {
            // Python needs native libraries matching target ABIs
            abiFilters "armeabi-v7a", "arm64-v8a", "x86", "x86_64"
        }

        python {
            version "3.10"
            pip {
                install "rdflib>=6.0"
            }
        }
    }
}

dependencies {
    // Add the library module or import the compiled AAR
    implementation project(':htfs-android') // if multi-project
    // OR implementation files('libs/htfs-android-debug.aar')
}
```

Ensure AndroidX is enabled in the app's `gradle.properties`:
```properties
android.useAndroidX=true
```

---

## Usage Example (Java / Kotlin)

Use the [HTFS](file:///linuxdev/github/HTFS-android/android-lib/htfs-android/src/main/java/org/htfs/android/HTFS.java) wrapper in your Android components. Specify a boundary path (like app internal files directory) where `.tagfs.db` and `.tagfs.ttl` files should be created:

```java
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import org.htfs.android.HTFS;
import java.io.File;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private HTFS htfs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Use internal app storage as the HTFS tag boundary directory
        File tagfsBoundary = new File(getFilesDir(), "tagfs_root");
        if (!tagfsBoundary.exists()) {
            tagfsBoundary.mkdirs();
        }

        // 1. Initialize the library
        htfs = new HTFS(this, tagfsBoundary.getAbsolutePath());
        htfs.initialize();

        // 2. Add hierarchical tags (e.g., Reports under Alpha phase of Project)
        htfs.addTags(Arrays.asList("Project/Alpha/Reports", "Status/Completed"));

        // 3. Track a resource (e.g., an internal document or media file)
        File sampleDoc = new File(tagfsBoundary, "quarterly_report.pdf");
        String resourceUrl = sampleDoc.getAbsolutePath();
        
        htfs.addResource(resourceUrl);

        // 4. Associate tags with the resource
        htfs.tagResource(resourceUrl, Arrays.asList("Reports", "Status/Completed"));

        // 5. Query resources by expression
        List<String> matchingResources = htfs.getResourcesByTagExpr("Reports & ~Completed");
        
        // 6. Cleanup on exit
        htfs.close();
    }
}
```
