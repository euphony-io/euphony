## Intro
---
![travis_ci](https://travis-ci.org/designe/euphony.svg?branch=master) [![Coverage Status](https://coveralls.io/repos/github/designe/euphony/badge.svg?branch=master)](https://coveralls.io/github/designe/euphony?branch=master) [![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0) [ ![Download](https://api.bintray.com/packages/jbear/maven/euphony/images/download.svg?version=0.7.1.6) ](https://bintray.com/jbear/maven/euphony/0.7.1.6/link)  
Euphony is a handiness library designed to communicate with other devices using mic & recorder.  
Euphony.js is based on Web Audio API. Below list is available.
- Chrome 14 <=
- Firefox 23 <=
- Edge
- Opera 15 <=
- Safari 6 <=

## Demo 
---
<center><p> <input id='euphy_text' class='demo_text_edit' type='text' value='hello, euphony'  /> <input id='euphy_btn' class='demo_btn' type='button' value='broadcast' onclick='generateSound()' /></p></center>  

When you click to broadcast button, data sound will be generated.
Then, you can see the result using Android Receiver Demo.

## Usage
---
We currently support Java & Kotlin & Javascript for Android & Web :)

### For web browser
#### 1) Prerequisite
```html
<script src='https://cdn.jsdelivr.net/gh/designe/euphony.js/dist/euphony.m.min.js'></script>

<!-- if you want to use module version of euphony, import this like below.
<script type='module'>
    import {Euphony} from "https://cdn.jsdelivr.net/gh/designe/euphony.js/dist/euphony.m.min.js";
</script>
-->
```
#### 2) Generate acoustic sound!
```js
var euphony = new Euphony(); // initialize
euphony.setCode("Hello, Euphony!"); 
// set the message to transmit
euphony.play();
```
it is only now offering transmitter version.


### For Android

#### 1) Prerequisite
```
// AndroidManifest.xml
<uses-permission android:name="android.permission.RECORD_AUDIO" />

// build.gradle in app module
dependencies {
    implementation 'euphony.lib:euphony:0.7.1.6'
}
```
#### 2) Enjoy the euphony library!
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
---

That's all!
