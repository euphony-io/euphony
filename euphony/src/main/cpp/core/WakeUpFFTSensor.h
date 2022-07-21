//
// Created by opener on 22. 7. 15..
//

#ifndef EUPHONY_WAKEUPFFTSENSOR_H
#define EUPHONY_WAKEUPFFTSENSOR_H

#include <memory>
#include "FFTModel.h"
#include "WakeUpSensor.h"

namespace Euphony {
    class WakeUpFFTSensor : public WakeUpSensor {
    public:
        WakeUpFFTSensor(int sampleRate);
        bool detectWakeUpSign(const float* audioSrc, const int size);
        bool isWakeUp();
        bool isWakeUp(int signFrequency);

    private:
        int isWaveDetected(const float* audioSrc, const int size);
        int isStartSignalDetected(const float* audioSrc, const int size);
        std::unique_ptr<FFTModel> preFFT;
        std::unique_ptr<FFTModel> postFFT;
        int preFFTSize;
        int postFFTSize;
        bool isStarted;
        int sampleRate;
    };
}

#endif //EUPHONY_WAKEUPFFTSENSOR_H
