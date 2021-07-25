#ifndef EUPHONY_BASE16_H
#define EUPHONY_BASE16_H

#include "BinaryCodec.h"

namespace Euphony {

    class Base16 : public BinaryCodec {
    public:
        Base16() = default;
        ~Base16() = default;
        string encode(string src);
        string decode(string src);
    };
}

#endif //EUPHONY_BASE16_H
