#include "../DefaultCharset.h"
using namespace Euphony;

HexVector DefaultCharset::encode(std::string src) {
    return HexVector(src);
}

std::string DefaultCharset::decode(const HexVector& src) {
    return src.toString();
}
