#ifndef EUPHONY_MODEM_H
#define EUPHONY_MODEM_H

#include <string>
#include <Definitions.h>
#include <packet/Packet.h>
#include <wave/Wave.h>

using std::string;
using std::vector;
using std::shared_ptr;

namespace Euphony {

    class Modem {
    public:
        virtual ~Modem() = default;
        virtual WaveList modulate(string code) = 0;
        virtual WaveList modulate(Packet* packet) = 0;
        virtual std::shared_ptr<Packet> demodulate(const WaveList& waveList) = 0;
        virtual std::shared_ptr<Packet> demodulate(const float* source, int sourceLength, int bufferSize) = 0;
    };
}

#endif //EUPHONY_MODEM_H
