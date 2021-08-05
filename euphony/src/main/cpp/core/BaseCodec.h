//
// Created by desig on 2021-07-25.
//

#ifndef EUPHONY_BASECODEC_H
#define EUPHONY_BASECODEC_H

#include "Definitions.h"
#include <string>
using std::string;

namespace Euphony {
    class BaseCodec {
        CharacterSet characterSet;
    public:
        virtual ~BaseCodec() = default;

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

#endif //EUPHONY_BASECODEC_H
