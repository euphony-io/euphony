#include "../Base32.h"
#include <iomanip>
#include <sstream>
#include <cmath>

using namespace Euphony;

Base32::Base32(const HexVector &hexVectorSrc)
: hexVector(hexVectorSrc) {}

std::string Base32::getBaseString() {
    std::stringstream ss;
    int sum = 0;
    const int bitDivisionUnit = 5;
    int rest = hexVector.getSize() % bitDivisionUnit;
    if(!rest) rest = bitDivisionUnit;

    int count = 0;
    for(u_int8_t hex : hexVector) {
        sum = (sum << 4) | hex;
        if(++count == rest) {
            ss << bitsToBase32(sum);
            rest += bitDivisionUnit;
            sum = 0;
        }
    }

    return ss.str();
}

int Euphony::Base32::convertChar2Int(char source) const {
    switch(source) {
        case '0': case '1': case '2':
        case '3': case '4': case '5':
        case '6': case '7': case '8':
        case '9':
            return source - '0';
        case 'a': case 'b': case 'c':
        case 'd': case 'e': case 'f':
        case 'g': case 'h': case 'i':
        case 'j': case 'k': case 'l':
        case 'm': case 'n': case 'o':
        case 'p': case 'q': case 'r':
        case 's': case 't': case 'u':
        case 'v':
            return source - 'a' + 10;
        default:
            throw Base32Exception();
    }
}

char Base32::convertInt2Char(int source) const {
    const char base32Array[32] = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
                               'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j',
                               'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't',
                               'u', 'v'};

    return base32Array[source];
}

const Euphony::HexVector &Euphony::Base32::getHexVector() const {
    return hexVector;
}

std::string Base32::bitsToBase32(int value){
    std::string result;
    if(!value)result = "0000";
    while(value != 0) {
        result = convertInt2Char(value & 0x1f) + result;
        value >>= 5;
    }
    return result;
}