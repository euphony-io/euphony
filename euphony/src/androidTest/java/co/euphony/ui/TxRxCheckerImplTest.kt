package co.euphony.ui

import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PixelMap
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import co.euphony.common.Constants.*
import co.euphony.ui.theme.LightGreen
import co.euphony.ui.theme.LightRed
import co.euphony.ui.viewmodel.TxRxCheckerViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.junit.MockitoJUnitRunner

@ExperimentalMaterialApi
@ExperimentalComposeUiApi
@RunWith(MockitoJUnitRunner::class)
class TxRxCheckerImplTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Mock
    private lateinit var viewModel: TxRxCheckerViewModel

    private val dataLength = 3

    private val isProcessing = MutableStateFlow(false)
    private val txCode = MutableStateFlow("")

    @Before
    fun setup() {
        `when`(viewModel.isProcessing).thenReturn(isProcessing)
        `when`(viewModel.txCode).thenReturn(txCode)

        composeTestRule.setContent {
            TxRxCheckerImpl(viewModel = viewModel, dataLength)
        }
    }

    @Test
    fun initTxRxChecker() {
        composeTestRule.onNodeWithTag(TAG_INPUT).assertIsNotEnabled()
        composeTestRule.onNodeWithTag(TAG_BUTTON).assertTextContains(PLAY_BUTTON)
    }

    @Test
    fun startTxRxCheckerAndInputTextIsRandomlyGenerated() {
        composeTestRule.onNodeWithTag(TAG_BUTTON).performClick()

        for((key, value) in composeTestRule.onNodeWithTag(TAG_INPUT).fetchSemanticsNode().config) {
            if (key.name == "EditableText") {
                assertEquals(value.toString().length, dataLength)
            }
        }
    }

    @Test
    fun startTxRxCheckerAndReturnSuccess() {
        composeTestRule.onNodeWithTag(TAG_BUTTON).performClick()

        processTxRxChecker()
        composeTestRule.onNode(hasText(PROGRESS_BUTTON)).assertIsDisplayed()

        doneTxRxChecker()
        `when`(viewModel.isSuccess()).thenReturn(true)

        composeTestRule.onNode(hasText(PLAY_BUTTON)).assertIsDisplayed()
        composeTestRule.onNodeWithTag(TAG_INPUT).assertContainsColor(LightGreen)
    }

    @Test
    fun startTxRxCheckerAndReturnFail() {
        composeTestRule.onNodeWithTag(TAG_BUTTON).performClick()

        processTxRxChecker()
        composeTestRule.onNode(hasText(PROGRESS_BUTTON)).assertIsDisplayed()

        doneTxRxChecker()
        `when`(viewModel.isSuccess()).thenReturn(false)

        composeTestRule.onNode(hasText(PLAY_BUTTON)).assertIsDisplayed()
        composeTestRule.onNodeWithTag(TAG_INPUT).assertContainsColor(LightRed)
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

    private fun processTxRxChecker() {
        isProcessing.value = true
    }

    private fun doneTxRxChecker() {
        isProcessing.value = false
        txCode.value = "data"
    }
}