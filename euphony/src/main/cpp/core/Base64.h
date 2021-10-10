#ifndef EUPHONY_BASE64_H
#define EUPHONY_BASE64_H

#include "Base.h"
#include "Base64Exception.h"

namespace Euphony {

    class Base64 : public Base {
    public:
        Base64(const HexVector &hexVectorSrc);
        ~Base64() = default;
        std::string getBaseString();
        const HexVector &getHexVector() const;
        int convertChar2Int(char source) const;
        char convertInt2Char(int source) const;

    private:
        HexVector hexVector;

        std::string bitsToBase64(int sum);
    };
}

#endif //EUPHONY_BASE64_H
