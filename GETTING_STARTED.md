# Getting Started
English | [한글](GETTING_STARTED_KR.md)
  
Euphony is a library that transmits data using critical bandwidths of the tonal frequency inaudible to humans.  
Since the API is written in Java, users can use it in any language running on JVM like Java or Kotlin.

This consists of two parts.

1) Transmitter
 - Generate sound waves and transmits through a speaker.
2) Receiver
 - Listen to sound waves and receive data through a MIC.
 
## in Transmitter

---

### Generate sound waves and transmits though a microphone.

1. **Create EuTxManager object which turns data into sound waves.**
- Java

```java
EuTxManager mTxManager = new EuTxManager();
```

- Kotlin

```kotlin
val mTxManager = EuTxManager()
```

2. **Use setCode method, enter the data you want to transmit.**
- Java

```java
mTxManager.setCode("Hello, Euphony");
```

- Kotlin

```kotlin
mTxManager.setCode("Hello, Euphony")
```

3. **With play method, set how many times to repeat the sound(data). (-1 is infinite)**
- Java

```java
mTxManager.play(-1);
```

- Kotlin

```kotlin
mTxManager.play(-1)
```

## in Receiver

---

### Part of receiving sound waves.

> **Requires permission of RECORD_AUDIO**

- Write in AndroidManifest.xml

```xml
<uses-permission android:name="android.permission.RECORD_AUDIO" />
```

1. **Create EuRxManager object that receives sound waves as data**
- Java

```java
EuRxManager mRxManager = new EuRxManager();
```

- Kotlin

```kotlin
val mRxManager = EuRxManager()
```

2. **Listener settings that receive and process sound waves**
- Java

```java
mRxManager.setAcousticSensor(new AcousticSensor() {
	@Override
	public void notify(String letters) {
		// when data is received
	}
});
```

- Kotlin

```kotlin
mRxManager.acousticSensor = AcousticSensor { letters ->
	//when data is received
}
```

3. **Start receiving sound waves**
- Java

```java
mRxManager.listen(); // Listening Start
```

- Kotlin

```kotlin
mRxManager.listen() // Listening Start
```

4. **End sound wave reception**
- Java

```java
mRxManager.finish(); // Listening finish
```

- Kotlin

```kotlin
mRxManager.finish() // Listening finish
```
