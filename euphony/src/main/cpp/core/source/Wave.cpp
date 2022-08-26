//
// Created by designe on 20. 9. 16.
//

#include "../Definitions.h"
#include "../Wave.h"
#include "../WaveBuilder.h"

using namespace Euphony;

Wave::Wave()
: mHz(0)
, mSize(0)
, mAmplitude(1)
, sampleRate(kSampleRate)
, crossfadeType(NONE)
{}

Wave::Wave(int hz, int bufferSize)
        : mHz(hz)
        , mSize(bufferSize)
        , mAmplitude(1)
        , sampleRate(kSampleRate)
        , crossfadeType(NONE)
{
    oscillate();
}

Wave::Wave(int hz, int bufferSize, int sampleRate)
: mHz(hz)
, mSize(bufferSize)
, mAmplitude(1)
, sampleRate(sampleRate)
, crossfadeType(NONE)
{
    oscillate();
}

Wave::Wave(const float *src, int bufferSize)
        : mHz(0)
        , mSize(bufferSize)
        , mAmplitude(1)
        , sampleRate(kSampleRate)
        , crossfadeType(NONE)
{
    for(int i = 0; i < bufferSize; ++i) {
        mSource.push_back(src[i]);
    }
}

Wave::Wave(const float *src, int bufferSize, int sampleRate)
: mHz(0)
, mSize(bufferSize)
, mAmplitude(1)
, sampleRate(sampleRate)
, crossfadeType(NONE)
{
    for(int i = 0; i < bufferSize; ++i) {
        mSource.push_back(src[i]);
    }
}

Wave::Wave(const Wave& copy)
: mHz(copy.mHz)
, mSize(copy.mSize)
, mAmplitude(copy.mAmplitude)
, sampleRate(copy.sampleRate)
, crossfadeType(copy.crossfadeType)
{
    oscillate();
}

WaveBuilder Euphony::Wave::create() {
    return WaveBuilder();
}

void Wave::updatePhaseIncrement(int hz) {
    mPhaseIncrement.store((kTwoPi * hz) / static_cast<double>(sampleRate));
}

void Wave::oscillate() {
    if(this->mHz > 0 && this->mSize > 0) {
        updatePhaseIncrement(this->mHz);

        float phase = 0.0;

        for(int i = 0; i < this->mSize; ++i) {
            mSource.push_back(sin(phase) * mAmplitude);
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

void Wave::oscillate(int hz, int size) {
    this->setHz(hz);
    this->setSize(size);
    this->setAmplitude(1);
    this->oscillate();
}

int Wave::getHz() const {
    return mHz;
}

void Wave::setHz(int hz) {
    mHz = hz;
}

int Wave::getSize() const {
    return mSize;
}

void Wave::setSize(int size) {
    mSize = size;
    mSource.reserve(size);
}

float Euphony::Wave::getAmplitude() const {
    return mAmplitude;
}

void Euphony::Wave::setAmplitude(float amplitude) {
    mAmplitude = amplitude;
}

int Wave::getSampleRate() const {
    return sampleRate;
}

void Wave::setSampleRate(int sampleRate) {
    this->sampleRate = sampleRate;
}


std::vector<float> Wave::getSource() const {
    std::vector<float> result;
    result.reserve(mSource.capacity());
    result.assign(mSource.begin(), mSource.end());
    return result;
}

std::vector<int16_t> Wave::getInt16Source() {
    std::vector<int16_t> result;

    if(mSource.empty()) {
        return result;
    }

    result.reserve(mSource.size());

    for(float src : mSource) {
        result.push_back(convertFloat2Int16(src));
    }

    return result;
}

void Wave::setSource(const std::vector<float> &source) {
    mSource = source;
}

int16_t Wave::convertFloat2Int16(float source) {
    return static_cast<float>(SHRT_MAX) * source;
}

void Wave::setCrossfade(CrossfadeType crossfadeType) {
    this->crossfadeType = crossfadeType;
}
