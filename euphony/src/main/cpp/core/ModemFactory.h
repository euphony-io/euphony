#ifndef EUPHONY_MODEMFACTORY_H
#define EUPHONY_MODEMFACTORY_H

#include "Definitions.h"
#include "Modem.h"
#include "FSK.h"

namespace Euphony {
    class ModemFactory {
    public:
        static std::shared_ptr<Modem> create(ModulationType type) {
            switch(type) {
                default:
                case ModulationType::FSK :
                    return std::make_shared<FSK>();
            }
        }
    };
}

#endif //EUPHONY_MODEMFACTORY_H
