//
// Created by desig on 2020-11-15.
//

#ifndef EUPHONY_RXENGINE_H
#define EUPHONY_RXENGINE_H

#include <memory>

namespace Euphony {
    class RxEngine {
    public:
        RxEngine();

        ~RxEngine();

        void stop();

        void start();

    private:
        // Do Not Copy EpnyRxEngine
        RxEngine(const RxEngine &);

        const RxEngine &operator=(const RxEngine &);

        class RxEngineImpl;

        std::unique_ptr<RxEngineImpl> pImpl;
    };
}


#endif //EUPHONY_RXENGINE_H
