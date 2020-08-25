//
// Created by desig on 2020-08-15.
//

#ifndef EUPHONY_EPNYTXENGINE_H
#define EUPHONY_EPNYTXENGINE_H

#include <memory>

class EpnyTxEngine {
public:
    EpnyTxEngine();
    void tap(bool isDown);
    void setAudioApi(oboe::AudioApi audioApi);
    void setChannelCount(int channelCount);
    void setDeviceId(int32_t deviceId);
    void setBufferSizeInBursts(int32_t numBursts);

private:
    //Do Not Copy EpnyTxEngine
    EpnyTxEngine(const EpnyTxEngine &);
    const EpnyTxEngine &operator =(const EpnyTxEngine &);

    class impl; std::unique_ptr<impl> pimpl;
};


#endif //EUPHONY_EPNYTXENGINE_H
