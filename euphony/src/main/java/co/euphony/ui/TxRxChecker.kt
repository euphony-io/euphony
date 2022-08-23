package co.euphony.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Cancel
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.PlayCircle
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import co.euphony.common.Constants.*
import co.euphony.ui.theme.LightSkyBlue
import co.euphony.ui.theme.Typography
import co.euphony.ui.viewmodel.TxRxCheckerViewModel

@Composable
fun TxRxChecker(
    viewModel: TxRxCheckerViewModel = TxRxCheckerViewModel(),
    randomLength: Int = 5
) {
    val isProcessing by viewModel.isProcessing.observeAsState(false)
    var textToSend by rememberSaveable { mutableStateOf("") }
    val charset = ('a'..'z') + ('A'..'Z') + ('0'..'9')

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        CustomTextField(
            text = textToSend,
            onTextChanged = { textToSend = it },
            enabled = (randomLength == 0),
            trailingIcon = {
                IconButton(
                    modifier = Modifier.testTag(TAG_BUTTON),
                    onClick = {
                        if (randomLength > 0) {
                            textToSend = (1..randomLength).map { charset.random() }.joinToString("")
                        }
                        viewModel.start(textToSend, 1)
                    }) {
                    Icon(Icons.Outlined.PlayCircle, "", tint = Color.Black)
                }
            },
            modifier = Modifier
                .testTag(TAG_INPUT)
                .width(300.dp)
                .height(48.dp)
                .padding(end = 20.dp)
        )

        if (!isProcessing && viewModel.txCode.value!!.isNotEmpty()) {
            if (viewModel.isSuccess()) {
                Icon(Icons.Outlined.CheckCircle, "", tint = Color.Green, modifier = Modifier.testTag(TAG_RESULT_SUCCESS))
            } else {
                Icon(Icons.Outlined.Cancel, "", tint = Color.Red, modifier = Modifier.testTag(TAG_RESULT_FAIL))
            }
        }
    }
}

@Composable
internal fun CustomTextField(
    modifier: Modifier = Modifier,
    text: String,
    onTextChanged: (String) -> Unit,
    enabled: Boolean = true,
    trailingIcon: @Composable()(() -> Unit),
) {
    TextField(
        modifier = modifier,
        shape = RoundedCornerShape(14.dp),
        trailingIcon = trailingIcon,
        colors = TextFieldDefaults.textFieldColors(
            backgroundColor = LightSkyBlue,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent,
            cursorColor = Color.Black
        ),
        placeholder = {
            Text(
                text = "Input Text",
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