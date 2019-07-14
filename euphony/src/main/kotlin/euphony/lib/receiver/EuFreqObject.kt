package euphony.lib.receiver

import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import java.nio.ShortBuffer
import java.util.ArrayList

import android.util.Log
import euphony.lib.util.COMMON
import euphony.lib.util.PacketErrorDetector

open class EuFreqObject {

    val SAMPLERATE = COMMON.SAMPLERATE//44100;
    val fftsize = COMMON.FFT_SIZE//512;
    val MAXREFERENCE = COMMON.MAX_REF//4000;
    val MINREFERENCE = COMMON.MIN_REF//50;
    val MAXFREQUENCY = COMMON.MAX_FREQ//22050.0;
    val DEFAULT_REF = COMMON.DEFAULT_REF//500
    val START_FREQ = COMMON.START_FREQ //18000
    val RXCHANNEL = COMMON.CHANNEL// 16
    private val STARTCHANNEL = 1
    private val mFreqSpan = COMMON.CHANNEL_SPAN // 86
    var START_BIT = START_FREQ - mFreqSpan

    var mStartSwt: Boolean? = false
    var isCompleted: Boolean? = false
    private var isRecording: Boolean? = false

    private val samples = allocateByteBuffer(fftsize)
    private val spectrum = allocateFloatBuffer(fftsize / 2 + 1)
    private val spectrum_p = allocateFloatBuffer(fftsize / 2 + 1)
    var receivedData: String? = null

    private val mFreqArray = IntArray(RXCHANNEL + STARTCHANNEL)
    private val mTempRef = IntArray(RXCHANNEL + STARTCHANNEL)
    private val mRefCntIndexArray = IntArray(RXCHANNEL + STARTCHANNEL)
    private val mDynamicRefArray = IntArray(RXCHANNEL + STARTCHANNEL)

    private val mChannelArrayList = ArrayList<Int>()

    private val recorder: AudioRecorder
    private val FFT = KissFFT(fftsize)

    private var mMaxIndex = -1
    private var mSampleIndex = 0
    private var mSampleTemp = 0
    private var mChannelTemp = 0
    var cntCheck = 0
    private var mArrMaxIndex = IntArray(RXCHANNEL + STARTCHANNEL)
    private val mArrSampleIndex = IntArray(RXCHANNEL + STARTCHANNEL)
    private val mArrSampleTemp = IntArray(RXCHANNEL + STARTCHANNEL)
    private val mArrChannelTemp = IntArray(RXCHANNEL + STARTCHANNEL)
    var mArrcntCheck = IntArray(RXCHANNEL + STARTCHANNEL)

    var mStartSampleCnt = 0

    private val SENSOR_LIMIT_COUNT = 2000

    init {
        recorder = AudioRecorder(SAMPLERATE)
        //INIT DYNAMIC REFERENCE ARRAY..
        for (i in 0 until RXCHANNEL + STARTCHANNEL)
            mDynamicRefArray[i] = DEFAULT_REF

        isRecording = false
    }


    fun StartFFT() {
        if (isRecording != true) {
            recorder.start()
            isRecording = true
        }
        recorder.read(samples, fftsize)
        FFT.spectrum(samples, spectrum)
        //FFT.spectrum_for_phase(samples, spectrum_p);
        //FFT.getRealPart(real);
        //FFT.getImagPart(image);
    }

    fun StartFFT(windowsNum: Short) {
        if (isRecording != true) {
            recorder.start()
            isRecording = true
        }
        recorder.read(samples, fftsize, windowsNum)
        FFT.spectrum(samples, spectrum)
    }

    fun DestroyFFT() {
        FFT.dispose()
        recorder.stop()
        isRecording = false
    }

