#include "../ASK.h"
#include "../FFTProcessor.h"
#include "../Definitions.h"
#include "../WaveBuilder.h"
#include "../Base16Exception.h"
#include "../Packet.h"
#include <cmath>

using namespace Euphony;

ASK::ASK() : fftModel(std::make_unique<FFTProcessor>(kFFTSize, kSampleRate)) {}

WaveList ASK::modulate(Packet *packet) {
    return this->modulate(packet->toString());
}

WaveList ASK::modulate(std::string code) {

    vector<shared_ptr<Wave>> result;

    for (char c : code) {
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
            case '0': case '1':
                result.push_back(
                        Wave::create()
                                .vibratesAt(kStandardFrequency)
                                .setSize(kBufferSize)
                                .setCrossfade(BOTH)
                                .setAmplitude(c - '0')
                                .build()
                );
                break;
            default:
                throw std::runtime_error("Exception from ASK. Please make Base2Exception.\n");
        }
    }

    return result;
}

shared_ptr<Packet> ASK::demodulate(const WaveList& waveList) {
    HexVector hexVector = HexVector(waveList.size());
    const float threshold = 0.009;
    const int startIdx = getStartFreqIdx();

    for(const auto& wave : waveList) {
        auto vectorFloatSource = wave->getSource();
        float* floatSource = &vectorFloatSource[0];
        auto spectrums = fftModel->makeSpectrum(floatSource);
        float *resultBuf = spectrums.amplitudeSpectrum;
        if(resultBuf[startIdx] > threshold)
            hexVector.pushBack(1);
        else
            hexVector.pushBack(0);
    }

    return std::make_shared<Packet>(hexVector);
}


std::shared_ptr<Packet>
ASK::demodulate(const float *source, int sourceLength, int bufferSize) {
    int dataSize = sourceLength / bufferSize;
    HexVector hexVector = HexVector(dataSize);

    WaveList waveList;
    for(int i=0; i<dataSize; i++){
        waveList.push_back(std::make_shared<Wave>(source + (i * bufferSize), bufferSize));
    }
    return demodulate(waveList);
}

int ASK::getStartFreqIdx() {
    return std::lround((static_cast<float>(static_cast<float>(kStandardFrequency) / static_cast<float>(kSampleRate >> 1)) * (kFFTSize >> 1)));
}