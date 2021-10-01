#include "../UTF16Charset.h"
#include <sstream>
#include <iomanip>
#include <codecvt>
#include <bitset>

using namespace Euphony;

const unsigned int BIT_COUNT_IN_HEX_NUM = 4;
const unsigned int BYTE_COUNT = 2;
const unsigned int HEX_NUM_COUNT = BYTE_COUNT * 8 / BIT_COUNT_IN_HEX_NUM;

HexVector UTF16Charset::encode(std::string src)
{
    std::wstring_convert<std::codecvt_utf8_utf16<char16_t>, char16_t> convert;
    std::u16string utf16s = convert.from_bytes(src);
    HexVector result = HexVector(2 * utf16s.size());

    for (int char_idx = 0; char_idx < utf16s.size(); char_idx++)
    {
        char16_t now_char = utf16s[char_idx];
        std::bitset<16> char_bits = std::bitset<16>(now_char);
        std::vector<u_int8_t> tmp;
        for (int hex_idx = 0; hex_idx < HEX_NUM_COUNT; hex_idx++)
        {
            u_int8_t hex_num = 0;
            for (int bit_idx = 0; bit_idx < BIT_COUNT_IN_HEX_NUM; bit_idx++)
            {
                if (char_bits.test(BIT_COUNT_IN_HEX_NUM * hex_idx + bit_idx))
                {
                    hex_num += 1 << bit_idx;
                }
            }
            tmp.push_back(hex_num);
        }
        for (auto iter = tmp.rbegin(); iter != tmp.rend(); iter++)
        {
            result.pushBack(*iter);
        }
    }

    return result;
}

std::string UTF16Charset::decode(const HexVector &src)
{
    std::wstring_convert<std::codecvt_utf8_utf16<char16_t>, char16_t> convert;
    std::u16string utf16s = u"";
    std::string result = "";
    std::vector<u_int8_t> hexSource = src.getHexSource();
    char16_t ch = 0;

    for (int hex_idx = 0; hex_idx < hexSource.size() / HEX_NUM_COUNT; hex_idx++)
    {
        for (int bit_idx = 0; bit_idx < BIT_COUNT_IN_HEX_NUM; bit_idx++)
        {
            u_int8_t now_num = hexSource[BIT_COUNT_IN_HEX_NUM * hex_idx + bit_idx];
            int add = now_num << (BYTE_COUNT * 8 - (bit_idx+1) * BIT_COUNT_IN_HEX_NUM);
            ch += add;
        }
        utf16s += ch;
        ch = 0;
    }

    result = convert.to_bytes(utf16s);

    return result;
}