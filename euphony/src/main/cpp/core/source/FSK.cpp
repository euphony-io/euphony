#include "../Definitions.h"
#include "../FSK.h"
#include "../WaveBuilder.h"
#include "../Base16Exception.h"

using namespace Euphony;
using std::string;
using std::vector;
using std::make_shared;

vector<shared_ptr<Wave>> FSK::modulate(string code) {

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

int FSK::demodulate(const float *source, const int size) {
    int maxIndex = 0;
    float maxValue = 0;
    for(int i = getStartFreqIdx() - 1; i < getEndFreqIdx(); i++) {
        if(source[i] > maxValue) {
            maxValue = source[i];
            maxIndex = i;
        }
    }

    return maxIndex - getStartFreqIdx();
}

const int FSK::getStartFreqIdx() const {
    return static_cast<int>(static_cast<float>(static_cast<float>(kStandardFrequency) / static_cast<float>(kSampleRate >> 1)) * (kFFTSize >> 1));

}

const int FSK::getEndFreqIdx() const {
    return static_cast<int>(static_cast<float>(static_cast<float>(kStandardFrequency) / static_cast<float>(kSampleRate >> 1)) * (kFFTSize >> 1)) + 16;
}

