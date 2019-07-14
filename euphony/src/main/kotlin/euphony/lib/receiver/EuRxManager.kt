package euphony.lib.receiver

import android.os.Handler
import android.os.Message
import android.util.Log
import euphony.lib.util.COMMON

class EuRxManager {
    private var mRxThread: Thread? = null
    private var mRxRunner: RxRunner? = null
    private var mPsRunner: PsRunner? = null
    private var mPsThread: Thread? = null
    private var _active: Boolean = false

    private var mHex = false

    var acousticSensor: AcousticSensor? = null

    private val mHandler = object : Handler() {
        override fun handleMessage(msg: Message) {
            when (msg.what) {
                RX_DECODE -> acousticSensor!!.notify(msg.obj.toString() + "")
                else -> {
                }
            }
        }
    }

    var positionDetector: PositionDetector? = null

    private val mPsHandler = object : Handler() {
        override fun handleMessage(msg: Message) {
            when (msg.what) {
                PS_DECODE -> positionDetector!!.detectSignal(msg.obj as Int)
            }
        }
    }

    constructor() {}
    constructor(hex: Boolean) {
        mHex = hex
    }

    @JvmOverloads
    fun listen(hex: Boolean = mHex) {
        _active = true
        mRxRunner = RxRunner(hex)
        mRxThread = Thread(mRxRunner, "RX")
        mRxThread!!.start()
    }

    fun find() {
        _active = true
        mPsRunner = PsRunner()
        mPsThread = Thread(mPsRunner, "PS")
        mPsThread!!.start()
    }

    fun finishToFind() {
        if (mPsThread != null) {
            _active = false
            while (true) {
                try {
                    mPsThread!!.join()
                    break
                } catch (e: InterruptedException) {
                    Log.i("FINISH", e.message)
                }

            }
        }

        if (mPsRunner != null)
            mPsRunner!!.DestroyFFT()

        mPsThread = null
        mPsRunner = null
    }

    fun finish() {
        if (mRxThread != null) {
            _active = false
            while (true) {
                try {
                    mRxThread!!.join()
                    break
                } catch (e: InterruptedException) {
                    Log.i("FINISH", e.message)
                }

            }
        }
        if (mRxRunner != null)
            mRxRunner!!.DestroyFFT()

        mRxThread = null
        mRxRunner = null
    }

    private inner class RxRunner : EuFreqObject, Runnable {
        internal var mHex = false

        internal constructor() {}
        internal constructor(hex: Boolean) {
            mHex = hex
        }

        override fun run() {
            while (_active) {
                StartFFT()
                if (mStartSwt!!)
                    euCatchSingleData()
                else
                    mStartSwt = euCheckStartPoint()

                if (isCompleted!!) {
                    val msg = mHandler.obtainMessage()
                    msg.what = RX_DECODE
                    msg.obj = if (mHex) receivedData else EuDataDecoder.decodeStaticHexCharSource(receivedData)
                    isCompleted = false
                    mHandler.sendMessage(msg)
                    mRxRunner!!.DestroyFFT()
                    return
                }
            }
        }
    }

    private inner class PsRunner : EuFreqObject(), Runnable {

        override fun run() {
            var startswt = false
            var startcnt = 0
            var specificFreq = 0
            Log.i("START", "START LISTEN")
            while (_active) {
                //To find the frequency point
                while (!startswt) {
                    StartFFT()
                    var i: Int
                    i = 21000
                    while (i >= 16500) {
                        if (100 < euDetectFreq(i)) {
                            startswt = true
                            break
                        }
                        i -= COMMON.CHANNEL_SPAN
                    }
                    specificFreq = i

                    //there is no af area..
                    if (startcnt++ > 1000) {
                        _active = false
                        startswt = true
                        Log.i("START", "FAILED to find any position")
                    }
                }

                var signal: Int
                var max_signal = 0
                var avr_signal = 0
                var noSignalCnt = 0
                var processingCnt = 0
                var maxCnt = 0
                do {
                    StartFFT()
                    signal = euDetectFreq(specificFreq)

                    if (signal < 20)
                        noSignalCnt++
                    else {
                        noSignalCnt = 0

                        if (max_signal < signal) {
                            maxCnt++
                            max_signal = signal
                            avr_signal += max_signal
                        }
                        if (++processingCnt > 50) {
                            avr_signal /= maxCnt
                            val msg = mPsHandler.obtainMessage()
                            msg.what = PS_DECODE
                            msg.obj = avr_signal
                            mPsHandler.sendMessage(msg)
                            processingCnt = 0
                            max_signal = 0
                            avr_signal = 0
                            maxCnt = 0
                        }
                    }
                } while (noSignalCnt < 50 && _active)

                val msg = mPsHandler.obtainMessage()
                msg.what = PS_DECODE
                msg.obj = -1
                mPsHandler.sendMessage(msg)
                break

            }
        }
    }

    companion object {

        private val RX_DECODE = 1
        private val PS_DECODE = 2
    }
}