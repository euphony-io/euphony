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

WaveBuilder& WaveBuilder::setCrossfade(CrossfadeType type) {
    wave.setCrossfade(type);
    return *this;
}

shared_ptr<Wave> WaveBuilder::build() {
    if(wave.getSize() > 0 && wave.getHz() > 0) {
        wave.oscillate();
    }

    return std::make_shared<Wave>(wave);
}






