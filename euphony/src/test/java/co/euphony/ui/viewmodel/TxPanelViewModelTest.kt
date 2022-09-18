package co.euphony.ui.viewmodel

import co.euphony.tx.EuTxManager
import org.junit.After
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class TxPanelViewModelTest {

    private lateinit var viewModel: TxPanelViewModel

    @Mock
    private lateinit var txManager: EuTxManager

    @Before
    fun setUp() {
        viewModel = TxPanelViewModel(txManager)
    }

    @After
    fun tearDown() {
        viewModel.stop()
    }

    @Test
    fun `if onBtnClick starts, isProcessing becomes true`() {
        assertFalse(viewModel.isProcessing.value)
        viewModel.onBtnClick("")
        assertTrue(viewModel.isProcessing.value)
    }

    @Test
    fun `if stop() is called, isProcessing becomes false`() {
        viewModel.onBtnClick("")
        assertTrue(viewModel.isProcessing.value)

        viewModel.stop()
        assertFalse(viewModel.isProcessing.value)
    }
}