#ifndef EUPHONY_ASK_H
#define EUPHONY_ASK_H

#include "FFTModel.h"
#include "Modem.h"
#include <string>
#include <vector>

namespace Euphony {

    class ASK : public Modem {
    public:
        ASK();
        WaveList modulate(std::string code);
        WaveList modulate(Packet* packet);
        std::shared_ptr<Packet> demodulate(const WaveList& waveList);
        std::shared_ptr<Packet> demodulate(const float* source, int sourceLength, int bufferSize);

    private:
        std::unique_ptr<FFTModel> fftModel;
        static int getStartFreqIdx() ;
    };
}
#endif //EUPHONY_ASK_H