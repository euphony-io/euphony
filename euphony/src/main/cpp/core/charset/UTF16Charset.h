#ifndef EUPHONY_UTF16CHARSET_H
#define EUPHONY_UTF16CHARSET_H

#include "Charset.h"

namespace Euphony {

    class UTF16Charset : public Charset {
    public:
        UTF16Charset() = default;
        ~UTF16Charset() = default;
        HexVector encode(std::string src);
        std::string decode(const HexVector &src);
    private:
        const u_int8_t BIT_COUNT_IN_HEX_NUM = 4;
        const u_int8_t BYTE_COUNT = 2;
        const u_int8_t HEX_NUM_COUNT = BYTE_COUNT * 8 / BIT_COUNT_IN_HEX_NUM;
        const char16_t BIT_MASK = 0x000F;
    };
}

#endif //EUPHONY_UTF16CHARSET_H