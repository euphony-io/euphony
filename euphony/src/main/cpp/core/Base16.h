#ifndef EUPHONY_BASE16_H
#define EUPHONY_BASE16_H

#include "Base.h"
#include "Base16Exception.h"

namespace Euphony {

    class Base16 : public Base {
    public:
        Base16(const HexVector &hexVectorSrc);
        ~Base16() = default;
        std::string getBaseString();
        const HexVector &getHexVector() const;
        int convertChar2Int(char source) const;
        char convertInt2Char(int source) const;

    private:
        HexVector hexVector;
    };
}

#endif //EUPHONY_BASE16_H
