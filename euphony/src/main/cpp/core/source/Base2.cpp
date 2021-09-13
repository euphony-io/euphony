#include "../Base2.h"
#include <sstream>

using namespace Euphony;

Base2::Base2(const HexVector &hexVectorSrc)
: hexVector(hexVectorSrc) { }

std::string Base2::getBaseString() {
    std::stringstream ss;

    for(u_int8_t hex : hexVector) {
        ss << hexToBase2(hex);
    }

    return ss.str();
}

const HexVector &Base2::getHexVector() const {
    return hexVector;
}

int Base2::convertChar2Int(char source) const {
    return 0;
}

char Base2::convertInt2Char(int source) const {
    return 0;
}

std::string Base2::hexToBase2(u_int8_t hex) {
    std::string result;

    result.push_back(((hex & 0x8) >> 3) ? '1' : '0');
    result.push_back(((hex & 0x4) >> 2) ? '1' : '0');
    result.push_back(((hex & 0x2) >> 1) ? '1' : '0');
    result.push_back((hex & 0x1) ? '1' : '0');

    return result;
}

