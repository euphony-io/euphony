Euphony
========
<p>
<img src = 'http://designe.asia/wp-content/uploads/2014/04/Euphony_header.png' width='50%'/></p>
Acoustic Data Telecommunication Library. This is for Android version.<br/>
Euphony provides a handiness library designed to communicate with other devices(android and web) using mic and recorder.

## Euphony is very easy to use

in Transmitter
```java
mContext = MainActivity.this;
EuTxManager mTxManager = new EuTxManager(mContext);
mTxManager.euInitTransmit("Hello, Euphony", 5); // To generate acoustic data "Hello, Euphony" for 5 times.
mTxManager.setSoftVolume(95.0); //To set software volume
mTxManager.process();
```

in Receiver
```java
EuRxManager mRxManager = new EuRxManager();
mRxManager.Listen();  //Listening Start
mRxManager.Finish();  //Listening End
mRxManager.setAcousticSensor(new AcousticSensor() {
@override
    public void notify(String letters) {
        //when data is received
    }
});
```

## Architecture
<p align='center'>
<img src = 'http://designe.asia/wp-content/uploads/2014/04/Euphony_architecture.png' width='50%' /> </p>
## Web version
Web version is also available now. but only transmitter version. [Web version.] (http://euphony.develog.net/js/euphony.js)

## Contributing
Changes are improvements are more than welcome! Feel Free to fork and open a pull request. Please make your changes in a specific branch and request to pull into `master`.

## License
Euphony is licensed under the [MIT license.] (https://github.com/designe/Euphony/blob/master/LICENSE.txt)
