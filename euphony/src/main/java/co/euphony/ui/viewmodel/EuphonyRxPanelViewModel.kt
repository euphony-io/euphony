package co.euphony.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.euphony.rx.AcousticSensor
import co.euphony.rx.EuRxManager
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class EuphonyRxPanelViewModel(
    private val rxManager: EuRxManager
) : ViewModel() {

    private val _isListening = MutableStateFlow(false)
    val isListening: StateFlow<Boolean> = _isListening

    private val _isListenStarted = MutableStateFlow(false)
    val isListenStarted: StateFlow<Boolean> = _isListenStarted

    private val _rxCode = MutableStateFlow("")
    val rxCode: StateFlow<String> = _rxCode

    private var limitTime = 10

    fun start() {
        if (!_isListening.value) {
            _rxCode.value = ""
            _isListening.value = true
            _isListenStarted.value = true

            listen()

            rxManager.acousticSensor = AcousticSensor {
                stop()
                _rxCode.value = it
            }
        }
    }

    private fun listen() {
        rxManager.listen()
        viewModelScope.launch {
            while (limitTime > 0) {
                delay(1000)
                limitTime--
            }
            stop()
        }
    }

    fun stop() {
        if (_isListening.value) {
            _isListening.value = false
            rxManager.finish()
        }
    }

    fun isSuccess(): Boolean {
        if (_rxCode.value.isNotEmpty()) {
            return true
        }
        return false
    }
}