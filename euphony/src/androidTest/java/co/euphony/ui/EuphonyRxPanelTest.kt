package co.euphony.ui

import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PixelMap
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import co.euphony.common.Constants
import co.euphony.ui.theme.LightGreen
import co.euphony.ui.theme.LightRed
import co.euphony.ui.theme.LightSkyBlue
import co.euphony.ui.viewmodel.EuphonyRxPanelViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner

@ExperimentalMaterialApi
@ExperimentalComposeUiApi
@RunWith(MockitoJUnitRunner::class)
class EuphonyRxPanelTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Mock
    private lateinit var viewModel: EuphonyRxPanelViewModel

    private val isListening = MutableStateFlow(false)
    private val rxCode = MutableStateFlow("")
    private val limitTime = MutableStateFlow(10)

    @Before
    fun setup() {
        Mockito.`when`(viewModel.isListening).thenReturn(isListening)
        Mockito.`when`(viewModel.rxCode).thenReturn(rxCode)
        Mockito.`when`(viewModel.limitTime).thenReturn(limitTime)

        composeTestRule.setContent {    
            EuphonyRxPanelImpl(viewModel = viewModel)
        }
    }

    @Test
    fun initEuphonyRxPanel() {
        composeTestRule.onNodeWithTag(Constants.LISTEN_TAG_BUTTON)
            .assertTextContains(Constants.LISTEN_BUTTON)
        composeTestRule.onNodeWithTag(Constants.LISTEN_TAG_OUTPUT).assertIsNotEnabled()
        composeTestRule.onNodeWithTag(Constants.LISTEN_TAG_OUTPUT).assertContainsColor(LightSkyBlue)

    }

    @Test
    fun startEuphonyRxPanelAndReturnSuccess() {
        composeTestRule.onNodeWithTag(Constants.LISTEN_TAG_BUTTON).performClick()

        processEuphonyRxPanel()
        composeTestRule.onNode(hasText(Constants.LISTEN_PROGRESS_BUTTON)).assertIsDisplayed()

        rxCode.value = "data"

        doneEuphonyRxPanel()
        Mockito.`when`(viewModel.isSuccess()).thenReturn(true)

        composeTestRule.onNodeWithTag(Constants.LISTEN_TAG_BUTTON)
            .assertTextContains(Constants.LISTEN_BUTTON)
        composeTestRule.onNodeWithTag(Constants.LISTEN_TAG_OUTPUT).assertContainsColor(LightGreen)
        composeTestRule.onNodeWithTag(Constants.LISTEN_TAG_OUTPUT).assertTextContains("data")
    }

    @Test
    fun startEuphonyRxPanelAndReturnFail() {
        composeTestRule.onNodeWithTag(Constants.LISTEN_TAG_BUTTON).performClick()

        processEuphonyRxPanel()
        composeTestRule.onNode(hasText(Constants.LISTEN_PROGRESS_BUTTON)).assertIsDisplayed()

        doneEuphonyRxPanel()
        Mockito.`when`(viewModel.isSuccess()).thenReturn(false)

        composeTestRule.onNodeWithTag(Constants.LISTEN_TAG_BUTTON)
            .assertTextContains(Constants.LISTEN_BUTTON)
        composeTestRule.onNodeWithTag(Constants.LISTEN_TAG_OUTPUT).assertContainsColor(LightRed)
        composeTestRule.onNodeWithTag(Constants.LISTEN_TAG_OUTPUT).assertTextContains("")
    }

    private fun processEuphonyRxPanel() {
        limitTime.value = 10
        isListening.value = true
    }

    private fun doneEuphonyRxPanel() {
        limitTime.value = 0
        isListening.value = false
    }

    private fun SemanticsNodeInteraction.assertContainsColor(tint: Color): SemanticsNodeInteraction {
        val imageBitmap = captureToImage()
        val buffer = IntArray(imageBitmap.width * imageBitmap.height)
        imageBitmap.readPixels(buffer, 0, 0, imageBitmap.width - 1, imageBitmap.height - 1)
        val pixelColors = PixelMap(buffer, 0, 0, imageBitmap.width - 1, imageBitmap.height - 1)

        (0 until imageBitmap.width).forEach { x ->
            (0 until imageBitmap.height).forEach { y ->
                if (pixelColors[x, y] == tint) return this
            }
        }
        throw AssertionError("Assert failed: The component does not contain the color")
    }
}
