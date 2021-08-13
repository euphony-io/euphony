//
// Created by designe on 20. 9. 16.
//

#include "../Definitions.h"
#include "../Wave.h"
#include "../WaveBuilder.h"

using namespace Euphony;

Euphony::Wave::Wave()
:mHz(0),
mSize(0),
crossfadeType(NONE)
{}

Euphony::Wave::Wave(int hz, int size)
:mHz(hz),
mSize(size),
crossfadeType(NONE)
{
    oscillate();
}

Euphony::Wave::Wave(const Wave& copy)
: mHz(copy.mHz),
mSize(copy.mSize),
crossfadeType(copy.crossfadeType)
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

        if(crossfadeType != NONE) {
            for (int i = 0; i < kBufferFadeLength; ++i) {
                float miniWindow = static_cast<float>(i) / static_cast<float>(kBufferFadeLength);
                switch (crossfadeType) {
                    case BOTH:
                        mSource[i] *= miniWindow;
                        mSource[this->mSize - 1 - i] *= miniWindow;
                        break;
                    case END:
                        mSource[this->mSize - 1 - i] *= miniWindow;
                        break;
                    case FRONT:
                        mSource[i] *= miniWindow;
                        break;
                    default:
                        continue;
                }
            }
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

void Wave::setCrossfade(CrossfadeType crossfadeType) {
    this->crossfadeType = crossfadeType;
}
