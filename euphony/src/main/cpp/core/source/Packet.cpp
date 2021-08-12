
#include "../Packet.h"
#include "../Base16.h"
#include "../PacketErrorDetector.h"
#include <sstream>
using namespace Euphony;

string Packet::create(string code) {
    std::stringstream result;
    Base16 base16 = Base16();

    string encodedCode = base16.encode(code);
    for(char& c : encodedCode) {
        payload.push_back(base16.convertChar2Int(c));
    }

    string errorCode = PacketErrorDetector::makeParityAndChecksum(payload);
    this->checksum = base16.convertChar2Int(errorCode.at(0));
    this->parityCode = base16.convertChar2Int(errorCode.at(1));
    this->isVerified = true;

    result << "S" << encodedCode << errorCode;
    return result.str();
}

void Packet::clear() {
    this->checksum = -1;
    this->parityCode = -1;
    this->isVerified = false;
    (this->payload).clear();
}


