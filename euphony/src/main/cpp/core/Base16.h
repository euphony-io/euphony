#ifndef EUPHONY_BASE16_H
#define EUPHONY_BASE16_H

#include "BaseCodec.h"

namespace Euphony {

    class Base16 : public BaseCodec {
    public:
        Base16() = default;
        ~Base16() = default;
        string encode(string src);
        string decode(string src);
        int convertChar2Int(char source);
    };
}

#endif //EUPHONY_BASE16_H
