# Table of Contents

- [Prerequisite](#prerequisite)
  - [1. Add euphony to your project](#1-add-euphony-to-your-project)
    - [1.1 Using Maven repository](#11-using-maven-repository)
    - [1.2 Import the aar/jar file directly](#12-import-the-aarjar-file-directly)
    - [1.3 Import the Euphony module in your project](#13-import-the-euphony-module-in-your-project)
  - [2. Edit your AndroidManifest.xml](#2-edit-your-androidmanifestxml)
    - [Request permission to users](#request-permission-to-users)

# Prerequisite

You need to do two actions.

1. Add euphony to your project
2. Edit your AndroidManifest.xml to permit euphony to record audio.

---

## 1. Add euphony to your project

There are 3 ways adding euphony to your project.

&nbsp;&nbsp;&nbsp;&nbsp;1. Using Maven repository

&nbsp;&nbsp;&nbsp;&nbsp;2. Import the aar/jar file directly

&nbsp;&nbsp;&nbsp;&nbsp;3. Import the Euphony module in your project

### 1.1 Using Maven repository

---

1. Show **_Project window_** with **_Android project view_**. You can select a project view mode in the dropdown list, then open your `build.gradle` file under `Gradle Scripts`.

<img width="392" alt="maven_001_auto_x2_colored_toned" src="https://user-images.githubusercontent.com/27720475/130187144-6bcd31e2-589e-48e0-91ff-d05d28bea4e4.png">

2. Add the following line to the `dependencies` section:

```gradle
dependencies {
	// other dependencies
	// ...
	implementation 'co.euphony.lib:euphony:0.8.0.1'
}
```

### 1.2 Import the aar/jar file directly

---

#### Environment

> OS : Windows 10  
> Android Studio : Arctic Fox | 2020.3.1

1. Download `euphony.aar` : [MavenCentral euphony artifact](https://search.maven.org/artifact/co.euphony.lib/euphony/0.8.0.1/aar) follow the link and download aar file

<img width="392" alt="aar_001" src="https://user-images.githubusercontent.com/27720475/130188260-a0f3ee6d-7afc-4dfc-928e-5562eca476ba.png">

2. Put `euphony.aar` file in `libs` folder, and just click `Refactor`

<img width="392" alt="aar_002_auto_x2_colored_toned" src="https://user-images.githubusercontent.com/27720475/130187177-b97b55ef-158a-4975-b0f8-b9e8bfdc5886.png">

<img width="392" alt="aar_003_auto_x2_colored_toned" src="https://user-images.githubusercontent.com/76645322/184471738-64d3dd96-73e2-4de8-9dc6-133100e5470d.png">

3. Check your `build.gradle` in app module

```gradle
repositories {
    flatDir {
        dirs 'libs'
    }
}

dependencies {
    implementation name: 'euphony-0.8.0.1', ext: 'aar'
}

```

then click `sync` and you can use euphony library!!

### 1.3 Import the Euphony module in your project

#### Environment

> OS : Mac OS Big Sur  
> Android Gradle Plugin Version : 4.2.1
> Gradle Version : 6.7.1
> Language : Java

1. Download an zip file at [https://github.com/euphony-io/euphony](https://github.com/euphony-io/euphony) and unzip it in your computer

<img width="392" alt="module_001" src="https://user-images.githubusercontent.com/27720475/130185343-fcff2b7b-a164-4b44-852e-ecd650dfaabf.png">

2. Click `File` > `New` > `Import Module...`

<img width="392" alt="module_002" src="https://user-images.githubusercontent.com/27720475/130185349-13eacf92-da1a-47cc-b2fa-cdc3c91b6aaf.png">

<img width="392" alt="module_003" src="https://user-images.githubusercontent.com/27720475/130185355-dfef2041-53c8-4146-905f-39744e499a75.png">

3. Put what you downloaded before in source directory and click `Finish`

<img width="392" alt="module_004" src="https://user-images.githubusercontent.com/27720475/130185365-e4ef05af-60e1-4f37-b0a4-6b87cf2b17b1.png">

> ! The summary below covers some possible errors. Unfold it if you need.
>
> <details>
>    <summary>Possible errors</summary>
> <img width="392" alt="module_005" src="https://user-images.githubusercontent.com/27720475/130185374-b42d0860-6b2e-48a7-aa1f-a99eaf52435e.png">
>
> If Could not get unknown property 'language' for build 'import_Euphony' of type org.gradle.invocation.DefaultGradle. error occur, please add a sentence below in setting.gradle(Project) and click 'Sync Now'
>
> ```gradle
> gradle.ext.language = "java"; // or kotlin
> ```
>
> <img width="392" alt="module_006" src="https://user-images.githubusercontent.com/27720475/130185379-8f6fb9d8-d4a5-497e-b7ec-514aeb1088a0.png">
>
> > If you want to know more about 'Gradle', [https://www.baeldung.com/gradle-build-settings-properties](https://www.baeldung.com/gradle-build-settings-properties) will be helpful
>
> <img width="392" alt="module_007" src="https://user-images.githubusercontent.com/27720475/130185391-02c82964-299e-484c-80e1-810254dd7070.png">
>
> > If Caused by: groovy.lang.MissingPropertyException: Cannot get property 'signing.keyId' on extra properties extension as it does not exist error occur, please delete the sentence below in euphony/gradle.build and click 'Sync Now'apply from: file('publish.gradle')
>
> <img width="392" alt="module_008" src="https://user-images.githubusercontent.com/27720475/130185404-16a862f0-28ee-4c0e-917c-826fe73d25bb.png">
> </details>

4. Now, we'll gonna add a dependency. In `File` > `Project Structure`, click `+` button at `Dependencies` > `app`. Then click `Module Dependency`

<img width="392" alt="module_009" src="https://user-images.githubusercontent.com/27720475/130185413-7dc68c68-c756-455a-8829-8d61e128b939.png">

<img width="392" alt="module_010" src="https://user-images.githubusercontent.com/27720475/130185419-68d5df4c-7f71-4d08-b1dd-bea9ddd975c2.png">

After that, you can see a sentence is added in `build.gradle(Module:app)`

```gradle
dependencies{
     ...
      implementation project(path: ':euphony')
     ...
}

```

> Path could be different, if you change module name at step 3

---

## 2. Edit your AndroidManifest.xml

Add the following line to the AndroidManifest.xml:

```xml
<uses-permission android:name="android.permission.RECORD_AUDIO" />
```

### Request permission to users

Plus, you should consider that the permission model is different depending on the user's android version.
Its details is below.

<details>
    <summary>Show details</summary>

So, if you wanna use this library in the right way, you should check your permission manually in app configuration. That's because after Android 6.0 Marshmallow, Android introduced a new permissions model that lets apps request permissions from the user at runtime, rather than prior to installation.

You can read a below docs to check details.[https://developer.android.com/training/permissions/usage-notes?hl=en](https://developer.android.com/training/permissions/usage-notes?hl=en)

So when you develop the android app, you should consider when app requests permission to users, and you should typing codes that requests permssions to users.

Over the all, as this library's minimun sdk is 14, we must consider two types app 14<= sdk < 16 app and sdk >= 16 app.

Let's check out how can it possible.

The sample code below is an example of adding permission through the UI. Please refer to it.

![https://user-images.githubusercontent.com/50264056/129441912-2058e3b3-391d-48f0-a5ff-38ee27e82f0a.png](https://user-images.githubusercontent.com/50264056/129441912-2058e3b3-391d-48f0-a5ff-38ee27e82f0a.png)

![https://user-images.githubusercontent.com/50264056/129441943-3fc854db-c432-42d4-a3ab-8e07d77e0daf.png](https://user-images.githubusercontent.com/50264056/129441943-3fc854db-c432-42d4-a3ab-8e07d77e0daf.png)

Let's look up the code.

```java
package com.example.euphonytest;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {
    private Button button;
    private ActivityResultLauncher<String[]> multiplePermissionLauncher;
    final String[] permissions = {
            Manifest.permission.RECORD_AUDIO
            // add other permissions
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), isGranted -> {
            if(isGranted.toString().contains("false")) {
                Log.d("Permissions", "result: " + isGranted.toString());
                finish();
            }
        });

        button = findViewById(R.id.button2);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    multiplePermissionLauncher.launch(permissions);
                }
            }
        });
    }

}
```

This is `MainActivity` for adding permission.

```java
ActivityResultLauncher<String[]> multiplePermissionLauncher = registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), isGranted -> {
    if(isGranted.toString().contains("false")) {
        Log.d("Permissions", "result: " + isGranted.toString());
        finish();
    }
});

if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
    multiplePermissionLauncher.launch(permissions);
}
```

We can know whether permission is denied or not. And we also consider both sdk version is higher than 16 which is mashmellow and call function requestPermission.

	
</details>
