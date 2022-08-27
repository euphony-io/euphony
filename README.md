<p align='center'><img src='https://github.com/designe/euphony/raw/master/assets/euphony_logo.png' width='400px' /></p>
<hr style='border-style:inset; border-width:0.5px'/>
<p align='center'>
    <a href = 'https://search.maven.org/artifact/co.euphony.lib/euphony'><img src='https://maven-badges.herokuapp.com/maven-central/co.euphony.lib/euphony/badge.svg' alt='mavenCentral'/></a> <a href = 'https://coveralls.io/github/designe/euphony?branch=master'><img src='https://coveralls.io/repos/github/designe/euphony/badge.svg?branch=master' alt='Coverage Status'/></a> <a href = 'https://opensource.org/licenses/Apache-2.0'><img src='https://img.shields.io/badge/License-Apache%202.0-blue.svg' alt='License'/></a>
</p>

Acoustic Data Telecommunication Library. This is for Android version.  
Euphony provides a handiness library designed to communicate with other devices(android and web) using mic and recorder.  

- [Official Facebook Page](https://www.facebook.com/euphonyproject)
- [Official Library Site](https://dev.jbear.co/euphony)


## Prerequisite
1) build.gradle in app module
```gradle
dependencies {
    implementation 'co.euphony.lib:euphony:0.8.1'
}
```

2) AndroidManifest.xml
```xml
<uses-permission android:name="android.permission.RECORD_AUDIO" />
```

For more detailed prerequisite, please refer to the link below.  
- [Full Prerequisite Guide](PREREQUISITE.md)

## Euphony is very easy to use

**Transmitter**
```java
EuTxManager mTxManager = new EuTxManager();
mTxManager.setCode("Hello, Euphony"); // To generate acoustic data "Hello, Euphony"
mTxManager.play(-1); // generate sound infinite.
```

**Receiver**
```java
EuRxManager mRxManager = new EuRxManager();
mRxManager.setAcousticSensor(new AcousticSensor() {
@Override
    public void notify(String letters) {
        //when data is received
    }
});

mRxManager.listen();  //Listening Start
// if you want to finish listening, call the finish();
// mRxManager.finish();

```

Below links are detail guides.
- [Getting Started Guide](GETTING_STARTED.md)
- [How To Build & Unit Test Guide](HOWTOBUILD.md)

## Architecture
<p align='center'> <img src='https://github.com/euphony-io/euphony/raw/master/assets/euphony_architecture.png' alt='euphony architecture'> </p>


## Web version
Web version is also available now. but only transmitter version. 
- [Web version](https://github.com/euphony-io/euphony.js) : This is written in Javascript.


## Sample Projects
- [Euphony Listener](https://github.com/euphony-io/euphony-listener) : Euphony Demo to recognize sound data.
- [Euphony Speaker](https://github.com/euphony-io/euphony-speaker) : Euphony Demo to generate sound data.
- [Sound Helper](https://github.com/euphony-io/sound-helper) : Euphony Demo for patients and assistants in case of an emergency.
- [Soundless Check-in](https://github.com/euphony-io/soundless-check-in) : Euphony Demo to check in through sound wave.
- [Here Is My Card](https://github.com/euphony-io/here-is-my-card) : Euphony Demo for sharing business cards.
- [Looking For Job](https://github.com/euphony-io/looking-for-job) : Euphony Demo to send and receive resumes.
- [Listen My Order](https://github.com/euphony-io/listen-my-order) : Euphony Demo for communicate restaurant menu information.
- [Wave-In](https://github.com/euphony-io/wave-in-speaker) : COVID-19 Check-in solution using a safe number based on MVVM model.
- [Wave-In (for store)](https://github.com/euphony-io/wave-in-listener) : COVID-19 Check-in solution for store using a safe number based on MVVM model.

## Contributing
Changes and improvements are more than welcome! Feel Free to fork and open a pull request. Please make your changes in a specific branch and request to pull into `master`.


## License
* [Apache 2.0](https://github.com/euphony-io/euphony/blob/master/LICENSE) : Euphony is licensed under the Apache 2.0 license.  
