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

1. Import `euphony` library to your project
2. Add `android.permission.RECORD_AUDIO` in `AndroidManifest.xml`

## 1. Import `euphony` library to your project

There are 3 ways adding euphony to your project.

1. Using Maven repository
2. Import the aar/jar file directly
3. Import the Euphony module in your project

### 1.1 Using Maven repository

1. Show **_Project window_** with **_Android project view_**.   
   You can select a project view mode in the dropdown list, then open your `build.gradle` file under `Gradle Scripts`.

<img width="392" alt="maven_001_auto_x2_colored_toned" src="https://user-images.githubusercontent.com/27720475/130187144-6bcd31e2-589e-48e0-91ff-d05d28bea4e4.png">

2. Add the following line to the `dependencies` section:

```gradle
dependencies {
	// other dependencies
	// ...
	implementation 'co.euphony.lib:euphony:0.8.2'
}
```

### 1.2 Import the aar/jar file directly

1. Download `euphony.aar` : [MavenCentral euphony artifact](https://search.maven.org/artifact/co.euphony.lib/euphony/0.8.2/aar) follow the link and download aar file.
2. Put `euphony.aar` file in `libs` folder.

<img width="392" alt="aar_002_auto_x2_colored_toned" src="https://user-images.githubusercontent.com/27720475/130187177-b97b55ef-158a-4975-b0f8-b9e8bfdc5886.png">

3. Check your `build.gradle` in app module

```gradle
repositories {
    flatDir {
        dirs 'libs'
    }
}

dependencies {
    implementation name: 'euphony-0.8.2', ext: 'aar'
}

```

then click `sync` and you can use euphony library.

### 1.3 Import the `euphony` module in your project

1. Download the zip file at [https://github.com/euphony-io/euphony](https://github.com/euphony-io/euphony) and unzip it in your computer

<img width="392" alt="module_001" src="https://user-images.githubusercontent.com/27720475/130185343-fcff2b7b-a164-4b44-852e-ecd650dfaabf.png">

2. Click `File` > `New` > `Import Module...`

<img width="392" alt="module_002" src="https://user-images.githubusercontent.com/27720475/130185349-13eacf92-da1a-47cc-b2fa-cdc3c91b6aaf.png">

<img width="392" alt="module_003" src="https://user-images.githubusercontent.com/27720475/130185355-dfef2041-53c8-4146-905f-39744e499a75.png">

3. Put what you downloaded before in source directory and click `Finish`

<img width="392" alt="module_004" src="https://user-images.githubusercontent.com/27720475/130185365-e4ef05af-60e1-4f37-b0a4-6b87cf2b17b1.png">

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

## 2. Add `android.permission.RECORD_AUDIO` in `AndroidManifest.xml`

Add the following line to the `AndroidManifest.xml`:

```xml
<uses-permission android:name="android.permission.RECORD_AUDIO" />
```

### Request permission to users

Plus, you should consider that the permission model is different depending on the user's android version.
Its details is below.

<details>
    <summary>Show details</summary>
 if you wanna use this library in the right way, you should check your permission manually in app configuration. That's because after Android 6.0 Marshmallow, Android introduced a new permissions model that lets apps request permissions from the user at runtime, rather than prior to installation.

You can read a below docs to check details.[https://developer.android.com/training/permissions/usage-notes?hl=en](https://developer.android.com/training/permissions/usage-notes?hl=en)

So when you develop the android app, you should consider when app requests permission to users, and you should typing codes that requests permssions to users.

Over the all, as this library's minimun sdk is 21.

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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        String[] permissions = {
                Manifest.permission.RECORD_AUDIO
                // add other permissions
        };

        // after requestPermissions
        ActivityResultLauncher<String[]> multiplePermissionLauncher = registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), grantResults -> {
            for(Boolean result : grantResults.values()) {
                if (!result) {
                    finish();
                }
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
String[] permissions = {
    Manifest.permission.RECORD_AUDIO
    // add other permissions
};

// after requestPermissions
ActivityResultLauncher<String[]> multiplePermissionLauncher = registerForActivityResult(new 
ActivityResultContracts.RequestMultiplePermissions(), grantResults -> {
    for(Boolean result : grantResults.values()) {
        if (!result) {
            finish();
        }
    }
});
if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
    multiplePermissionLauncher.launch(permissions);
}
```

We can know whether permissions are denied or not. And we also consider both sdk version is higher than 16 which is marshmallow and call function RequestMultiplePermissions.

	
</details>
