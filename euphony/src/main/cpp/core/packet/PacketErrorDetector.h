#ifndef EUPHONY_PACKETERRORDETECTOR_H
#define EUPHONY_PACKETERRORDETECTOR_H

#include <vector>
#include <string>
#include "HexVector.h"

namespace Euphony {

    class PacketErrorDetector {
    public:
        static std::string makeParityAndChecksum(const HexVector& payload);
        static std::string makeParityAndChecksum(std::string payloadStr);
        static HexVector makeChecksum(const HexVector& payload);
        static HexVector makeParallelParity(const HexVector& payload);
        static bool verifyChecksum(const HexVector& payload, int checksum);
        static bool verifyParallelParity(const HexVector& payload, int parity);
    };
}
#endif //EUPHONY_PACKETERRORDETECTOR_H
