#ifndef EUPHONY_BASEFACTORY_H
#define EUPHONY_BASEFACTORY_H

#include "Definitions.h"
#include "Base2.h"
#include "Base16.h"


namespace Euphony {
    class BaseFactory {
    public:
        static std::shared_ptr<Base> create(BaseType type, const HexVector &hexVectorSrc) {
            switch(type) {
                case BaseType::BASE2:
                    return std::make_shared<Base2>(hexVectorSrc);
                case BaseType::BASE16:
                default:
                    return std::make_shared<Base16>(hexVectorSrc);
            }
        }
    };
}




#endif //EUPHONY_BASEFACTORY_H
