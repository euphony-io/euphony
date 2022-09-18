package euphony.lib.ui.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner

import co.euphony.rx.EuRxManager
import co.euphony.ui.viewmodel.EuphonyRxPanelViewModel
import kotlin.OptIn
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.*
import org.mockito.Mockito.never
import org.mockito.Mockito.verify

@RunWith(MockitoJUnitRunner::class)
@OptIn(ExperimentalCoroutinesApi::class)
class EuphonyRxPanelViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    val dispatcher = UnconfinedTestDispatcher()

    private lateinit var viewModel: EuphonyRxPanelViewModel

    @Mock
    private lateinit var rxManager: EuRxManager

    @Before
    fun setup() {
        viewModel = EuphonyRxPanelViewModel(rxManager)
        Dispatchers.setMain(dispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `if start, livedata should be initialized and Euphony Rx should work`() {
        viewModel.start()

        verify(rxManager).listen()

        Assert.assertEquals(true, viewModel.isListenStarted.value)
        Assert.assertEquals(true, viewModel.isListening.value)
        Assert.assertEquals("", viewModel.rxCode.value)
    }

    @Test
    fun `if stop when listening, convert isListening to false & finish Rx`() {
        viewModel.setIsListening(true)
        viewModel.stop()

        verify(rxManager).finish()

        Assert.assertEquals(false, viewModel.isListening.value)
    }

    @Test
    fun `if stop when not listening, isListening value is maintained`() {
        viewModel.setIsListening(false)
        viewModel.stop()

        verify(rxManager, never()).finish()

        Assert.assertEquals(false, viewModel.isListening.value)
    }

    @Test
    fun `if Rx result is not empty, isSuccess() should be true`() {
        viewModel.setRxCode("hello")

        Assert.assertEquals(true, viewModel.isSuccess())
    }

    @Test
    fun `if Rx result is empty, isSuccess() should be false`() {
        viewModel.setRxCode("")

        Assert.assertEquals(false, viewModel.isSuccess())
    }
}
