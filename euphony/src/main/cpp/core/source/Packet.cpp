
#include "../Packet.h"
#include "../PacketErrorDetector.h"
#include <sstream>
using namespace Euphony;

string Packet::create(string source) {
    std::stringstream result;

    string encodedCode = base16.encode(source);
    for(char& c : encodedCode) {
        payload.push_back(base16.convertChar2Int(c));
    }

    string errorCode = makeErrorDetectorCode();

    result << "S" << encodedCode << errorCode;
    return result.str();
}

void Packet::push(int data) {
    payload.push_back(data);
}

void Packet::clear() {
    this->checksum = -1;
    this->parityCode = -1;
    this->isVerified = false;
    (this->payload).clear();
}

string Packet::makeErrorDetectorCode() {
    string errorCode = PacketErrorDetector::makeParityAndChecksum(payload);
    this->checksum = Base16::convertChar2Int(errorCode.at(0));
    this->parityCode = Base16::convertChar2Int(errorCode.at(1));
    this->isVerified = true;

    return errorCode;
}


string Packet::getStrPayload() {
    std::stringstream result;

    for(auto data : payload) {
        result << std::hex << data;
    }

    return result.str();
}

string Packet::toString() {
    std::stringstream result;

    result << "S";
    for(auto data : payload) {
        result << std::hex << data;
    }
    result << makeErrorDetectorCode();

    return result.str();
}



