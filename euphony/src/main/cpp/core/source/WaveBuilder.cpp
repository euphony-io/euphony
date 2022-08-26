#include <Definitions.h>
#include "../WaveBuilder.h"

using namespace Euphony;

WaveBuilder& WaveBuilder::vibratesAt(int hz) {
    wave.setHz(hz);
    return *this;
}

WaveBuilder& WaveBuilder::setSize(int size) {
    wave.setSize(size);
    return *this;
}

WaveBuilder& WaveBuilder::setAmplitude(float amplitude) {
    wave.setAmplitude(amplitude);
    return *this;
}


WaveBuilder& WaveBuilder::setCrossfade(CrossfadeType type) {
    wave.setCrossfade(type);
    return *this;
}

WaveBuilder& WaveBuilder::setSampleRate(int sampleRate) {
    wave.setSampleRate(sampleRate);
    return *this;
}

std::shared_ptr<Wave> WaveBuilder::build() {
    if(wave.getSampleRate() == 0)
        wave.setSampleRate(kSampleRate);

    if(wave.getSize() > 0 && wave.getHz() > 0) {
        wave.oscillate();
        return std::make_shared<Wave>(wave);
    } else {
        return nullptr;
    }
}






