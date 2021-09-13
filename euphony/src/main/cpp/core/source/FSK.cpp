#include "../FSK.h"
#include "../FFTProcessor.h"
#include "../Definitions.h"
#include "../WaveBuilder.h"
#include "../Base16Exception.h"
#include "../Packet.h"
#include <cmath>

using namespace Euphony;


FSK::FSK() : fftModel(std::make_unique<FFTProcessor>(kFFTSize, kSampleRate)){ }

WaveList FSK::modulate(Packet* packet)
{
    return this->modulate(packet->toString());
}

WaveList FSK::modulate(string code) {

    vector<shared_ptr<Wave>> result;

    for (char c : code ) {
        switch(c) {
            case 'S':
                result.push_back(
                        Wave::create()
                                .vibratesAt(kStartSignalFrequency)
                                .setSize(kBufferSize)
                                .setCrossfade(BOTH)
                                .build()
                );
                break;
            case '0': case '1': case '2':
            case '3': case '4': case '5':
            case '6': case '7': case '8':
            case '9':
                result.push_back(
                        Wave::create()
                                .vibratesAt(kStandardFrequency + ((c - '0') * kFrequencyInterval))
                                .setSize(kBufferSize)
                                .setCrossfade(BOTH)
                                .build()
                );
                break;
            case 'a': case 'b': case 'c':
            case 'd': case 'e': case 'f':
                result.push_back(
                        Wave::create()
                                .vibratesAt(kStandardFrequency + ((c - 'a' + 10) * kFrequencyInterval))
                                .setSize(kBufferSize)
                                .setCrossfade(BOTH)
                                .build()
                );
                break;
            default:
                throw Base16Exception();
        }
    }

    return result;
}

int FSK::getMaxIdxFromSource(const float *fft_source) {
    int maxIndex = 0;
    float maxValue = 0;
    const int startIdx = getStartFreqIdx();
    const int endIdx = getEndFreqIdx();
    for(int i = startIdx - 1; i < endIdx; i++) {
        if(fft_source[i] > maxValue) {
            maxValue = fft_source[i];
            maxIndex = i;
        }
    }

    return maxIndex - getStartFreqIdx();
}


int FSK::getMaxIdxFromSource(const float *fft_source, const int baseSize, const int sampleRate, const int fftSize) {
    int maxIndex = 0;
    float maxValue = 0;
    const int startIdx = getStartFreqIdx(sampleRate, fftSize);
    const int endIdx = startIdx + baseSize;
    for(int i = startIdx - 1; i < endIdx; i++) {
        if(fft_source[i] > maxValue) {
            maxValue = fft_source[i];
            maxIndex = i;
        }
    }

    return maxIndex - getStartFreqIdx();
}


shared_ptr<Packet> FSK::demodulate(const WaveList& waveList) {
    HexVector hexVector = HexVector(waveList.size());
    
    for(const auto& wave : waveList) {
        auto vectorInt16Source = wave->getInt16Source();
        int16_t* int16Source = &vectorInt16Source[0];
        float *resultBuf = fftModel->makeSpectrum(int16Source);
        hexVector.pushBack(getMaxIdxFromSource(resultBuf));
    }

    return std::make_shared<Packet>(hexVector);
}

std::shared_ptr<Packet> FSK::demodulate(const float *source, int sourceLength, int bufferSize) {
    int dataSize = sourceLength / bufferSize;
    HexVector hexVector = HexVector(dataSize);

    WaveList waveList;
    for(int i = 0; i < dataSize; i++) {
        waveList.push_back(std::make_shared<Wave>(source + (i * bufferSize), bufferSize));
    }

    return demodulate(waveList);
}

int FSK::getStartFreqIdx() {
    return std::lround((static_cast<float>(static_cast<float>(kStandardFrequency) / static_cast<float>(kSampleRate >> 1)) * (kFFTSize >> 1)));
}

int FSK::getStartFreqIdx(const int sampleRate, const int fftSize) {
    return std::lround((static_cast<float>(static_cast<float>(kStandardFrequency) / static_cast<float>(sampleRate >> 1)) * (fftSize >> 1)));
}

int FSK::getEndFreqIdx() {
    return std::lround((static_cast<float>(static_cast<float>(kStandardFrequency) / static_cast<float>(kSampleRate >> 1)) * (kFFTSize >> 1))) + 16;
}

int FSK::getEndFreqIdx(const int sampleRate, const int fftSize) {
    return std::lround((static_cast<float>(static_cast<float>(kStandardFrequency) / static_cast<float>(sampleRate >> 1)) * (fftSize >> 1))) + 16;
}
