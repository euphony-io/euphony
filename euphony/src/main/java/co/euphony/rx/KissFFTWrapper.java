package co.euphony.rx;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

public class KissFFTWrapper extends FFTStrategy {
    KissFFT fft;

    public KissFFTWrapper(int fftSize) {
        super(fftSize);
    }

    @Override
    void initialize(int fftSize) {
        fft = new KissFFT(fftSize);
    }

    @Override
    public FloatBuffer makeSpectrum(ShortBuffer samples, FloatBuffer spectrum) {
        fft.doSpectrums(samples, spectrum);
        return spectrum;
    }

    @Override
    public void finish() {
        fft.dispose();
    }
}
