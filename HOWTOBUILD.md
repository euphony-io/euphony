## How to Build
### Clone this repository
```
$ git clone https://github.com/euphony-io/euphony.git
$ cd euphony/
```
### Build from the command line
### on Windows
```
> gradlew build
```
### on Linux or MacOS
```
$ ./gradlew build
```
* AAR result files:  ./euphony/build/outputs/aar
### Build with Android Studio
* Click _Build_ and _Make Module_
![](https://user-images.githubusercontent.com/68395698/129325217-dc6d027d-a8d0-483e-b195-96cab6e681f2.png)

## How to Unit Test
### Test from the command line
### on Windows
```
> gradlew test
```
### on Linux or MacOS
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
