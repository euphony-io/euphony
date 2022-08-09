//
// Created by desig on 2020-11-15.
//

#ifndef EUPHONY_FFTMODEL_H
#define EUPHONY_FFTMODEL_H

namespace Euphony {

    struct Spectrums {
        Spectrums(float *pAmplitudeSpectrum, float *pPhaseSpectrum);

        float* amplitudeSpectrum;
        float* phaseSpectrum;
    };

    class FFTModel {
    public:
        FFTModel(int fftSizeSrc, int sampleRateSrc);
        virtual ~FFTModel() = default;

        virtual Spectrums makeSpectrum(const short *pcmSrc) = 0;
        virtual Spectrums makeSpectrum(const float *pcmSrc) = 0;

        float makeAmplitudeSpectrum(float real, float im) const;
        float makePhaseSpectrum(float real, float im) const;

        int getFFTSize() const;
        void setFFTSize(int fftSize);
        int getSampleRate() const;
        void setSampleRate(int sampleRate);

    private:
        int fftSize;
        int sampleRate;

    };
}

#endif //EUPHONY_FFTMODEL_H
