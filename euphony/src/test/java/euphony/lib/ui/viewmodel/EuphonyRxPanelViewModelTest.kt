package euphony.lib.ui.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner

import co.euphony.rx.EuRxManager
import co.euphony.tx.EuTxManager
import co.euphony.ui.viewmodel.EuphonyRxPanelViewModel
import kotlin.OptIn
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.*
import org.mockito.Mockito

@RunWith(MockitoJUnitRunner::class)
@OptIn(ExperimentalCoroutinesApi::class)
class EuphonyRxPanelViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    val dispatcher = UnconfinedTestDispatcher()

    private lateinit var viewModel: EuphonyRxPanelViewModel

    @Mock
    private lateinit var txManager: EuTxManager

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

        Mockito.verify(rxManager).listen()

        Assert.assertEquals(true, viewModel.isListenStarted.value)
        Assert.assertEquals(true, viewModel.isListening.value)
        Assert.assertEquals("", viewModel.rxCode.value)
    }
}
