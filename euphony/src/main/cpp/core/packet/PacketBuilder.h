#ifndef EUPHONY_PACKETBUILDER_H
#define EUPHONY_PACKETBUILDER_H

#include "Packet.h"

namespace Euphony {
    class PacketBuilder {
    public:
        PacketBuilder& setPayload(const HexVector& source);
        PacketBuilder& setPayloadWithASCII(std::string asciiSrc);
        PacketBuilder& basedOnBase16();
        PacketBuilder& basedOnBase2();
        std::shared_ptr<Packet> build();

    private:
        Packet packet;
    };
}
#endif //EUPHONY_PACKETBUILDER_H
