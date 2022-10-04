package co.euphony.ui

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import co.euphony.common.Constants.*
import co.euphony.tx.EuTxManager
import co.euphony.ui.theme.LightBlue
import co.euphony.ui.theme.LightSkyBlue
import co.euphony.ui.viewmodel.TxPanelViewModel
import co.euphony.ui.viewmodel.TxPanelViewModelFactory

@Composable
fun EuphonyTxPanel(
    hint: String = "",
    textColor: Color = Black,
    hintTextColor: Color = LightGray,
    textFieldBackgroundColor: Color = LightSkyBlue,
    buttonBackgroundColor: Color = LightBlue,
    panelHeight: Dp = 54.dp,
    cornerRadius: Dp = 8.dp,
    iconTintColor: Color = White
) {
    val viewModel: TxPanelViewModel =
        viewModel(factory = TxPanelViewModelFactory(EuTxManager.getInstance()))

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
                .height(panelHeight),
            value = textData,
            onValueChange = {
                textData = it
            },
            shape = RoundedCornerShape(topStart = cornerRadius, bottomStart = cornerRadius),
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
                .height(panelHeight)
                .testTag(TAG_TX_BTN),
            shape = RoundedCornerShape(bottomEnd = cornerRadius, topEnd = cornerRadius),
            colors = ButtonDefaults.buttonColors(backgroundColor = buttonBackgroundColor),
            elevation = null,
        ) {
            Icon(
                imageVector = if (btnStatus.value) {
                    Icons.Default.Close
                } else {
                    Icons.Default.Send
                },
                contentDescription = if (btnStatus.value) {
                    TAG_TX_ICON_STOP
                } else {
                    TAG_TX_ICON_START
                },
                tint = iconTintColor,
            )
        }
    }
}
