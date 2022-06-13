#ifndef EUPHONY_RXENGINE_H
#define EUPHONY_RXENGINE_H

#include <memory>
#include "Definitions.h"

namespace Euphony {

    class RxEngine {
    public:
        RxEngine();
        ~RxEngine();

        Euphony::Result start();
        void stop();

    private:
        RxEngine(const RxEngine &);
        const RxEngine &operator=(const RxEngine &);
        class RxEngineImpl;
        std::unique_ptr<RxEngineImpl> pImpl;
    };
}

#endif //EUPHONY_RXENGINE_H
