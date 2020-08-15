//
// Created by desig on 2020-08-15.
//

#ifndef EUPHONY_EPNYTXENGINE_H
#define EUPHONY_EPNYTXENGINE_H

#include <memory>

class EpnyTxEngine {

private:
    //Do Not Copy EpnyTxEngine
    EpnyTxEngine(const EpnyTxEngine &);
    const EpnyTxEngine &operator =(const EpnyTxEngine &);

    class impl; std::unique_ptr<impl> pimpl;
};


#endif //EUPHONY_EPNYTXENGINE_H
