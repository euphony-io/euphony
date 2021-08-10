//
// Created by designe on 20. 9. 16.
//

#include "../Definitions.h"
#include "../Wave.h"
#include "../WaveBuilder.h"

using namespace Euphony;

Euphony::Wave::Wave()
:mHz(0),
mSize(0)
{}

Euphony::Wave::Wave(int hz, int size)
:mHz(hz),
mSize(size)
{
    oscillate(hz, size);
}

WaveBuilder Euphony::Wave::create() {
    return WaveBuilder();
}

void Euphony::Wave::updatePhaseIncrement(int hz) {
    mPhaseIncrement.store((kTwoPi * hz) / static_cast<double>(kSampleRate));
}

void Euphony::Wave::oscillate(int hz, int size) {
    float phase = 0.0;
    updatePhaseIncrement(hz);

    for(int i = 0; i < size; ++i) {
        mSource[i] = (float) sin(phase);
        phase += mPhaseIncrement;
        if(phase > kTwoPi) phase -= kTwoPi;
    }
}

int Euphony::Wave::getHz() const {
    return mHz;
}

void Euphony::Wave::setHz(int hz) {
    mHz = hz;
}

int Euphony::Wave::getSize() const {
    return mSize;
}

void Euphony::Wave::setSize(int size) {
    mSize = size;
}

const std::vector<float> &Euphony::Wave::getSource() const {
    return mSource;
}

void Euphony::Wave::setSource(const std::vector<float> &source) {
    mSource = source;
}