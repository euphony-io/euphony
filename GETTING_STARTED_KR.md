# 시작하기
[English](GETTING_STARTED.md) | 한글

euphony는 사람에게 들리지 않는 가청주파수 임계대역을 활용해 데이터를 전송하는 라이브러리입니다.  
API는 Java로 작성되어 있기 때문에 사용자는 Java 또는 Kotlin과 같은 JVM에서 동작하는 모든 언어에서 사용 가능합니다.  

라이브러리는 크게 두개의 파트로 구성되어 있습니다.  

1) 송신 파트
데이터를 담은 음파를 생성하며, 이는 스피커를 통해 전송합니다.

2) 수신 파트
데이터를 담은 음파를 분석하고 인식합니다. 마이크를 통해 음파를 수신합니다.

## 송신 파트

### 데이터를 담은 음파를 생성하며, 이는 스피커를 통해 전송합니다.

1. **데이터를 음파로 바꾸는 EuTxManager 객체를 생성**
- Java

```java
EuTxManager mTxManager = new EuTxManager();
```

- Kotlin

```kotlin
val mTxManager = EuTxManager()
```

2. **setCode 메소드로 전송할 문장을 입력**
- Java

```java
mTxManager.setCode("Hello, Euphony");
```

- Kotlin

```kotlin
mTxManager.setCode("Hello, Euphony")
```

3. **play 메소드로 문장(음파)을 몇 번 반복할지 설정 (-1은 무한반복)**
- Java

```java
mTxManager.play(-1);
```

- Kotlin

```kotlin
mTxManager.play(-1)
```

## 수신 파트

### 마이크를 통해 음파를 수신하고, 데이터를 담은 음파를 분석하고 인식합니다. 

> **RECORD_AUDIO 권한 필요**

- AndroidManifest.xml에 작성한다.

```xml
<uses-permission android:name="android.permission.RECORD_AUDIO" />
```

1. **음파를 데이터로 인식하기 위한 EuRxManager 객체 생성**
- Java

```java
EuRxManager mRxManager = new EuRxManager();
```

- Kotlin

```kotlin
val mRxManager = EuRxManager()
```

2. **음파를 받아오고 처리하는 Listener를 설정**
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

3. **음파 수신 시작**
- Java

```java
mRxManager.listen(); // Listening Start
```

- Kotlin

```kotlin
mRxManager.listen() // Listening Start
```

4. **음파 수신 종료**
- Java

```java
mRxManager.finish(); // Listening finish
```

- Kotlin

```kotlin
mRxManager.finish() // Listening finish
```
