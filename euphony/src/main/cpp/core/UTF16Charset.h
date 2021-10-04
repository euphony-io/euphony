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
    };
}

#endif //EUPHONY_UTF16CHARSET_H
