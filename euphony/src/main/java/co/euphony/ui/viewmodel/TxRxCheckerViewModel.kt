package co.euphony.ui.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.euphony.rx.AcousticSensor
import co.euphony.rx.EuRxManager
import co.euphony.tx.EuTxManager
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.ceil

class TxRxCheckerViewModel(
    private val txManager: EuTxManager = EuTxManager(),
    private val rxManager: EuRxManager = EuRxManager()
) : ViewModel() {

    private val _isProcessing = MutableLiveData(false)
    val isProcessing get() = _isProcessing

    private val _txCode = MutableLiveData("")
    val txCode get() = _txCode

    private val _rxCode = MutableLiveData("")
    val rxCode get() = _rxCode

    private var transmitCount = 0
    private var transmitTime = 0

    fun start(textToSend: String, count: Int) {
        if (isProcessing.value != true) {
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
        val txCodeLength = _txCode.value?.length ?: 0
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
        if (_isProcessing.value == true) {
            _isProcessing.value = false
            txManager.stop()
            rxManager.finish()
        }
    }

    fun isSuccess(): Boolean {
        if (_txCode.value == _rxCode.value) {
            return true
        }
        return false
    }
}