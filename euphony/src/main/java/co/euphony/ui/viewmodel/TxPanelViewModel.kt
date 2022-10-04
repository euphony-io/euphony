package co.euphony.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import co.euphony.tx.EuTxManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class TxPanelViewModel(
    private val txManager: EuTxManager
) : ViewModel() {

    private val _isProcessing = MutableStateFlow(false)
    val isProcessing: StateFlow<Boolean> = _isProcessing

    fun onBtnClick(data: String) {
        if (_isProcessing.value) {
            stop()
        } else {
            start(data)
        }
    }

    fun stop() {
        txManager.stop()
        if (_isProcessing.value) {
            _isProcessing.value = false
        }
    }

    private fun start(data: String) {
        txManager.code = data
        txManager.play(-1)
        _isProcessing.value = true
    }

    override fun onCleared() {
        super.onCleared()
        txManager.stop()
        _isProcessing.value = false
    }
}

class TxPanelViewModelFactory(
    private val euTxManager: EuTxManager
) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T =
        TxPanelViewModel(euTxManager) as T
}