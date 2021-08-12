//
// Created by desig on 2020-11-15.
//

#ifndef EUPHONY_FFTMODEL_H
#define EUPHONY_FFTMODEL_H

namespace Euphony {
    class FFTModel {
    public:
        virtual ~FFTModel() = default;

        void initialize(int fftSize);
        virtual void initialize(int fftSize, int sampleRate) = 0;
        virtual void destroy() = 0;
        virtual float *makeSpectrum(short *pcmSrc) = 0;
    };
}

#endif //EUPHONY_FFTMODEL_H
