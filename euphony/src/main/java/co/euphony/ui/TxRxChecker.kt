package co.euphony.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import co.euphony.common.Constants.*
import co.euphony.rx.EuRxManager
import co.euphony.tx.EuTxManager
import co.euphony.ui.theme.*
import co.euphony.ui.viewmodel.TxRxCheckerViewModel

@Composable
fun TxRxChecker(
    dataLength: Int = 5
) {
    val viewModel = TxRxCheckerViewModel(EuTxManager(), EuRxManager())
    TxRxCheckerImpl(viewModel = viewModel, dataLength = dataLength)
}

@Composable
internal fun TxRxCheckerImpl(
    viewModel: TxRxCheckerViewModel,
    dataLength: Int = 5
) {
    val isProcessing by viewModel.isProcessing.collectAsState()
    val txCode by viewModel.txCode.collectAsState()
    var textToSend by rememberSaveable { mutableStateOf("") }
    val charset = ('a'..'z') + ('A'..'Z') + ('0'..'9')

    val textBackgroundColor =
        if (!isProcessing && txCode.isNotEmpty()) {
            if (viewModel.isSuccess()) {
                LightGreen
            } else {
                LightRed
            }
        } else {
            LightSkyBlue
        }

    val buttonText = when(isProcessing) {
        true -> PROGRESS_BUTTON
        false -> PLAY_BUTTON
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        CustomTextField(
            text = textToSend,
            onTextChanged = { textToSend = it },
            enabled = false,
            backgroundColor = textBackgroundColor,
            modifier = Modifier
                .testTag(TAG_INPUT)
                .width(230.dp)
                .height(48.dp)
        )
        Button(
            modifier = Modifier
                .testTag(TAG_BUTTON)
                .width(60.dp)
                .height(48.dp)
            ,
            shape = RoundedCornerShape(
                topEnd = 14.dp,
                bottomEnd = 14.dp
            ),
            colors = ButtonDefaults.buttonColors(backgroundColor = LightBlue),
            onClick = {
                textToSend = (1..dataLength).map { charset.random() }.joinToString("")
                viewModel.start(textToSend, 1)
            }) {
            Text(text = buttonText, color = Color.White)
        }
    }
}

@Composable
internal fun CustomTextField(
    modifier: Modifier = Modifier,
    text: String,
    backgroundColor: Color = LightSkyBlue,
    onTextChanged: (String) -> Unit,
    enabled: Boolean = true,
) {
    TextField(
        modifier = modifier.testTag(backgroundColor.toString()),
        shape = RoundedCornerShape(
            topStart = 14.dp,
            bottomStart = 14.dp
        ),
        colors = TextFieldDefaults.textFieldColors(
            backgroundColor = backgroundColor,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent,
            cursorColor = Color.Black,
            textColor = Color.White,
            disabledTextColor = Color.White,
        ),
        placeholder = {
            Text(
                color = Color.White,
                text = "Text will be randomly generated",
                style = Typography.body1
            )
        },
        enabled = enabled,
        value = text,
        onValueChange = { onTextChanged(it) },
        singleLine = true
    )
}

@Preview(showBackground = true)
@Composable
fun TxRxCheckerPreview() {
    TxRxChecker()
}