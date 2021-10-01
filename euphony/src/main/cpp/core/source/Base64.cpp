#include "../Base64.h"
#include <iomanip>
#include <sstream>
#include <cmath>

using namespace Euphony;

Base64::Base64(const HexVector &hexVectorSrc)
: hexVector(hexVectorSrc) {}

std::string Base64::getBaseString() {
    std::stringstream ss;
    int sum = 0;
    int rest = hexVector.getSize() % 3;

    int count = 0;
    for(u_int8_t hex : hexVector) {
        count++;
        sum = (sum << 4) | hex;
        if(count % 3 == rest) {
            ss << bitsToBase64(sum);
            sum = 0;
        }
    }

    return ss.str();
}

int Euphony::Base64::convertChar2Int(char source) const {
    switch(source) {
        case 'A': case 'B': case 'C':
        case 'D': case 'E': case 'F':
        case 'G': case 'H': case 'I':
        case 'J': case 'K': case 'L':
        case 'M': case 'N': case 'O':
        case 'P': case 'Q': case 'R':
        case 'S': case 'T': case 'U':
        case 'V': case 'W': case 'X':
        case 'Y': case 'Z':
            return source - 'A';
        case 'a': case 'b': case 'c':
        case 'd': case 'e': case 'f':
        case 'g': case 'h': case 'i':
        case 'j': case 'k': case 'l':
        case 'm': case 'n': case 'o':
        case 'p': case 'q': case 'r':
        case 's': case 't': case 'u':
        case 'v': case 'w': case 'x':
        case 'y': case 'z':
            return source - 'a' + 26;
        case '0': case '1': case '2':
        case '3': case '4': case '5':
        case '6': case '7': case '8':
        case '9':
            return source - '0' + 52;
        case '+': return 62;
        case '/': return 63;
        default:
            throw Base64Exception();
    }
}

char Base64::convertInt2Char(int source) const {
    const char base64Array[64] = {'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M',
                                  'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z',
                                  'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm',
                                  'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z',
                                  '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '+', '/'};

    return base64Array[source];
}

const Euphony::HexVector &Euphony::Base64::getHexVector() const {
    return hexVector;
}

std::string Base64::bitsToBase64(int value){
    std::string result;
    int count = 0;
    int base64;

    for (int i = 0; i < 12; i += 6) {
        if ((value >> i) > 0) count = i / 6;
    }

    for (int i = count * 6; i >= 0; i -= 6) {
        base64 = ((value >> i) & 63);
        result.push_back(convertInt2Char(base64));
    }

    return result;
}