    fun euDetectFreq(fFrequency: Int): Int {

        val fFreqRatio: Double
        val freqIndex: Int
        val fmax: Float

        fFreqRatio = fFrequency / MAXFREQUENCY
        freqIndex = (fFreqRatio * fftsize / 2).toInt() + 1

        //f1 = spectrum.get(freqIndex-1);
        fmax = spectrum.get(freqIndex)
        //f3 = spectrum_p.get(freqIndex);
        //r1 = real.get(freqIndex);
        //i1 = image.get(freqIndex);
        /*
		String s = "";
		for(int i = freqIndex - 1; i <= freqIndex + 1; i++)
			s += " " + spectrum.get(i);
		Log.i("HELLO FREQ", freqIndex + "::" + s);
		*/
        //f3 = spectrum.get(freqIndex+1);

        //if( fmax < f2 )	fmax = f2;
        //if( fmax < f3 )	fmax = f3;

        return (fmax * 100000).toInt()
    }

    fun euCatchSingleData() {
        // UPDATED ON 1/2/2014
        // START BIT's Frequency Detection
        mSampleTemp = this.euDetectFreq(START_BIT)
        mMaxIndex = RXCHANNEL
        // START BIT's Dynamic Reference Catch
        mDynamicRefArray[RXCHANNEL] = euGetDynamicReference(mSampleTemp, RXCHANNEL)

        // Rest of frequency processing
        for (i in 0 until RXCHANNEL) {
            var currentFreq = this.euDetectFreq(START_FREQ + mFreqSpan * i)
            mDynamicRefArray[i] = euGetDynamicReference(currentFreq, i)

            if (currentFreq >= mSampleTemp && currentFreq >= mDynamicRefArray[i]) {
                mSampleTemp = currentFreq
                mMaxIndex = i
            }
        }

        // BEFORE VERSION
        /*for(int i = 0; i < RXCHANNEL + STARTCHANNEL ; i++)
		{
			if(i != RXCHANNEL)
				currentFreq = (int) this.euDetectFreq(START_FREQ + mFreqSpan*i);
			else
				currentFreq = (int) this.euDetectFreq(START_BIT);
			mDynamicRefArray[i] = euGetDynamicReference(currentFreq, i);

			if(currentFreq>=mSampleTemp && currentFreq >=mDynamicRefArray[i]){
				mSampleTemp = currentFreq;
				mMaxIndex = i;
			}
		}*/

        if (mMaxIndex != -1) {
            mFreqArray[mMaxIndex]++
        }
        if (mSampleIndex < 7) {
            mSampleIndex++
        } else {
            for (i in 0 until RXCHANNEL + STARTCHANNEL) {
                if (mFreqArray[i] > mChannelTemp) {
                    mChannelTemp = mFreqArray[i]
                    mMaxIndex = i
                }
                //mbFreqArray[i] = 0;
            }

            if (mChannelTemp > 2 && mMaxIndex != -1) {
                if (mMaxIndex == 16) {
                    if (mChannelArrayList.size > 1) {
                        cntCheck++
                        Log.v("mChanelArrayList.size()", "mChanelArrayList.size():" + mChannelArrayList.size)

                        receivedData = ""
                        val a = IntArray(mChannelArrayList.size - 2)
                        for (i in 0 until mChannelArrayList.size - 2) {
                            a[i] = mChannelArrayList[i]
                            if (a[i] > 9)
                                receivedData += "" + ('a'.toInt() + (a[i] - 10)).toChar()
                            else
                                receivedData += "" + a[i]
                        }

                        if (PacketErrorDetector.makeCheckSum(a) == mChannelArrayList[mChannelArrayList.size - 2] && PacketErrorDetector.makeParellelParity(a) == mChannelArrayList[mChannelArrayList.size - 1]) {
                            mStartSwt = false
                            isCompleted = true
                            receiveStr = EuDataDecoder.decodeStaticHexCharSource(a)
                        }
                        mChannelArrayList.clear()
                    }
                } else {
                    mChannelArrayList.add(mMaxIndex)
                }
                Log.v("DATA", "DATAIndex : " + mMaxIndex + "  mCheckedByte :" + mSampleIndex + "  tempxcnt :" + mChannelTemp + "   Data :" + this.euDetectFreq(START_FREQ + mFreqSpan * mMaxIndex))
                Log.v("DaTA", "0:" + mFreqArray[0] + " 1:" + mFreqArray[1] + " 2:" + mFreqArray[2] + " 3:" + mFreqArray[3] + " 4:" + mFreqArray[4] + " 5:" + mFreqArray[5] + " 6:" + mFreqArray[6] + " 7:" + mFreqArray[7] + " 8:" + mFreqArray[8]
                        + " 9:" + mFreqArray[9] + " 10:" + mFreqArray[10] + " 11:" + mFreqArray[11] + " 12:" + mFreqArray[12] + " 13:" + mFreqArray[13] + " 14:" + mFreqArray[14] + " 15:" + mFreqArray[15] + " 16:" + mFreqArray[16])
            } else {
                mChannelArrayList.add(-1)
                Log.v("DATAX", "DATAIndex : " + mMaxIndex + "  mCheckedByte :" + mSampleIndex + "   Data :" + this.euDetectFreq(START_FREQ + mFreqSpan * mMaxIndex))
                Log.v("DaTA", "0:" + mFreqArray[0] + " 1:" + mFreqArray[1] + " 2:" + mFreqArray[2] + " 3:" + mFreqArray[3] + " 4:" + mFreqArray[4] + " 5:" + mFreqArray[5] + " 6:" + mFreqArray[6] + " 7:" + mFreqArray[7] + " 8:" + mFreqArray[8]
                        + " 9:" + mFreqArray[9] + " 10:" + mFreqArray[10] + " 11:" + mFreqArray[11] + " 12:" + mFreqArray[12] + " 13:" + mFreqArray[13] + " 14:" + mFreqArray[14] + " 15:" + mFreqArray[15] + " 16:" + mFreqArray[16])

            }
            mMaxIndex = -1
            mSampleIndex = 0
            mSampleTemp = 0
            mChannelTemp = 0
            for (i in 0 until RXCHANNEL + STARTCHANNEL) {////
                mFreqArray[i] = 0////
            }/////
        }

    }

