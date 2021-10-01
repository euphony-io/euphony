#include "../Base32.h"
#include <iomanip>
#include <sstream>
#include <cmath>

using namespace Euphony;

Base32::Base32(const HexVector &hexVectorSrc)
: hexVector(hexVectorSrc) {}

std::string Base32::getBaseString() {
    int sum = 0;

    for(u_int8_t hex : hexVector) {
        sum = (sum << 4) | hex;
    }

    return decToBase32(sum);
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

std::string Base32::decToBase32(int sum){
    std::string result;
    int count = 0;
    int base32;

    for (int i = 0; i < 32; i += 5) {
        if ((sum >> i) > 0) count = i / 5;
    }

    for (int i = count * 5; i >= 0; i -= 5) {
        base32 = ((sum >> i) & 31);
        if (base32 < 10) result.push_back(base32 + '0');
        else result.push_back(base32 - 10 + 'a');
    }

    return result;
}