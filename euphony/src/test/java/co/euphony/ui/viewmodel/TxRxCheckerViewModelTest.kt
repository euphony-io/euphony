package euphony.lib.ui.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import co.euphony.rx.AcousticSensor
import co.euphony.rx.EuRxManager
import co.euphony.tx.EuTxManager
import co.euphony.ui.viewmodel.TxRxCheckerViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.Mockito.verify
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
@OptIn(ExperimentalCoroutinesApi::class)
class TxRxCheckerViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    val dispatcher = UnconfinedTestDispatcher()

    private lateinit var viewModel: TxRxCheckerViewModel

    @Mock
    private lateinit var txManager: EuTxManager

    @Mock
    private lateinit var rxManager: EuRxManager


    @Before
    fun setup() {
        viewModel = TxRxCheckerViewModel(txManager, rxManager)
        Dispatchers.setMain(dispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `if start, livedata should be initialized and Euphony Tx & Rx should work`() {
        val textToSend = "hello"
        val count = 5

        viewModel.start(textToSend, count)

        verify(txManager).code = textToSend
        verify(txManager).play(count)
        verify(rxManager).listen()

        assertEquals(viewModel.isProcessing.value, true)
        assertEquals(viewModel.txCode.value, textToSend)
        assertEquals(viewModel.rxCode.value, "")
    }

    @Test
    fun `if stop, livedata should be updated and Euphony Tx & Rx should end`() {
        val textToSend = "hello"
        val count = 5

        viewModel.start(textToSend, count)
        viewModel.stop()

        verify(txManager).stop()
        verify(rxManager).finish()

        assertEquals(viewModel.isProcessing.value, false)
    }

    @Test
    fun `if Tx & Rx result are not same, isSuccess() should be false`() {
        val textToSend = "hello"
        val count = 5
        val rxResult = "hell"

        viewModel.start(textToSend, count)
        `when`(rxManager.acousticSensor).thenAnswer {
            AcousticSensor {
                viewModel.stop()
                viewModel.setRxCode(rxResult)
                viewModel.setIsProcessing(false)
            }
        }
        rxManager.acousticSensor.notify(rxResult)

        assertEquals(viewModel.rxCode.value, rxResult)
        assertEquals(viewModel.isSuccess(), false)
    }

    @Test
    fun `if Tx & Rx result is same, isSuccess() should be true`() {
        val textToSend = "hello"
        val count = 5
        val rxResult = "hello"

        viewModel.start(textToSend, count)
        `when`(rxManager.acousticSensor).thenAnswer {
            AcousticSensor {
                viewModel.stop()
                viewModel.setRxCode(rxResult)
                viewModel.setIsProcessing(false)
            }
        }
        rxManager.acousticSensor.notify(rxResult)

        assertEquals(viewModel.rxCode.value, rxResult)
        assertEquals(viewModel.isSuccess(), true)
    }
}