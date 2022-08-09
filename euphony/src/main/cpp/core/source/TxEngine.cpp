//
// Created by desig on 2020-08-15.
//
#include <oboe/Oboe.h>

#include <utility>
#include <Log.h>
#include "../Base2.h"
#include "../ModemFactory.h"
#include "../PacketBuilder.h"
#include "../TxEngine.h"
#include "../EuPIRenderer.h"
#include "../AudioStreamCallback.h"
#include "../WaveRenderer.h"

using namespace Euphony;

class TxEngine::TxEngineImpl : public IRestartable{
public:
    std::mutex mLock;
    std::shared_ptr<oboe::AudioStream> mStream;
    oboe::AudioStreamBuilder mStreamBuilder;
    std::unique_ptr<AudioStreamCallback> mCallback;
    //std::shared_ptr<EuphonyAudioSource> mAudioSource = nullptr;
    std::shared_ptr<EuPIRenderer> mEuPIRenderer;
    std::shared_ptr<WaveRenderer> mWaveRenderer;
    bool mIsLatencyDetectionSupported = false;
    oboe::Result mStreamResult = oboe::Result::ErrorBase;

    int32_t mDeviceId = oboe::Unspecified;
    oboe::AudioApi mAudioApi = oboe::AudioApi::Unspecified;

    std::shared_ptr<Packet> txPacket = nullptr;
    std::shared_ptr<Modem> mModem = nullptr;
    ModulationType mModulationType;
    BaseType mBaseCodingType;
    ModeType mModeType;
    Status mStatus;

    TxEngineImpl()
    : mModulationType(ModulationType::FSK)
    , mBaseCodingType(BaseType::BASE16)
    , mModeType(ModeType::DEFAULT)
    , mEuPIRenderer(EuPIRenderer::getInstance(kSampleRate, kChannelCount))
    , mWaveRenderer(WaveRenderer::getInstance())
    , mStatus(Status::STOP){
        createCallback();
        mStreamResult = createPlaybackStream();
        if(mStreamResult == oboe::Result::OK)
            LOGD("EUPHONY / EpnyTxEngine: %s", oboe::convertToText(mStreamResult));
        else
            LOGE("Error creating playback stream. Error: %s", oboe::convertToText(mStreamResult));

        setModulation(ModulationType::FSK);
        //start();
    }

    virtual ~TxEngineImpl() = default;

    void createCallback() {
        mCallback = std::make_unique<AudioStreamCallback>(*this);
    }

    oboe::Result createPlaybackStream() {
        return mStreamBuilder.setSharingMode(oboe::SharingMode::Exclusive)
                ->setPerformanceMode(oboe::PerformanceMode::LowLatency)
                ->setFormat(oboe::AudioFormat::Float)
                ->setCallback(mCallback.get())
                ->setChannelCount(kChannelCount)
                ->setSampleRate(kSampleRate)
                ->setDeviceId(mDeviceId)
                ->openStream(mStream);
    }

    void setPerformance(oboe::PerformanceMode mode) {
        mStreamBuilder.setPerformanceMode(mode)->openStream(mStream);
    }

    void restart() override {
        start();
    }

    void stop() {
        std::lock_guard<std::mutex> lock(mLock);
        if(mStream) {
            switch(mModeType) {
                case ModeType::DEFAULT:
                default:
                    stopDefaultMode();
                    break;
                case ModeType::EUPI:
                    stopEuPIMode();
                    break;
            }
            mStream->requestPause();
            mStream->requestFlush();
            mStatus = Status::STOP;
        }
    }

    void stopDefaultMode() {
        mWaveRenderer->tap(false);
    }

    void stopEuPIMode() {
        mEuPIRenderer->tap(false);
    }

    Euphony::Result reopenStream() {
        {
            // Stop and close in case not already closed.
            std::lock_guard<std::mutex> lock(mLock);
            if (mStream) {
                mStream->stop();
                mStream->close();
            }
        }
        return start();
    }

