# Getting Started

Euphony is very easy to use

This consists of two parts.

1) Transmitter
 - Generate sound waves and transmits through a microphone.
2) Receiver
 - Listen to sound waves and receive data.
 
## in Transmitter

---

### Generate sound waves and transmits though a microphone.

1. **Create EuTxManager type object which turns data into sound waves.**
- Java

```java
EuTxManager mTxManager = new EuTxManager();
```

- Kotlin

```kotlin
val mTxManager = EuTxManager()
```

2. **Use euInitTransmit method, enter the data you want to transmit.**
- Java

```java
mTxManager.euInitTransmit("Hello, Euphony");
```

- Kotlin

```kotlin
mTxManager.euInitTransmit("Hello, Euphony")
```

3. **With process method, set how many times to repeat the sound(data). (-1 is infinite)**
- Java

```java
mTxManager.process(-1);
```

- Kotlin

```kotlin
mTxManager.process(-1)
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
      //when data is received
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
