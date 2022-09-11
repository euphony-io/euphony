#ifndef EUPHONY_CHARSETAUTOSELECTOR_H
#define EUPHONY_CHARSETAUTOSELECTOR_H

#include "Charset.h"

namespace Euphony {

    class CharsetAutoSelector : public Charset {
    public:
        CharsetAutoSelector() = default;
        ~CharsetAutoSelector() = default;
        HexVector select(std::string src);
    };
}

#endif //EUPHONY_CHARSETAUTOSELECTOR_H