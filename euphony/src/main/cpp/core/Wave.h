//
// Created by designe on 20. 9. 16.
//

#ifndef EUPHONY_WAVE_H
#define EUPHONY_WAVE_H

#include <vector>

constexpr double kPi = M_PI;
constexpr double kTwoPi = kPi * 2.0;

namespace Euphony {

    class WaveBuilder;

    enum CrossfadeType {
        FRONT, END, BOTH
    };

    class Wave {
    private:
        int mHz;
        int mSize;
        std::vector<float> mSource;
        float mPhase = 0.0;
        std::atomic<double> mPhaseIncrement{0.0};

        void oscillate(int hz, int size);
        void updatePhaseIncrement(int hz);
    public:
        friend class WaveBuilder;

        Wave(int hz, int size);
        Wave();
        static WaveBuilder create();

        int getHz() const;
        void setHz(int hz);
        int getSize() const;
        void setSize(int size);
        const std::vector<float> &getSource() const;
        void setSource(const std::vector<float> &source);
        void applyCrossfade(CrossfadeType);
    };

}

#endif //EUPHONY_WAVE_H
