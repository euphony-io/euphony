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
    shared_ptr<EuphonyAudioSource> mAudioSource = nullptr;
    bool mIsLatencyDetectionSupported = false;

    double eupiFreq;
    int32_t mDeviceId = oboe::Unspecified;
    int32_t mChannelCount = oboe::Unspecified;
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
    , mStatus(STOP){
        createCallback();
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

    std::shared_ptr<EuphonyAudioSource> createAudioSource(ModeType modeType) {
        switch(modeType) {
            default:
            case ModeType::DEFAULT: {
                auto modulationResult = mModem->modulate(txPacket->toString());
                return std::make_shared<WaveRenderer>(modulationResult, kChannelCount);
            }
            case ModeType::EUPI:
                mAudioSource = std::make_shared<EuPIRenderer>(kSampleRate, kChannelCount);
                std::dynamic_pointer_cast<EuPIRenderer>(mAudioSource)->setFrequency(eupiFreq);
                return mAudioSource;
        }
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
            mStream->stop();
            mStatus = STOP;
        }
    }

    oboe::Result reopenStream() {
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

    oboe::Result start() {
        std::lock_guard<std::mutex> lock(mLock);
        auto result = createPlaybackStream();
        if(result == oboe::Result::OK) {
            mCallback->setSource(std::dynamic_pointer_cast<IRenderableAudio>(mAudioSource));
            mStream->start();
            mIsLatencyDetectionSupported = (mStream->getTimestamp((CLOCK_MONOTONIC)) != oboe::Result::ErrorUnimplemented);
            mStatus = RUNNING;
            LOGD("EUPHONY / EpnyTxEngine: %s", oboe::convertToText(result));
        } else {
            mStatus = STOP;
            LOGE("Error creating playback stream. Error: %s", oboe::convertToText(result));
        }

        return result;
    }

    void setCode(std::string data) {
        txPacket = Packet::create()
                .setPayloadWithASCII(std::move(data))
                .basedOnBase16()
                .build();

        txPacket->setBaseType(mBaseCodingType);

        if (mAudioSource != nullptr) {
            auto waveList = mModem->modulate(txPacket->toString());
            std::dynamic_pointer_cast<WaveRenderer>(mAudioSource)->setWaveList(waveList);
        }
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

        mAudioSource = createAudioSource(mModeType);
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

    void setEupiFrequency(double freq) {
        eupiFreq = freq;
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
    if(pImpl->mAudioSource != nullptr)
        pImpl->mAudioSource->tap(isDown);
}

void TxEngine::tapCount(bool isDown, int count) {
    if(pImpl->mAudioSource != nullptr)
        std::dynamic_pointer_cast<WaveRenderer>(pImpl->mAudioSource)->tapCount(isDown, count);
}

void TxEngine::setEupiFrequency(double freq) {
    pImpl->setEupiFrequency(freq);

    if(pImpl->mAudioSource != nullptr)
        std::dynamic_pointer_cast<EuPIRenderer>(pImpl->mAudioSource)->setFrequency(freq);
}

void TxEngine::stop() {
    pImpl->stop();
}

void TxEngine::start() {
    pImpl->start();
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

void TxEngine::setChannelCount(int channelCount) {
    pImpl->mChannelCount = channelCount;
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

