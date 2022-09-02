#ifndef EUPHONY_WAVEBUILDER_H
#define EUPHONY_WAVEBUILDER_H

#include "Wave.h"

namespace Euphony {
    class WaveBuilder {
    private:
        Wave wave;

    public:
        WaveBuilder& vibratesAt(int hz);
        WaveBuilder& setSize(int size);
        WaveBuilder& setAmplitude(float amplitude);
        WaveBuilder& setCrossfade(CrossfadeType type);
        WaveBuilder& setSampleRate(int sampleRate);
        std::shared_ptr<Wave> build();
    };
};
#endif //EUPHONY_WAVEBUILDER_H