    fun euCatchMultiData() {
        for (j in 0 until RXCHANNEL + STARTCHANNEL) {
            mArrMaxIndex[j] = -1
            mArrSampleIndex[j] = 0
            mArrSampleTemp[j] = 0
            mArrChannelTemp[j] = 0
            mArrcntCheck[j] = 0
            mFreqArray[j] = 0
        }
        val arrCurrentFreq = IntArray(RXCHANNEL + STARTCHANNEL)
        for (i in 0 until RXCHANNEL + STARTCHANNEL) {
            arrCurrentFreq[i] = this.euDetectFreq(START_FREQ + mFreqSpan * i)
            if (arrCurrentFreq[i] >= 200) {
                //mArrSampleTemp[i] = arrCurrentFreq[i];
                mArrMaxIndex[i] = i
            }
        }
    }

    fun getmMaxIndex(): Int {
        return mMaxIndex
    }

    fun setmMaxIndex(mMaxIndex: Int) {
        this.mMaxIndex = mMaxIndex
    }

    fun getmArrMaxIndex(): IntArray {
        return mArrMaxIndex
    }

    fun setmArrMaxIndex(mArrMaxIndex: IntArray) {
        this.mArrMaxIndex = mArrMaxIndex
    }

    fun euCheckStartPoint(): Boolean? {
        var tempFreq = 50
        var tempIndex = -1

        for (i in -1..1) {
            var currentFreq = this.euDetectFreq(START_BIT + mFreqSpan * i)
            if (currentFreq > tempFreq) {
                tempFreq = currentFreq
                tempIndex = 0
            }
        }
        if (tempIndex == 0) {
            if (mStartSampleCnt == 8)
                return true
            else
                mStartSampleCnt++
        } else
            mStartSampleCnt = 0

        return false
    }