    void startDefaultMode() {
        mWaveRenderer->tap(true);
        mCallback->setSource(std::dynamic_pointer_cast<IRenderableAudio>(mWaveRenderer));
        mStream->start();
        mIsLatencyDetectionSupported = (mStream->getTimestamp((CLOCK_MONOTONIC)) != oboe::Result::ErrorUnimplemented);
        mStatus = Status::RUNNING;
    }

    void startEuPIMode() {
        mEuPIRenderer->tap(true);
        mCallback->setSource(std::dynamic_pointer_cast<IRenderableAudio>(mEuPIRenderer));
        mStream->start();
        mIsLatencyDetectionSupported = (mStream->getTimestamp((CLOCK_MONOTONIC)) != oboe::Result::ErrorUnimplemented);
        mStatus = Status::RUNNING;
    }

    Euphony::Result start() {
        std::lock_guard<std::mutex> lock(mLock);

        if(mStatus == Status::RUNNING)
            return Euphony::Result::ERROR_ALREADY_RUNNING;

        if(mStreamResult == oboe::Result::OK) {
            switch(mModeType) {
                case ModeType::DEFAULT:
                default:
                    startDefaultMode();
                    break;
                case ModeType::EUPI:
                    startEuPIMode();
                    break;
            }
        }
        else {
            mStatus = Status::STOP;
            return Euphony::Result::ERROR_GENERAL;
        }

        return Euphony::Result::OK;
    }

    void setCode(std::string data) {
        txPacket = Packet::create()
                .setPayloadWithASCII(std::move(data))
                .basedOnBase16()
                .build();

        txPacket->setBaseType(mBaseCodingType);

        auto waveList = mModem->modulate(txPacket->toString());
        mWaveRenderer->setWaveList(waveList);
    }

    void setCodingType(int codingTypeSrc) {
        switch(codingTypeSrc) {
            case 0:
                mBaseCodingType = BaseType::BASE2;
                break;
            default:
            case 1:
                mBaseCodingType = BaseType::BASE16;
                break;
        }

        if(txPacket != nullptr) {
            txPacket->setBaseType(mBaseCodingType);
        }
    }

    void setMode(int modeSrc) {
        switch(modeSrc) {
            case 0:
            default:
                mModeType = ModeType::DEFAULT;
                break;
            case 1:
                mModeType = ModeType::EUPI;
                break;
        }
    }

    void setModulation(int modulationTypeSrc) {
        switch(modulationTypeSrc) {
            case 0:
            default:
                mModulationType = ModulationType::FSK;
                break;
        }

        mModem = ModemFactory::create(mModulationType);
    }

    void setModulation(ModulationType modulationTypeSrc) {
        switch(modulationTypeSrc) {
            case ModulationType::FSK:
            default:
                mModulationType = ModulationType::FSK;
                break;
        }

        mModem = ModemFactory::create(mModulationType);
    }

    void setBufferSizeInBursts(int32_t numBursts)
    {
        std::lock_guard<std::mutex> lock(mLock);
        if (!mStream) return;

        mIsLatencyDetectionSupported = false;
        mCallback->setBufferTuneEnabled(numBursts == kBufferSizeAutomatic);
        auto result = mStream->setBufferSizeInFrames(
                numBursts * mStream->getFramesPerBurst());
        if (result) {
            LOGD("Buffer size successfully changed to %d", result.value());
        } else {
            LOGW("Buffer size could not be changed, %d", result.error());
        }
    }

