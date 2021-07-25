//
// Created by desig on 2021-07-25.
//

#ifndef EUPHONY_BINARYCODEC_H
#define EUPHONY_BINARYCODEC_H

#include "Definitions.h"
#include <string>
using std::string;

namespace Euphony {
    class BinaryCodec {
        CharacterSet characterSet;
    public:
        virtual ~BinaryCodec() = default;

        CharacterSet getCharacterSet() {
            return characterSet;
        }

        void setCharacterSet(CharacterSet charSet) {
            characterSet = charSet;
        }

        virtual string encode(string src) = 0;
        virtual string decode(string src) = 0;
    };
}

#endif //EUPHONY_BINARYCODEC_H
