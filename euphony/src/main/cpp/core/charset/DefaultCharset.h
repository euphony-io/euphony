#ifndef EUPHONY_DEFAULTCHARSET_H
#define EUPHONY_DEFAULTCHARSET_H

#include "Charset.h"

namespace Euphony {

    class DefaultCharset : public Charset {
    public:
        DefaultCharset() = default;
        ~DefaultCharset() = default;
        HexVector encode(std::string src);
        std::string decode(const HexVector &src);
    };
}


#endif //EUPHONY_DEFAULTCHARSET_H
