#ifndef EUPHONY_UTF8CHARSET_H
#define EUPHONY_UTF8CHARSET_H

#include "Charset.h"

namespace Euphony {

    class UTF8Charset : public Charset {
    public:
        UTF8Charset() = default;
        ~UTF8Charset() = default;
        HexVector encode(std::string src);
        std::string decode(const HexVector &src);
    };
}

#endif //EUPHONY_UTF8CHARSET_H