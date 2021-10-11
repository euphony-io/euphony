#ifndef EUPHONY_UTF32CHARSET_H
#define EUPHONY_UTF32CHARSET_H

#include "Charset.h"

namespace Euphony {

    class UTF32Charset : public Charset {
    public:
        UTF32Charset() = default;
        ~UTF32Charset() = default;
        HexVector encode(std::string src);
        std::string decode(const HexVector &src);
    private:
        const u_int8_t BIT_COUNT_IN_HEX_NUM = 4;
        const u_int8_t BYTE_COUNT = 4;
        const u_int8_t HEX_NUM_COUNT = BYTE_COUNT * 8 / BIT_COUNT_IN_HEX_NUM;
        const char32_t BIT_MASK = 0x0000000F;
    };
}

#endif //EUPHONY_UTF32CHARSET_H