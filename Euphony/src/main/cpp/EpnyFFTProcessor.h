//
// Created by opener on 20. 11. 30.
//

#ifndef EUPHONY_EPNYFFTPROCESSOR_H
#define EUPHONY_EPNYFFTPROCESSOR_H

#include <memory>
#include "kiss_fftr.h"
#include "IFFTProcessor.h"

/*
 * Define FFT Structure
 * This space is similar to device driver's structure.
 * Euphony is now using KissFFT Library.
 * If you want to change FFT Library, Just fulfill IFFTProcessor interface.
 */

struct KissFFT
{
    kiss_fftr_cfg config;
    /* unique_ptr's array version. it is available on c++14. */
    std::unique_ptr<kiss_fft_cpx[]> spectrum;
    int numSamples;
};

class EpnyFFTProcessor : IFFTProcessor {
public:
    virtual void create(int fft_size);
    virtual void destroy();
    virtual void doSpectrum();
    virtual void doSpectrums();

    EpnyFFTProcessor(int fft_size);
    virtual ~EpnyFFTProcessor() = 0;

private:
    std::unique_ptr<KissFFT> mFFT;
};


#endif //EUPHONY_EPNYFFTPROCESSOR_H
