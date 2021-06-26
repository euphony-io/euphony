package co.euphony.rx;

import java.nio.ByteOrder;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

public abstract class FFTStrategy {

    ByteBuffer samples;
    FloatBuffer spectrum;

    public FFTStrategy(int fftSize) {
        initialize(fftSize);
        samples = makeByteBuffer(fftSize);
        spectrum = makeFloatBuffer(fftSize);
    }

    abstract void initialize(int fftSize);
    public FloatBuffer makeSpectrum(ByteBuffer samples) {
        return makeSpectrum(samples.asShortBuffer());
    }
    public FloatBuffer makeSpectrum(short[] samples) {
        byte[] bytes = new byte[samples.length * 2];
        ShortBuffer buf = ByteBuffer.wrap(bytes).order(ByteOrder.nativeOrder()).asShortBuffer().get(samples);

        return makeSpectrum(buf);
    }
    public abstract FloatBuffer makeSpectrum(ShortBuffer samples);
    public abstract void finish();

    public FloatBuffer getSpectrum() {
        return spectrum;
    }



    private ByteBuffer makeByteBuffer(int numSamples) {
        ByteBuffer buffer = ByteBuffer.allocateDirect(numSamples * 2);
        buffer.order(ByteOrder.nativeOrder());
        return buffer;
    }

    private FloatBuffer makeFloatBuffer(int numSamples) {
        ByteBuffer buffer = makeByteBuffer(numSamples);
        return buffer.asFloatBuffer();
    }

    private ShortBuffer makeShortBuffer(int numSamples) {
        ByteBuffer buffer = makeByteBuffer(numSamples);
        return buffer.asShortBuffer();
    }
}
