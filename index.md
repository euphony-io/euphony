Euphony is a handiness library designed to communicate with other devices using mic & recorder.

<input id='euphy_text' type='text' value='hello, euphony'  />
<input id='euphy_btn' type='button' value='broadcast' onclick='generateSound()' />

## Usage
```js
var euphony = new Euphony();
euphony.initChannel(); // initialize
euphony.setCode("Hello, Euphony!"); // set the message to transmit
euphony.play();
```

That's all!
