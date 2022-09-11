#ifndef EUPHONY_BASE_H
#define EUPHONY_BASE_H

#include <string>
#include "HexVector.h"

namespace Euphony {
    class Base {
    public:
        virtual ~Base() = default;

        virtual std::string getBaseString() = 0;
        virtual const HexVector &getHexVector() const = 0;
        virtual int convertChar2Int(char src) const = 0;
        virtual char convertInt2Char(int src) const = 0;
    };
}

#endif //EUPHONY_BASE_H
