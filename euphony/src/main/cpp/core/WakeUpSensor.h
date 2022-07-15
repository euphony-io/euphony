#ifndef EUPHONY_WAKEUPSENSOR_H
#define EUPHONY_WAKEUPSENSOR_H

namespace Euphony {

    class WakeUpSensor {
    public:
        virtual ~WakeUpSensor() = default;
        virtual int feedAudioData(float* audioSrc, int size) = 0;
        virtual bool isWakeUp() = 0;
    };

}

#endif //EUPHONY_WAKEUPSENSOR_H