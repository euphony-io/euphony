package co.euphony.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.euphony.rx.AcousticSensor
import co.euphony.rx.EuRxManager
import co.euphony.tx.EuTxManager
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlin.math.ceil

class TxRxCheckerViewModel(
    private val txManager: EuTxManager,
    private val rxManager: EuRxManager
) : ViewModel() {

    private val _isProcessing = MutableStateFlow(false)
    val isProcessing: StateFlow<Boolean> = _isProcessing

    private val _txCode = MutableStateFlow("")
    val txCode: StateFlow<String> = _txCode

    private val _rxCode = MutableStateFlow("")
    val rxCode: StateFlow<String> = _rxCode

    private var transmitCount = 0
    private var transmitTime = 0

    fun start(textToSend: String, count: Int) {
        if (!_isProcessing.value) {
            transmitCount = count
            _rxCode.value = ""
            _txCode.value = textToSend
            _isProcessing.value = true

            listen()
            txManager.code = textToSend
            txManager.play(count)

            rxManager.acousticSensor = AcousticSensor {
                stop()
                _rxCode.value = it
            }
        }
    }

    private fun listen() {
        rxManager.listen()
        val txCodeLength = _txCode.value.length
        transmitTime = ceil(2048 * (2 * txCodeLength.toDouble() + 3) * transmitCount / 44100).toInt()

        viewModelScope.launch {
            while(transmitTime > 0) {
                delay(1000)
                transmitTime--
            }
            stop()
        }
    }

    fun stop() {
        if (_isProcessing.value) {
            _isProcessing.value = false
            txManager.stop()
            rxManager.finish()
        }
    }

    fun setTxCode(code: String) {
        _txCode.value = code
    }

    fun setRxCode(code: String) {
        _rxCode.value = code
    }

    fun setIsProcessing(flag: Boolean) {
        _isProcessing.value = flag
    }

    fun isSuccess(): Boolean {
        if (_txCode.value.isNotEmpty() && _txCode.value == _rxCode.value) {
            return true
        }
        return false
    }
}