package co.euphony.ui

import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import co.euphony.common.Constants
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner


@ExperimentalMaterialApi
@ExperimentalComposeUiApi
@RunWith(MockitoJUnitRunner::class)
class EuphonyTxPanelKtTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Before
    fun setup() {
        composeTestRule.setContent {
            EuphonyTxPanel(
                hint = "hint text"
            )
        }
    }

    @Test
    fun haveEmptyValueAtFirst() {
        composeTestRule.onNodeWithText("hint text").assertExists()
        composeTestRule.onNodeWithContentDescription(Constants.TAG_TX_ICON_START).assertExists()
    }

    @Test
    fun startSendTextAndFinishSending() {
        composeTestRule.onNodeWithText("").performTextInput("Text")
        composeTestRule.onNodeWithTag(Constants.TAG_TX_BTN).performClick()
        composeTestRule.onNodeWithContentDescription(Constants.TAG_TX_ICON_STOP).assertExists()

        composeTestRule.onNodeWithTag(Constants.TAG_TX_BTN).performClick()
        composeTestRule.onNodeWithContentDescription(Constants.TAG_TX_ICON_START).assertExists()
    }
}