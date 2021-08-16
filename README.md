<p align='center'><img src='https://github.com/designe/euphony/raw/master/assets/euphony_logo.png' width='400px' /></p>
<hr style='border-style:inset; border-width:0.5px'/>
<p align='center'>
    <a href = 'https://search.maven.org/artifact/co.jbear.lib/euphony/0.7.1.6/aar'><img src='https://maven-badges.herokuapp.com/maven-central/co.jbear.lib/euphony/badge.svg' alt='mavenCentral'/></a> <a href = 'https://coveralls.io/github/designe/euphony?branch=master'><img src='https://coveralls.io/repos/github/designe/euphony/badge.svg?branch=master' alt='Coverage Status'/></a> <a href = 'https://opensource.org/licenses/Apache-2.0'><img src='https://img.shields.io/badge/License-Apache%202.0-blue.svg' alt='License'/></a>
</p>

Acoustic Data Telecommunication Library. This is for Android version.  
Euphony provides a handiness library designed to communicate with other devices(android and web) using mic and recorder.  

Official Facebook Page : https://www.facebook.com/euphonyproject  
Official Library Site : https://dev.jbear.co/euphony


## Prerequisite
1) build.gradle in app module
```
dependencies {
    implementation 'euphony.lib:euphony:0.7.1.6'
}
```

2) AndroidManifest.xml
```
<uses-permission android:name="android.permission.RECORD_AUDIO" />
```

## Euphony is very easy to use

in Transmitter
```java
EuTxManager mTxManager = new EuTxManager();
mTxManager.euInitTransmit("Hello, Euphony"); // To generate acoustic data "Hello, Euphony"
mTxManager.process(-1); // generate sound infinite.
```

in Receiver
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


## How to Build
### Clone this repository
```
$ git clone https://github.com/euphony-io/euphony.git
$ cd euphony/
```
### Build from the command line
```
$ ./gradlew build
```
* AAR result files:  ./euphony/build/outputs/aar
### Build with Android Studio
* Click _Build_ and _Make Module_
![](https://user-images.githubusercontent.com/68395698/129325217-dc6d027d-a8d0-483e-b195-96cab6e681f2.png)

## How to Unit Test
### Test from the command line
```
$ ./gradlew test 
```
* HTML test result files:  ./euphony/build/reports/tests
* XML test result files: ./euphony/build/test-results
### Test with Android Studio
* Right-click on the test file that you want to test and _Run_
![](https://user-images.githubusercontent.com/68395698/129325505-39466528-8862-4784-91ab-6859d302e985.png)
### Success
* Success if Expected and Actual have the same results
![image](https://user-images.githubusercontent.com/66951780/129039524-2d6488db-a71f-4da1-97d1-2cdbcd74df01.png)
### Failure
* **Tests failed** because the results of Expected and Actual are different
```
Expected : hello
Actual : hell
```
![image](https://user-images.githubusercontent.com/66951780/129039632-b46ab05c-8eae-4262-b1be-bd3ac2e07a16.png)


## Architecture
<p align='center'> <img src='https://github.com/designe/euphony/raw/master/assets/euphony_architecture.png' alt='euphony architecture'> </p>


## Web version
Web version is also available now. but only transmitter version. [Web version.] (https://github.com/designe/euphony.js)


## Sample Projects
- [Euphony Listener](https://github.com/designe/euphony-listener) : Euphony Demo to recognize sound data.
- [Euphony Speaker](https://github.com/designe/euphony-speaker) : Euphony Demo to generate sound data.


## Contributing
Changes and improvements are more than welcome! Feel Free to fork and open a pull request. Please make your changes in a specific branch and request to pull into `master`.


## License
Euphony is licensed under the Apache 2.0 license. (https://github.com/designe/Euphony/blob/master/LICENSE)
