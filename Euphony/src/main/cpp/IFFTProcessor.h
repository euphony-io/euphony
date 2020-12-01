//
// Created by desig on 2020-11-15.
//

#ifndef EUPHONY_IFFTPROCESSOR_H
#define EUPHONY_IFFTPROCESSOR_H

class IFFTProcessor {
public:
    virtual void create(int fft_size, int samplerate) = 0;
    virtual void destroy() = 0;
    virtual float doSpectrum(int spectrum_idx) = 0;
    virtual float* doSpectrums(int from_idx, int to_idx, short* pcm_src) = 0;
};

#endif //EUPHONY_IFFTPROCESSOR_H
