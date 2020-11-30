//
// Created by desig on 2020-11-15.
//

#ifndef EUPHONY_IFFTPROCESSOR_H
#define EUPHONY_IFFTPROCESSOR_H

class IFFTProcessor {
public:
    virtual void create(int fft_size) = 0;
    virtual void destroy() = 0;
    virtual void doSpectrum() = 0;
    virtual void doSpectrums() = 0;
};

#endif //EUPHONY_IFFTPROCESSOR_H