    double getCurrentOutputLatencyMillis() {
        if (!mIsLatencyDetectionSupported) return -1.0;

        std::lock_guard<std::mutex> lock(mLock);
        if (!mStream) return -1.0;

        // Get the time that a known audio frame was presented for playing
        auto result = mStream->getTimestamp(CLOCK_MONOTONIC);
        double outputLatencyMillis = -1;
        const int64_t kNanosPerMillisecond = 1000000;
        if (result == oboe::Result::OK) {
            oboe::FrameTimestamp playedFrame = result.value();
            // Get the write index for the next audio frame
            int64_t writeIndex = mStream->getFramesWritten();
            // Calculate the number of frames between our known frame and the write index
            int64_t frameIndexDelta = writeIndex - playedFrame.position;
            // Calculate the time which the next frame will be presented
            int64_t frameTimeDelta = (frameIndexDelta * oboe::kNanosPerSecond) /  (mStream->getSampleRate());
            int64_t nextFramePresentationTime = playedFrame.timestamp + frameTimeDelta;
            // Assume that the next frame will be written at the current time
            using namespace std::chrono;
            int64_t nextFrameWriteTime =
                    duration_cast<nanoseconds>(steady_clock::now().time_since_epoch()).count();
            // Calculate the latency
            outputLatencyMillis = static_cast<double>(nextFramePresentationTime - nextFrameWriteTime)
                                  / kNanosPerMillisecond;
        } else {
            LOGE("Error calculating latency: %s", oboe::convertToText(result.error()));
        }

        return outputLatencyMillis;
    }

    bool isLatencyDetectionSupported() const {
        return mIsLatencyDetectionSupported;
    }
};

TxEngine::TxEngine()
: pImpl(std::make_unique<TxEngineImpl>()) { }


TxEngine::~TxEngine() = default;


void TxEngine::tap(bool isDown) {
    switch(pImpl->mModeType) {
        case ModeType::DEFAULT:
        default:
            pImpl->mWaveRenderer->tap(isDown);
            break;
        case ModeType::EUPI:
            pImpl->mEuPIRenderer->tap(isDown);
            break;
    }
}

void TxEngine::tapCount(bool isDown, int count) {
    pImpl->mWaveRenderer->tapCount(isDown, count);
}

void TxEngine::setEupiFrequency(double freq) {
    pImpl->mEuPIRenderer->setFrequency(freq);
}

void TxEngine::stop() {
    pImpl->stop();
}

Euphony::Result TxEngine::start() {
    return pImpl->start();
}

void TxEngine::setCode(std::string data) {
    pImpl->setCode(std::move(data));
}

void TxEngine::setCodingType(int codingTypeSrc) {
    pImpl->setModulation(codingTypeSrc);
}

void TxEngine::setMode(int modeSrc) {
    pImpl->setMode(modeSrc);
}

void TxEngine::setModulation(int modulationTypeSrc) {
    pImpl->setModulation(modulationTypeSrc);
}

bool TxEngine::isLatencyDetectionSupported() {
    return pImpl->isLatencyDetectionSupported();
}

void TxEngine::setAudioApi(oboe::AudioApi audioApi) {
    pImpl->mAudioApi = audioApi;
}

void TxEngine::setPerformance(oboe::PerformanceMode mode) {
    pImpl->setPerformance(mode);
}

void TxEngine::setDeviceId(int32_t deviceId) {
    pImpl->mDeviceId = deviceId;
}

int TxEngine::getFramesPerBursts() {
    return pImpl->mStream->getFramesPerBurst();
}

void TxEngine::setBufferSizeInBursts(int32_t numBursts) {
    pImpl->setBufferSizeInBursts(numBursts);
}

double TxEngine::getCurrentOutputLatencyMillis() {
    return pImpl->getCurrentOutputLatencyMillis();
}

Status Euphony::TxEngine::getStatus() {
    return pImpl->mStatus;
}

std::string TxEngine::getCode() {
    return pImpl->txPacket->getPayloadStr();
}

std::string TxEngine::getGenCode() {
    return pImpl->txPacket->toString();
}

float *Euphony::TxEngine::getGenWaveSource() {
    return pImpl->mWaveRenderer->getWaveSource();
}

int Euphony::TxEngine::getGenWaveSourceSize() {
    return pImpl->mWaveRenderer->getWaveSourceSize();
}
