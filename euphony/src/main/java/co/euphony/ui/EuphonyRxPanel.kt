package co.euphony.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import co.euphony.common.Constants
import co.euphony.rx.EuRxManager
import co.euphony.ui.theme.*
import co.euphony.ui.viewmodel.EuphonyRxPanelViewModel

@Composable
fun EuphonyRxPanel() {
    val viewModel = EuphonyRxPanelViewModel(EuRxManager.getInstance())
    EuphonyRxPanelImpl(viewModel = viewModel)
}

@Composable
internal fun EuphonyRxPanelImpl(
    viewModel: EuphonyRxPanelViewModel
) {
    val isListening by viewModel.isListening.collectAsState()
    val rxCode by viewModel.rxCode.collectAsState()
    val time by viewModel.limitTime.collectAsState()

    val textBackgroundColor =
        if (!isListening && time <= 0) {
            if (viewModel.isSuccess()) {
                LightGreen
            } else {
                LightRed
            }
        } else {
            LightSkyBlue
        }

    val buttonText = when (isListening) {
        true -> Constants.LISTEN_PROGRESS_BUTTON
        false -> Constants.LISTEN_BUTTON
    }
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Button(
            modifier = Modifier
                .testTag(Constants.LISTEN_TAG_BUTTON)
                .width(60.dp)
                .height(48.dp),
            shape = RoundedCornerShape(
                topStart = 14.dp,
                bottomStart = 14.dp
            ),
            colors = ButtonDefaults.buttonColors(backgroundColor = LightBlue),
            onClick = {
                viewModel.start()
            }) {
            Text(text = buttonText, color = Color.White)
        }
        ResultField(
            text = rxCode,
            enabled = false,
            backgroundColor = textBackgroundColor,
            modifier = Modifier
                .testTag(Constants.LISTEN_TAG_OUTPUT)
                .width(230.dp)
                .height(48.dp)
        )
    }
}

@Composable
internal fun ResultField(
    modifier: Modifier = Modifier,
    text: String,
    backgroundColor: Color = LightSkyBlue,
    enabled: Boolean = true,
) {
    TextField(
        modifier = modifier.testTag(backgroundColor.toString()),

        shape = RoundedCornerShape(
            topEnd = 14.dp,
            bottomEnd = 14.dp
        ),
        colors = TextFieldDefaults.textFieldColors(
            backgroundColor = backgroundColor,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent,
            cursorColor = Color.Black,
            textColor = Color.White,
        ),
        placeholder = {
            Text(
                color = Color.White,
                text = "Text will be displayed here",
                style = Typography.body1
            )
        },
        enabled = enabled,
        value = text,
        onValueChange = { (it) },
        singleLine = true
    )
}

@Preview(showBackground = true)
@Composable
fun EuphonyRxPanelPreview() {
    EuphonyRxPanel()
}
