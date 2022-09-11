#include "Packet.h"
#include "PacketBuilder.h"
#include "PacketErrorDetector.h"
#include <base/BaseFactory.h>
#include <sstream>
using namespace Euphony;


Packet::Packet()
        : baseType(BaseType::BASE16),
          payload(nullptr),
          checksum(nullptr),
          parityCode(nullptr),
          isVerified(false) {
}

Packet::Packet(const HexVector& source)
        : baseType(BaseType::BASE16),
          payload(nullptr),
          checksum(nullptr),
          parityCode(nullptr),
          isVerified(false) {
    payload = BaseFactory::create(baseType, source);
    initialize();
}

Packet::Packet(const BaseType baseTypeSrc, const HexVector& source)
: baseType(baseTypeSrc),
  payload(nullptr),
  checksum(nullptr),
  parityCode(nullptr),
  isVerified(false) {
    payload = BaseFactory::create(baseType, source);
    initialize();
}


PacketBuilder Packet::create() {
    return PacketBuilder();
}

void Packet::initialize() {
    //std::string errorCode = PacketErrorDetector::makeParityAndChecksum(payload->getBaseString());
    //this->checksum = BaseFactory::create(baseType, )
    this->checksum = BaseFactory::create(
            baseType,
            PacketErrorDetector::makeChecksum(payload->getHexVector())
            );
    this->parityCode = BaseFactory::create(
            baseType,
            PacketErrorDetector::makeParallelParity(payload->getHexVector())
            );

    this->isVerified = true;
}

void Packet::clear() {
    this->checksum = nullptr;
    this->parityCode = nullptr;
    this->isVerified = false;

    payload = nullptr;
}

std::string Packet::getPayloadStr() const {
    return payload->getBaseString();
}

std::string Packet::toString() {
    std::stringstream result;

    result << "S" <<
    payload->getBaseString() <<
    checksum->getBaseString() <<
    parityCode->getBaseString();

    return result.str();
}

void Packet::setPayload(std::shared_ptr<Base> payloadSrc) {
    payload = std::move(payloadSrc);
    initialize();
}

void Packet::setPayload(const HexVector &source) {
    payload = BaseFactory::create(baseType, source);
    initialize();
}

std::shared_ptr<Base> Packet::getChecksum() {
    return checksum;
}

std::shared_ptr<Base> Packet::getParityCode() {
    return parityCode;
}

BaseType Packet::getBaseType() const {
    return baseType;
}

void Packet::setBaseType(BaseType baseTypeSrc) {
    baseType = baseTypeSrc;
    if(payload != nullptr) {
        payload = BaseFactory::create(baseType, payload->getHexVector());
        initialize();
    }
}
