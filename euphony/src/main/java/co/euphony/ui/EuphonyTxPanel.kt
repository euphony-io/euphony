package co.euphony.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Send
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Black
import androidx.compose.ui.graphics.Color.Companion.LightGray
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import co.euphony.tx.EuTxManager
import co.euphony.ui.theme.LightBlue
import co.euphony.ui.theme.LightSkyBlue
import co.euphony.ui.viewmodel.TxPanelViewModel
import co.euphony.ui.viewmodel.TxPanelViewModelFactory

/*
 * 커스텀 되는 설정 : 색상, 아이콘, radius
 */
@Preview
@Composable
fun EuphonyTxPanelPreview() {
    Column(
        modifier = Modifier.padding(
            vertical = 40.dp,
            horizontal = 16.dp
        )
    ) {
        EuphonyTxPanel("Hint Text")
    }
}

@Composable
fun EuphonyTxPanel(
    hint: String = "",
    textColor: Color = Black,
    hintTextColor: Color = LightGray,
    textFieldBackgroundColor: Color = LightSkyBlue,
    buttonBackgroundColor: Color = LightBlue,
    panelHeight: Dp = 54.dp,
    iconTintColor: Color = White
) {
    // TODO : EuTxManager() should be changed to use singleton pattern
    val viewModel: TxPanelViewModel = viewModel(factory = TxPanelViewModelFactory(EuTxManager()))

    val btnStatus = viewModel.isProcessing.collectAsState()
    var textData by remember { mutableStateOf("") }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(panelHeight),
        verticalAlignment = Alignment.CenterVertically
    ) {
        TextField(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight(),
            value = textData,
            onValueChange = {
                textData = it
            },
            shape = RoundedCornerShape(topStart = 8.dp, bottomStart = 8.dp),
            colors = TextFieldDefaults.textFieldColors(
                textColor = textColor,
                backgroundColor = textFieldBackgroundColor,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
            ),
            placeholder = {
                Text(
                    text = hint,
                    color = hintTextColor
                )
            }
        )
        Button(
            onClick = {
                viewModel.onBtnClick(textData)
            },
            modifier = Modifier
                .fillMaxHeight(),
            shape = RoundedCornerShape(bottomEnd = 8.dp, topEnd = 8.dp),
            colors = ButtonDefaults.buttonColors(backgroundColor = buttonBackgroundColor),
            elevation = null
        ) {
            Icon(
                imageVector = if (btnStatus.value) {
                    Icons.Default.Close
                } else {
                    Icons.Default.Send
                },
                contentDescription = "EuphonyTxPanel button",
                tint = iconTintColor,
            )
        }
    }
}
