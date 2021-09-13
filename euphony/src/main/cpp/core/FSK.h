#ifndef EUPHONY_FSK_H
#define EUPHONY_FSK_H

#include "FFTModel.h"
#include "Modem.h"
#include <string>
#include <vector>

namespace Euphony {
    class FSK : public Modem {
    public:
        FSK();
        WaveList modulate(std::string code);
        WaveList modulate(Packet* packet);
        static int getMaxIdxFromSource(const float* fft_source);
        static int getMaxIdxFromSource(const float* fft_source, const int baseSize, const int sampleRate, const int fftSize);
        std::shared_ptr<Packet> demodulate(const WaveList& waveList);
        std::shared_ptr<Packet> demodulate(const float* source, int sourceLength, int bufferSize);
    private:
        std::unique_ptr<FFTModel> fftModel;
        static int getStartFreqIdx() ;
        static int getEndFreqIdx() ;
        static int getStartFreqIdx(const int sampleRate, const int fftSize);
        static int getEndFreqIdx(const int sampleRate, const int fftSize);
    };
}
#endif //EUPHONY_FSK_H
