#include "UTF32Charset.h"
#include <bitset>
#include <codecvt>
#include <iomanip>

using namespace Euphony;

HexVector UTF32Charset::encode(std::string src) {
    std::wstring_convert<std::codecvt_utf8<char32_t>, char32_t> convert;
    std::u32string utf32s = convert.from_bytes(src);
    HexVector result = HexVector(2 * utf32s.size());

    for (char32_t ch : utf32s) {
        std::vector<u_int8_t> halfByteBuffer;
        for(int hexIdx = 0; hexIdx < HEX_NUM_COUNT; hexIdx++) {
            halfByteBuffer.push_back(ch & BIT_MASK);
            ch >>= BIT_COUNT_IN_HEX_NUM;
        }
        for (auto it = halfByteBuffer.rbegin(); it != halfByteBuffer.rend(); it++) {
            result.pushBack(*it);
        }
    }

    return result;
}

std::string UTF32Charset::decode(const HexVector &src) {
    std::wstring_convert<std::codecvt_utf8<char32_t>, char32_t> convert;
    std::u32string utf32s = U"";
    std::string result = "";
    std::vector<u_int8_t> hexSource = src.getHexSource();

    for (int hexIdx = 0; hexIdx < hexSource.size(); hexIdx+=HEX_NUM_COUNT) {
        char32_t ch = hexSource[hexIdx];
        for(int offset = 1; offset < HEX_NUM_COUNT; offset++)
            ch = ( ch << BIT_COUNT_IN_HEX_NUM ) | hexSource[hexIdx + offset];
        utf32s += ch;
    }

    result = convert.to_bytes(utf32s);

    return result;
}