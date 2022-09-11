#include "PacketErrorDetector.h"
#include <sstream>

using namespace Euphony;

std::string PacketErrorDetector::makeParityAndChecksum(const HexVector& payload) {
    char hexArray[16] = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
                         'a', 'b', 'c', 'd', 'e', 'f'};

    int evenParity[4] = {0,};
    int payloadSum = 0;

    for(int v : payload) {
        evenParity[0] += ((0x8 & v) >> 3);
        evenParity[1] += ((0x4 & v) >> 2);
        evenParity[2] += ((0x2 & v) >> 1);
        evenParity[3] += (0x1 & v);
        payloadSum += v;
    }

    payloadSum &= 0xF;
    payloadSum = (~payloadSum + 1) & 0xF;
    int evenParityResult =
            (evenParity[0] & 0x1) * 8 + (evenParity[1] & 0x1) * 4 +
            (evenParity[2] & 0x1) * 2 + (evenParity[3] & 0x1);

    std::stringstream result;
    result << hexArray[payloadSum] << hexArray[evenParityResult];

    return result.str();
}

std::string PacketErrorDetector::makeParityAndChecksum(std::string payload) {
    HexVector hexVector = HexVector(payload);
    return makeParityAndChecksum(hexVector);
}

HexVector PacketErrorDetector::makeChecksum(const HexVector& payload) {
    int payloadSum = 0;

    for(int v : payload) {
        payloadSum += v;
    }

    payloadSum &= 0xF;
    payloadSum = (~payloadSum + 1) & 0xF;

    HexVector result(1);
    result.pushBack(payloadSum);

    return result;
}

HexVector PacketErrorDetector::makeParallelParity(const HexVector& payload) {
    int evenParity[4] = {0,};

    for(int v : payload) {
        evenParity[0] += ((0x8 & v) >> 3);
        evenParity[1] += ((0x4 & v) >> 2);
        evenParity[2] += ((0x2 & v) >> 1);
        evenParity[3] += (0x1 & v);
    }

    int evenParityResult =
            (evenParity[0] & 0x1) * 8 + (evenParity[1] & 0x1) * 4 +
            (evenParity[2] & 0x1) * 2 + (evenParity[3] & 0x1);


    HexVector result(1);
    result.pushBack(evenParityResult);

    return result;
}

bool PacketErrorDetector::verifyChecksum(const HexVector& payload, int checksum) {
    HexVector checksumResult = makeChecksum(payload);

    if(checksumResult.getHexSource()[0] != checksum)
        return false;
    else
        return true;
}

bool PacketErrorDetector::verifyParallelParity(const HexVector& payload, int parity) {
    HexVector parityResult = makeParallelParity(payload);

    if(parityResult.getHexSource()[0] != parity)
        return false;
    else
        return true;
}
