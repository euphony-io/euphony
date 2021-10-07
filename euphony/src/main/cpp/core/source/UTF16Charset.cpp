#include "../UTF16Charset.h"
#include <codecvt>
#include <iomanip>

using namespace Euphony;

HexVector UTF16Charset::encode(std::string src) {
    std::wstring_convert<std::codecvt_utf8_utf16<char16_t>, char16_t> convert;
    std::u16string utf16s = convert.from_bytes(src);
    HexVector result = HexVector(2 * utf16s.size());

    for (char16_t ch : utf16s) {
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

std::string UTF16Charset::decode(const HexVector &src) {
    std::wstring_convert<std::codecvt_utf8_utf16<char16_t>, char16_t> convert;
    std::u16string utf16s = u"";
    std::string result = "";
    std::vector<u_int8_t> hexSource = src.getHexSource();

    for (int hexIdx = 0; hexIdx < hexSource.size(); hexIdx += HEX_NUM_COUNT) {
        char16_t ch = hexSource[hexIdx];
        ch = (ch << BIT_COUNT_IN_HEX_NUM) | hexSource[hexIdx + 1];
        ch = (ch << BIT_COUNT_IN_HEX_NUM) | hexSource[hexIdx + 2];
        ch = (ch << BIT_COUNT_IN_HEX_NUM) | hexSource[hexIdx + 3];
        utf16s += ch;
    }

    result = convert.to_bytes(utf16s);

    return result;
}