#include "../UTF8Charset.h"

#include <iostream>
#include <string>

using namespace Euphony;

HexVector UTF8Charset::encode(std::string src) {
    HexVector result = HexVector(src.size());

    for (int i = 0; i < src.size(); i++)
        result.pushBack(src[i]);

    return result;
}

std::string UTF8Charset::decode(const HexVector& src) {
    std::string result;
    std::vector<u_int8_t> hexSource = src.getHexSource();

    for (int i = 0; i < hexSource.size() / 2; i++)
        result += (hexSource[2 * i] << 4) | hexSource[2 * i + 1];

    return result;
}

