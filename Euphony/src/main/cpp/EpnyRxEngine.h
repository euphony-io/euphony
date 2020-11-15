//
// Created by desig on 2020-11-15.
//

#ifndef EUPHONY_EPNYRXENGINE_H
#define EUPHONY_EPNYRXENGINE_H

#include <memory>

class EpnyRxEngine {
public:
    EpnyRxEngine();
    ~EpnyRxEngine();

    void stop();
    void start();

private:
    // Do Not Copy EpnyRxEngine
    EpnyRxEngine(const EpnyRxEngine &);
    const EpnyRxEngine &operator =(const EpnyRxEngine &);

    class EpnyRxEngineImpl; std::unique_ptr<EpnyRxEngineImpl> pImpl;
};


#endif //EUPHONY_EPNYRXENGINE_H
