# Euphony Ui

## Introduction

Euphony provides basic UI components for easy use. These work based on Jetpack Compose.

## How to use

1. [Add Jetpack Compose](https://developer.android.com/jetpack/compose/interop/adding) to your
   project.
2. Declare Euphony UI where necessary
    ```kotlin
    @Composable
    fun MainScreen() {
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 40.dp)
        ) {
            EuphonyTxPanel(
                hint = "hint text",
                textColor = Red,
                panelHeight = 60.dp
            )
        }
    }
    ```

## Components

### EuphonyTxPanel

Write text to transmit and click button to create sound waves.

#### Preview

<img src='https://github.com/zion830/euphony/blob/master/assets/eutxpanel_screenshot.png?raw=true' width='350px' />

#### Options

|Name|Type|Description| 
|---|---|---| 
|hint|String|Hint text in the the text field.|
|textColor|Color|Text color of the text field. Default color is Black.| 
|hintTextColor|Color|Text color of the hint text. Default color is LightGray.| 
|textFieldBackgroundColor|Color|Background color of the text field. Default color is LightSkyBlue.| 
|buttonBackgroundColor|Color|Background color of the right side button. Default color is LightBlue.| 
|panelHeight|Dp|Height of this component.| 
|cornerRadius|Dp|Corner radius of this component.| 
|iconTintColor|Color|Icon color of the right side button. Default color is White.|
