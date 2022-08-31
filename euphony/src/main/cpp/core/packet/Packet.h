#ifndef EUPHONY_PACKET_H
#define EUPHONY_PACKET_H

#include "Definitions.h"
#include "base/Base.h"
#include <string>

namespace Euphony {
    class PacketBuilder;

    class Packet {
    public:
        Packet();
        Packet(const HexVector& source);
        Packet(const BaseType type, const HexVector& source);
        static PacketBuilder create();
        void clear();

        std::shared_ptr<Base> getChecksum();
        std::shared_ptr<Base> getParityCode();
        std::string getPayloadStr() const;
        void setPayload(std::shared_ptr<Base> payloadSrc);
        void setPayload(const HexVector& source);
        BaseType getBaseType() const;
        void setBaseType(BaseType baseTypeSrc);

        std::string toString();

    private:
        friend class PacketBuilder;
        BaseType baseType;
        std::shared_ptr<Base> payload;
        std::shared_ptr<Base> checksum;
        std::shared_ptr<Base> parityCode;
        bool isVerified;
        void initialize();
    };
}

#endif //EUPHONY_PACKET_H
