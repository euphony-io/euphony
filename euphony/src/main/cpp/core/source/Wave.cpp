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
    oscillate();
}

Euphony::Wave::Wave(const Wave& copy)
: mHz(copy.mHz),
mSize(copy.mSize)
{
    oscillate();
}

WaveBuilder Euphony::Wave::create() {
    return WaveBuilder();
}

void Euphony::Wave::updatePhaseIncrement(int hz) {
    mPhaseIncrement.store((kTwoPi * hz) / static_cast<double>(kSampleRate));
}

void Euphony::Wave::oscillate() {
    if(this->mHz > 0 && this->mSize > 0) {
        updatePhaseIncrement(this->mHz);

        float phase = 0.0;

        for(int i = 0; i < this->mSize; ++i) {
            mSource.push_back(sin(phase));
            phase += mPhaseIncrement;
            if(phase > kTwoPi) phase -= kTwoPi;
        }
    }
}

void Euphony::Wave::oscillate(int hz, int size) {
    this->setHz(hz);
    this->setSize(size);
    this->oscillate();
}

int Euphony::Wave::getHz() const {
    return mHz;
}

void Euphony::Wave::setHz(int hz) {
    mHz = hz;
    this->updatePhaseIncrement(hz);
}

int Euphony::Wave::getSize() const {
    return mSize;
}

void Euphony::Wave::setSize(int size) {
    mSize = size;
    mSource.reserve(size);
}

std::vector<float> Euphony::Wave::getSource() const {
    std::vector<float> result;
    result.reserve(mSource.capacity());
    result.assign(mSource.begin(), mSource.end());
    return result;
}

std::vector<int16_t> Euphony::Wave::getInt16Source() {
    std::vector<int16_t> result;

    if(mSource.empty()) {
        return result;
    }

    result.reserve(mSource.size());

    for(int i = 0; i < mSource.size(); i++) {
        result.push_back(convertFloat2Int16(mSource[i]));
    }

    return result;
}

void Euphony::Wave::setSource(const std::vector<float> &source) {
    mSource = source;
}

int16_t Euphony::Wave::convertFloat2Int16(float source) {
    return static_cast<float>(SHRT_MAX) * source;
}