    private fun euSpecificFreqSensor(_freq: Int): Boolean {
        var dynRef = 500
        val dynRefCnt = 0
        var criticalCnt = 0
        var limitCnt = 0
        var temp = 0
        val preservedRef = 0
        while (SENSOR_LIMIT_COUNT > limitCnt++) {
            this.StartFFT()

            val curFreq = this.euDetectFreq(_freq)
            dynRef = euGetDynamicRef(curFreq, dynRef, preservedRef, dynRefCnt)

            if (curFreq >= dynRef) {
                val prevFreq = this.euDetectFreq(_freq - mFreqSpan)
                val nextFreq = this.euDetectFreq(_freq + mFreqSpan)

                if (prevFreq > curFreq || nextFreq > curFreq)
                    continue
                else {
                    criticalCnt++
                    temp = limitCnt
                }
            }

            if (criticalCnt > 4) {
                if (limitCnt - temp > 12) {
                    criticalCnt = 0
                    continue
                } else
                    return true
            }
        }
        return false
    }

    fun euGetDynamicRef(curFreq: Int, dynRef: Int, preservedRef: Int, dynRefCnt: Int): Int {
        var dynRef = dynRef

        if (curFreq > dynRef)
        // DATA TRUE
        {
            when (dynRefCnt) {
                0 -> dynRef = curFreq
                1 -> dynRef = (curFreq + dynRef) / 3
                2 -> dynRef = preservedRef
            }
        } else
        //DATA FALSE
        {
            dynRef -= ((dynRef - curFreq) * 0.9).toInt()
        }

        // MAXIMUM and MIMINUM DATA CATCHING
        if (dynRef > MAXREFERENCE)
            dynRef = MAXREFERENCE
        if (dynRef < MINREFERENCE)
            dynRef = MINREFERENCE

        return dynRef
    }

    fun euGetDynamicReference(nfreq: Int, freqIndex: Int): Int {
        if (nfreq > mDynamicRefArray[freqIndex])
        // DATA TRUE
        {
            when (mRefCntIndexArray[freqIndex]) {
                0 -> {
                    mDynamicRefArray[freqIndex] = nfreq
                    mRefCntIndexArray[freqIndex]++
                }
                1 -> {
                    mDynamicRefArray[freqIndex] = (nfreq + mDynamicRefArray[freqIndex]) / 2
                    mTempRef[freqIndex] = mDynamicRefArray[freqIndex]
                    mRefCntIndexArray[freqIndex]++
                }
                2 -> mDynamicRefArray[freqIndex] = mTempRef[freqIndex]
            }
        } else
        //DATA FALSE
        {
            mDynamicRefArray[freqIndex] -= ((mDynamicRefArray[freqIndex] - nfreq) * 0.9).toInt()
            mRefCntIndexArray[freqIndex] = 0
        }

        // MAXIMUM and MIMINUM DATA CATCHING
        if (mDynamicRefArray[freqIndex] > MAXREFERENCE)
            mDynamicRefArray[freqIndex] = MAXREFERENCE
        if (mDynamicRefArray[freqIndex] < MINREFERENCE)
            mDynamicRefArray[freqIndex] = MINREFERENCE

        return mDynamicRefArray[freqIndex]
    }


    private fun allocateByteBuffer(numSamples: Int): ByteBuffer {
        val buffer = ByteBuffer.allocateDirect(numSamples * 2)
        buffer.order(ByteOrder.nativeOrder())
        return buffer
    }

    private fun allocateFloatBuffer(numSamples: Int): FloatBuffer {
        val buffer = allocateByteBuffer(numSamples * 2)
        return buffer.asFloatBuffer()
    }

    private fun allocateShortBuffer(numSamples: Int): ShortBuffer {
        val buffer = allocateByteBuffer(numSamples * 2)
        return buffer.asShortBuffer()
    }

    companion object {
        var receiveStr = ""
    }

}
