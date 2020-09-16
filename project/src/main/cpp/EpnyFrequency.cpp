//
// Created by designe on 20. 9. 16.
//

#include "EpnyFrequency.h"

EpnyFrequency::EpnyFrequency()
:mHz(0),
mSize(0)
{}

EpnyFrequency::EpnyFrequency(int hz, int size)
:mHz(hz),
mSize(size)
{}

void EpnyFrequency::createFrequency(int hz, int size) {
    float phase = 0.0;
    mPhaseIncrement.store((kTwoPi * hz) / static_cast<double>(kSampleRate));

    for(int i = 0; i < size; ++i) {
        mSource[i] = (float) sin(phase);
        phase += mPhaseIncrement;
        if(phase > kTwoPi) phase -= kTwoPi;
    }
}



int EpnyFrequency::getHz() const {
    return mHz;
}

void EpnyFrequency::setHz(int hz) {
    mHz = hz;
    if(mSize != 0)
        createFrequency(mHz, mSize);
}

int EpnyFrequency::getSize() const {
    return mSize;
}

void EpnyFrequency::setSize(int size) {
    mSize = size;
    if(mHz != 0)
        createFrequency(mHz, mSize);
}

const std::vector<float> &EpnyFrequency::getSource() const {
    return mSource;
}

void EpnyFrequency::setSource(const std::vector<float> &source) {
    mSource = source;
}


