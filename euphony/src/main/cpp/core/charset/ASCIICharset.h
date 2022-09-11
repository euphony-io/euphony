#ifndef EUPHONY_ASCIICHARSET_H
#define EUPHONY_ASCIICHARSET_H

#include "Charset.h"

namespace Euphony {

    class ASCIICharset : public Charset {
    public:
        ASCIICharset() = default;
        ~ASCIICharset() = default;
        HexVector encode(std::string src);
        std::string decode(const HexVector &src);
    };
}

#endif //EUPHONY_ASCIICHARSET_H
