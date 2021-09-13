#ifndef EUPHONY_BASE2_H
#define EUPHONY_BASE2_H

#include "Base.h"

namespace Euphony {

    class Base2 : public Base {
    public:
        Base2(const HexVector &hexVectorSrc);
        ~Base2() = default;
        std::string getBaseString();
        const HexVector &getHexVector() const;
        int convertChar2Int(char source) const;
        char convertInt2Char(int source) const;

    private:
        HexVector hexVector;

        std::string hexToBase2(u_int8_t hex);
    };
}

#endif //EUPHONY_BASE2_H
