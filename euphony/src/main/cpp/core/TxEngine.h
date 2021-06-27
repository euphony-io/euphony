//
// Created by desig on 2020-08-15.
//

#ifndef EUPHONY_TXENGINE_H
#define EUPHONY_TXENGINE_H

#include <memory>

constexpr int32_t kBufferSizeAutomatic = 0;

namespace Euphony {
    enum Status {
        RUNNING, STOP
    };

    class TxEngine {
    public:
        TxEngine();

        ~TxEngine();

        void tap(bool isDown);

        void stop();

        void start();

        void setAudioApi(oboe::AudioApi audioApi);

        void setChannelCount(int channelCount);

        void setDeviceId(int32_t deviceId);

        int getFramesPerBursts();

        void setBufferSizeInBursts(int32_t numBursts);

        void setPerformance(oboe::PerformanceMode mode);

        void setAudioFrequency(double freq);

        double getCurrentOutputLatencyMillis();

        Status getStatus();

        bool isLatencyDetectionSupported();

    private:
        //Do Not Copy EpnyTxEngine
        TxEngine(const TxEngine &);

        const TxEngine &operator=(const TxEngine &);

        class TxEngineImpl;

        std::unique_ptr<TxEngineImpl> pImpl;
    };
}

#endif //EUPHONY_TXENGINE_H
