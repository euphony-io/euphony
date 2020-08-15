//
// Created by desig on 2020-08-15.
//

#ifndef EUPHONY_EPNYSOUNDGENERATOR_H
#define EUPHONY_EPNYSOUNDGENERATOR_H


#include <TappableAudioSource.h>
#include <Oscillator.h>

class EpnySoundGenerator : public TappableAudioSource{
    static constexpr size_t kSharedBufferSize = 1024;
public:
    EpnySoundGenerator(int32_t sampleRate, int32_t channelCount);
    ~EpnySoundGenerator() = default;

    EpnySoundGenerator(EpnySoundGenerator&& other) = default;
    EpnySoundGenerator& operator= (EpnySoundGenerator&& other) = default;

    void tap(bool isDown) override;
    void renderAudio(float *audioData, int32_t numFrames) override;

private:
    std::unique_ptr<Oscillator[]> mOscillators;
    std::unique_ptr<float[]> mBuffer = std::make_unique<float[]>(kSharedBufferSize);

};


#endif //EUPHONY_EPNYSOUNDGENERATOR_H
