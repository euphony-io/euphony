//
// Created by opener on 22. 7. 15..
//

#ifndef EUPHONY_WAKEUPFFTSENSOR_H
#define EUPHONY_WAKEUPFFTSENSOR_H

#include "WakeUpSensor.h"

namespace Euphony {
    class WakeUpFFTSensor : public WakeUpSensor {
    public:
        WakeUpFFTSensor(int sampleRate);
        int feedAudioData(float* audioSrc, int size);
        bool isWakeUp();
        bool isWakeUp(int signFrequency);

    private:
        std::unique_ptr<FFTModel> gear1st;
        std::unique_ptr<FFTModel> gear2nd;
        int gear1stFFTSize = 0;
        int gear2ndFFTSize = 0;
        bool isStarted = false;
        int fftSize = 0;
        int sampleRate = 0;
    };
}

#endif //EUPHONY_WAKEUPFFTSENSOR_H
