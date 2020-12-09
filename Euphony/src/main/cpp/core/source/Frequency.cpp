//
// Created by designe on 20. 9. 16.
//

#include "../Frequency.h"

Euphony::Frequency::Frequency()
:mHz(0),
mSize(0)
{}

Euphony::Frequency::Frequency(int hz, int size)
:mHz(hz),
mSize(size)
{}

void Euphony::Frequency::createFrequency(int hz, int size) {
    float phase = 0.0;
    mPhaseIncrement.store((kTwoPi * hz) / static_cast<double>(kSampleRate));

    for(int i = 0; i < size; ++i) {
        mSource[i] = (float) sin(phase);
        phase += mPhaseIncrement;
        if(phase > kTwoPi) phase -= kTwoPi;
    }
}



int Euphony::Frequency::getHz() const {
    return mHz;
}

void Euphony::Frequency::setHz(int hz) {
    mHz = hz;
    if(mSize != 0)
        createFrequency(mHz, mSize);
}

int Euphony::Frequency::getSize() const {
    return mSize;
}

void Euphony::Frequency::setSize(int size) {
    mSize = size;
    if(mHz != 0)
        createFrequency(mHz, mSize);
}

const std::vector<float> &Euphony::Frequency::getSource() const {
    return mSource;
}

void Euphony::Frequency::setSource(const std::vector<float> &source) {
    mSource = source;
}