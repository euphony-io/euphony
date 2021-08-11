#include "../WaveBuilder.h"

using namespace Euphony;

WaveBuilder& Euphony::WaveBuilder::vibratesAt(int hz) {
    wave.setHz(hz);
    return *this;
}

WaveBuilder& Euphony::WaveBuilder::setSize(int size) {
    wave.setSize(size);
    return *this;
}

shared_ptr<Wave> Euphony::WaveBuilder::build() {
    if(wave.getSize() > 0 && wave.getHz() > 0) {
        wave.oscillate();
    }

    return std::make_shared<Wave>(wave);
}





