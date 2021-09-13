#include "../Base16.h"
#include <iomanip>

using namespace Euphony;

Base16::Base16(const HexVector &hexVectorSrc)
: hexVector(hexVectorSrc) { }

std::string Base16::getBaseString() {
    return hexVector.toString();
}

int Euphony::Base16::convertChar2Int(char source) const {
    switch(source) {
        case '0': case '1': case '2':
        case '3': case '4': case '5':
        case '6': case '7': case '8':
        case '9':
            return source - '0';
        case 'a': case 'b': case 'c':
        case 'd': case 'e': case 'f':
            return source - 'a' + 10;
        default:
            throw Base16Exception();
    }
}

char Base16::convertInt2Char(int source) const {
    const char hexArray[16] = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
                         'a', 'b', 'c', 'd', 'e', 'f'};

    return hexArray[source];
}

const Euphony::HexVector &Euphony::Base16::getHexVector() const {
    return hexVector;
}




