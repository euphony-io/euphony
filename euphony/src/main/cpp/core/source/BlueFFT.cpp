#include "../BlueFFT.h"
#include "../Definitions.h"

Euphony::BlueFFT::BlueFFT(int fft_size, int sample_rate)
: FFTModel(fft_size, sample_rate)
, fftSize(fft_size)
, halfOfFFTSize(fft_size >> 1)
{
    spectrum.resize(fft_size);
    result.resize(halfOfFFTSize);
}

Euphony::BlueFFT::~BlueFFT() {
    std::vector<cpx>().swap(spectrum);
    std::vector<float>().swap(result);
}

float* Euphony::BlueFFT::makeSpectrum(short *src) {
    /* TODO: should implement makeSpectrum for short source */
    return nullptr;
}

float *Euphony::BlueFFT::makeSpectrum(float *src) {

    for(int i = 0; i < fftSize; i++) {
        spectrum[i].real(src[i]);
    }

    FFT(spectrum, false);

    int startIdx = frequencyToIndex(kStartSignalFrequency) - 1;
    int lenHalfOfNumSamples = halfOfFFTSize;

    for(int i = startIdx; i <= lenHalfOfNumSamples; ++i) {
        float re = spectrum[i].real() * (float)fftSize;
        float im = spectrum[i].imag() * (float)fftSize;

        result[i] = sqrtf(re * re + im * im) / (float) lenHalfOfNumSamples;
    }

    return &result[0];
}

int Euphony::BlueFFT::getResultSize() const {
    return halfOfFFTSize + 1;
}

float Euphony::BlueFFT::shortToFloat(const short val) {
    if( val < 0 )
        return val * ( 1 / 32768.0f );
    else
        return val * ( 1 / 32767.0f );
}

inline int Euphony::BlueFFT::frequencyToIndex(const int freq) const {
    return (int)(((halfOfFFTSize) + 1) * ((double)freq / (double)getSampleRate()));
}

void Euphony::BlueFFT::FFT(std::vector<cpx> &spectrum, bool inv) {
    int N = fftSize;
    for(int i=1, j=0; i<N; i++) {
        int bit = N >> 1;

        while(j >= bit) {
            j -= bit;
            bit >>= 1;
        }
        j += bit;

        if(i < j)
            swap(spectrum[i], spectrum[j]);
    }

    for(int k=1; k<N; k*=2) {
        double angle = (inv ? PI/k : -PI/k);
        cpx dir(cos(angle), sin(angle));

        for(int i=0; i<N; i+=k*2) {
            cpx unit(1, 0);

            for(int j=0; j<k; j++) {
                cpx u = spectrum[i+j];
                cpx v = spectrum[i+j+k] * unit;

                spectrum[i+j] = u + v;
                spectrum[i+j+k] = u - v;

                unit *= dir;
            }
        }
    }

    if(inv)
        for(int i=0; i<N; i++) spectrum[i] /= N;
}
