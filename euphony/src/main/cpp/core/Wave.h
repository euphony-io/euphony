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
    public:
        Wave();
        Wave(int hz, int size);
        explicit Wave(const Wave& copy);

        static WaveBuilder create();
        void oscillate();
        void oscillate(int hz, int size);
        void applyCrossfade(CrossfadeType);

        int getHz() const;
        void setHz(int hz);
        int getSize() const;
        void setSize(int size);
        std::vector<float> getSource() const;
        void setSource(const std::vector<float> &source);
        std::vector<int16_t> getInt16Source();
        int16_t convertFloat2Int16(float source);

    private:
        friend class WaveBuilder;

        int mHz;
        int mSize;
        std::vector<float> mSource;
        float mPhase = 0.0;
        std::atomic<double> mPhaseIncrement{0.0};
        void updatePhaseIncrement(int hz);

    };

}

#endif //EUPHONY_WAVE_H
