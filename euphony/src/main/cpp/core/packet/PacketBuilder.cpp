#include <charset/ASCIICharset.h>
#include <Definitions.h>
#include "PacketBuilder.h"

using namespace Euphony;

PacketBuilder &Euphony::PacketBuilder::setPayload(const HexVector &source) {
    packet.setPayload(source);
    return *this;
}

std::shared_ptr <Packet> Euphony::PacketBuilder::build() {
    return std::make_shared<Packet>(packet);
}

PacketBuilder &PacketBuilder::basedOnBase16() {
    packet.setBaseType(BaseType::BASE16);
    return *this;
}

PacketBuilder &PacketBuilder::basedOnBase2() {
    packet.setBaseType(BaseType::BASE2);
    return *this;
}

PacketBuilder &PacketBuilder::setPayloadWithASCII(std::string asciiSrc) {
    packet.setPayload(HexVector(ASCIICharset().encode(asciiSrc)));
    return *this;
}