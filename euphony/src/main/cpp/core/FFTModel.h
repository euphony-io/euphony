//
// Created by desig on 2020-11-15.
//

#ifndef EUPHONY_FFTMODEL_H
#define EUPHONY_FFTMODEL_H

namespace Euphony {
    class FFTModel {
    public:
        FFTModel(int fftSizeSrc, int sampleRateSrc);
        virtual ~FFTModel() = default;

        virtual float *makeSpectrum(short *pcmSrc) = 0;

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
