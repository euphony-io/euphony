#include "../PacketErrorDetector.h"
#include "../Base16.h"
#include <sstream>

string Euphony::PacketErrorDetector::makeParityAndChecksum(vector<int> payload) {
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

string Euphony::PacketErrorDetector::makeParityAndChecksum(string payload) {
    std::vector<int> intPayload;
    intPayload.reserve(payload.size());

    for(char& c : payload) {
        intPayload.push_back(Base16().convertChar2Int(c));
    }

    return makeParityAndChecksum(intPayload);
}

int Euphony::PacketErrorDetector::makeChecksum(vector<int> payload) {
    int payloadSum = 0;

    for(int v : payload) {
        payloadSum += v;
    }

    payloadSum &= 0xF;
    payloadSum = (~payloadSum + 1) & 0xF;

    return payloadSum;
}

int Euphony::PacketErrorDetector::makeParallelParity(vector<int> payload) {
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

    return evenParityResult;
}

bool Euphony::PacketErrorDetector::verifyChecksum(vector<int> payload, int checksum) {
    int checksumResult = makeChecksum(payload);

    if(checksumResult != checksum)
        return false;
    else
        return true;
}

bool Euphony::PacketErrorDetector::verifyParallelParity(vector<int> payload, int parity) {
    int parityResult = makeParallelParity(payload);

    if(parityResult != parity)
        return false;
    else
        return true;
}
