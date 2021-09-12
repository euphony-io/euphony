#ifndef EUPHONY_EUPHONYAUDIOSOURCE_H
#define EUPHONY_EUPHONYAUDIOSOURCE_H

#include "IRenderableAudio.h"

class EuphonyAudioSource : public IRenderableAudio {
public:
    ~EuphonyAudioSource() = default;
    virtual void tap(bool isDown) = 0;
};
#endif //EUPHONY_EUPHONYAUDIOSOURCE_H
