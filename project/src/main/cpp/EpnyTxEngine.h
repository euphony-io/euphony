//
// Created by desig on 2020-08-15.
//

#ifndef EUPHONY_EPNYTXENGINE_H
#define EUPHONY_EPNYTXENGINE_H

#include <memory>

constexpr int32_t kBufferSizeAutomatic = 0;

class EpnyTxEngine {
public:
    EpnyTxEngine();
    ~EpnyTxEngine();

    void tap(bool isDown);
    void setAudioApi(oboe::AudioApi audioApi);
    void setChannelCount(int channelCount);
    void setDeviceId(int32_t deviceId);
    void setBufferSizeInBursts(int32_t numBursts);
    double getCurrentOutputLatencyMillis();
    bool isLatencyDetectionSupported();

private:
    //Do Not Copy EpnyTxEngine
    EpnyTxEngine(const EpnyTxEngine &);
    const EpnyTxEngine &operator =(const EpnyTxEngine &);

    class Impl; std::unique_ptr<Impl> pImpl;
};


#endif //EUPHONY_EPNYTXENGINE_H
