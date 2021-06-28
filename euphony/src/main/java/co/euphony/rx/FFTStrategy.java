package co.euphony.rx;

import java.nio.ByteOrder;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;
import java.util.Arrays;

public abstract class FFTStrategy {

    int fftSize;
    int floatBufferSize;
    int byteBufferSize;
    int shortBufferSize;

    /* recycelSamples & Spectrum are direct buffers for quick processing.
     * That's why they're allocated just once on Constructor.  */
    ByteBuffer recycleSamples;
    FloatBuffer recycleSpectrum;

    public FFTStrategy(int fftSize) {
        this.fftSize = fftSize;
        this.byteBufferSize = fftSize;
        this.floatBufferSize = fftSize >> 1;
        this.shortBufferSize = this.floatBufferSize;

        initialize(fftSize);
        recycleSamples = makeByteBuffer(fftSize);
        recycleSpectrum = makeFloatBuffer(this.floatBufferSize + 1);
    }

    abstract void initialize(int fftSize);

    public FloatBuffer[] makeSpectrum(short[] inputShortSamples) {
        int lenSamples = inputShortSamples.length;
        FloatBuffer[] result;// = makeFloatBuffer(lenSamples);
        int cycleCount = 1;
        if(lenSamples > shortBufferSize) {
            cycleCount = lenSamples / shortBufferSize + 1;
        }
        result = new FloatBuffer[cycleCount];

        ShortBuffer sb = makeShortBuffer(shortBufferSize);
        for(int i = 0; i < cycleCount; i++)
        {
            short[] src = Arrays.copyOfRange(inputShortSamples, i * shortBufferSize, (i + 1) * shortBufferSize);
            recycleSpectrum.clear();
            sb.put(src);
            makeSpectrum(sb, recycleSpectrum);
            result[i] = recycleSpectrum.duplicate();
            //result[i].put(recycleSpectrum);
            sb.clear();
        }

        return result;
    }

    public abstract FloatBuffer makeSpectrum(ShortBuffer samples, FloatBuffer spectrum);
    public abstract void finish();


    public int getFFTSize() {
        return fftSize;
    }

    public void setFFTSize(int fftSize) {
        this.fftSize = fftSize;
    }

    public FloatBuffer getSpectrum() {
        return recycleSpectrum;
    }

    private ByteBuffer makeByteBuffer(int numSamples) {
        ByteBuffer buf = ByteBuffer.allocateDirect(numSamples);
        buf.order(ByteOrder.nativeOrder());
        return buf;
    }

    private FloatBuffer makeFloatBuffer(int numSamples) {
        return makeByteBuffer(numSamples * 4).asFloatBuffer();
    }

    private FloatBuffer makeFloatBuffer(float[] inputFloatArray) {
        return makeByteBuffer(inputFloatArray.length * 4).asFloatBuffer().put(inputFloatArray);
    }

    private ShortBuffer makeShortBuffer(int numSamples) {
        return makeByteBuffer(numSamples * 2).asShortBuffer();
    }

    private ShortBuffer makeShortBuffer(short[] inputShortArray) {
        return makeByteBuffer(inputShortArray.length * 2).asShortBuffer().put(inputShortArray);
    }


}
