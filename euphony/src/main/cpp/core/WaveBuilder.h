#ifndef EUPHONY_WAVEBUILDER_H
#define EUPHONY_WAVEBUILDER_H

#include "Wave.h"
using std::shared_ptr;

namespace Euphony {
    class WaveBuilder {
    private:
        Wave wave;

    public:
        WaveBuilder& vibratesAt(int hz);
        WaveBuilder& setSize(int size);
        WaveBuilder& setCrossfade(CrossfadeType type);
        shared_ptr<Wave> build();
    };
};
#endif //EUPHONY_WAVEBUILDER_H
