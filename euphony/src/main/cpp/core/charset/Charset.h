#ifndef EUPHONY_CHARSET_H
#define EUPHONY_CHARSET_H

#include <string>
#include "HexVector.h"

namespace Euphony {
    class Charset {
    public:
        virtual ~Charset() = default;

        virtual HexVector encode(std::string src) = 0;
        virtual std::string decode(const HexVector &src) = 0;
    };
}
#endif //EUPHONY_CHARSET_H
